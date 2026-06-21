package com.example.realitycheck.data.repository

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.realitycheck.data.model.GameSession
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Order
import java.text.SimpleDateFormat
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import java.util.UUID
import io.github.jan.supabase.postgrest.rpc
import com.example.realitycheck.data.model.TodayLeaderboardRow
import io.github.jan.supabase.postgrest.rpc

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
                playedAt = nowIso()
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

    @RequiresApi(Build.VERSION_CODES.O)
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

    override suspend fun getTodayLeaderboard(limit: Int, offset: Int): Result<List<Pair<String, Int>>> {
        return try {
            val rows = supabaseClient.postgrest.rpc(
                "get_today_leaderboard",
                mapOf("limit_count" to limit, "offset_count" to offset)
            ).decodeList<TodayLeaderboardRow>()
            Result.success(rows.map { it.userId to it.xpToday })
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

    /** ISO-8601 UTC timestamp, e.g. 2026-06-12T14:23:01.123Z — avoids java.time.Instant (API 26+). */
    private fun nowIso(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
        sdf.timeZone = TimeZone.getTimeZone("UTC")
        return sdf.format(Date())
    }
}