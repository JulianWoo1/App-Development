package com.example.realitycheck.ui.auth

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import com.example.realitycheck.data.di.SupabaseModule
import com.example.realitycheck.ui.components.AuthButton
import com.example.realitycheck.ui.components.AuthTextField
import kotlinx.coroutines.launch

@Composable
fun RegisterScreen(onNavigateToLogin: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var email by rememberSaveable { mutableStateOf("") }
    var username by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var confirmPassword by rememberSaveable { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(48.dp))
        Text(
            text = "Register",
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.headlineLarge
        )
        Spacer(modifier = Modifier.height(32.dp))

        AuthTextField(
            value = email,
            onValueChange = { email = it },
            label = "Email",
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )
        Spacer(modifier = Modifier.height(16.dp))

        AuthTextField(
            value = username,
            onValueChange = { username = it },
            label = "Username",
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
        )
        Spacer(modifier = Modifier.height(16.dp))

        AuthTextField(
            value = password,
            onValueChange = { password = it },
            label = "Password",
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

        AuthButton(
            text = "Register",
            onClick = {
                if (email.isEmpty() || username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                    Toast.makeText(context, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                    return@AuthButton
                }
                if (username.length < 3) {
                    Toast.makeText(context, "Username must be at least 3 characters", Toast.LENGTH_SHORT).show()
                    return@AuthButton
                }
                if (password != confirmPassword) {
                    Toast.makeText(context, "Passwords do not match", Toast.LENGTH_SHORT).show()
                    return@AuthButton
                }
                if (password.length < 6) {
                    Toast.makeText(context, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show()
                    return@AuthButton
                }

                scope.launch {
                    SupabaseModule.authRepository.signUp(email, password, username).fold(
                        onSuccess = {
                            Toast.makeText(context, "Registration successful! Please log in.", Toast.LENGTH_SHORT).show()
                            onNavigateToLogin()
                        },
                        onFailure = { Toast.makeText(context, "Registration failed: ${it.message}", Toast.LENGTH_SHORT).show() }
                    )
                }
            }
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Already have an account? Login",
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.clickable { onNavigateToLogin() }
        )
    }
}
