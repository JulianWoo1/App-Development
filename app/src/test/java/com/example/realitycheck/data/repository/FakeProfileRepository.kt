package com.example.realitycheck.data.repository

import com.example.realitycheck.data.model.Profile

class FakeProfileRepository(private val authRepository: FakeAuthRepository = FakeAuthRepository()) : ProfileRepository {
    val profiles = mutableMapOf<String, Profile>()
    var shouldFail = false

    override suspend fun getCurrentUserProfile(): Result<Profile> {
        if (shouldFail) return Result.failure(Exception("Network error"))
        val userId = authRepository.getCurrentUserId() ?: return Result.failure(Exception("No user logged in"))
        val profile = profiles[userId] ?: Profile(id = userId)
        return Result.success(profile)
    }

    override suspend fun getProfile(userId: String): Result<Profile> {
        if (shouldFail) return Result.failure(Exception("Network error"))
        val profile = profiles[userId] ?: return Result.failure(Exception("Profile not found"))
        return Result.success(profile)
    }

    override suspend fun getTopProfiles(limit: Int): Result<List<Profile>> {
        if (shouldFail) return Result.failure(Exception("Network error"))
        return Result.success(profiles.values.sortedByDescending { it.totalXp }.take(limit))
    }

    override suspend fun updateUsername(newUsername: String): Result<Profile> {
        val userId = authRepository.getCurrentUserId() ?: return Result.failure(Exception("No user logged in"))
        val profile = profiles[userId] ?: Profile(id = userId)
        val updated = profile.copy(username = newUsername)
        profiles[userId] = updated
        return Result.success(updated)
    }

    override suspend fun addXp(amount: Int): Result<Unit> {
        val userId = authRepository.getCurrentUserId()
            ?: return Result.failure(Exception("No user logged in"))

        val profile = profiles[userId] ?: Profile(id = userId)

        val updated = profile.copy(
            totalXp = profile.totalXp + amount
        )

        profiles[userId] = updated

        return Result.success(Unit)
    }

    override suspend fun updateHighScore(newStreak: Int): Result<Profile> {
        val currentProfileResult = getCurrentUserProfile()
        if (currentProfileResult.isFailure) return currentProfileResult
        val currentProfile = currentProfileResult.getOrNull()!!
        
        if (newStreak <= currentProfile.highScoreStreak) {
            return Result.success(currentProfile)
        }
        
        val updated = currentProfile.copy(highScoreStreak = newStreak)
        profiles[currentProfile.id] = updated
        return Result.success(updated)
    }
}
