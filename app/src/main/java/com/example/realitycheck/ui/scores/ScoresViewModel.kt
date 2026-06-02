package com.example.realitycheck.ui.scores

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.realitycheck.data.repository.ProfileRepository
import com.example.realitycheck.ui.game.LevelSystem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class LeaderboardEntry(
    val rank: Int,
    val username: String,
    val totalXp: Int,
    val level: Int,
    val highScoreStreak: Int
)

data class ScoresUiState(
    val entries: List<LeaderboardEntry> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)

class ScoresViewModel(
    private val profileRepository: ProfileRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ScoresUiState())
    val uiState: StateFlow<ScoresUiState> = _uiState.asStateFlow()

    init { loadLeaderboard() }

    fun loadLeaderboard() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            profileRepository.getTopProfiles(20).fold(
                onSuccess = { profiles ->
                    val entries = profiles.mapIndexed { index, profile ->
                        LeaderboardEntry(
                            rank = index + 1,
                            username = profile.username ?: "Anonymous",
                            totalXp = profile.totalXp,
                            level = LevelSystem.levelFromXp(profile.totalXp),
                            highScoreStreak = profile.highScoreStreak
                        )
                    }
                    _uiState.value = _uiState.value.copy(entries = entries, isLoading = false)
                },
                onFailure = { e ->
                    _uiState.value = _uiState.value.copy(isLoading = false, error = e.message)
                }
            )
        }
    }
}
