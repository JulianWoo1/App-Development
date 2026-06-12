package com.example.realitycheck.data.repository

import com.example.realitycheck.data.model.Profile
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Order
import io.github.jan.supabase.postgrest.rpc

class SupabaseProfileRepository(
    private val supabaseClient: SupabaseClient,
    private val authRepository: AuthRepository
) : ProfileRepository {

    private val table = supabaseClient.postgrest["profiles"]

    override suspend fun getCurrentUserProfile(): Result<Profile> {
        val userId = authRepository.getCurrentUserId()
            ?: return Result.failure(Exception("No user logged in"))

        return getProfile(userId)
    }

    override suspend fun getProfile(userId: String): Result<Profile> {
        return try {
            val profile = table.select {
                filter { eq("id", userId) }
            }.decodeSingle<Profile>()

            Result.success(profile)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getTopProfiles(limit: Int, offset: Int): Result<List<Profile>> {
        return try {
            val profiles = table.select {
                order("total_xp", Order.DESCENDING)
                range(offset.toLong(), (offset + limit - 1).toLong())
            }.decodeList<Profile>()

            Result.success(profiles)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateUsername(newUsername: String): Result<Profile> {
        val userId = authRepository.getCurrentUserId()
            ?: return Result.failure(Exception("No user logged in"))

        return try {
            val updated = table.update({
                set("username", newUsername)
            }) {
                filter { eq("id", userId) }
            }.decodeSingle<Profile>()

            Result.success(updated)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun addXp(amount: Int): Result<Unit> {
        return try {
            supabaseClient.postgrest.rpc(
                "add_xp",
                mapOf("amount" to amount)
            )
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateHighScore(newStreak: Int): Result<Profile> {
        val current = getCurrentUserProfile()
        if (current.isFailure) return current.map { it }

        val profile = current.getOrNull()!!

        if (newStreak <= profile.highScoreStreak) {
            return Result.success(profile)
        }

        return try {
            val updated = table.update({
                set("high_score_streak", newStreak)
            }) {
                filter { eq("id", profile.id) }
            }.decodeSingle<Profile>()

            Result.success(updated)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getUserRankFromLeaderboard(): Result<Int> {
        return try {
            val userId = authRepository.getCurrentUserId()
                ?: return Result.failure(Exception("No user logged in"))

            val leaderboard = getTopProfiles(limit = 1000).getOrThrow()

            val index = leaderboard.indexOfFirst { it.id == userId }

            Result.success(if (index >= 0) index + 1 else 0)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


}