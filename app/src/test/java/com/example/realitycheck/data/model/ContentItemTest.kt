package com.example.realitycheck.data.model

import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Test

class ContentItemTest {
    @Test
    fun testSerialization() {
        val jsonString = """
            {
                "id": "123e4567-e89b-12d3-a456-426614174000",
                "type": "image",
                "content_url": "https://example.com/image.png",
                "is_ai": true,
                "hint": "Look at the fingers",
                "difficulty_rating": 2
            }
        """.trimIndent()

        val item = Json.decodeFromString<ContentItem>(jsonString)
        
        assertEquals("123e4567-e89b-12d3-a456-426614174000", item.id)
        assertEquals("image", item.type)
        assertEquals("https://example.com/image.png", item.contentUrl)
        assertEquals(true, item.isAi)
        assertEquals("Look at the fingers", item.hint)
        assertEquals(2, item.difficultyRating)
    }
}
