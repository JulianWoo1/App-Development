package com.example.realitycheck.data.repository

import com.example.realitycheck.data.model.Badge
import com.example.realitycheck.data.model.UserBadge
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Order

class SupabaseBadgeRepository(
    private val supabaseClient: SupabaseClient,
    private val authRepository: AuthRepository
) : BadgeRepository {

    private val badgesTable = supabaseClient.postgrest["badges"]
    private val userBadgesTable = supabaseClient.postgrest["user_badges"]

    override suspend fun getAllBadges(): Result<List<Badge>> {
        return try {
            val badges = badgesTable.select {
                order("sort_order", Order.ASCENDING)
            }.decodeList<Badge>()
            Result.success(badges)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getCurrentUserBadges(): Result<List<UserBadge>> {
        val userId = authRepository.getCurrentUserId()
            ?: return Result.failure(Exception("No user logged in"))

        return try {
            val badges = userBadgesTable.select {
                filter { eq("user_id", userId) }
            }.decodeList<UserBadge>()
            Result.success(badges)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun earnBadge(badgeId: String): Result<Unit> {
        val userId = authRepository.getCurrentUserId()
            ?: return Result.failure(Exception("No user logged in"))

        return try {
            val userBadge = UserBadge(
                userId = userId,
                badgeId = badgeId,
                earnedAt = java.time.Instant.now().toString()
            )
            userBadgesTable.insert(userBadge)
            Result.success(Unit)
        } catch (e: Exception) {
            if (e.message?.contains("duplicate key", ignoreCase = true) == true) {
                Result.success(Unit)
            } else {
                Result.failure(e)
            }
        }
    }
}
