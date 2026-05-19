package com.example.realitycheck.ui.auth

import android.content.Intent
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
import com.example.realitycheck.MainActivity
import com.example.realitycheck.data.di.SupabaseModule
import com.example.realitycheck.ui.components.AuthButton
import com.example.realitycheck.ui.components.AuthTextField
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(onNavigateToRegister: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(48.dp))
        Text(
            text = "Login",
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
            value = password,
            onValueChange = { password = it },
            label = "Password",
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )

        Text(
            text = "Forgot Password?",
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier
                .align(Alignment.End)
                .padding(vertical = 12.dp)
                .clickable {
                    if (email.isEmpty()) {
                        Toast.makeText(context, "Please enter email first", Toast.LENGTH_SHORT).show()
                        return@clickable
                    }
                    scope.launch {
                        SupabaseModule.authRepository.resetPassword(email).fold(
                            onSuccess = { Toast.makeText(context, "Reset email sent", Toast.LENGTH_SHORT).show() },
                            onFailure = { Toast.makeText(context, "Error: ${it.message}", Toast.LENGTH_SHORT).show() }
                        )
                    }
                }
        )

        Spacer(modifier = Modifier.height(12.dp))

        AuthButton(
            text = "Login",
            onClick = {
                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(context, "Please enter email and password", Toast.LENGTH_SHORT).show()
                    return@AuthButton
                }
                scope.launch {
                    SupabaseModule.authRepository.signIn(email, password).fold(
                        onSuccess = { navigateToMain(context) },
                        onFailure = { Toast.makeText(context, "Login failed: ${it.message}", Toast.LENGTH_SHORT).show() }
                    )
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Don't have an account? Register",
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.clickable { onNavigateToRegister() }
        )

        Spacer(modifier = Modifier.weight(1f))

        HorizontalDivider(color = MaterialTheme.colorScheme.surface, thickness = 1.dp)

        TextButton(
            onClick = { navigateToMain(context) },
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text("Skip (Dev Mode)", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
        }
    }
}

private fun navigateToMain(context: android.content.Context) {
    val intent = Intent(context, MainActivity::class.java)
    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    context.startActivity(intent)
    (context as? android.app.Activity)?.finish()
}
