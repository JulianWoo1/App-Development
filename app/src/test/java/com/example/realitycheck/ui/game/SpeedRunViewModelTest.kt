package com.example.realitycheck.ui.game

import com.example.realitycheck.data.model.Profile
import com.example.realitycheck.data.repository.FakeAuthRepository
import com.example.realitycheck.data.repository.FakeContentRepository
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
class SpeedRunViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var authRepo: FakeAuthRepository
    private lateinit var profileRepo: FakeProfileRepository
    private lateinit var contentRepo: FakeContentRepository

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        authRepo = FakeAuthRepository()
        profileRepo = FakeProfileRepository(authRepo)
        contentRepo = FakeContentRepository()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel(): SpeedRunViewModel {
        authRepo.storedUserId = "test-user"
        profileRepo.profiles["test-user"] = Profile(id = "test-user")
        val vm = SpeedRunViewModel(profileRepo, contentRepo)
        vm.onGameOverDismissed()
        return vm
    }

    private fun forceRoundType(viewModel: SpeedRunViewModel, type: RoundType) {
        while (viewModel.uiState.value.roundType != type) {
            viewModel.loadNextRound()
        }
    }

    @Test
    fun `correct count starts at 0`() = runTest(testDispatcher) {
        val viewModel = createViewModel()
        assertEquals(0, viewModel.currentCorrectCount.value)
    }

    @Test
    fun `correct answer increases correct count`() = runTest(testDispatcher) {
        val viewModel = createViewModel()

        forceRoundType(viewModel, RoundType.ONE_REAL)
        viewModel.onSelect(viewModel.uiState.value.isCorrectTop)
        advanceUntilIdle()

        assertEquals(1, viewModel.currentCorrectCount.value)
    }

    @Test
    fun `wrong answer does not increase correct count`() = runTest(testDispatcher) {
        val viewModel = createViewModel()

        forceRoundType(viewModel, RoundType.ONE_REAL)
        viewModel.onSelect(!viewModel.uiState.value.isCorrectTop)
        advanceUntilIdle()

        assertEquals(0, viewModel.currentCorrectCount.value)
    }

    @Test
    fun `multiple correct answers increase count incrementally`() = runTest(testDispatcher) {
        val viewModel = createViewModel()

        forceRoundType(viewModel, RoundType.ONE_REAL)
        viewModel.onSelect(viewModel.uiState.value.isCorrectTop)
        advanceUntilIdle()
        assertEquals(1, viewModel.currentCorrectCount.value)

        forceRoundType(viewModel, RoundType.ONE_REAL)
        viewModel.onSelect(viewModel.uiState.value.isCorrectTop)
        advanceUntilIdle()
        assertEquals(2, viewModel.currentCorrectCount.value)

        forceRoundType(viewModel, RoundType.ONE_REAL)
        viewModel.onSelect(viewModel.uiState.value.isCorrectTop)
        advanceUntilIdle()
        assertEquals(3, viewModel.currentCorrectCount.value)
    }
}
