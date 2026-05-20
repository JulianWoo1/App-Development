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

enum class RoundType { ONE_REAL, BOTH_AI, BOTH_REAL }

data class GameUiState(
    val topImageUrl: String? = null,
    val bottomImageUrl: String? = null,
    val roundType: RoundType = RoundType.ONE_REAL,
    val isCorrectImageTop: Boolean = true,
    val showOverlay: Boolean = false,
    val lastResultCorrect: Boolean = false,
    val tappedTop: Boolean? = null,
    val isGameOver: Boolean = false,
    val isLoading: Boolean = true
)

class GameViewModel(
    private val profileRepository: ProfileRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    private val _currentStreak = MutableStateFlow(0)
    val currentStreak: StateFlow<Int> = _currentStreak.asStateFlow()

    init {
        loadNextRound()
    }

    fun loadNextRound() {
        val randomId = (1 until MAX_IMAGE_ID - 3).random()
        val realUrl1 = "https://picsum.photos/id/$randomId/$IMAGE_SIZE/$IMAGE_SIZE"
        val realUrl2 = "https://picsum.photos/id/${randomId + 2}/$IMAGE_SIZE/$IMAGE_SIZE"
        val aiUrl1   = "https://picsum.photos/id/${randomId + 1}/$IMAGE_SIZE/$IMAGE_SIZE"
        val aiUrl2   = "https://picsum.photos/id/${randomId + 3}/$IMAGE_SIZE/$IMAGE_SIZE"

        val roundType = RoundType.entries.random()

        when (roundType) {
            RoundType.ONE_REAL -> {
                val correctTop = Random.nextBoolean()
                _uiState.value = _uiState.value.copy(
                    topImageUrl = if (correctTop) realUrl1 else aiUrl1,
                    bottomImageUrl = if (correctTop) aiUrl1 else realUrl1,
                    roundType = roundType,
                    isCorrectImageTop = correctTop,
                    showOverlay = false,
                    isLoading = false
                )
            }
            RoundType.BOTH_AI -> {
                _uiState.value = _uiState.value.copy(
                    topImageUrl = aiUrl1,
                    bottomImageUrl = aiUrl2,
                    roundType = roundType,
                    showOverlay = false,
                    isLoading = false
                )
            }
            RoundType.BOTH_REAL -> {
                _uiState.value = _uiState.value.copy(
                    topImageUrl = realUrl1,
                    bottomImageUrl = realUrl2,
                    roundType = roundType,
                    showOverlay = false,
                    isLoading = false
                )
            }
        }
    }

    fun onImageSelected(isTop: Boolean) {
        if (_uiState.value.showOverlay || _uiState.value.isLoading) return

        val isCorrect = when (_uiState.value.roundType) {
            RoundType.ONE_REAL -> isTop == _uiState.value.isCorrectImageTop
            RoundType.BOTH_AI -> false
            RoundType.BOTH_REAL -> false
        }
        handleResult(isCorrect, tappedTop = isTop)
    }

    fun onBothAnswer(guessedAi: Boolean) {
        if (_uiState.value.showOverlay || _uiState.value.isLoading) return

        val isCorrect = when (_uiState.value.roundType) {
            RoundType.ONE_REAL -> false
            RoundType.BOTH_AI -> guessedAi
            RoundType.BOTH_REAL -> !guessedAi
        }
        handleResult(isCorrect, tappedTop = null)
    }

    private fun handleResult(correct: Boolean, tappedTop: Boolean?) {
        _uiState.value = _uiState.value.copy(
            showOverlay = true,
            lastResultCorrect = correct,
            tappedTop = tappedTop
        )

        viewModelScope.launch {
            delay(1200)
            if (correct) {
                _currentStreak.value += 1
                loadNextRound()
            } else {
                profileRepository.updateHighScore(_currentStreak.value)
                _uiState.value = _uiState.value.copy(isGameOver = true)
            }
        }
    }

    companion object {
        private const val IMAGE_SIZE = 600
        private const val MAX_IMAGE_ID = 1000
    }
}
