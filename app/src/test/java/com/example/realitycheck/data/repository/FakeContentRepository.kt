package com.example.realitycheck.data.repository

import com.example.realitycheck.data.model.ContentItem

class FakeContentRepository : ContentRepository {
    private val pairs = listOf(
        ContentItem(id = "1", type = "image", isAi = false) to ContentItem(id = "2", type = "image", isAi = true),
        ContentItem(id = "3", type = "image", isAi = true) to ContentItem(id = "4", type = "image", isAi = true),
        ContentItem(id = "5", type = "image", isAi = false) to ContentItem(id = "6", type = "image", isAi = false),
    )
    private var index = 0

    override suspend fun getNextPair(): Result<Pair<ContentItem, ContentItem>> {
        val pair = pairs[index % pairs.size]
        index++
        return Result.success(pair)
    }
}
