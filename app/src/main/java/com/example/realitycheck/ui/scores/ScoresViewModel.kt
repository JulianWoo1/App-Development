package com.example.realitycheck.ui.scores

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.realitycheck.data.repository.GameSessionRepository
import com.example.realitycheck.data.repository.ProfileRepository
import com.example.realitycheck.ui.game.LevelSystem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class LeaderboardFilter { ALL_TIME, TODAY }

data class LeaderboardEntry(
    val rank: Int,
    val username: String,
    val totalXp: Int,
    val level: Int,
    val highScoreStreak: Int
)

data class ScoresUiState(
    val entries: List<LeaderboardEntry> = emptyList(),
    val filter: LeaderboardFilter = LeaderboardFilter.ALL_TIME,
    val isLoading: Boolean = true,
    val error: String? = null
)

class ScoresViewModel(
    private val profileRepository: ProfileRepository,
    private val gameSessionRepository: GameSessionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ScoresUiState())
    val uiState: StateFlow<ScoresUiState> = _uiState.asStateFlow()

    init { loadLeaderboard() }

    fun setFilter(filter: LeaderboardFilter) {
        if (filter == _uiState.value.filter) return
        _uiState.value = _uiState.value.copy(filter = filter)
        loadLeaderboard()
    }

    fun loadLeaderboard() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            when (_uiState.value.filter) {
                LeaderboardFilter.ALL_TIME -> {
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
                LeaderboardFilter.TODAY -> {
                    gameSessionRepository.getTodayLeaderboard(20).fold(
                        onSuccess = { todayTotals ->
                            val entries = todayTotals.mapIndexedNotNull { index, (userId, xpToday) ->
                                val profile = profileRepository.getProfile(userId).getOrNull()
                                profile?.let {
                                    LeaderboardEntry(
                                        rank = index + 1,
                                        username = it.username ?: "Anonymous",
                                        totalXp = xpToday,
                                        level = LevelSystem.levelFromXp(it.totalXp),
                                        highScoreStreak = it.highScoreStreak
                                    )
                                }
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
    }
}