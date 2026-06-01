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
import kotlin.random.Random

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
            contentRepository.getNextPair().fold(
                onSuccess = { (first, second) ->
                    val topIsReal = Random.nextBoolean()
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
                },
                onFailure = { loadNextRoundFallback() }
            )
        }
    }

    private fun loadNextRoundFallback() {
        val id    = (1..MAX_IMAGE_ID).random()
        val real1 = "$BASE_URL/Real/$id.jpg"
        val ai1   = "$BASE_URL/AI/$id.jpg"
        val topIsReal = Random.nextBoolean()

        _uiState.value = _uiState.value.copy(
            topContent    = if (topIsReal) real1 else ai1,
            bottomContent = if (topIsReal) ai1 else real1,
            isCorrectTop  = topIsReal,
            isImageMode   = true,
            roundType     = RoundType.ONE_REAL,
            isLoading     = false,
            showOverlay   = false
        )
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
            if (correct) _correctCount.value++
            loadNextRound()
        }
    }

    fun onGameOverDismissed() { timerJob?.cancel() }

    companion object {
        private const val MAX_IMAGE_ID = 400
        private const val BASE_URL =
            "https://vxqxbbkokdmxgirkhttc.supabase.co/storage/v1/object/public/images"
    }
}