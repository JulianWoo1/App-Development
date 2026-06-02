package com.example.realitycheck.ui.game

/**
 * Pure XP → Level conversion logic.
 *
 * Progression curve: each level requires progressively more XP.
 *   Level 1  :     0 XP  (starting level)
 *   Level 2  :   100 XP
 *   Level 3  :   250 XP
 *   Level 4  :   450 XP
 *   Level n  :  previous + (n-1) * 100
 *
 * This gives a satisfying early ramp that slows at higher levels.
 */
object LevelSystem {

    /** XP required to *reach* the given level (level 1 = 0 XP). */
    fun xpForLevel(level: Int): Int {
        if (level <= 1) return 0
        // Sum of 100 + 150 + 200 + … for each step
        // step k costs k * 100, starting at k=1 for level 2
        var total = 0
        for (k in 1 until level) {
            total += k * 100
        }
        return total
    }

    /** The level a player is at for a given total XP amount. */
    fun levelFromXp(totalXp: Int): Int {
        var level = 1
        while (xpForLevel(level + 1) <= totalXp) {
            level++
        }
        return level
    }

    /**
     * Progress within the current level, as a fraction 0f..1f.
     * e.g. halfway between level 3 and 4 → 0.5f
     */
    fun progressFraction(totalXp: Int): Float {
        val level = levelFromXp(totalXp)
        val currentLevelXp = xpForLevel(level)
        val nextLevelXp = xpForLevel(level + 1)
        val span = (nextLevelXp - currentLevelXp).coerceAtLeast(1)
        return ((totalXp - currentLevelXp).toFloat() / span).coerceIn(0f, 1f)
    }

    /** XP still needed to reach the next level. */
    fun xpToNextLevel(totalXp: Int): Int {
        val level = levelFromXp(totalXp)
        return xpForLevel(level + 1) - totalXp
    }

    /** Human-readable title for a level tier. */
    fun levelTitle(level: Int): String = when {
        level >= 50 -> "Legende"
        level >= 30 -> "Expert"
        level >= 20 -> "Gevorderd"
        level >= 10 -> "Leerling"
        else        -> "Beginner"
    }
}