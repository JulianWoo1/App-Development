package com.example.realitycheck.ui.auth

import com.example.realitycheck.data.repository.FakeAuthRepository
import org.junit.Assert.assertEquals
import org.junit.Test

class AuthViewModelTest {
    @Test
    fun `initial state is Idle`() {
        val repo = FakeAuthRepository()
        val viewModel = AuthViewModel(repo)
        assertEquals(AuthState.Idle, viewModel.authState.value)
    }
}