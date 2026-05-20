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
    val isLoading: Boolean = true,
    val topImageLoaded: Boolean = false,
    val bottomImageLoaded: Boolean = false,
    val topImageError: Boolean = false,
    val bottomImageError: Boolean = false
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
        val randomId = (1..MAX_IMAGE_ID).random()
        val realUrl1 = "$SUPABASE_STORAGE_URL/Real/$randomId.jpg"
        val realUrl2 = "$SUPABASE_STORAGE_URL/Real/${(randomId % MAX_IMAGE_ID) + 1}.jpg"
        val aiUrl1   = "$SUPABASE_STORAGE_URL/AI/$randomId.jpg"
        val aiUrl2   = "$SUPABASE_STORAGE_URL/AI/${(randomId % MAX_IMAGE_ID) + 1}.jpg"

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
                    isLoading = true,
                    topImageLoaded = false,
                    bottomImageLoaded = false,
                    topImageError = false,
                    bottomImageError = false
                )
            }
            RoundType.BOTH_AI -> {
                _uiState.value = _uiState.value.copy(
                    topImageUrl = aiUrl1,
                    bottomImageUrl = aiUrl2,
                    roundType = roundType,
                    showOverlay = false,
                    isLoading = true,
                    topImageLoaded = false,
                    bottomImageLoaded = false,
                    topImageError = false,
                    bottomImageError = false
                )
            }
            RoundType.BOTH_REAL -> {
                _uiState.value = _uiState.value.copy(
                    topImageUrl = realUrl1,
                    bottomImageUrl = realUrl2,
                    roundType = roundType,
                    showOverlay = false,
                    isLoading = true,
                    topImageLoaded = false,
                    bottomImageLoaded = false,
                    topImageError = false,
                    bottomImageError = false
                )
            }
        }
    }

    fun onImageLoadSuccess(isTop: Boolean) {
        val current = _uiState.value
        if (isTop) {
            _uiState.value = current.copy(topImageLoaded = true, topImageError = false)
        } else {
            _uiState.value = current.copy(bottomImageLoaded = true, bottomImageError = false)
        }
        checkBothImagesLoaded()
    }

    fun onImageLoadError(isTop: Boolean) {
        val current = _uiState.value
        if (isTop) {
            _uiState.value = current.copy(topImageError = true, topImageLoaded = true)
        } else {
            _uiState.value = current.copy(bottomImageError = true, bottomImageLoaded = true)
        }
        checkBothImagesLoaded()
    }

    private fun checkBothImagesLoaded() {
        val current = _uiState.value
        if (current.topImageLoaded && current.bottomImageLoaded && current.isLoading) {
            _uiState.value = current.copy(isLoading = false)
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
        private const val MAX_IMAGE_ID = 400
        private const val SUPABASE_STORAGE_URL = "https://vxqxbbkokdmxgirkhttc.supabase.co/storage/v1/object/public/images"
    }
}
