package com.example.realitycheck.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TodayLeaderboardRow(
    @SerialName("user_id") val userId: String,
    @SerialName("xp_today") val xpToday: Int
)