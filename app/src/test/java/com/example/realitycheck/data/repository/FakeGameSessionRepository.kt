package com.example.realitycheck.data.repository

import com.example.realitycheck.data.model.GameSession

class FakeGameSessionRepository : GameSessionRepository {

    val sessions = mutableListOf<GameSession>()
    var shouldFail = false

    override suspend fun recordGameSession(mode: String, streak: Int, xpEarned: Int): Result<Unit> {
        if (shouldFail) return Result.failure(Exception("Fake failure"))
        sessions.add(
            GameSession(
                id = "session-${sessions.size}",
                userId = "test-user",
                gameMode = mode,
                streak = streak,
                xpEarned = xpEarned,
                playedAt = java.time.Instant.now().toString()
            )
        )
        return Result.success(Unit)
    }

    override suspend fun getRecentSessions(limit: Int): Result<List<GameSession>> {
        if (shouldFail) return Result.failure(Exception("Fake failure"))
        return Result.success(sessions.take(limit))
    }

    override suspend fun getSessionsThisWeek(): Result<List<GameSession>> {
        if (shouldFail) return Result.failure(Exception("Fake failure"))
        return Result.success(sessions.toList())
    }

    override suspend fun getGamesPlayedCount(): Result<Int> {
        if (shouldFail) return Result.failure(Exception("Fake failure"))
        return Result.success(sessions.size)
    }
}
