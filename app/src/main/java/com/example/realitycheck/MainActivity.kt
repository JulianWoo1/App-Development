package com.example.realitycheck

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.*
import com.example.realitycheck.ui.MainNavHost
import com.example.realitycheck.ui.nointernet.NoInternetScreen
import com.example.realitycheck.ui.theme.RealityCheckTheme
import com.example.realitycheck.viewmodel.NetworkViewModel
import androidx.compose.runtime.remember
import androidx.compose.runtime.CompositionLocalProvider
import com.example.realitycheck.audio.LocalSoundManager
import com.example.realitycheck.audio.SoundManager


class MainActivity : ComponentActivity()
{
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            RealityCheckTheme {

                val soundManager = remember {
                    SoundManager(this)
                }

                CompositionLocalProvider(
                    LocalSoundManager provides soundManager
                ) {

                    val viewModel: NetworkViewModel = viewModel()
                    val isOnline by viewModel.isOnline.collectAsState()

                    if (!isOnline) {
                        NoInternetScreen(
                            onRetrySuccess = { }
                        )
                    } else {
                        MainNavHost()
                    }
                }
            }
        }
    }

    companion object {
        const val EXTRA_FINAL_STREAK = "extra_final_streak"
    }
}
