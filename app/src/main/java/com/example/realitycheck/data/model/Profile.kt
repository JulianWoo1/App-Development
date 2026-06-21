package com.example.realitycheck.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Profile(
    val id: String,
    val username: String? = null,
    @SerialName("avatar_url") val avatarUrl: String? = null,
    @SerialName("total_xp") val totalXp: Int = 0,
    @SerialName("high_score_streak") val highScoreStreak: Int = 0,
    @SerialName("updated_at") val updatedAt: String? = null
)
