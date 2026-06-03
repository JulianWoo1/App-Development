package com.example.realitycheck.ui.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.realitycheck.data.repository.ContentRepository
import com.example.realitycheck.data.repository.ProfileRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class SpeedRunViewModel(
    private val profileRepository: ProfileRepository,
    private val contentRepository: ContentRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        GameUiState(mode = GameMode.SPEED, timeRemainingSeconds = 60)
    )
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    private val _correctCount = MutableStateFlow(0)
    val currentCorrectCount: StateFlow<Int> = _correctCount.asStateFlow()

    /**
     * Total XP earned during this speed-run session.
     * Read by [MainNavHost] just before navigating to GameOverScreen.
     */
    var sessionXp: Int = 0
        private set

    private var timerJob: Job? = null

    init {
        startTimer()
        loadNextRound()
    }

    private fun startTimer() {
        timerJob = viewModelScope.launch {
            while (isActive && (_uiState.value.timeRemainingSeconds ?: 0) > 0) {
                delay(1000)
                val remaining = (_uiState.value.timeRemainingSeconds ?: 0) - 1
                _uiState.value = _uiState.value.copy(timeRemainingSeconds = remaining)
            }
            if (!_uiState.value.isGameOver) {
                profileRepository.updateHighScore(_correctCount.value)
                _uiState.value = _uiState.value.copy(isGameOver = true)
            }
        }
    }

    fun loadNextRound() {
        if (_uiState.value.isGameOver) return
        _uiState.value = _uiState.value.copy(isLoading = true)

        viewModelScope.launch {
            contentRepository.getNextPair().onSuccess { (first, second) ->
                val topIsReal = (0..1).random() == 0
                val (realItem, aiItem) = if (!first.isAi) first to second else second to first

                _uiState.value = _uiState.value.copy(
                    topContent    = if (topIsReal) realItem.contentUrl else aiItem.contentUrl,
                    bottomContent = if (topIsReal) aiItem.contentUrl else realItem.contentUrl,
                    isCorrectTop  = topIsReal,
                    isImageMode   = true,
                    roundType     = RoundType.ONE_REAL,
                    isLoading     = false,
                    showOverlay   = false
                )
            }
        }
    }

    fun onSelect(isTop: Boolean) {
        if (_uiState.value.showOverlay || _uiState.value.isGameOver) return
        val correct = when (_uiState.value.roundType) {
            RoundType.ONE_REAL  -> isTop == _uiState.value.isCorrectTop
            RoundType.BOTH_AI   -> false
            RoundType.BOTH_REAL -> false
        }
        handleResult(correct, isTop)
    }

    fun onBothAnswer(guessedAi: Boolean) {
        if (_uiState.value.showOverlay || _uiState.value.isGameOver) return
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
            delay(600)
            if (correct) {
                _correctCount.value++
                val xp = GameRewards.CORRECT_ANSWER_XP  // speed run uses base XP only
                sessionXp += xp                          // ← accumulate session total
                profileRepository.addXp(xp)
            }
            loadNextRound()
        }
    }

    fun onGameOverDismissed() { timerJob?.cancel() }

    companion object
}