package com.example.realitycheck.data.repository

import com.example.realitycheck.data.model.ContentItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class SupabaseContentRepository(
    private val fetchBatch: suspend () -> List<ContentItem>,
    private val threshold: Int = 4,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.IO),
    private val randomize: Boolean = true
) : ContentRepository {

    private val buffer = mutableListOf<ContentItem>()
    private val mutex = Mutex()
    private var fetchJob: Job? = null

    override suspend fun getNextPair(): Result<Pair<ContentItem, ContentItem>> {
        mutex.withLock {
            if (buffer.size < 2) {
                // Not enough items, must fetch synchronously before returning
                val newItems = try {
                    fetchBatch()
                } catch (e: Exception) {
                    return Result.failure(e)
                }
                if (newItems.isEmpty()) {
                    return Result.failure(Exception("No more content available"))
                }
                val itemsToAdd = if (randomize) newItems.shuffled() else newItems
                buffer.addAll(itemsToAdd)
            }

            if (buffer.size < 2) {
                 return Result.failure(Exception("Not enough content to form a pair"))
            }

            val pair = Pair(buffer.removeAt(0), buffer.removeAt(0))

            // Check if we need to fetch more in the background
            if (buffer.size <= threshold && fetchJob?.isActive != true) {
                fetchJob = scope.launch {
                    try {
                        val moreItems = fetchBatch()
                        mutex.withLock {
                            val itemsToAdd = if (randomize) moreItems.shuffled() else moreItems
                            buffer.addAll(itemsToAdd)
                        }
                    } catch (e: Exception) {
                        // Background fetch failed, we'll try again next time we drop below threshold
                    }
                }
            }

            return Result.success(pair)
        }
    }
}
