package com.example.realitycheck.ui.badges

import com.example.realitycheck.ui.game.GameMode
import java.time.LocalDateTime
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class BadgeConditionTest {

    private val streaker = StreakerCondition()
    private val nightowl = NightOwlCondition()
    private val speeddemon = SpeedDemonCondition()

    @Test
    fun `streaker earned at streak 20`() {
        val ctx = BadgeEvaluationContext(streak = 20, gameMode = GameMode.IMAGE)
        assertTrue(streaker.evaluate(ctx))
    }

    @Test
    fun `streaker earned at streak above 20`() {
        val ctx = BadgeEvaluationContext(streak = 25, gameMode = GameMode.TEXT)
        assertTrue(streaker.evaluate(ctx))
    }

    @Test
    fun `streaker not earned below streak 20`() {
        val ctx = BadgeEvaluationContext(streak = 19, gameMode = GameMode.IMAGE)
        assertFalse(streaker.evaluate(ctx))
    }

    @Test
    fun `streaker not earned at streak 0`() {
        val ctx = BadgeEvaluationContext(streak = 0, gameMode = GameMode.IMAGE)
        assertFalse(streaker.evaluate(ctx))
    }

    @Test
    fun `nightowl earned at midnight`() {
        val ctx = BadgeEvaluationContext(
            streak = 0, gameMode = GameMode.IMAGE,
            playedAt = LocalDateTime.of(2026, 1, 1, 0, 0)
        )
        assertTrue(nightowl.evaluate(ctx))
    }

    @Test
    fun `nightowl earned at 5am`() {
        val ctx = BadgeEvaluationContext(
            streak = 0, gameMode = GameMode.IMAGE,
            playedAt = LocalDateTime.of(2026, 1, 1, 5, 59)
        )
        assertTrue(nightowl.evaluate(ctx))
    }

    @Test
    fun `nightowl not earned at 6am`() {
        val ctx = BadgeEvaluationContext(
            streak = 0, gameMode = GameMode.IMAGE,
            playedAt = LocalDateTime.of(2026, 1, 1, 6, 0)
        )
        assertFalse(nightowl.evaluate(ctx))
    }

    @Test
    fun `nightowl not earned at noon`() {
        val ctx = BadgeEvaluationContext(
            streak = 0, gameMode = GameMode.IMAGE,
            playedAt = LocalDateTime.of(2026, 1, 1, 12, 0)
        )
        assertFalse(nightowl.evaluate(ctx))
    }

    @Test
    fun `nightowl not earned at 11pm`() {
        val ctx = BadgeEvaluationContext(
            streak = 0, gameMode = GameMode.IMAGE,
            playedAt = LocalDateTime.of(2026, 1, 1, 23, 0)
        )
        assertFalse(nightowl.evaluate(ctx))
    }

    @Test
    fun `speeddemon earned in speed mode at streak 10`() {
        val ctx = BadgeEvaluationContext(streak = 10, gameMode = GameMode.SPEED)
        assertTrue(speeddemon.evaluate(ctx))
    }

    @Test
    fun `speeddemon earned in speed mode above streak 10`() {
        val ctx = BadgeEvaluationContext(streak = 15, gameMode = GameMode.SPEED)
        assertTrue(speeddemon.evaluate(ctx))
    }

    @Test
    fun `speeddemon not earned in speed mode below streak 10`() {
        val ctx = BadgeEvaluationContext(streak = 9, gameMode = GameMode.SPEED)
        assertFalse(speeddemon.evaluate(ctx))
    }

    @Test
    fun `speeddemon not earned in image mode at streak 10`() {
        val ctx = BadgeEvaluationContext(streak = 10, gameMode = GameMode.IMAGE)
        assertFalse(speeddemon.evaluate(ctx))
    }

    @Test
    fun `speeddemon not earned in text mode at streak 10`() {
        val ctx = BadgeEvaluationContext(streak = 10, gameMode = GameMode.TEXT)
        assertFalse(speeddemon.evaluate(ctx))
    }
}
