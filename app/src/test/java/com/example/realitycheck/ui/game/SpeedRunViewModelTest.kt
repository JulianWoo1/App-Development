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
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SpeedRunViewModelTest {

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

    private fun createViewModel(): SpeedRunViewModel {
        authRepo.storedUserId = "test-user"
        profileRepo.profiles["test-user"] = Profile(id = "test-user")
        val vm = SpeedRunViewModel(
            profileRepository = profileRepo,
            contentRepository = contentRepo,
            badgeService = BadgeService(badgeRepo)
        )
        vm.onGameOverDismissed()
        return vm
    }

    @Test
    fun `streak starts at 0`() = runTest(testDispatcher) {
        val viewModel = createViewModel()
        assertEquals(0, viewModel.currentStreak.value)
    }

    @Test
    fun `correct answer increases streak`() = runTest(testDispatcher) {
        val viewModel = createViewModel()

        viewModel.onSelect(viewModel.uiState.value.isCorrectTop)
        advanceUntilIdle()

        assertEquals(1, viewModel.currentStreak.value)
    }

    @Test
    fun `wrong answer does not increase streak and ends game`() = runTest(testDispatcher) {
        val viewModel = createViewModel()

        viewModel.onSelect(!viewModel.uiState.value.isCorrectTop)
        advanceUntilIdle()

        assertEquals(0, viewModel.currentStreak.value)
        assertTrue(viewModel.uiState.value.isGameOver)
    }

    @Test
    fun `multiple correct answers increase streak incrementally`() = runTest(testDispatcher) {
        val viewModel = createViewModel()

        viewModel.onSelect(viewModel.uiState.value.isCorrectTop)
        advanceUntilIdle()
        assertEquals(1, viewModel.currentStreak.value)

        viewModel.onSelect(viewModel.uiState.value.isCorrectTop)
        advanceUntilIdle()
        assertEquals(2, viewModel.currentStreak.value)

        viewModel.onSelect(viewModel.uiState.value.isCorrectTop)
        advanceUntilIdle()
        assertEquals(3, viewModel.currentStreak.value)
    }

    @Test
    fun `wrong answer triggers game over immediately`() = runTest(testDispatcher) {
        val viewModel = createViewModel()

        viewModel.onSelect(!viewModel.uiState.value.isCorrectTop)
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.isGameOver)
    }

    @Test
    fun `wrong answer triggers badge evaluation`() = runTest(testDispatcher) {
        val viewModel = createViewModel()

        viewModel.onSelect(!viewModel.uiState.value.isCorrectTop)
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.isGameOver)
        assertTrue(badgeRepo.getUserBadgesCallCount > 0)
    }

    @Test
    fun `correct answer does not trigger badge evaluation`() = runTest(testDispatcher) {
        val viewModel = createViewModel()

        viewModel.onSelect(viewModel.uiState.value.isCorrectTop)
        advanceUntilIdle()

        assertEquals(1, viewModel.currentStreak.value)
        assertEquals(0, badgeRepo.getUserBadgesCallCount)
    }
}
