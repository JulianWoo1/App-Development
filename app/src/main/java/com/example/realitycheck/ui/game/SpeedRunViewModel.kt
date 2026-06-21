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
    private val rulesMode: RulesMode = RulesMode.CLASSIC,
    private val onXpUpdated: () -> Unit = {}
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        GameUiState(mode = GameMode.SPEED, rulesMode = rulesMode, timeRemainingSeconds = REVEAL_SECONDS)
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

    private fun allowedRoundTypes(): List<RoundType> = when (rulesMode) {
        RulesMode.CLASSIC -> listOf(RoundType.ONE_REAL)
        RulesMode.CHAOS   -> RoundType.entries
    }

    fun loadNextRound() {
        if (_uiState.value.isGameOver) return
        revealJob?.cancel()
        _uiState.value = _uiState.value.copy(isLoading = true, imagesHidden = false)

        viewModelScope.launch {
            when (allowedRoundTypes().random()) {
                RoundType.ONE_REAL  -> loadOneRealRound()
                RoundType.BOTH_AI   -> loadBothSameRound(wantAi = true)
                RoundType.BOTH_REAL -> loadBothSameRound(wantAi = false)
            }
        }
    }

    private suspend fun loadOneRealRound(attempt: Int = 0) {
        if (attempt >= MAX_RETRIES) { _uiState.value = _uiState.value.copy(isGameOver = true); return }

        contentRepository.getNextPair().fold(
            onSuccess = { (a, b) ->
                val real = listOf(a, b).firstOrNull { !it.isAi }
                val ai   = listOf(a, b).firstOrNull {  it.isAi }

                if (real == null || ai == null) { loadOneRealRound(attempt + 1); return }

                val topIsReal = (0..1).random() == 0
                _uiState.value = _uiState.value.copy(
                    topContent           = if (topIsReal) real.contentUrl else ai.contentUrl,
                    bottomContent        = if (topIsReal) ai.contentUrl else real.contentUrl,
                    isCorrectTop         = topIsReal,
                    isImageMode          = true,
                    roundType            = RoundType.ONE_REAL,
                    isLoading            = false,
                    showOverlay          = false,
                    timeRemainingSeconds = REVEAL_SECONDS
                )
                startRevealTimer()
            },
            onFailure = { loadOneRealRound(attempt + 1) }
        )
    }

    private suspend fun loadBothSameRound(wantAi: Boolean, attempt: Int = 0) {
        val type = if (wantAi) RoundType.BOTH_AI else RoundType.BOTH_REAL
        if (attempt >= MAX_RETRIES) { _uiState.value = _uiState.value.copy(isGameOver = true); return }

        contentRepository.getNextPair().fold(
            onSuccess = { (a, b) ->
                val candidates = listOf(a, b).filter { it.isAi == wantAi }
                if (candidates.size < 2) { loadBothSameRound(wantAi, attempt + 1); return }

                _uiState.value = _uiState.value.copy(
                    topContent           = candidates[0].contentUrl,
                    bottomContent        = candidates[1].contentUrl,
                    isImageMode          = true,
                    roundType            = type,
                    isLoading            = false,
                    showOverlay          = false,
                    timeRemainingSeconds = REVEAL_SECONDS
                )
                startRevealTimer()
            },
            onFailure = { loadBothSameRound(wantAi, attempt + 1) }
        )
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
        val correct = when (_uiState.value.roundType) {
            RoundType.ONE_REAL                     -> isTop == _uiState.value.isCorrectTop
            RoundType.BOTH_AI, RoundType.BOTH_REAL -> false
        }
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
                profileRepository.updateHighScore(_streak.value)
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
        private const val MAX_RETRIES = 10
    }
}