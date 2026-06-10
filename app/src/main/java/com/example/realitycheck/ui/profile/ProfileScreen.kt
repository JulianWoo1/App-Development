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
import com.example.realitycheck.ui.components.XpProgressBar

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
                    SupabaseModule.authRepository
                ) as T
            }
        }
    )

    val state by viewModel.uiState.collectAsState()
    val profile = state.profile

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
            ProfileStatCard("🏆", "${state.winRate}%", "Win rate", Green, Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            ProfileStatCard("🔥", "${profile?.highScoreStreak ?: 0}", "Best streak", Orange, Modifier.weight(1f))
            ProfileStatCard("🎖", "${state.badges}", "Badges", Gold, Modifier.weight(1f))
        }
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