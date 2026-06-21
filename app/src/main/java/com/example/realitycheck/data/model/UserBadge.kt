package com.example.realitycheck.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserBadge(
    @SerialName("user_id") val userId: String,
    @SerialName("badge_id") val badgeId: String,
    @SerialName("earned_at") val earnedAt: String
)
