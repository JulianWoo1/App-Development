package com.example.realitycheck.ui.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.realitycheck.data.repository.ContentRepository
import com.example.realitycheck.data.repository.ProfileRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.random.Random

enum class GameMode  { IMAGE, TEXT, SPEED }
enum class RoundType { ONE_REAL, BOTH_AI, BOTH_REAL }

/**
 * Controls which round types are allowed.
 * CLASSIC  → always one real + one AI
 * CHAOS    → randomly picks ONE_REAL, BOTH_AI, or BOTH_REAL
 */
enum class RulesMode { CLASSIC, CHAOS }

data class GameUiState(
    val mode: GameMode = GameMode.IMAGE,
    val rulesMode: RulesMode = RulesMode.CLASSIC,

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

    val earnedXp: Int = 0,
    val timeRemainingSeconds: Int? = null
)

class GameViewModel(
    private val profileRepository: ProfileRepository,
    private val contentRepository: ContentRepository,
    private val onXpUpdated: () -> Unit = {}
) : ViewModel() {

    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    private val _streak = MutableStateFlow(0)
    val currentStreak: StateFlow<Int> = _streak.asStateFlow()

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

    init { loadNextRound() }

    fun setGameMode(mode: GameMode) {
        _uiState.value = _uiState.value.copy(mode = mode, isGameOver = false)
    }

    fun setRulesMode(mode: RulesMode) {
        _uiState.value = _uiState.value.copy(rulesMode = mode)
    }

    // Keep setMode for compatibility with LaunchedEffect calls
    fun setMode(mode: GameMode) = setGameMode(mode)

    private fun allowedRoundTypes(): List<RoundType> = when (_uiState.value.rulesMode) {
        RulesMode.CLASSIC -> listOf(RoundType.ONE_REAL)
        RulesMode.CHAOS   -> RoundType.entries
    }

    fun loadNextRound() {
        when (_uiState.value.mode) {
            GameMode.IMAGE, GameMode.SPEED -> loadImageRound()
            GameMode.TEXT                  -> loadTextRound()
        }
    }

    // ── Image ────────────────────────────────────────────────────────────────

    private fun loadImageRound() {
        _uiState.value = _uiState.value.copy(isLoading = true)
        viewModelScope.launch {
            when (val type = allowedRoundTypes().random()) {
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
                    topContent    = if (topIsReal) real.contentUrl else ai.contentUrl,
                    bottomContent = if (topIsReal) ai.contentUrl else real.contentUrl,
                    isCorrectTop  = topIsReal,
                    isImageMode   = true, roundType = RoundType.ONE_REAL,
                    isLoading = false, showOverlay = false
                )
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
                    topContent = candidates[0].contentUrl, bottomContent = candidates[1].contentUrl,
                    isImageMode = true, roundType = type,
                    isLoading = false, showOverlay = false
                )
            },
            onFailure = { loadBothSameRound(wantAi, attempt + 1) }
        )
    }

    // ── Text ─────────────────────────────────────────────────────────────────

    private fun loadTextRound() {
        val type  = allowedRoundTypes().random()
        val ai1   = aiTexts.random()
        val ai2   = aiTexts.filter { it != ai1 }.random()
        val real1 = realTexts.random()
        val real2 = realTexts.filter { it != real1 }.random()

        when (type) {
            RoundType.ONE_REAL -> {
                val top = Random.nextBoolean()
                _uiState.value = _uiState.value.copy(
                    topContent = if (top) real1 else ai1, bottomContent = if (top) ai1 else real1,
                    isCorrectTop = top, isImageMode = false, roundType = type,
                    isLoading = false, showOverlay = false
                )
            }
            RoundType.BOTH_AI   -> _uiState.value = _uiState.value.copy(
                topContent = ai1, bottomContent = ai2, isImageMode = false, roundType = type,
                isLoading = false, showOverlay = false
            )
            RoundType.BOTH_REAL -> _uiState.value = _uiState.value.copy(
                topContent = real1, bottomContent = real2, isImageMode = false, roundType = type,
                isLoading = false, showOverlay = false
            )
        }
    }

    // ── Input ────────────────────────────────────────────────────────────────

    fun onSelect(isTop: Boolean) {
        if (_uiState.value.showOverlay) return
        val correct = when (_uiState.value.roundType) {
            RoundType.ONE_REAL                     -> isTop == _uiState.value.isCorrectTop
            RoundType.BOTH_AI, RoundType.BOTH_REAL -> false
        }
        handleResult(correct, isTop)
    }

    fun onBothAnswer(guessedAi: Boolean) {
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
            delay(1000)
            if (correct) {
                _streak.value++
                val xp = GameRewards.CORRECT_ANSWER_XP + GameRewards.streakBonus(_streak.value)
                _uiState.value = _uiState.value.copy(earnedXp = xp)
                profileRepository.addXp(xp).onSuccess { onXpUpdated() }
                delay(800)
                _uiState.value = _uiState.value.copy(earnedXp = 0)
                loadNextRound()
            } else {
                profileRepository.updateHighScore(_streak.value)
                _uiState.value = _uiState.value.copy(isGameOver = true)
            }
        }
    }

    companion object {
        private const val MAX_RETRIES = 10
    }
}
