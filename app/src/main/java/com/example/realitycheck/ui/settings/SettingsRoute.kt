package com.example.realitycheck.ui.settings

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.realitycheck.data.di.SupabaseModule
import com.example.realitycheck.ui.profile.ProfileViewModel

@Composable
fun SettingsRoute(
    onBackClick: () -> Unit,
    onGuideClick: () -> Unit = {},
    onPrivacyClick: () -> Unit = {},
    onAboutClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {}
) {

    val viewModel: ProfileViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return ProfileViewModel(
                    SupabaseModule.profileRepository,
                    SupabaseModule.authRepository
                ) as T
            }
        }
    )

    val state by viewModel.uiState.collectAsState()

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
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.closeUsernameDialog() }) {
                    Text("Cancel")
                }
            }
        )
    }

    SettingsScreen(
        username = state.profile?.username ?: "",
        email = state.email,

        onBackClick = onBackClick,
        onGuideClick = onGuideClick,
        onPrivacyClick = onPrivacyClick,
        onAboutClick = onAboutClick,

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