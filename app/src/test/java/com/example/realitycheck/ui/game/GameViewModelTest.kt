package com.example.realitycheck.ui.game

import com.example.realitycheck.data.repository.FakeProfileRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class GameViewModelTest {
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `correct guess increases streak`() = runTest(testDispatcher) {
        val profileRepo = FakeProfileRepository()

        val viewModel = GameViewModel(profileRepo)

        // Keep loading until we get ONE_REAL round (1/3 chance each time)
        while (viewModel.uiState.value.roundType != RoundType.ONE_REAL) {
            viewModel.loadNextRound()
        }

        // Tap whichever image is the correct one
        viewModel.onImageSelected(isTop = viewModel.uiState.value.isCorrectImageTop)
        advanceUntilIdle()

        assertEquals(1, viewModel.currentStreak.value)
    }
}
