package com.example.realitycheck

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.realitycheck.data.di.SupabaseModule
import com.example.realitycheck.ui.auth.AuthNavHost
import com.example.realitycheck.ui.theme.RealityCheckTheme
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.handleDeeplinks

class AuthActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val isPasswordReset = intent?.data?.host == "reset-password"
        if (isPasswordReset) {
            try {
                SupabaseModule.client.handleDeeplinks(intent)
            } catch (e: Exception) {
                Toast.makeText(this, "Failed to process password reset link", Toast.LENGTH_LONG).show()
            }
        }

        if (!isPasswordReset && SupabaseModule.client.auth.currentSessionOrNull() != null) {
            val intent = Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            startActivity(intent)
            finish()
            return
        }

        setContent {
            RealityCheckTheme {
                AuthNavHost(isPasswordResetFlow = isPasswordReset)
            }
        }
    }
}
