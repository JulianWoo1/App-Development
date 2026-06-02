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

    private val buffer = mutableListOf<ContentItem>()
    private val usedIds = mutableSetOf<String>()
    private val mutex = Mutex()
    private var fetchJob: Job? = null

    override suspend fun getNextPair(): Result<Pair<ContentItem, ContentItem>> {
        mutex.withLock {

            ensureBuffer()

            if (buffer.size < 2) {
                return Result.failure(Exception("Not enough unique content available"))
            }

            val first = buffer.removeAt(0)
            val second = buffer.removeAt(0)

            usedIds.add(first.id)
            usedIds.add(second.id)

            triggerPrefetchIfNeeded()

            return Result.success(first to second)
        }
    }

    private suspend fun ensureBuffer() {
        if (buffer.size >= 2) return

        val newItems = fetchBatch().filter { it.id !in usedIds }

        if (newItems.isEmpty()) {
            usedIds.clear()
            buffer.addAll(fetchBatch().shuffled())
        } else {
            buffer.addAll(if (randomize) newItems.shuffled() else newItems)
        }
    }

    private fun triggerPrefetchIfNeeded() {
        if (buffer.size <= threshold && fetchJob?.isActive != true) {
            fetchJob = scope.launch {
                try {
                    val more = fetchBatch()
                        .filter { it.id !in usedIds }

                    mutex.withLock {
                        buffer.addAll(if (randomize) more.shuffled() else more)
                    }
                } catch (_: Exception) {
                }
            }
        }
    }
}