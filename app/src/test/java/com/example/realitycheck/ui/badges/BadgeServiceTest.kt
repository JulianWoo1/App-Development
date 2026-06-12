package com.example.realitycheck.ui.badges

import com.example.realitycheck.data.repository.FakeBadgeRepository
import com.example.realitycheck.ui.game.GameMode
import java.time.LocalDateTime
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class BadgeServiceTest {

    private lateinit var badgeRepo: FakeBadgeRepository
    private lateinit var service: BadgeService

    @Before
    fun setup() {
        badgeRepo = FakeBadgeRepository()
        service = BadgeService(badgeRepo)
    }

    @Test
    fun `no badges awarded when no conditions met`() = runTest {
        val ctx = BadgeEvaluationContext(streak = 0, gameMode = GameMode.IMAGE)

        val result = service.checkAndAwardBadges(ctx)

        assertTrue(result.isSuccess)
        assertEquals(emptyList<Any>(), result.getOrNull())
        assertTrue(badgeRepo.earnedBadgeCalls.isEmpty())
    }

    @Test
    fun `awards streaker badge at streak 20`() = runTest {
        val ctx = BadgeEvaluationContext(streak = 20, gameMode = GameMode.IMAGE)

        val result = service.checkAndAwardBadges(ctx)

        assertEquals(listOf("streaker"), result.getOrNull()?.map { it.id })
        assertEquals(listOf("streaker"), badgeRepo.earnedBadgeCalls)
    }

    @Test
    fun `awards nightowl badge between 00 and 06`() = runTest {
        val ctx = BadgeEvaluationContext(
            streak = 0, gameMode = GameMode.IMAGE,
            playedAt = LocalDateTime.of(2026, 1, 1, 3, 0)
        )

        val result = service.checkAndAwardBadges(ctx)

        assertEquals(listOf("nightowl"), result.getOrNull()?.map { it.id })
        assertEquals(listOf("nightowl"), badgeRepo.earnedBadgeCalls)
    }

    @Test
    fun `does not award nightowl badge at 6am`() = runTest {
        val ctx = BadgeEvaluationContext(
            streak = 0, gameMode = GameMode.IMAGE,
            playedAt = LocalDateTime.of(2026, 1, 1, 6, 0)
        )

        val result = service.checkAndAwardBadges(ctx)

        assertTrue(result.isSuccess)
        assertTrue(badgeRepo.earnedBadgeCalls.isEmpty())
    }

    @Test
    fun `awards speeddemon badge in speed mode at streak 10`() = runTest {
        val ctx = BadgeEvaluationContext(streak = 10, gameMode = GameMode.SPEED)

        val result = service.checkAndAwardBadges(ctx)

        assertEquals(listOf("speeddemon"), result.getOrNull()?.map { it.id })
        assertEquals(listOf("speeddemon"), badgeRepo.earnedBadgeCalls)
    }

    @Test
    fun `awards multiple badges when all conditions met`() = runTest {
        val ctx = BadgeEvaluationContext(
            streak = 20, gameMode = GameMode.SPEED,
            playedAt = LocalDateTime.of(2026, 1, 1, 0, 0)
        )

        val result = service.checkAndAwardBadges(ctx)

        assertEquals(setOf("streaker", "nightowl", "speeddemon"), result.getOrNull()?.map { it.id }?.toSet())
        assertEquals(setOf("streaker", "nightowl", "speeddemon"), badgeRepo.earnedBadgeCalls.toSet())
    }

    @Test
    fun `does not re-award already earned badge`() = runTest {
        badgeRepo.userBadges.add(
            com.example.realitycheck.data.model.UserBadge(
                userId = "test-user", badgeId = "streaker", earnedAt = "earlier"
            )
        )

        val ctx = BadgeEvaluationContext(streak = 20, gameMode = GameMode.IMAGE)

        val result = service.checkAndAwardBadges(ctx)

        assertTrue(result.isSuccess)
        assertTrue(badgeRepo.earnedBadgeCalls.isEmpty())
    }

    @Test
    fun `awards only new badges when some already earned`() = runTest {
        badgeRepo.userBadges.add(
            com.example.realitycheck.data.model.UserBadge(
                userId = "test-user", badgeId = "streaker", earnedAt = "earlier"
            )
        )

        val ctx = BadgeEvaluationContext(
            streak = 20, gameMode = GameMode.SPEED,
            playedAt = LocalDateTime.of(2026, 1, 1, 3, 0)
        )

        val result = service.checkAndAwardBadges(ctx)

        assertEquals(setOf("nightowl", "speeddemon"), result.getOrNull()?.map { it.id }?.toSet())
        assertEquals(setOf("nightowl", "speeddemon"), badgeRepo.earnedBadgeCalls.toSet())
    }

    @Test
    fun `skips awarding when repository errors`() = runTest {
        badgeRepo.shouldFail = true
        val ctx = BadgeEvaluationContext(streak = 20, gameMode = GameMode.IMAGE)

        val result = service.checkAndAwardBadges(ctx)

        assertTrue(result.isSuccess)
        assertEquals(emptyList<Any>(), result.getOrNull())
        assertTrue(badgeRepo.earnedBadgeCalls.isEmpty())
    }
}
