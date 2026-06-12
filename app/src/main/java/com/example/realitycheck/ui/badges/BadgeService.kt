package com.example.realitycheck.ui.badges

import com.example.realitycheck.data.repository.BadgeRepository

class BadgeService(
    private val badgeRepository: BadgeRepository,
    private val badgeConditions: List<BadgeCondition> = BadgeConditions.defaults
) {
    suspend fun checkAndAwardBadges(context: BadgeEvaluationContext): Result<List<BadgeUiItem>> {
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

        if (toAward.isEmpty()) return Result.success(emptyList())

        val allBadgesResult = badgeRepository.getAllBadges()
        if (allBadgesResult.isFailure) return Result.success(emptyList())

        val allBadges = allBadgesResult.getOrDefault(emptyList())
        val items = allBadges
            .filter { it.id in toAward }
            .map { badge ->
                BadgeUiItem(
                    id = badge.id,
                    iconUrl = badge.iconUrl,
                    name = badge.name,
                    description = badge.description,
                    criteriaDescription = badge.criteriaDescription,
                    isUnlocked = true
                )
            }

        return Result.success(items)
    }
}
