package com.example.realitycheck.ui.settings

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.graphics.Color
import com.example.realitycheck.data.di.SupabaseModule
import com.example.realitycheck.ui.profile.ProfileViewModel

@Composable
fun SettingsRoute(
    onBackClick: () -> Unit,
    onGuideClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {}
) {

    val viewModel: ProfileViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return ProfileViewModel(
                    SupabaseModule.profileRepository,
                    SupabaseModule.authRepository,
                    SupabaseModule.badgeRepository,
                    SupabaseModule.gameSessionRepository
                ) as T
            }
        }
    )

    val state by viewModel.uiState.collectAsState()

    var showPrivacyDialog by remember { mutableStateOf(false) }
    var showAboutDialog by remember { mutableStateOf(false) }

    if (state.isEditingUsername) {
        AlertDialog(
            onDismissRequest = { viewModel.closeUsernameDialog() },
            title = { Text("Edit username") },
            text = {
                OutlinedTextField(
                    value = state.usernameInput,
                    onValueChange = { viewModel.onUsernameChange(it) },
                    singleLine = true
                )
            },
            confirmButton = {
                TextButton(onClick = { viewModel.saveUsername() }) {
                    Text("Save", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.closeUsernameDialog() }) {
                    Text("Cancel", color = Color.White)
                }
            }
        )
    }

    if (showPrivacyDialog) {
        AlertDialog(
            onDismissRequest = { showPrivacyDialog = false },
            title = { Text("Privacy") },
            text = {
                Text(
                    """
                    RealityCheck Privacy
                    
                    • We store your email and username
                    • We store XP, level, streak and progress
                    • Used only for gameplay features
                    • No data is sold or shared
                """.trimIndent()
                )
            },
            confirmButton = {
                TextButton(onClick = { showPrivacyDialog = false }) {
                    Text("Close", color = Color.White)
                }
            }
        )
    }

    if (showAboutDialog) {
        AlertDialog(
            onDismissRequest = { showAboutDialog = false },
            title = { Text("App Information") },
            text = {
                Text(
                    """
                    RealityCheck

                    Version 1.0.0
                    
                    A game where you guess whether content is real or AI-generated.  
                    Earn XP, climb levels, and compete on leaderboards.
                    
                    Credits:
                    • RealityCheck Team
                    • UI & Game Logic: Android Compose
                    • Backend: Supabase
                    
                    Built with:
                    • Kotlin / Jetpack Compose
                    • Supabase backend integration
                """.trimIndent()
                )
            },
            confirmButton = {
                TextButton(onClick = { showAboutDialog = false }) {
                    Text("Close", color = Color.White)
                }
            }
        )
    }

    SettingsScreen(
        username = state.profile?.username ?: "",
        email = state.email,

        onBackClick = onBackClick,

        onGuideClick = onGuideClick,

        onPrivacyClick = {
            showPrivacyDialog = true
        },

        onAboutClick = {
            showAboutDialog = true
        },

        onLogoutClick = {
            viewModel.logout {
                onLogoutClick()
            }
        },

        onUsernameClick = {
            viewModel.openUsernameDialog()
        }
    )
}