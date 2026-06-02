package com.example.realitycheck.ui.auth

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.serialization.Serializable

@Serializable
object LoginRoute

@Serializable
object RegisterRoute

@Serializable
object SetNewPasswordRoute

@Composable
fun AuthNavHost(isPasswordResetFlow: Boolean = false) {
    val navController = rememberNavController()
    val startDestination = if (isPasswordResetFlow) SetNewPasswordRoute else LoginRoute

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable<LoginRoute> {
            LoginScreen(
                onNavigateToRegister = {
                    navController.navigate(RegisterRoute)
                }
            )
        }
        composable<RegisterRoute> {
            RegisterScreen(
                onNavigateToLogin = {
                    navController.popBackStack()
                }
            )
        }
        composable<SetNewPasswordRoute> {
            SetNewPasswordScreen(
                onPasswordResetSuccess = {
                    navController.navigate(LoginRoute) {
                        popUpTo(SetNewPasswordRoute) { inclusive = true }
                    }
                }
            )
        }
    }
}
