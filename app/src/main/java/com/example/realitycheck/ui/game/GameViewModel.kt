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

// ---------------- MODE ----------------

enum class GameMode {
    IMAGE,
    TEXT,
    SPEED
}

enum class RoundType {
    ONE_REAL,
    BOTH_AI,
    BOTH_REAL
}

// ---------------- UI STATE ----------------

data class GameUiState(
    val mode: GameMode = GameMode.IMAGE,

    val topContent: String? = null,
    val bottomContent: String? = null,

    val isImageMode: Boolean = true,

    val roundType: RoundType = RoundType.ONE_REAL,
    val isCorrectTop: Boolean = true,

    val showOverlay: Boolean = false,
    val lastResultCorrect: Boolean = false,
    val tappedTop: Boolean? = null,

    val isGameOver: Boolean = false,
    val isLoading: Boolean = true,
    val timeRemainingSeconds: Int? = null
)

// ---------------- VIEWMODEL ----------------

class GameViewModel(
    private val profileRepository: ProfileRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    private val _streak = MutableStateFlow(0)
    val currentStreak: StateFlow<Int> = _streak.asStateFlow()

    // Dummy TEXT data (later kan dit uit database)
    private val aiTexts = listOf(
        "AI generates synthetic content",
        "Neural networks process patterns",
        "Machine learning predicts outcomes",
        "Generated text with statistical patterns"
    )

    private val realTexts = listOf(
        "I went to the store today",
        "The weather is nice",
        "I had breakfast this morning",
        "I am going to school"
    )

    init {
        loadNextRound()
    }

    // ---------------- MODE SWITCH ----------------

    fun setMode(mode: GameMode) {
        _uiState.value = _uiState.value.copy(
            mode = mode,
            isGameOver = false
        )
        loadNextRound()
    }

    fun loadNextRound() {
        when (_uiState.value.mode) {
            GameMode.IMAGE -> loadImageRound()
            GameMode.TEXT -> loadTextRound()
            GameMode.SPEED -> { }
        }
    }

    // ---------------- IMAGE MODE ----------------

    private fun loadImageRound() {

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
                    isCorrectTop = topIsReal,
                    isImageMode = true,
                    roundType = type,
                    isLoading = false,
                    showOverlay = false
                )
            }

            RoundType.BOTH_AI -> {
                _uiState.value = _uiState.value.copy(
                    topContent = ai1,
                    bottomContent = ai2,
                    isImageMode = true,
                    roundType = type,
                    isLoading = false,
                    showOverlay = false
                )
            }

            RoundType.BOTH_REAL -> {
                _uiState.value = _uiState.value.copy(
                    topContent = real1,
                    bottomContent = real2,
                    isImageMode = true,
                    roundType = type,
                    isLoading = false,
                    showOverlay = false
                )
            }
        }
    }

    // ---------------- TEXT MODE ----------------

    private fun loadTextRound() {

        val type = RoundType.entries.random()

        val ai1 = aiTexts.random()
        val ai2 = aiTexts.random()

        val real1 = realTexts.random()
        val real2 = realTexts.random()

        when (type) {

            RoundType.ONE_REAL -> {
                val topIsReal = Random.nextBoolean()

                _uiState.value = _uiState.value.copy(
                    topContent = if (topIsReal) real1 else ai1,
                    bottomContent = if (topIsReal) ai1 else real1,
                    isCorrectTop = topIsReal,
                    isImageMode = false,
                    roundType = type,
                    isLoading = false,
                    showOverlay = false
                )
            }

            RoundType.BOTH_AI -> {
                _uiState.value = _uiState.value.copy(
                    topContent = ai1,
                    bottomContent = ai2,
                    isImageMode = false,
                    roundType = type,
                    isLoading = false,
                    showOverlay = false
                )
            }

            RoundType.BOTH_REAL -> {
                _uiState.value = _uiState.value.copy(
                    topContent = real1,
                    bottomContent = real2,
                    isImageMode = false,
                    roundType = type,
                    isLoading = false,
                    showOverlay = false
                )
            }
        }
    }
    // ---------------- INPUT ----------------

    fun onSelect(isTop: Boolean) {

        if (_uiState.value.showOverlay) return

        val correct = when (_uiState.value.roundType) {
            RoundType.ONE_REAL -> isTop == _uiState.value.isCorrectTop
            RoundType.BOTH_AI -> false
            RoundType.BOTH_REAL -> false
        }

        handleResult(correct, isTop)
    }

    fun onBothAnswer(guessedAi: Boolean) {

        val correct = when (_uiState.value.roundType) {
            RoundType.ONE_REAL -> false
            RoundType.BOTH_AI -> guessedAi
            RoundType.BOTH_REAL -> !guessedAi
        }

        handleResult(correct, null)
    }

    // ---------------- RESULT HANDLING ----------------

    private fun handleResult(correct: Boolean, tappedTop: Boolean?) {

        _uiState.value = _uiState.value.copy(
            showOverlay = true,
            lastResultCorrect = correct,
            tappedTop = tappedTop
        )

        viewModelScope.launch {
            delay(1000)

            if (correct) {
                _streak.value++
                loadNextRound()
            } else {
                profileRepository.updateHighScore(_streak.value)
                _uiState.value = _uiState.value.copy(isGameOver = true)
            }
        }
    }

    companion object {
        private const val MAX_IMAGE_ID = 400
        private const val SUPABASE_STORAGE_URL = "https://vxqxbbkokdmxgirkhttc.supabase.co/storage/v1/object/public/images"
    }
}
