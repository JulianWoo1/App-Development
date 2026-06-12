package com.example.realitycheck.ui.scores

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.realitycheck.ui.game.LevelSystem

private val Background = Color(0xFF050505)
private val CardBg = Color(0xFF0D0D0D)
private val Purple = Color(0xFF5B2EFF)
private val SubtitleGray = Color(0xFF9A9A9A)
private val Gold = Color(0xFFD4AF37)
private val Silver = Color(0xFFC0C0C0)
private val Bronze = Color(0xFFCD7F32)
private val Orange = Color(0xFFFF8A3D)

@Composable
fun ScoresScreen(viewModel: ScoresViewModel) {
    val state by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Text(
            text = "Leaderboard",
            color = Color.White,
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Top players ranked by XP",
            color = SubtitleGray,
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        // ── Filter selector ─────────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(CardBg, RoundedCornerShape(12.dp))
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            listOf("All-time" to LeaderboardFilter.ALL_TIME, "Today" to LeaderboardFilter.TODAY)
                .forEach { (label, filterValue) ->
                    val selected = state.filter == filterValue
                    Button(
                        onClick = { viewModel.setFilter(filterValue) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (selected) Purple else Color.Transparent,
                            contentColor = if (selected) Color.White else SubtitleGray
                        ),
                        elevation = ButtonDefaults.buttonElevation(0.dp)
                    ) {
                        Text(
                            text = label,
                            fontSize = 13.sp,
                            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
                        )
                    }
                }
        }

        Spacer(modifier = Modifier.height(16.dp))

        when {
            state.isLoading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Purple)
                }
            }

            state.error != null -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = state.error ?: "An error occurred",
                            color = Color.Red,
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        TextButton(onClick = { viewModel.loadLeaderboard() }) {
                            Text("Try again", color = Purple)
                        }
                    }
                }
            }

            state.entries.isEmpty() -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = if (state.filter == LeaderboardFilter.TODAY)
                            "No scores yet today — be the first!"
                        else "No scores yet",
                        color = SubtitleGray,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            else -> {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    itemsIndexed(
                        items = state.entries,
                        key = { _, entry -> entry.rank }
                    ) { _, entry ->
                        LeaderboardRow(entry = entry)
                    }

                    if (state.hasMore) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                if (state.isLoadingMore) {
                                    CircularProgressIndicator(
                                        color = Purple,
                                        modifier = Modifier.size(24.dp)
                                    )
                                } else {
                                    TextButton(onClick = { viewModel.loadMore() }) {
                                        Text(
                                            "View more",
                                            color = Purple,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LeaderboardRow(entry: LeaderboardEntry) {
    val rankColor = when (entry.rank) {
        1 -> Gold
        2 -> Silver
        3 -> Bronze
        else -> SubtitleGray
    }

    val containerColor = if (entry.isMe) {
        Purple.copy(alpha = 0.15f)
    } else {
        CardBg
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // Rank
            Text(
                text = "#${entry.rank}",
                color = rankColor,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                modifier = Modifier.width(40.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Username
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = if (entry.isMe) "${entry.username} (You)" else entry.username,
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = LevelSystem.levelTitle(entry.level),
                    color = SubtitleGray,
                    fontSize = 11.sp
                )
            }

            // Level
            Box(
                modifier = Modifier.width(40.dp),
                contentAlignment = Alignment.Center
            ) {
                LevelBadge(level = entry.level)
            }

            // XP
            Box(
                modifier = Modifier.width(80.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Text(
                    text = "${formatXp(entry.totalXp)} XP",
                    color = Purple,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }

            // Streak
            Box(
                modifier = Modifier.width(50.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "\uD83D\uDD25",
                        fontSize = 14.sp
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    Text(
                        text = "${entry.highScoreStreak}",
                        color = Orange,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun LevelBadge(level: Int) {
    Box(
        modifier = Modifier
            .size(32.dp)
            .background(Purple.copy(alpha = 0.2f), RoundedCornerShape(8.dp)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "$level",
            color = Purple,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 12.sp
        )
    }
}

private fun formatXp(xp: Int): String {
    return when {
        xp >= 1_000 -> "${xp / 1_000}.${(xp % 1_000) / 100}k"
        else -> xp.toString()
    }
}