package com.example.realitycheck

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import com.example.realitycheck.ui.MainNavHost
import com.example.realitycheck.ui.nointernet.NoInternetScreen
import com.example.realitycheck.ui.theme.RealityCheckTheme
import com.example.realitycheck.utils.NetworkMonitor

class MainActivity : ComponentActivity()
{
    private lateinit var networkMonitor: NetworkMonitor

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)

        setContent {
            RealityCheckTheme {

                var isOnline by remember { mutableStateOf(true) }

                networkMonitor = NetworkMonitor(this) { connected ->
                    isOnline = connected
                }

                LaunchedEffect(Unit) {
                    networkMonitor.startMonitoring()
                }

                DisposableEffect(Unit) {
                    onDispose {
                        networkMonitor.stopMonitoring()
                    }
                }

                if (!isOnline)
                {
                    NoInternetScreen( onRetrySuccess = { })
                }
                else
                {
                    MainNavHost()
                }
            }
        }
    }
}