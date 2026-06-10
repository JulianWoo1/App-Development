package com.example.realitycheck.data.repository

interface AuthRepository {
    suspend fun signUp(email: String, password: String, username: String): Result<String>
    suspend fun signIn(email: String, password: String): Result<String>
    suspend fun signOut(): Result<Unit>
    suspend fun resetPassword(email: String): Result<Unit>
    suspend fun updatePassword(newPassword: String): Result<Unit>
    fun getCurrentUserId(): String?
    suspend fun getCurrentUserEmail(): String
}
