package com.example.realitycheck.ui.badges

import com.example.realitycheck.data.repository.BadgeRepository

class BadgeService(
    private val badgeRepository: BadgeRepository,
    private val badgeConditions: List<BadgeCondition> = BadgeConditions.defaults
) {
    suspend fun checkAndAwardBadges(context: BadgeEvaluationContext): Result<List<String>> {
        val earnedResult = badgeRepository.getCurrentUserBadges()
        if (earnedResult.isFailure) return Result.success(emptyList())

        val earnedIds = earnedResult.getOrDefault(emptyList())
            .map { it.badgeId }
            .toSet()

        val toAward = badgeConditions
            .filter { it.badgeId !in earnedIds }
            .filter { it.evaluate(context) }
            .map { it.badgeId }

        for (badgeId in toAward) {
            badgeRepository.earnBadge(badgeId)
        }

        return Result.success(toAward)
    }
}
