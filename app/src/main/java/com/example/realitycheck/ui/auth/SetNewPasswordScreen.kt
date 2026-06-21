package com.example.realitycheck.ui.auth

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.realitycheck.data.di.SupabaseModule
import com.example.realitycheck.ui.components.AuthButton
import com.example.realitycheck.ui.components.AuthTextField

@Composable
fun SetNewPasswordScreen(onPasswordResetSuccess: () -> Unit) {
    val context = LocalContext.current
    val viewModel: AuthViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return AuthViewModel(SupabaseModule.authRepository) as T
            }
        }
    )

    val passwordResetState by viewModel.passwordResetState.collectAsState()
    var newPassword by rememberSaveable { mutableStateOf("") }
    var confirmPassword by rememberSaveable { mutableStateOf("") }

    LaunchedEffect(passwordResetState) {
        when (val state = passwordResetState) {
            is PasswordResetState.Success -> {
                Toast.makeText(context, "Password reset successfully. Please sign in.", Toast.LENGTH_LONG).show()
                onPasswordResetSuccess()
            }
            is PasswordResetState.Error -> {
                Toast.makeText(context, state.message, Toast.LENGTH_LONG).show()
            }
            else -> {}
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(48.dp))
        Text(
            text = "Set New Password",
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.headlineLarge
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Enter your new password below",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(modifier = Modifier.height(32.dp))

        AuthTextField(
            value = newPassword,
            onValueChange = { newPassword = it },
            label = "New Password",
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )
        Spacer(modifier = Modifier.height(16.dp))

        AuthTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = "Confirm Password",
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )
        Spacer(modifier = Modifier.height(24.dp))

        if (passwordResetState is PasswordResetState.Loading) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        } else {
            AuthButton(
                text = "Reset Password",
                onClick = {
                    when {
                        newPassword.isEmpty() -> {
                            Toast.makeText(context, "Please enter a new password", Toast.LENGTH_SHORT).show()
                        }
                        newPassword.length < 6 -> {
                            Toast.makeText(context, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show()
                        }
                        newPassword != confirmPassword -> {
                            Toast.makeText(context, "Passwords do not match", Toast.LENGTH_SHORT).show()
                        }
                        else -> viewModel.updatePassword(newPassword)
                    }
                }
            )
        }
    }
}
