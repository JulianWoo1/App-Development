package com.example.realitycheck.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.realitycheck.ui.theme.Primary

private val CardBg = Color(0xFF0D0D0D)
private val ButtonPurple = Color(0xFF5B2EFF)
private val SubtitleGray = Color(0xFF9A9A9A)
private val WelcomeGray = Color(0xFF8E8E93)
private val ProgressBg = Color(0xFF252525)
private val PurpleCircle = Color(0xFF5B2EFF)

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onStartGame: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF050505))
    ) {
        if (state.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = ButtonPurple
            )
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp)
            ) {
                // Welcome
                Text(
                    text = "Welcome Back",
                    color = WelcomeGray,
                    style = MaterialTheme.typography.bodyMedium
                )

                // Name
                Text(
                    text = state.displayName,
                    color = Color.White,
                    style = MaterialTheme.typography.headlineLarge,
                    modifier = Modifier.padding(top = 4.dp)
                )

                // Subtitle
                Text(
                    text = "Can you differentiate real from AI?",
                    color = SubtitleGray,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(top = 4.dp)
                )

                // Level Card
                LevelCard(
                    level = state.level,
                    xpInCurrent = state.xpInCurrentLevel,
                    xpForNext = state.xpForNextLevel,
                    streak = state.highScoreStreak,
                    onStartGame = onStartGame,
                    modifier = Modifier.padding(top = 28.dp)
                )

                // Stats Row
                StatsRow(
                    modifier = Modifier.padding(top = 18.dp)
                )
            }
        }
    }
}

@Composable
private fun LevelCard(
    level: Int,
    xpInCurrent: Int,
    xpForNext: Int,
    streak: Int,
    onStartGame: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Robot emoji in purple circle
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .background(PurpleCircle, RoundedCornerShape(26.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "\uD83E\uDD16", style = MaterialTheme.typography.titleLarge)
                }

                // Level info
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 14.dp)
                ) {
                    Text(
                        text = "Level $level",
                        color = Color.White,
                        style = MaterialTheme.typography.titleLarge
                    )

                    // Progress bar
                    val progress = if (xpForNext > 0) xpInCurrent.toFloat() / xpForNext.toFloat() else 0f
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .padding(top = 10.dp),
                        trackColor = ProgressBg,
                        color = ButtonPurple
                    )

                    Text(
                        text = "$xpInCurrent / $xpForNext XP",
                        color = SubtitleGray,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                // Streak
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "\uD83D\uDD25", style = MaterialTheme.typography.bodyLarge)
                    Text(
                        text = streak.toString(),
                        color = Color.White,
                        style = MaterialTheme.typography.titleLarge
                    )
                    Text(
                        text = "streak",
                        color = WelcomeGray,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            // Play Button
            Button(
                onClick = onStartGame,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .padding(top = 22.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ButtonPurple),
                shape = RoundedCornerShape(20.dp)
            ) {
                Text(
                    text = "Play Now \u2192",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
private fun StatsRow(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        StatCard(
            emoji = "\uD83C\uDFAF",
            value = "68%",
            label = "Accurate",
            modifier = Modifier.weight(1f)
        )
        StatCard(
            emoji = "\uD83C\uDFC6",
            value = "#14",
            label = "Best rank",
            modifier = Modifier.weight(1f)
        )
        StatCard(
            emoji = "\u26A1",
            value = "1.2s",
            label = "Fastest",
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun StatCard(
    emoji: String,
    value: String,
    label: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.height(120.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = emoji, style = MaterialTheme.typography.titleMedium)
            Text(
                text = value,
                color = Color.White,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(top = 10.dp)
            )
            Text(
                text = label,
                color = WelcomeGray,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
