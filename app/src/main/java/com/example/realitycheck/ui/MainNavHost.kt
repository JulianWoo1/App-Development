package com.example.realitycheck.ui

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.realitycheck.data.di.SupabaseModule
import com.example.realitycheck.ui.game.GameScreen
import com.example.realitycheck.ui.game.GameViewModel
import com.example.realitycheck.ui.home.HomeScreen
import com.example.realitycheck.ui.home.HomeViewModel

@Composable
fun MainNavHost() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            val homeViewModel: HomeViewModel = viewModel(
                factory = object : ViewModelProvider.Factory {
                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                        return HomeViewModel(SupabaseModule.profileRepository) as T
                    }
                }
            )
            HomeScreen(homeViewModel, onStartGame = { navController.navigate("game") })
        }
        composable("game") {
            val gameViewModel: GameViewModel = viewModel(
                factory = object : ViewModelProvider.Factory {
                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                        return GameViewModel(SupabaseModule.profileRepository) as T
                    }
                }
            )
            GameScreen(gameViewModel, onGameOver = { navController.popBackStack() })
        }
    }
}
