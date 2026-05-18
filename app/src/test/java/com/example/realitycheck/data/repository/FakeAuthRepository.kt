package com.example.realitycheck.data.repository

class FakeAuthRepository : AuthRepository {
    var storedUserId: String? = null
    var shouldFail = false

    override suspend fun signUp(email: String, password: String): Result<String> {
        if (shouldFail) return Result.failure(Exception("Signup failed"))
        val newId = "fake-user-id"
        storedUserId = newId
        return Result.success(newId)
    }

    override suspend fun signIn(email: String, password: String): Result<String> {
        if (shouldFail) return Result.failure(Exception("Signin failed"))
        val id = "fake-user-id"
        storedUserId = id
        return Result.success(id)
    }

    override suspend fun signOut(): Result<Unit> {
        currentUserId = null
        return Result.success(Unit)
    }

    override suspend fun resetPassword(email: String): Result<Unit> {
        return Result.success(Unit)
    }

    override fun getCurrentUserId(): String? = currentUserId
}
