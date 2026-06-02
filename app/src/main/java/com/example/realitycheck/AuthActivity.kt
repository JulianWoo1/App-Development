package com.example.realitycheck

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.realitycheck.data.di.SupabaseModule
import com.example.realitycheck.ui.auth.AuthNavHost
import com.example.realitycheck.ui.theme.RealityCheckTheme
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
        setContent {
            RealityCheckTheme {
                AuthNavHost(isPasswordResetFlow = isPasswordReset)
            }
        }
    }
}
