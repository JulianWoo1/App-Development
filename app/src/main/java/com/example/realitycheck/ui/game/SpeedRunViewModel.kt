package com.example.realitycheck.ui.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.realitycheck.data.repository.ContentRepository
import com.example.realitycheck.data.repository.GameSessionRepository
import com.example.realitycheck.data.repository.ProfileRepository
import com.example.realitycheck.ui.badges.BadgeEvaluationContext
import com.example.realitycheck.ui.badges.BadgeService
import com.example.realitycheck.ui.badges.BadgeUiItem
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class SpeedRunViewModel(
    private val profileRepository: ProfileRepository,
    private val contentRepository: ContentRepository,
    private val badgeService: BadgeService,
    private val gameSessionRepository: GameSessionRepository,
    private val onXpUpdated: () -> Unit = {}
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        GameUiState(mode = GameMode.SPEED, timeRemainingSeconds = REVEAL_SECONDS)
    )
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    private val _streak = MutableStateFlow(0)
    val currentStreak: StateFlow<Int> = _streak.asStateFlow()

    var sessionXp: Int = 0
        private set

    var sessionNewBadges: List<BadgeUiItem> = emptyList()
        private set

    private var timerJob: Job? = null
    private var revealJob: Job? = null

    init { loadNextRound() }

    fun loadNextRound() {
        if (_uiState.value.isGameOver) return
        revealJob?.cancel()
        _uiState.value = _uiState.value.copy(isLoading = true, imagesHidden = false)

        viewModelScope.launch {
            contentRepository.getNextPair().onSuccess { (first, second) ->
                val topIsReal = (0..1).random() == 0
                val (realItem, aiItem) = if (!first.isAi) first to second else second to first

                _uiState.value = _uiState.value.copy(
                    topContent           = if (topIsReal) realItem.contentUrl else aiItem.contentUrl,
                    bottomContent        = if (topIsReal) aiItem.contentUrl else realItem.contentUrl,
                    isCorrectTop         = topIsReal,
                    isImageMode          = true,
                    roundType            = RoundType.ONE_REAL,
                    isLoading            = false,
                    showOverlay          = false,
                    timeRemainingSeconds = REVEAL_SECONDS
                )
                startRevealTimer()
            }
        }
    }

    private fun startRevealTimer() {
        revealJob = viewModelScope.launch {
            var remaining = REVEAL_SECONDS
            while (isActive && remaining > 0) {
                delay(1000)
                remaining--
                _uiState.value = _uiState.value.copy(timeRemainingSeconds = remaining)
            }
            if (!_uiState.value.showOverlay && !_uiState.value.isGameOver) {
                _uiState.value = _uiState.value.copy(
                    imagesHidden        = true,
                    topContent          = null,
                    bottomContent       = null,
                    timeRemainingSeconds = null
                )
            }
        }
    }

    fun onSelect(isTop: Boolean) {
        if (_uiState.value.showOverlay || _uiState.value.isGameOver) return
        revealJob?.cancel()
        val correct = isTop == _uiState.value.isCorrectTop
        handleResult(correct, isTop)
    }

    fun onBothAnswer(guessedAi: Boolean) {
        if (_uiState.value.showOverlay || _uiState.value.isGameOver) return
        revealJob?.cancel()
        val correct = when (_uiState.value.roundType) {
            RoundType.ONE_REAL  -> false
            RoundType.BOTH_AI   -> guessedAi
            RoundType.BOTH_REAL -> !guessedAi
        }
        handleResult(correct, null)
    }

    private fun handleResult(correct: Boolean, tappedTop: Boolean?) {
        _uiState.value = _uiState.value.copy(
            showOverlay = true, lastResultCorrect = correct, tappedTop = tappedTop
        )
        viewModelScope.launch {
            if (correct) {
                _streak.value++
                val xp = GameRewards.CORRECT_ANSWER_XP
                sessionXp += xp

                profileRepository.addXp(xp)
                onXpUpdated()

                delay(600)
                loadNextRound()
            } else {
                delay(600)
                gameSessionRepository.recordGameSession(
                    mode = GameMode.SPEED.name,
                    streak = _streak.value,
                    xpEarned = sessionXp
                )
                val result = badgeService.checkAndAwardBadges(
                    BadgeEvaluationContext(
                        streak = _streak.value,
                        gameMode = GameMode.SPEED
                    )
                )
                sessionNewBadges = result.getOrDefault(emptyList())
                _uiState.value = _uiState.value.copy(isGameOver = true)
            }
        }
    }

    fun onGameOverDismissed() { revealJob?.cancel() }

    companion object {
        private const val REVEAL_SECONDS = 3
    }
}