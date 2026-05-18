package com.example.realitycheck.data.model

import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Test

class ProfileTest {
    @Test
    fun testSerialization() {
        val jsonString = """
            {
                "id": "user-uuid-123",
                "username": "PlayerOne",
                "avatar_url": "https://example.com/avatar.png",
                "total_xp": 150,
                "high_score_streak": 5,
                "updated_at": "2023-10-27T10:00:00Z"
            }
        """.trimIndent()

        val profile = Json.decodeFromString<Profile>(jsonString)
        
        assertEquals("user-uuid-123", profile.id)
        assertEquals("PlayerOne", profile.username)
        assertEquals("https://example.com/avatar.png", profile.avatarUrl)
        assertEquals(150, profile.totalXp)
        assertEquals(5, profile.highScoreStreak)
        assertEquals("2023-10-27T10:00:00Z", profile.updatedAt)
    }
}
