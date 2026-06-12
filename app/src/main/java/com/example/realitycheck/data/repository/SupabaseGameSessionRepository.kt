package com.example.realitycheck.data.repository

import com.example.realitycheck.data.model.GameSession
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Order
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID

class SupabaseGameSessionRepository(
    private val supabaseClient: SupabaseClient,
    private val authRepository: AuthRepository
) : GameSessionRepository {

    private val table = supabaseClient.postgrest["game_sessions"]

    override suspend fun recordGameSession(mode: String, streak: Int, xpEarned: Int): Result<Unit> {
        val userId = authRepository.getCurrentUserId()
            ?: return Result.failure(Exception("No user logged in"))

        return try {
            val session = GameSession(
                id = UUID.randomUUID().toString(),
                userId = userId,
                gameMode = mode,
                streak = streak,
                xpEarned = xpEarned,
                playedAt = java.time.Instant.now().toString()
            )
            table.insert(session)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getRecentSessions(limit: Int): Result<List<GameSession>> {
        val userId = authRepository.getCurrentUserId()
            ?: return Result.failure(Exception("No user logged in"))

        return try {
            val sessions = table.select {
                filter { eq("user_id", userId) }
                order("played_at", Order.DESCENDING)
                limit(limit.toLong())
            }.decodeList<GameSession>()
            Result.success(sessions)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getSessionsThisWeek(): Result<List<GameSession>> {
        val userId = authRepository.getCurrentUserId()
            ?: return Result.failure(Exception("No user logged in"))

        val now = ZonedDateTime.now(ZoneId.systemDefault())
        val startOfWeek = now.with(DayOfWeek.MONDAY).toLocalDate().atStartOfDay(ZoneId.systemDefault())
        val startOfWeekIso = startOfWeek.format(DateTimeFormatter.ISO_INSTANT)

        return try {
            val sessions = table.select {
                filter { eq("user_id", userId) }
                filter { gte("played_at", startOfWeekIso) }
                order("played_at", Order.DESCENDING)
            }.decodeList<GameSession>()
            Result.success(sessions)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getGamesPlayedCount(): Result<Int> {
        val userId = authRepository.getCurrentUserId()
            ?: return Result.failure(Exception("No user logged in"))

        return try {
            val sessions = table.select {
                filter { eq("user_id", userId) }
            }.decodeList<GameSession>()
            Result.success(sessions.size)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
