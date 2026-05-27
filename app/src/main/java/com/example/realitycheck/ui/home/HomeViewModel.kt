package com.example.realitycheck.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.realitycheck.data.repository.ProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class HomeUiState(
    val displayName: String = "Speler",
    val totalXp: Int = 0,
    val level: Int = 1,
    val xpInCurrentLevel: Int = 0,
    val xpForNextLevel: Int = 100,
    val highScoreStreak: Int = 0,
    val isLoading: Boolean = true
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
            _uiState.value = _uiState.value.copy(isLoading = true)

            profileRepository.getCurrentUserProfile()
                .onSuccess { profile ->

                    val levelInfo = calculateLevel(profile.totalXp)

                    _uiState.value = HomeUiState(
                        displayName = profile.username ?: "Speler",
                        totalXp = profile.totalXp,
                        highScoreStreak = profile.highScoreStreak,
                        level = levelInfo.level,
                        xpInCurrentLevel = levelInfo.xpInCurrentLevel,
                        xpForNextLevel = levelInfo.xpForNextLevel,
                        isLoading = false
                    )
                }
                .onFailure {
                    _uiState.value = _uiState.value.copy(isLoading = false)
                }
        }
    }

    private fun calculateLevel(totalXp: Int): LevelInfo {
        var xp = totalXp
        var level = 1
        var needed = 100

        while (xp >= needed) {
            xp -= needed
            level++

            needed = when {
                level <= 5 -> 100
                level <= 10 -> 250
                level <= 20 -> 500
                else -> 1000
            }
        }

        return LevelInfo(level, xp, needed)
    }

    data class LevelInfo(
        val level: Int,
        val xpInCurrentLevel: Int,
        val xpForNextLevel: Int
    )
}