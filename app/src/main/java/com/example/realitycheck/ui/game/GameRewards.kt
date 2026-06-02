package com.example.realitycheck.ui.game

object GameRewards {

    const val CORRECT_ANSWER_XP = 10

    fun streakBonus(streak: Int): Int {
        return when {
            streak >= 30 -> 30
            streak >= 20 -> 20
            streak >= 10 -> 10
            else -> 0
        }
    }

    fun difficultyBonus(difficulty: Int): Int {
        return difficulty * 5
    }
}