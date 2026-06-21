package com.example.realitycheck.data.repository

import com.example.realitycheck.data.model.GameSession

interface GameSessionRepository {
    suspend fun recordGameSession(mode: String, streak: Int, xpEarned: Int): Result<Unit>
    suspend fun getRecentSessions(limit: Int = 10): Result<List<GameSession>>
    suspend fun getSessionsThisWeek(): Result<List<GameSession>>
    suspend fun getGamesPlayedCount(): Result<Int>
    suspend fun getTodayLeaderboard(limit: Int = 20, offset: Int = 0): Result<List<Pair<String, Int>>>
}