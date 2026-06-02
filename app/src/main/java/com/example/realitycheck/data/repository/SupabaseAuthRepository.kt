package com.example.realitycheck.data.repository

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.providers.builtin.Email

class SupabaseAuthRepository(private val supabaseClient: SupabaseClient) : AuthRepository {
    override suspend fun signUp(email: String, password: String): Result<String> {
        return try {
            val user = supabaseClient.auth.signUpWith(Email) {
                this.email = email
                this.password = password
            }
            Result.success(user?.id ?: throw Exception("User ID is null"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun signIn(email: String, password: String): Result<String> {
        return try {
            supabaseClient.auth.signInWith(Email) {
                this.email = email
                this.password = password
            }
            val userId = supabaseClient.auth.currentSessionOrNull()?.user?.id 
                ?: throw Exception("User ID is null after sign in")
            Result.success(userId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun signOut(): Result<Unit> {
        return try {
            supabaseClient.auth.signOut()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun resetPassword(email: String): Result<Unit> {
        return try {
            supabaseClient.auth.resetPasswordForEmail(
                email,
                redirectUrl = "com.example.realitycheck://reset-password"
            )
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updatePassword(newPassword: String): Result<Unit> {
        return try {
            supabaseClient.auth.modifyUser {
                password = newPassword
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getCurrentUserId(): String? {
        return supabaseClient.auth.currentSessionOrNull()?.user?.id
    }
}
