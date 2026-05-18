package com.example.realitycheck.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ContentItem(
    val id: String,
    val type: String,
    @SerialName("content_url") val contentUrl: String? = null,
    @SerialName("is_ai") val isAi: Boolean,
    val hint: String? = null,
    @SerialName("difficulty_rating") val difficultyRating: Int = 1
)
