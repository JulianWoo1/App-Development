package com.example.realitycheck.data.repository

import com.example.realitycheck.data.model.ContentItem

class FakeContentRepository : ContentRepository {
    var nextPair: Pair<ContentItem, ContentItem>? = null
    override suspend fun getNextPair(): Result<Pair<ContentItem, ContentItem>> {
        return nextPair?.let { Result.success(it) } ?: Result.failure(Exception("No pair"))
    }
}
