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

class MainActivity : ComponentActivity()
{
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)

        setContent {
            RealityCheckTheme {

                val viewModel: NetworkViewModel = viewModel()
                val isOnline by viewModel.isOnline.collectAsState()

                if (!isOnline)
                {
                    NoInternetScreen(
                        onRetrySuccess = { }
                    )
                }
                else
                {
                    MainNavHost()
                }
            }
        }
    }
}