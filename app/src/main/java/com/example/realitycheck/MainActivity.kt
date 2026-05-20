package com.example.realitycheck

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.realitycheck.ui.MainNavHost
import com.example.realitycheck.ui.theme.RealityCheckTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RealityCheckTheme {
                MainNavHost()
            }
        }
    }

    companion object {
        const val EXTRA_FINAL_STREAK = "extra_final_streak"
    }
}
