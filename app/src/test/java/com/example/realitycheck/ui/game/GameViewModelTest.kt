package com.example.realitycheck.ui.game

import com.example.realitycheck.data.model.Profile
import com.example.realitycheck.data.repository.FakeAuthRepository
import com.example.realitycheck.data.repository.FakeBadgeRepository
import com.example.realitycheck.data.repository.FakeContentRepository
import com.example.realitycheck.data.repository.FakeProfileRepository
import com.example.realitycheck.ui.badges.BadgeService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class GameViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var authRepo: FakeAuthRepository
    private lateinit var profileRepo: FakeProfileRepository
    private lateinit var contentRepo: FakeContentRepository
    private lateinit var badgeRepo: FakeBadgeRepository

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        authRepo = FakeAuthRepository()
        profileRepo = FakeProfileRepository(authRepo)
        contentRepo = FakeContentRepository()
        badgeRepo = FakeBadgeRepository()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel(): GameViewModel {
        authRepo.storedUserId = "test-user"
        profileRepo.profiles["test-user"] =
            Profile(id = "test-user")

        return GameViewModel(
            profileRepository = profileRepo,
            contentRepository = contentRepo,
            badgeService = BadgeService(badgeRepo)
        )
    }

    private suspend fun forceRoundType(viewModel: GameViewModel, type: RoundType, scope: TestScope) {
        while (viewModel.uiState.value.roundType != type) {
            viewModel.loadNextRound()
            scope.advanceUntilIdle()
        }
    }

    @Test
    fun `streak starts at 0`() = runTest(testDispatcher) {
        val viewModel = createViewModel()
        advanceUntilIdle()
        assertEquals(0, viewModel.currentStreak.value)
    }

    @Test
    fun `correct guess increases streak`() = runTest(testDispatcher) {
        val viewModel = createViewModel()
        advanceUntilIdle()

        forceRoundType(viewModel, RoundType.ONE_REAL, this)

        val isTopCorrect = viewModel.uiState.value.isCorrectTop
        viewModel.onSelect(isTopCorrect)
        advanceUntilIdle()

        assertEquals(1, viewModel.currentStreak.value)
    }

    @Test
    fun `wrong guess does not increase streak`() = runTest(testDispatcher) {
        val viewModel = createViewModel()
        advanceUntilIdle()

        forceRoundType(viewModel, RoundType.ONE_REAL, this)

        val isTopWrong = !viewModel.uiState.value.isCorrectTop
        viewModel.onSelect(isTopWrong)
        advanceUntilIdle()

        assertEquals(0, viewModel.currentStreak.value)
    }

    @Test
    fun `multiple correct guesses increase streak incrementally`() = runTest(testDispatcher) {
        val viewModel = createViewModel()
        advanceUntilIdle()

        forceRoundType(viewModel, RoundType.ONE_REAL, this)
        viewModel.onSelect(viewModel.uiState.value.isCorrectTop)
        advanceUntilIdle()
        assertEquals(1, viewModel.currentStreak.value)

        forceRoundType(viewModel, RoundType.ONE_REAL, this)
        viewModel.onSelect(viewModel.uiState.value.isCorrectTop)
        advanceUntilIdle()
        assertEquals(2, viewModel.currentStreak.value)

        forceRoundType(viewModel, RoundType.ONE_REAL, this)
        viewModel.onSelect(viewModel.uiState.value.isCorrectTop)
        advanceUntilIdle()
        assertEquals(3, viewModel.currentStreak.value)
    }

    @Test
    fun `wrong guess triggers game over`() = runTest(testDispatcher) {
        val viewModel = createViewModel()
        advanceUntilIdle()

        forceRoundType(viewModel, RoundType.ONE_REAL, this)

        val isTopWrong = !viewModel.uiState.value.isCorrectTop
        viewModel.onSelect(isTopWrong)
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.isGameOver)
    }

    @Test
    fun `wrong guess saves streak to highscore`() = runTest(testDispatcher) {
        val viewModel = createViewModel()
        advanceUntilIdle()

        forceRoundType(viewModel, RoundType.ONE_REAL, this)

        val isTopWrong = !viewModel.uiState.value.isCorrectTop
        viewModel.onSelect(isTopWrong)
        advanceUntilIdle()

        val savedStreak = profileRepo.profiles["test-user"]?.highScoreStreak
        assertEquals(0, savedStreak)
    }

    @Test
    fun `correct onBothAnswer for BOTH_AI increases streak`() = runTest(testDispatcher) {
        val viewModel = createViewModel()
        viewModel.setRulesMode(RulesMode.CHAOS)
        advanceUntilIdle()

        forceRoundType(viewModel, RoundType.BOTH_AI, this)

        viewModel.onBothAnswer(true)
        advanceUntilIdle()

        assertEquals(1, viewModel.currentStreak.value)
    }

    @Test
    fun `correct onBothAnswer for BOTH_REAL increases streak`() = runTest(testDispatcher) {
        val viewModel = createViewModel()
        viewModel.setRulesMode(RulesMode.CHAOS)
        advanceUntilIdle()

        forceRoundType(viewModel, RoundType.BOTH_REAL, this)

        viewModel.onBothAnswer(false)
        advanceUntilIdle()

        assertEquals(1, viewModel.currentStreak.value)
    }

    @Test
    fun `wrong answer triggers badge evaluation`() = runTest(testDispatcher) {
        val viewModel = createViewModel()
        advanceUntilIdle()

        forceRoundType(viewModel, RoundType.ONE_REAL, this)

        val isTopWrong = !viewModel.uiState.value.isCorrectTop
        viewModel.onSelect(isTopWrong)
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.isGameOver)
        assertTrue(badgeRepo.getUserBadgesCallCount > 0)
    }

    @Test
    fun `correct answer does not trigger badge evaluation`() = runTest(testDispatcher) {
        val viewModel = createViewModel()
        advanceUntilIdle()

        forceRoundType(viewModel, RoundType.ONE_REAL, this)

        val isTopCorrect = viewModel.uiState.value.isCorrectTop
        viewModel.onSelect(isTopCorrect)
        advanceUntilIdle()

        assertEquals(0, badgeRepo.getUserBadgesCallCount)
    }
}