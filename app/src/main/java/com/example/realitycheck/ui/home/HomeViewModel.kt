package com.example.realitycheck.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.realitycheck.data.model.Profile
import com.example.realitycheck.data.repository.ProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class HomeUiState(
    val profile: Profile? = null,
    val isLoading: Boolean = true,
    val displayName: String = "Speler",
    val totalXp: Int = 0,
    val highScoreStreak: Int = 0,
    val level: Int = 1,
    val xpInCurrentLevel: Int = 0,
    val xpForNextLevel: Int = 5000
)

class HomeViewModel(private val profileRepository: ProfileRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadProfile()
    }

    private fun loadProfile() {
        viewModelScope.launch {
            profileRepository.getCurrentUserProfile().onSuccess { profile ->
                val levelInfo = calculateLevel(profile.totalXp)
                _uiState.value = _uiState.value.copy(
                    profile = profile,
                    isLoading = false,
                    displayName = profile.username ?: "Speler",
                    totalXp = profile.totalXp,
                    highScoreStreak = profile.highScoreStreak,
                    level = levelInfo.level,
                    xpInCurrentLevel = levelInfo.xpInCurrentLevel,
                    xpForNextLevel = levelInfo.xpForNextLevel
                )
            }.onFailure {
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }

    private fun calculateLevel(totalXp: Int): LevelInfo {
        var remaining = totalXp
        var level = 1
        var xpForNext = 5000
        var xpInCurrent = 0

        while (remaining >= xpForNext) {
            remaining -= xpForNext
            level++
            xpInCurrent = remaining
            xpForNext = when {
                level <= 5 -> 5000
                level <= 10 -> 7500
                level <= 20 -> 10000
                else -> 15000
            }
        }
        xpInCurrent = remaining

        return LevelInfo(level, xpInCurrent, xpForNext)
    }

    data class LevelInfo(val level: Int, val xpInCurrentLevel: Int, val xpForNextLevel: Int)
}
