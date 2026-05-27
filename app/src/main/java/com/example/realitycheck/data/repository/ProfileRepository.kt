package com.example.realitycheck.data.repository

import com.example.realitycheck.data.model.Profile

interface ProfileRepository {
    suspend fun getCurrentUserProfile(): Result<Profile>
    suspend fun getProfile(userId: String): Result<Profile>
    suspend fun getTopProfiles(limit: Int = 10): Result<List<Profile>>
    suspend fun updateUsername(newUsername: String): Result<Profile>
    suspend fun updateHighScore(newStreak: Int): Result<Profile>
    suspend fun addXp(amount: Int): Result<Unit>
}
