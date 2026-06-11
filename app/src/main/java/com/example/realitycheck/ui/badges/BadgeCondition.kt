package com.example.realitycheck.ui.badges

import com.example.realitycheck.ui.game.GameMode
import java.time.LocalDateTime

data class BadgeEvaluationContext(
    val streak: Int,
    val gameMode: GameMode,
    val playedAt: LocalDateTime = LocalDateTime.now()
)

interface BadgeCondition {
    val badgeId: String
    fun evaluate(context: BadgeEvaluationContext): Boolean
}

class StreakerCondition : BadgeCondition {
    override val badgeId = "streaker"
    override fun evaluate(context: BadgeEvaluationContext): Boolean =
        context.streak >= 20
}

class NightOwlCondition : BadgeCondition {
    override val badgeId = "nightowl"
    override fun evaluate(context: BadgeEvaluationContext): Boolean {
        val hour = context.playedAt.hour
        return hour in 0..5
    }
}

class SpeedDemonCondition : BadgeCondition {
    override val badgeId = "speeddemon"
    override fun evaluate(context: BadgeEvaluationContext): Boolean =
        context.gameMode == GameMode.SPEED && context.streak >= 10
}

object BadgeConditions {
    val defaults: List<BadgeCondition> = listOf(
        StreakerCondition(),
        NightOwlCondition(),
        SpeedDemonCondition()
    )
}
