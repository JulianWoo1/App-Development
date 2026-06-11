package com.example.realitycheck.data.repository

import com.example.realitycheck.data.model.Badge
import com.example.realitycheck.data.model.UserBadge

class FakeBadgeRepository : BadgeRepository {
    val badges = mutableListOf(
        Badge(id = "streaker", name = "Streaker", description = "", iconUrl = ""),
        Badge(id = "nightowl", name = "Night Owl", description = "", iconUrl = ""),
        Badge(id = "speeddemon", name = "Speed Demon", description = "", iconUrl = "")
    )

    val userBadges = mutableListOf<UserBadge>()
    val earnedBadgeCalls = mutableListOf<String>()
    var getUserBadgesCallCount = 0
    var shouldFail = false

    override suspend fun getAllBadges(): Result<List<Badge>> {
        if (shouldFail) return Result.failure(Exception("Error"))
        return Result.success(badges.toList())
    }

    override suspend fun getCurrentUserBadges(): Result<List<UserBadge>> {
        getUserBadgesCallCount++
        if (shouldFail) return Result.failure(Exception("Error"))
        return Result.success(userBadges.toList())
    }

    override suspend fun earnBadge(badgeId: String): Result<Unit> {
        if (shouldFail) return Result.failure(Exception("Error"))
        earnedBadgeCalls.add(badgeId)
        userBadges.add(UserBadge(userId = "test-user", badgeId = badgeId, earnedAt = "now"))
        return Result.success(Unit)
    }
}
