package com.example.realitycheck.ui.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.realitycheck.data.repository.ProfileRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.random.Random

class SurvivalViewModel(
    private val profileRepository: ProfileRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        GameUiState(
            mode = GameMode.SURVIVAL,
            timeRemainingSeconds = null
        )
    )
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    private val _score = MutableStateFlow(0)
    val currentScore: StateFlow<Int> = _score.asStateFlow()

    private val _lives = MutableStateFlow(3)
    val currentLives: StateFlow<Int> = _lives.asStateFlow()

    init {
        loadNextRound()
    }

    // ---------------- ROUND LOADING ----------------

    fun loadNextRound() {
        if (_lives.value <= 0) return

        val id = (1..MAX_IMAGE_ID).random()

        val real1 = "$SUPABASE_STORAGE_URL/Real/$id.jpg"
        val real2 = "$SUPABASE_STORAGE_URL/Real/${id + 1}.jpg"
        val ai1 = "$SUPABASE_STORAGE_URL/AI/$id.jpg"
        val ai2 = "$SUPABASE_STORAGE_URL/AI/${id + 1}.jpg"

        val type = RoundType.entries.random()

        when (type) {

            RoundType.ONE_REAL -> {
                val topIsReal = Random.nextBoolean()

                _uiState.value = _uiState.value.copy(
                    topContent = if (topIsReal) real1 else ai1,
                    bottomContent = if (topIsReal) ai1 else real1,
                    isImageMode = true,
                    roundType = type,
                    isCorrectTop = topIsReal,
                    isLoading = false,
                    showOverlay = false,
                    isGameOver = false
                )
            }

            RoundType.BOTH_AI -> {
                _uiState.value = _uiState.value.copy(
                    topContent = ai1,
                    bottomContent = ai2,
                    isImageMode = true,
                    roundType = type,
                    isLoading = false,
                    showOverlay = false,
                    isGameOver = false
                )
            }

            RoundType.BOTH_REAL -> {
                _uiState.value = _uiState.value.copy(
                    topContent = real1,
                    bottomContent = real2,
                    isImageMode = true,
                    roundType = type,
                    isLoading = false,
                    showOverlay = false,
                    isGameOver = false
                )
            }
        }
    }

    // ---------------- INPUT ----------------

    fun onSelect(isTop: Boolean) {
        if (_uiState.value.showOverlay || _lives.value <= 0) return

        val correct = when (_uiState.value.roundType) {
            RoundType.ONE_REAL -> isTop == _uiState.value.isCorrectTop
            RoundType.BOTH_AI -> false
            RoundType.BOTH_REAL -> false
        }

        handleResult(correct)
    }

    fun onBothAnswer(guessedAi: Boolean) {
        if (_uiState.value.showOverlay || _lives.value <= 0) return

        val correct = when (_uiState.value.roundType) {
            RoundType.ONE_REAL -> false
            RoundType.BOTH_AI -> guessedAi
            RoundType.BOTH_REAL -> !guessedAi
        }

        handleResult(correct)
    }

    // ---------------- RESULT ----------------

    private fun handleResult(correct: Boolean) {

        _uiState.value = _uiState.value.copy(
            showOverlay = true,
            lastResultCorrect = correct
        )

        viewModelScope.launch {
            delay(800)

            if (correct) {
                _score.value++
            } else {
                _lives.value -= 1
            }

            if (_lives.value <= 0) {
                profileRepository.updateHighScore(_score.value)

                _uiState.value = _uiState.value.copy(
                    isGameOver = true
                )
            } else {
                loadNextRound()
            }
        }
    }

    fun onGameOverDismissed() {
        // optional reset logic
    }

    fun setMode(mode: GameMode) {
        // survival is fixed mode
    }

    companion object {
        private const val MAX_IMAGE_ID = 400
        private const val SUPABASE_STORAGE_URL =
            "https://vxqxbbkokdmxgirkhttc.supabase.co/storage/v1/object/public/images"
    }
}