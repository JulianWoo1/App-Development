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

    val earnedXp: Int = 0,
    val timeRemainingSeconds: Int? = null
)

// ---------------- VIEWMODEL ----------------
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

    init {
        loadNextRound()
    }

    fun setMode(mode: GameMode) {
        _uiState.value = _uiState.value.copy(mode = mode, isGameOver = false)
        loadNextRound()
    }

    fun loadNextRound() {
        when (_uiState.value.mode) {
            GameMode.IMAGE -> loadImageRound()
            GameMode.TEXT  -> loadTextRound()
            GameMode.SPEED -> loadImageRound()
        }
    }

    private fun loadImageRound() {
        _uiState.value = _uiState.value.copy(isLoading = true)

        viewModelScope.launch {
            contentRepository.getNextPair().fold(
                onSuccess = { (first, second) ->
                    val type = RoundType.entries.random()

                    when (type) {
                        RoundType.ONE_REAL -> {
                            // Ensure one is AI and one is real; if both same type fall back
                            val (realItem, aiItem) = if (!first.isAi && second.isAi) {
                                first to second
                            } else if (first.isAi && !second.isAi) {
                                second to first
                            } else {
                                // Both same — treat as BOTH_AI or BOTH_REAL
                                val bothAi = first.isAi
                                val topUrl = first.contentUrl ?: ""
                                val botUrl = second.contentUrl ?: ""
                                _uiState.value = _uiState.value.copy(
                                    topContent    = topUrl,
                                    bottomContent = botUrl,
                                    isImageMode   = true,
                                    roundType     = if (bothAi) RoundType.BOTH_AI else RoundType.BOTH_REAL,
                                    isLoading     = false,
                                    showOverlay   = false
                                )
                                return@fold
                            }
                            val topIsReal = Random.nextBoolean()
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

                        RoundType.BOTH_AI -> {
                            _uiState.value = _uiState.value.copy(
                                topContent    = first.contentUrl,
                                bottomContent = second.contentUrl,
                                isImageMode   = true,
                                roundType     = RoundType.BOTH_AI,
                                isLoading     = false,
                                showOverlay   = false
                            )
                        }

                        RoundType.BOTH_REAL -> {
                            _uiState.value = _uiState.value.copy(
                                topContent    = first.contentUrl,
                                bottomContent = second.contentUrl,
                                isImageMode   = true,
                                roundType     = RoundType.BOTH_REAL,
                                isLoading     = false,
                                showOverlay   = false
                            )
                        }
                    }
                },
                onFailure = {
                    // Fallback to URL construction if repository fails
                    loadImageRoundFallback()
                }
            )
        }
    }

    /** Fallback for when the content repository is unavailable. */
    private fun loadImageRoundFallback() {
        val id    = (1..MAX_IMAGE_ID).random()
        val real1 = "$BASE_URL/Real/$id.jpg"
        val real2 = "$BASE_URL/Real/${id + 1}.jpg"
        val ai1   = "$BASE_URL/AI/$id.jpg"
        val ai2   = "$BASE_URL/AI/${id + 1}.jpg"
        val type  = RoundType.entries.random()

        when (type) {
            RoundType.ONE_REAL -> {
                val topIsReal = Random.nextBoolean()
                _uiState.value = _uiState.value.copy(
                    topContent    = if (topIsReal) real1 else ai1,
                    bottomContent = if (topIsReal) ai1 else real1,
                    isCorrectTop  = topIsReal,
                    isImageMode   = true, roundType = type,
                    isLoading = false, showOverlay = false
                )
            }
            RoundType.BOTH_AI -> _uiState.value = _uiState.value.copy(
                topContent = ai1, bottomContent = ai2,
                isImageMode = true, roundType = type,
                isLoading = false, showOverlay = false
            )
            RoundType.BOTH_REAL -> _uiState.value = _uiState.value.copy(
                topContent = real1, bottomContent = real2,
                isImageMode = true, roundType = type,
                isLoading = false, showOverlay = false
            )
        }
    }

    private fun loadTextRound() {
        val type  = RoundType.entries.random()
        val ai1   = aiTexts.random()
        val ai2   = aiTexts.random()
        val real1 = realTexts.random()
        val real2 = realTexts.random()

        when (type) {
            RoundType.ONE_REAL -> {
                val topIsReal = Random.nextBoolean()
                _uiState.value = _uiState.value.copy(
                    topContent    = if (topIsReal) real1 else ai1,
                    bottomContent = if (topIsReal) ai1 else real1,
                    isCorrectTop  = topIsReal,
                    isImageMode   = false, roundType = type,
                    isLoading = false, showOverlay = false
                )
            }
            RoundType.BOTH_AI -> _uiState.value = _uiState.value.copy(
                topContent = ai1, bottomContent = ai2,
                isImageMode = false, roundType = type,
                isLoading = false, showOverlay = false
            )
            RoundType.BOTH_REAL -> _uiState.value = _uiState.value.copy(
                topContent = real1, bottomContent = real2,
                isImageMode = false, roundType = type,
                isLoading = false, showOverlay = false
            )
        }
    }

    fun onSelect(isTop: Boolean) {
        if (_uiState.value.showOverlay) return
        val correct = when (_uiState.value.roundType) {
            RoundType.ONE_REAL  -> isTop == _uiState.value.isCorrectTop
            RoundType.BOTH_AI   -> false
            RoundType.BOTH_REAL -> false
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
                val xpEarned = GameRewards.CORRECT_ANSWER_XP + GameRewards.streakBonus(_streak.value)
                _uiState.value = _uiState.value.copy(earnedXp = xpEarned)
                profileRepository.addXp(xpEarned).onSuccess { onXpUpdated() }
                delay(1200)
                _uiState.value = _uiState.value.copy(earnedXp = 0)
                loadNextRound()
            } else {
                profileRepository.updateHighScore(_streak.value)
                _uiState.value = _uiState.value.copy(isGameOver = true)
            }
        }
    }

    companion object {
        private const val MAX_IMAGE_ID = 400
        private const val BASE_URL =
            "https://vxqxbbkokdmxgirkhttc.supabase.co/storage/v1/object/public/images"
    }
}