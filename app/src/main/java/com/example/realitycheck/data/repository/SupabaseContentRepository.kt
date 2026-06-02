package com.example.realitycheck.data.repository

import com.example.realitycheck.data.model.ContentItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * Buffers content items fetched from Supabase.
 *
 * Pre-fetch behaviour:
 *  - Buffer is filled eagerly on first use.
 *  - Whenever the buffer drops to or below [threshold] items a background
 *    fetch is triggered immediately, so the next [threshold] pairs are
 *    ready before the player needs them.
 *  - [threshold] defaults to 10 (= 5 pairs) to satisfy the "pre-fetch
 *    the next 5 items" requirement with headroom.
 */
class SupabaseContentRepository(
    private val fetchBatch: suspend () -> List<ContentItem>,
    private val threshold: Int = 10,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.IO),
    private val randomize: Boolean = true
) : ContentRepository {

    private val buffer   = mutableListOf<ContentItem>()
    private val mutex    = Mutex()
    private var fetchJob: Job? = null

    override suspend fun getNextPair(): Result<Pair<ContentItem, ContentItem>> {
        mutex.withLock {
            // Synchronous fetch if buffer is empty
            if (buffer.size < 2) {
                val newItems = try {
                    fetchBatch()
                } catch (e: Exception) {
                    return Result.failure(e)
                }
                if (newItems.isEmpty()) {
                    return Result.failure(Exception("No content available"))
                }
                buffer.addAll(if (randomize) newItems.shuffled() else newItems)
            }

            if (buffer.size < 2) {
                return Result.failure(Exception("Not enough content to form a pair"))
            }

            val pair = Pair(buffer.removeAt(0), buffer.removeAt(0))

            // Trigger background pre-fetch whenever buffer falls to threshold
            if (buffer.size <= threshold && fetchJob?.isActive != true) {
                fetchJob = scope.launch {
                    try {
                        val more = fetchBatch()
                        mutex.withLock {
                            buffer.addAll(if (randomize) more.shuffled() else more)
                        }
                    } catch (_: Exception) {
                        // Will retry next time buffer drops below threshold
                    }
                }
            }

            return Result.success(pair)
        }
    }
}