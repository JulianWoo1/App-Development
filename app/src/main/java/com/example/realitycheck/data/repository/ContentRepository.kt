package com.example.realitycheck.data.repository

import com.example.realitycheck.data.model.ContentItem

interface ContentRepository {
    /**
     * Retrieves the next pair of content items for the game.
     * Manages a local buffer to ensure seamless delivery without waiting for network requests
     * during active gameplay.
     */
    suspend fun getNextPair(): Result<Pair<ContentItem, ContentItem>>
}
