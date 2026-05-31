package com.example.realitycheck.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.realitycheck.data.di.SupabaseModule
import com.example.realitycheck.ui.badges.BadgesScreen
import com.example.realitycheck.ui.game.GameMode
import com.example.realitycheck.ui.game.GameScreen
import com.example.realitycheck.ui.game.GameViewModel
import com.example.realitycheck.ui.game.SpeedRunViewModel
import com.example.realitycheck.ui.gameover.GameOverScreen
import com.example.realitycheck.ui.home.HomeScreen
import com.example.realitycheck.ui.home.HomeViewModel
import com.example.realitycheck.ui.onboarding.OnboardingScreen
import com.example.realitycheck.ui.profile.ProfileScreen
import com.example.realitycheck.ui.scores.ScoresScreen

private val bottomNavItems = listOf("home", "scores", "badges", "profile")

private data class BottomNavItem(
    val route: String,
    val label: String,
    val icon: ImageVector
)

private val bottomNavItemsList = listOf(
    BottomNavItem("home", "Home", Icons.Default.Home),
    BottomNavItem("scores", "Scores", Icons.Default.Star),
    BottomNavItem("badges", "Badges", Icons.Default.Favorite),
    BottomNavItem("profile", "Profiel", Icons.Default.Person)
)

@Composable
fun MainNavHost() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val showBottomNav = currentDestination?.route in bottomNavItems

    Scaffold(
        bottomBar = {
            if (showBottomNav) {
                NavigationBar {
                    bottomNavItemsList.forEach { item ->
                        val selected = currentDestination?.route == item.route
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo("home") { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(item.icon, contentDescription = item.label) },
                            label = { Text(item.label) }
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(paddingValues)
        ) {
            composable("onboarding") {
                OnboardingScreen(
                    onComplete = {
                        navController.navigate("home") {
                            popUpTo("onboarding") { inclusive = true }
                        }
                    }
                )
            }
            composable("home") {
                val homeViewModel: HomeViewModel = viewModel(
                    factory = object : ViewModelProvider.Factory {
                        override fun <T : ViewModel> create(modelClass: Class<T>): T {
                            return HomeViewModel(SupabaseModule.profileRepository) as T
                        }
                    }
                )
                HomeScreen(
                    homeViewModel,
                    onStartGame = { mode ->
                        val route = when (mode) {
                            GameMode.IMAGE -> "game/image"
                            GameMode.TEXT -> "game/text"
                            GameMode.SPEED -> "game/speed"
                        }
                        navController.navigate(route)
                    }
                )
            }
            composable("scores") {
                ScoresScreen()
            }
            composable("badges") {
                BadgesScreen()
            }
            composable("profile") {
                ProfileScreen()
            }
            composable("game/{mode}") { backStackEntry ->
                val mode = backStackEntry.arguments?.getString("mode")

                val gameMode = when (mode) {
                    "text" -> GameMode.TEXT
                    else -> GameMode.IMAGE
                }

                val gameViewModel: GameViewModel = viewModel(
                    factory = object : ViewModelProvider.Factory {
                        override fun <T : ViewModel> create(modelClass: Class<T>): T {
                            return GameViewModel(SupabaseModule.profileRepository) as T
                        }
                    }
                )

                LaunchedEffect(gameMode) {
                    gameViewModel.setMode(gameMode)
                }

                val state by gameViewModel.uiState.collectAsState()

                LaunchedEffect(state.isGameOver) {
                    if (state.isGameOver) {
                        navController.navigate("gameover") {
                            popUpTo("game/image") { inclusive = true }
                        }
                    }
                }

                val streak by gameViewModel.currentStreak.collectAsState()

                GameScreen(
                    uiState = state,
                    streak = streak,
                    timeRemainingSeconds = null,
                    onSelect = { gameViewModel.onSelect(it) },
                    onBothAnswer = { gameViewModel.onBothAnswer(it) },
                    onGameOverDismissed = { navController.popBackStack() }
                )
            }

            composable("game/speed") {
                val speedRunViewModel: SpeedRunViewModel = viewModel(
                    factory = object : ViewModelProvider.Factory {
                        override fun <T : ViewModel> create(modelClass: Class<T>): T {
                            return SpeedRunViewModel(SupabaseModule.profileRepository) as T
                        }
                    }
                )

                val state by speedRunViewModel.uiState.collectAsState()

                LaunchedEffect(state.isGameOver) {
                    if (state.isGameOver) {
                        navController.navigate("gameover") {
                            popUpTo("game/image") { inclusive = true }
                        }
                    }
                }

                val correctCount by speedRunViewModel.currentCorrectCount.collectAsState()

                GameScreen(
                    uiState = state,
                    streak = correctCount,
                    timeRemainingSeconds = state.timeRemainingSeconds,
                    scoreLabel = "correct",
                    onSelect = { speedRunViewModel.onSelect(it) },
                    onBothAnswer = { speedRunViewModel.onBothAnswer(it) },
                    onGameOverDismissed = {
                        speedRunViewModel.onGameOverDismissed()
                        navController.popBackStack()
                    }
                )
            }
            composable("gameover") {
                GameOverScreen(
                    score = "0", // temporary, we’ll fix this in step 4
                    accuracy = "74%",
                    fastestTime = "1.4s",
                    bestRank = "#18",
                    level = 23,
                    currentXp = 2820,
                    maxXp = 5000,
                    gainedXp = 230,
                    onPlayAgain = {
                        navController.navigate("home") {
                            popUpTo("gameover") { inclusive = true }
                        }
                    },
                    onHome = {
                        navController.navigate("home") {
                            popUpTo("gameover") { inclusive = true }
                        }
                    }
                )
            }
        }
    }
}
