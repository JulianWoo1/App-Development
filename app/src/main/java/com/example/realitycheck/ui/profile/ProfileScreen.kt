package com.example.realitycheck.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.realitycheck.data.di.SupabaseModule
import com.example.realitycheck.data.model.GameSession
import com.example.realitycheck.ui.components.XpProgressBar
import java.time.DayOfWeek
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

private val Background = Color(0xFF050505)
private val CardBg = Color(0xFF0D0D0D)
private val Purple = Color(0xFF5B2EFF)
private val WelcomeGray = Color(0xFF8E8E93)
private val Gold = Color(0xFFD4AF37)
private val Green = Color(0xFF6DDC6D)
private val Orange = Color(0xFFFF8A3D)

@Composable
fun ProfileScreen(
    onOpenSettings: () -> Unit
) {

    val viewModel: ProfileViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return ProfileViewModel(
                    SupabaseModule.profileRepository,
                    SupabaseModule.authRepository,
                    SupabaseModule.badgeRepository,
                    SupabaseModule.gameSessionRepository
                ) as T
            }
        }
    )

    val state by viewModel.uiState.collectAsState()
    val profile = state.profile

    LaunchedEffect(Unit) { viewModel.loadProfile() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // ── HEADER ─────────────────────────────
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {

            Box(
                modifier = Modifier
                    .size(74.dp)
                    .background(Color(0xFF120D2E), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = profile?.username?.firstOrNull()?.uppercase() ?: "?",
                    style = MaterialTheme.typography.headlineMedium
                )

                // LEVEL BADGE
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .offset(x = 4.dp, y = 4.dp)
                        .size(28.dp)
                        .background(Gold, RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${state.level}",
                        color = Color.White,
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {

                Text(
                    text = profile?.username ?: "Player",
                    color = Color.White,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "AI Detection Specialist",
                    color = WelcomeGray,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(top = 4.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {

                    ProfileBadge(
                        emoji = "🔥",
                        text = "${profile?.highScoreStreak ?: 0} streak",
                        color = Orange
                    )

                    ProfileBadge(
                        emoji = "🏆",
                        text = "#${state.rank} rank",
                        color = Gold
                    )
                }
            }

            IconButton(
                onClick = onOpenSettings
            ) {
                Icon(
                    imageVector = Icons.Outlined.Settings,
                    contentDescription = null,
                    tint = WelcomeGray
                )
            }
        }

        Spacer(modifier = Modifier.height(26.dp))

        // ── XP CARD ─────────────────────────────
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = CardBg)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {

                XpProgressBar(
                    totalXp = state.totalXp,
                    modifier = Modifier.fillMaxWidth()
                )

                Text(
                    text = "${state.xpNeeded} XP needed for next level",
                    color = WelcomeGray,
                    modifier = Modifier.padding(top = 12.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // ── STATS ─────────────────────────────
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            ProfileStatCard("🎮", "${state.gamesPlayed}", "Played", Purple, Modifier.weight(1f))
            ProfileStatCard("🏆", "${state.avgStreak}", "Avg streak", Green, Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            ProfileStatCard("🔥", "${profile?.highScoreStreak ?: 0}", "Best streak", Orange, Modifier.weight(1f))
            ProfileStatCard("🎖", "${state.badges}", "Badges", Gold, Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(18.dp))

        WeeklyActivityCard(state.weeklySessions)

        Spacer(modifier = Modifier.height(18.dp))

        RecentGamesCard(state.recentSessions)
    }
}

@Composable
private fun WeeklyActivityCard(sessions: List<GameSession>) {
    val now = ZonedDateTime.now(ZoneId.systemDefault())
    val dayCounts = (0..6).map { offset ->
        val day = now.minusDays(6 - offset.toLong())
        val dateStr = day.format(DateTimeFormatter.ISO_LOCAL_DATE)
        val count = sessions.count { s ->
            try {
                s.playedAt.startsWith(dateStr)
            } catch (_: Exception) { false }
        }
        day.dayOfWeek to count
    }

    val maxCount = dayCounts.maxOf { it.second }.coerceAtLeast(1)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "This week",
                color = Color.White,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                dayCounts.forEach { (day, count) ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.weight(1f)
                    ) {
                        val barHeight = (count.toFloat() / maxCount * 80).coerceAtLeast(4f).dp
                        val isToday = day == now.dayOfWeek

                        Box(
                            modifier = Modifier
                                .width(12.dp)
                                .height(barHeight)
                                .background(
                                    if (count > 0) Purple.copy(alpha = 0.7f) else Color(0xFF2A2A2A),
                                    RoundedCornerShape(6.dp)
                                )
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = day.name.take(2),
                            color = if (isToday) Purple else WelcomeGray,
                            style = MaterialTheme.typography.labelSmall
                        )
                        Text(
                            text = "$count",
                            color = if (count > 0) Color.White else WelcomeGray,
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RecentGamesCard(sessions: List<GameSession>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "Recent games",
                color = Color.White,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(12.dp))

            if (sessions.isEmpty()) {
                Text(
                    text = "No games played yet",
                    color = WelcomeGray,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(vertical = 16.dp)
                )
            } else {
                sessions.forEach { session ->
                    RecentGameRow(session)
                    if (session != sessions.last()) {
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun RecentGameRow(session: GameSession) {
    val modeEmoji = when (session.gameMode) {
        "SPEED" -> "⚡"
        "TEXT" -> "✏️"
        else -> "📷"
    }

    val timeAgo = try {
        val played = ZonedDateTime.parse(session.playedAt, DateTimeFormatter.ISO_DATE_TIME)
        val now = ZonedDateTime.now()
        val minutes = ChronoUnit.MINUTES.between(played, now)
        when {
            minutes < 1 -> "just now"
            minutes < 60 -> "${minutes}m ago"
            minutes < 1440 -> "${minutes / 60}h ago"
            else -> "${minutes / 1440}d ago"
        }
    } catch (_: Exception) { "" }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(modeEmoji, style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Streak ${session.streak}",
                color = Color.White,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "+${session.xpEarned} XP",
                color = WelcomeGray,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Text(
            text = timeAgo,
            color = WelcomeGray,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
private fun ProfileBadge(emoji: String, text: String, color: Color) {
    Box(
        modifier = Modifier
            .background(color.copy(alpha = 0.12f), RoundedCornerShape(14.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Row {
            Text(emoji)
            Spacer(modifier = Modifier.width(6.dp))
            Text(text, color = color)
        }
    }
}

@Composable
private fun ProfileStatCard(
    emoji: String,
    value: String,
    label: String,
    valueColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.height(110.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(emoji)
            Column {
                Text(value, color = valueColor, fontWeight = FontWeight.Bold)
                Text(label, color = WelcomeGray)
            }
        }
    }
}