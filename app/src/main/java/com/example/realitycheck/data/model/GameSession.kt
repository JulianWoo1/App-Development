package com.example.realitycheck.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GameSession(
    val id: String = "",
    @SerialName("user_id") val userId: String = "",
    @SerialName("game_mode") val gameMode: String = "",
    val streak: Int = 0,
    @SerialName("xp_earned") val xpEarned: Int = 0,
    @SerialName("played_at") val playedAt: String = ""
)
