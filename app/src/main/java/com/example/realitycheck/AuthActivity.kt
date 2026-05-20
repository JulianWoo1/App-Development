package com.example.realitycheck

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.realitycheck.ui.auth.AuthNavHost
import com.example.realitycheck.ui.theme.RealityCheckTheme

class AuthActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RealityCheckTheme {
                AuthNavHost()
            }
        }
    }
}
