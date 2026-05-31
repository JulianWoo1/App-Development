package com.example.realitycheck.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.realitycheck.data.model.Profile
import com.example.realitycheck.data.repository.ProfileRepository
import com.example.realitycheck.ui.game.LevelSystem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class HomeUiState(
    val profile: Profile? = null,
    val isLoading: Boolean = true,
    val error: String? = null,
    // Derived level fields — computed from profile.totalXp
    val level: Int = 1,
    val xpFraction: Float = 0f,
    val xpToNextLevel: Int = 100
)

class HomeViewModel(
    private val profileRepository: ProfileRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadProfile()
    }

    fun loadProfile() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            profileRepository.getCurrentUserProfile().fold(
                onSuccess = { profile ->
                    val xp = profile.totalXp
                    _uiState.value = _uiState.value.copy(
                        profile = profile,
                        isLoading = false,
                        level = LevelSystem.levelFromXp(xp),
                        xpFraction = LevelSystem.progressFraction(xp),
                        xpToNextLevel = LevelSystem.xpToNextLevel(xp)
                    )
                },
                onFailure = { e ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = e.message
                    )
                }
            )
        }
    }
}