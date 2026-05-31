package com.example.realitycheck.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.realitycheck.ui.game.LevelSystem

// ── Palette (matches the app's dark purple theme) ────────────────────────────
private val XpBarStart  = Color(0xFF5B2EFF)   // vivid purple
private val XpBarEnd    = Color(0xFF9B6DFF)   // soft lavender
private val TrackColor  = Color(0xFF1F1F2E)
private val LevelBadgeBg = Color(0xFF2A1A6E)

/**
 * Compact XP progress bar widget.
 *
 * Shows:
 *  • A small circular level badge on the left
 *  • A gradient fill bar that animates to [totalXp]
 *  • "X XP to level N+1" label on the right
 *
 * @param totalXp   The player's current cumulative XP.
 * @param modifier  Optional outer modifier.
 * @param barHeight Height of the progress track.
 */
@Composable
fun XpProgressBar(
    totalXp: Int,
    modifier: Modifier = Modifier,
    barHeight: Dp = 12.dp
) {
    val level    = LevelSystem.levelFromXp(totalXp)
    val fraction = LevelSystem.progressFraction(totalXp)
    val toNext   = LevelSystem.xpToNextLevel(totalXp)

    // Animate the bar fill whenever totalXp changes
    val animatedFraction by animateFloatAsState(
        targetValue = fraction,
        animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing),
        label = "xpBarProgress"
    )

    Column(modifier = modifier) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            // ── Level badge ──────────────────────────────────────────────────
            LevelBadge(level = level)

            Spacer(modifier = Modifier.width(10.dp))

            // ── Bar track + fill ─────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(barHeight)
                    .clip(RoundedCornerShape(50))
                    .background(TrackColor)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(animatedFraction)
                        .clip(RoundedCornerShape(50))
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(XpBarStart, XpBarEnd)
                            )
                        )
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            // ── "X XP" label ─────────────────────────────────────────────────
            Text(
                text = "$toNext XP",
                color = Color(0xFFA0A0C0),
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
            )
        }

        // Subtle sub-label: current XP and level title
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 42.dp, top = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = LevelSystem.levelTitle(level),
                color = Color(0xFF6A6A9A),
                fontSize = 10.sp
            )
            Text(
                text = "$totalXp XP totaal",
                color = Color(0xFF6A6A9A),
                fontSize = 10.sp
            )
        }
    }
}

/**
 * Compact circular badge showing the current level number.
 */
@Composable
fun LevelBadge(
    level: Int,
    size: Dp = 32.dp
) {
    Box(
        modifier = Modifier
            .size(size)
            .background(
                Brush.radialGradient(listOf(XpBarEnd, LevelBadgeBg)),
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "$level",
            color = Color.White,
            fontSize = (size.value * 0.36f).sp,
            fontWeight = FontWeight.ExtraBold,
            lineHeight = (size.value * 0.36f).sp
        )
    }
}