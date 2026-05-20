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

        // Wait until first round is ready
        advanceUntilIdle()

        // If not ONE_REAL, keep generating new rounds
        while (viewModel.uiState.value.roundType != RoundType.ONE_REAL) {
            viewModel.loadNextRound()
            advanceUntilIdle()
        }

        val state = viewModel.uiState.value

        // Pick correct answer based on real ViewModel logic
        val isTopCorrect = state.isCorrectTop

        // ACT: simulate user selection
        viewModel.onSelect(isTopCorrect)
        advanceUntilIdle()

        // ASSERT: streak increased
        assertEquals(1, viewModel.currentStreak.value)
    }
}