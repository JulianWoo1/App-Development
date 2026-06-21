package com.example.realitycheck.data.repository

import com.example.realitycheck.data.model.Badge
import com.example.realitycheck.data.model.UserBadge

interface BadgeRepository {
    suspend fun getAllBadges(): Result<List<Badge>>
    suspend fun getCurrentUserBadges(): Result<List<UserBadge>>
    suspend fun earnBadge(badgeId: String): Result<Unit>
}
