package com.example.realitycheck.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.realitycheck.data.model.GameSession
import com.example.realitycheck.data.model.Profile
import com.example.realitycheck.data.repository.AuthRepository
import com.example.realitycheck.data.repository.BadgeRepository
import com.example.realitycheck.data.repository.GameSessionRepository
import com.example.realitycheck.data.repository.ProfileRepository
import com.example.realitycheck.ui.game.LevelSystem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ProfileUiState(
    val profile: Profile? = null,
    val isLoading: Boolean = true,
    val error: String? = null,

    val email: String = "",

    val gamesPlayed: Int = 0,
    val avgStreak: Int = 0,
    val badges: Int = 0,

    val totalXp: Int = 0,
    val level: Int = 1,
    val nextLevel: Int = 2,
    val xpNeeded: Int = 0,

    val rank: Int = 0,

    val recentSessions: List<GameSession> = emptyList(),
    val weeklySessions: List<GameSession> = emptyList(),

    val isEditingUsername: Boolean = false,
    val usernameInput: String = ""
)

class ProfileViewModel(
    private val profileRepository: ProfileRepository,
    private val authRepository: AuthRepository,
    private val badgeRepository: BadgeRepository,
    private val gameSessionRepository: GameSessionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadProfile()
    }

    fun loadProfile() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            profileRepository.getCurrentUserProfile().fold(
                onSuccess = { profile ->

                    val email = authRepository.getCurrentUserEmail()

                    val xp = profile.totalXp
                    val level = LevelSystem.levelFromXp(xp)

                    val rank = profileRepository
                        .getUserRankFromLeaderboard()
                        .getOrDefault(0)

                    val userBadges = badgeRepository.getCurrentUserBadges().getOrDefault(emptyList())
                    val recentSessions = gameSessionRepository.getRecentSessions(10).getOrDefault(emptyList())
                    val weeklySessions = gameSessionRepository.getSessionsThisWeek().getOrDefault(emptyList())
                    val gamesPlayed = gameSessionRepository.getGamesPlayedCount().getOrDefault(0)

                    val avgStreak = if (recentSessions.isNotEmpty()) {
                        recentSessions.sumOf { it.streak } / recentSessions.size
                    } else 0

                    _uiState.value = _uiState.value.copy(
                        profile = profile,
                        email = email,
                        isLoading = false,

                        totalXp = xp,
                        level = level,
                        nextLevel = level + 1,
                        xpNeeded = LevelSystem.xpToNextLevel(xp),

                        rank = rank,

                        gamesPlayed = gamesPlayed,
                        avgStreak = avgStreak,
                        badges = userBadges.size,

                        recentSessions = recentSessions,
                        weeklySessions = weeklySessions
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

    fun openUsernameDialog() {
        _uiState.value = _uiState.value.copy(
            isEditingUsername = true,
            usernameInput = _uiState.value.profile?.username ?: ""
        )
    }

    fun closeUsernameDialog() {
        _uiState.value = _uiState.value.copy(
            isEditingUsername = false
        )
    }

    fun onUsernameChange(value: String) {
        _uiState.value = _uiState.value.copy(
            usernameInput = value
        )
    }

    fun saveUsername() {
        val newName = _uiState.value.usernameInput

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isEditingUsername = false
            )

            profileRepository.updateUsername(newName).fold(
                onSuccess = {
                    loadProfile()
                },
                onFailure = {
                    _uiState.value = _uiState.value.copy(
                        error = it.message,
                        isEditingUsername = false
                    )
                }
            )
        }
    }

    fun logout(onDone: () -> Unit) {
        viewModelScope.launch {
            authRepository.signOut().fold(
                onSuccess = {
                    onDone()
                },
                onFailure = {
                    _uiState.value = _uiState.value.copy(
                        error = it.message
                    )
                }
            )
        }
    }
}