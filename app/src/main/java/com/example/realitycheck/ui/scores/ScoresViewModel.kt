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
    val isLoadingMore: Boolean = false,
    val hasMore: Boolean = true,
    val error: String? = null
)

class ScoresViewModel(
    private val profileRepository: ProfileRepository,
    private val gameSessionRepository: GameSessionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ScoresUiState())
    val uiState: StateFlow<ScoresUiState> = _uiState.asStateFlow()

    private val pageSize = 20

    init { loadLeaderboard() }

    fun setFilter(filter: LeaderboardFilter) {
        if (filter == _uiState.value.filter) return
        _uiState.value = _uiState.value.copy(filter = filter)
        loadLeaderboard()
    }

    fun loadLeaderboard() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null, hasMore = true)
            fetchPage(offset = 0, append = false)
        }
    }

    fun loadMore() {
        val state = _uiState.value
        if (state.isLoading || state.isLoadingMore || !state.hasMore) return

        viewModelScope.launch {
            _uiState.value = state.copy(isLoadingMore = true)
            fetchPage(offset = state.entries.size, append = true)
        }
    }

    private suspend fun fetchPage(offset: Int, append: Boolean) {
        when (_uiState.value.filter) {
            LeaderboardFilter.ALL_TIME -> {
                profileRepository.getTopProfiles(limit = pageSize, offset = offset).fold(
                    onSuccess = { profiles ->
                        val newEntries = profiles.mapIndexed { index, profile ->
                            LeaderboardEntry(
                                rank = offset + index + 1,
                                username = profile.username ?: "Anonymous",
                                totalXp = profile.totalXp,
                                level = LevelSystem.levelFromXp(profile.totalXp),
                                highScoreStreak = profile.highScoreStreak
                            )
                        }
                        applyPage(newEntries, append, profiles.size)
                    },
                    onFailure = { e -> onPageError(e, append) }
                )
            }
            LeaderboardFilter.TODAY -> {
                gameSessionRepository.getTodayLeaderboard(limit = pageSize, offset = offset).fold(
                    onSuccess = { todayTotals ->
                        val newEntries = todayTotals.mapIndexedNotNull { index, (userId, xpToday) ->
                            val profile = profileRepository.getProfile(userId).getOrNull()
                            profile?.let {
                                LeaderboardEntry(
                                    rank = offset + index + 1,
                                    username = it.username ?: "Anonymous",
                                    totalXp = xpToday,
                                    level = LevelSystem.levelFromXp(it.totalXp),
                                    highScoreStreak = it.highScoreStreak
                                )
                            }
                        }
                        applyPage(newEntries, append, todayTotals.size)
                    },
                    onFailure = { e -> onPageError(e, append) }
                )
            }
        }
    }

    private fun applyPage(newEntries: List<LeaderboardEntry>, append: Boolean, fetchedCount: Int) {
        val combined = if (append) _uiState.value.entries + newEntries else newEntries
        _uiState.value = _uiState.value.copy(
            entries = combined,
            isLoading = false,
            isLoadingMore = false,
            hasMore = fetchedCount >= pageSize
        )
    }

    private fun onPageError(e: Throwable, append: Boolean) {
        _uiState.value = _uiState.value.copy(
            isLoading = false,
            isLoadingMore = false,
            error = if (append) null else e.message
        )
    }
}