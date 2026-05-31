package com.example.realitycheck.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.realitycheck.data.di.SupabaseModule
import com.example.realitycheck.ui.components.XpProgressBar
import com.example.realitycheck.ui.game.LevelSystem

private val BgDeep       = Color(0xFF050505)
private val CardBg       = Color(0xFF0D0D0D)
private val AccentPurple = Color(0xFF5B2EFF)
private val TextMuted    = Color(0xFFA0A0A0)

@Composable
fun ProfileScreen() {

    val viewModel: ProfileViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T =
                ProfileViewModel(SupabaseModule.profileRepository) as T
        }
    )

    val state by viewModel.uiState.collectAsState()
    val profile = state.profile

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDeep)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(modifier = Modifier.height(24.dp))

        // ── Avatar placeholder ────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .size(80.dp)
                .background(
                    Brush.radialGradient(listOf(Color(0xFF9B6DFF), AccentPurple)),
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = profile?.username?.take(1)?.uppercase() ?: "?",
                color = Color.White,
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = profile?.username ?: "Laden…",
            color = Color.White,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(4.dp))

        profile?.let {
            Text(
                text = LevelSystem.levelTitle(LevelSystem.levelFromXp(it.totalXp)),
                color = TextMuted,
                fontSize = 13.sp
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ── Level + XP card ──────────────────────────────────────────────────
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = CardBg)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Level voortgang", color = Color.White, fontWeight = FontWeight.SemiBold)
                    profile?.let {
                        val level = LevelSystem.levelFromXp(it.totalXp)
                        Text(
                            text = "Level $level",
                            color = AccentPurple,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (state.isLoading) {
                    LinearProgressIndicator(
                        modifier = Modifier.fillMaxWidth().height(12.dp),
                        color = AccentPurple,
                        trackColor = Color(0xFF1F1F2E)
                    )
                } else {
                    XpProgressBar(
                        totalXp = profile?.totalXp ?: 0,
                        modifier = Modifier.fillMaxWidth(),
                        barHeight = 14.dp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ── Stats row ────────────────────────────────────────────────────────
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatCard(
                label = "Totaal XP",
                value = "${profile?.totalXp ?: 0}",
                modifier = Modifier.weight(1f)
            )
            StatCard(
                label = "Beste streak",
                value = "${profile?.highScoreStreak ?: 0}",
                modifier = Modifier.weight(1f)
            )
        }

        state.error?.let {
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = it, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
        }
    }
}

@Composable
private fun StatCard(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Text(
                text = label,
                color = TextMuted,
                fontSize = 11.sp
            )
        }
    }
}