package com.example.realitycheck.data.repository

import com.example.realitycheck.data.model.ContentItem
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SupabaseContentRepositoryTest {

    private fun createDummyItems(count: Int): List<ContentItem> {
        return (1..count).map {
            ContentItem(
                id = it.toString(),
                type = "image",
                isAi = it % 2 == 0
            )
        }
    }

    @Test
    fun `getNextPair returns pair and pops from buffer`() = runTest {
        var fetchCount = 0
        val mockFetch: suspend () -> List<ContentItem> = {
            fetchCount++
            createDummyItems(10) // Returns 10 items
        }

        val repository = SupabaseContentRepository(fetchBatch = mockFetch, threshold = 4, randomize = false)
        
        // Initial call should trigger fetch and return first 2
        val result1 = repository.getNextPair()
        assertTrue(result1.isSuccess)
        assertEquals("1", result1.getOrNull()?.first?.id)
        assertEquals("2", result1.getOrNull()?.second?.id)
        assertEquals(1, fetchCount)

        // Second call should return next 2 without fetching
        val result2 = repository.getNextPair()
        assertEquals("3", result2.getOrNull()?.first?.id)
        assertEquals(1, fetchCount)
    }

    @Test
    fun `getNextPair triggers fetch when below threshold`() = runTest {
        var fetchCount = 0
        val mockFetch: suspend () -> List<ContentItem> = {
            fetchCount++
            createDummyItems(6) // Returns 6 items. Threshold is 4.
        }

        val repository = SupabaseContentRepository(fetchBatch = mockFetch, threshold = 4, randomize = false)
        
        repository.getNextPair() // Buffer has 4 left. fetchCount = 1
        assertEquals(1, fetchCount)
        
        repository.getNextPair() // Buffer has 2 left, drops below threshold (4), should trigger fetch
        // Coroutines in test will run the async fetch
        assertEquals(2, fetchCount)
    }
}
