package com.example.realitycheck.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.realitycheck.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    object Success : AuthState()
    data class Error(val message: String) : AuthState()
}

sealed class PasswordResetState {
    object Idle : PasswordResetState()
    object Loading : PasswordResetState()
    object Success : PasswordResetState()
    data class Error(val message: String) : PasswordResetState()
}

class AuthViewModel(private val repository: AuthRepository) : ViewModel() {
    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private val _passwordResetState = MutableStateFlow<PasswordResetState>(PasswordResetState.Idle)
    val passwordResetState: StateFlow<PasswordResetState> = _passwordResetState.asStateFlow()

    fun signIn(email: String, password: String) {
        _authState.value = AuthState.Loading
        viewModelScope.launch {
            repository.signIn(email, password).fold(
                onSuccess = { _authState.value = AuthState.Success },
                onFailure = { _authState.value = AuthState.Error(it.message ?: "Login failed") }
            )
        }
    }

    fun resetPassword(email: String) {
        _passwordResetState.value = PasswordResetState.Loading
        viewModelScope.launch {
            repository.resetPassword(email).fold(
                onSuccess = { _passwordResetState.value = PasswordResetState.Success },
                onFailure = { _passwordResetState.value = PasswordResetState.Error(it.message ?: "Failed to send reset email") }
            )
        }
    }

    fun updatePassword(newPassword: String) {
        _passwordResetState.value = PasswordResetState.Loading
        viewModelScope.launch {
            repository.updatePassword(newPassword).fold(
                onSuccess = { _passwordResetState.value = PasswordResetState.Success },
                onFailure = { _passwordResetState.value = PasswordResetState.Error(it.message ?: "Password update failed") }
            )
        }
    }
}