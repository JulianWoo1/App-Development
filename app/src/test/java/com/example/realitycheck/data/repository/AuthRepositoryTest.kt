package com.example.realitycheck.data.repository

import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertTrue
import org.junit.Test

class AuthRepositoryTest {

    private val authRepository = FakeAuthRepository()

    @Test
    fun testResetPassword() = runBlocking {
        val result = authRepository.resetPassword("test@example.com")
        assertTrue(result.isSuccess)
    }
}
