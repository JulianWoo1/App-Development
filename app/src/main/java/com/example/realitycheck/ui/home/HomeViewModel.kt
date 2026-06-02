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
import com.example.realitycheck.ui.game.RulesMode

data class HomeUiState(
    val profile: Profile? = null,
    val isLoading: Boolean = true,
    val error: String? = null,
    val displayName: String = "Speler",
    val level: Int = 1,
    val xpInCurrentLevel: Int = 0,
    val xpForNextLevel: Int = 100,
    val xpFraction: Float = 0f,
    val xpToNextLevel: Int = 100,
    val highScoreStreak: Int = 0,
    val rulesMode: RulesMode = RulesMode.CLASSIC
)

class HomeViewModel(
    private val profileRepository: ProfileRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadProfile()
    }

    fun setRulesMode(mode: RulesMode) {
        _uiState.value = _uiState.value.copy(rulesMode = mode)
    }

    fun loadProfile() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            profileRepository.getCurrentUserProfile().fold(
                onSuccess = { profile ->
                    val xp      = profile.totalXp
                    val level   = LevelSystem.levelFromXp(xp)
                    val current = LevelSystem.xpForLevel(level)
                    val next    = LevelSystem.xpForLevel(level + 1)

                    _uiState.value = _uiState.value.copy(
                        profile          = profile,
                        isLoading        = false,
                        displayName      = profile.username ?: "Speler",
                        level            = level,
                        xpInCurrentLevel = xp - current,
                        xpForNextLevel   = next - current,
                        xpFraction       = LevelSystem.progressFraction(xp),
                        xpToNextLevel    = LevelSystem.xpToNextLevel(xp),
                        highScoreStreak  = profile.highScoreStreak
                    )
                },
                onFailure = { e ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error     = e.message
                    )
                }
            )
        }
    }
}