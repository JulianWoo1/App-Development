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

object GameResultHolder {
    var score: String = "0"
    var accuracy: String = "0%"
    var fastestTime: String = "0s"
    var rank: String = "#0"
    var xpGained: Int = 0
}

private data class BottomNavItem(
    val route: String,
    val label: String,
    val icon: ImageVector
)

private val bottomNavItemsList = listOf(
    BottomNavItem("home",    "Home",    Icons.Default.Home),
    BottomNavItem("scores",  "Scores",  Icons.Default.Star),
    BottomNavItem("badges",  "Badges",  Icons.Default.Favorite),
    BottomNavItem("profile", "Profiel", Icons.Default.Person)
)

@Composable
fun MainNavHost() {

    val navController = rememberNavController()

    val homeViewModel: HomeViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T =
                HomeViewModel(SupabaseModule.profileRepository) as T
        }
    )

    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
    val showBottomNav = currentRoute in listOf("home", "scores", "badges", "profile")

    Scaffold(
        bottomBar = {
            if (showBottomNav) {
                NavigationBar {
                    bottomNavItemsList.forEach { item ->
                        NavigationBarItem(
                            selected = currentRoute == item.route,
                            onClick  = {
                                navController.navigate(item.route) {
                                    popUpTo("home") { saveState = true }
                                    launchSingleTop = true
                                    restoreState    = true
                                }
                            },
                            icon  = { Icon(item.icon, null) },
                            label = { Text(item.label) }
                        )
                    }
                }
            }
        }
    ) { paddingValues ->

        NavHost(
            navController    = navController,
            startDestination = "home",
            modifier         = Modifier.padding(paddingValues)
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
                HomeScreen(
                    viewModel    = homeViewModel,
                    onStartGame  = { mode ->
                        navController.navigate(
                            when (mode) {
                                GameMode.IMAGE -> "game/image"
                                GameMode.TEXT  -> "game/text"
                                GameMode.SPEED -> "game/speed"
                            }
                        )
                    }
                )
            }

            composable("game/{mode}") { backStackEntry ->
                val gameMode = when (backStackEntry.arguments?.getString("mode")) {
                    "text" -> GameMode.TEXT
                    else   -> GameMode.IMAGE
                }

                val gameViewModel: GameViewModel = viewModel(
                    factory = object : ViewModelProvider.Factory {
                        override fun <T : ViewModel> create(modelClass: Class<T>): T =
                            GameViewModel(
                                profileRepository   = SupabaseModule.profileRepository,
                                contentRepository   = SupabaseModule.contentRepository,
                                onXpUpdated         = { homeViewModel.loadProfile() }
                            ) as T
                    }
                )

                LaunchedEffect(gameMode) { gameViewModel.setMode(gameMode) }

                val state  by gameViewModel.uiState.collectAsState()
                val streak by gameViewModel.currentStreak.collectAsState()

                LaunchedEffect(state.isGameOver) {
                    if (state.isGameOver) {
                        GameResultHolder.score = streak.toString()
                        navController.navigate("gameover") {
                            popUpTo("home") { inclusive = false }
                        }
                    }
                }

                GameScreen(
                    uiState              = state,
                    streak               = streak,
                    timeRemainingSeconds = null,
                    onSelect             = { gameViewModel.onSelect(it) },
                    onBothAnswer         = { gameViewModel.onBothAnswer(it) },
                    onGameOverDismissed  = { navController.popBackStack() }
                )
            }

            composable("game/speed") {
                val speedRunViewModel: SpeedRunViewModel = viewModel(
                    factory = object : ViewModelProvider.Factory {
                        override fun <T : ViewModel> create(modelClass: Class<T>): T =
                            SpeedRunViewModel(
                                profileRepository = SupabaseModule.profileRepository,
                                contentRepository = SupabaseModule.contentRepository
                            ) as T
                    }
                )

                val state        by speedRunViewModel.uiState.collectAsState()
                val correctCount by speedRunViewModel.currentCorrectCount.collectAsState()

                LaunchedEffect(state.isGameOver) {
                    if (state.isGameOver) {
                        GameResultHolder.score = correctCount.toString()
                        navController.navigate("gameover") {
                            popUpTo("home") { inclusive = false }
                        }
                    }
                }

                GameScreen(
                    uiState              = state,
                    streak               = correctCount,
                    timeRemainingSeconds = state.timeRemainingSeconds,
                    scoreLabel           = "correct",
                    onSelect             = { speedRunViewModel.onSelect(it) },
                    onBothAnswer         = { speedRunViewModel.onBothAnswer(it) },
                    onGameOverDismissed  = {
                        speedRunViewModel.onGameOverDismissed()
                        navController.popBackStack()
                    }
                )
            }

            composable("gameover") {
                GameOverScreen(
                    score       = GameResultHolder.score,
                    accuracy    = GameResultHolder.accuracy,
                    fastestTime = GameResultHolder.fastestTime,
                    bestRank    = GameResultHolder.rank,
                    level       = 23,
                    currentXp   = 2820,
                    maxXp       = 5000,
                    gainedXp    = GameResultHolder.xpGained,
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

            composable("scores")  { ScoresScreen() }
            composable("badges")  { BadgesScreen() }
            composable("profile") { ProfileScreen() }
        }
    }
}