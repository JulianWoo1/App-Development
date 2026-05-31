package com.example.realitycheck.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.realitycheck.ui.components.XpProgressBar
import com.example.realitycheck.ui.game.GameMode

private val BgDeep      = Color(0xFF050505)
private val CardBg      = Color(0xFF0D0D0D)
private val ButtonPurple = Color(0xFF5B2EFF)
private val TextMuted   = Color(0xFFA0A0A0)

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onStartGame: (GameMode) -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDeep)
            .padding(horizontal = 24.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // ── Header ──────────────────────────────────────────────────────────
        Text(
            text = "RealityCheck",
            color = Color.White,
            fontSize = 28.sp,
            fontWeight = FontWeight.ExtraBold
        )

        Text(
            text = "Kun jij echt van AI onderscheiden?",
            color = TextMuted,
            fontSize = 13.sp,
            modifier = Modifier.padding(top = 4.dp, bottom = 24.dp)
        )

        // ── XP Progress card ─────────────────────────────────────────────────
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = CardBg)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = state.profile?.username ?: "Speler",
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp
                    )
                    Text(
                        text = "Beste streak: ${state.profile?.highScoreStreak ?: 0}",
                        color = TextMuted,
                        fontSize = 12.sp
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (state.isLoading) {
                    LinearProgressIndicator(
                        modifier = Modifier.fillMaxWidth().height(12.dp),
                        color = ButtonPurple,
                        trackColor = Color(0xFF1F1F2E)
                    )
                } else {
                    XpProgressBar(
                        totalXp = state.profile?.totalXp ?: 0,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // ── Mode selection ───────────────────────────────────────────────────
        Text(
            text = "Kies een modus",
            color = TextMuted,
            fontSize = 13.sp,
            modifier = Modifier
                .align(Alignment.Start)
                .padding(bottom = 12.dp)
        )

        GameModeButton(
            label = "📷  Afbeeldingen",
            description = "Echt of AI-gegenereerd?",
            onClick = { onStartGame(GameMode.IMAGE) }
        )

        Spacer(modifier = Modifier.height(12.dp))

        GameModeButton(
            label = "💬  Tekst",
            description = "Menselijk of machinaal?",
            onClick = { onStartGame(GameMode.TEXT) }
        )

        Spacer(modifier = Modifier.weight(1f))

        state.error?.let {
            Text(text = it, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
        }
    }
}

@Composable
private fun GameModeButton(
    label: String,
    description: String,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(containerColor = ButtonPurple)
    ) {
        Column(horizontalAlignment = Alignment.Start, modifier = Modifier.fillMaxWidth()) {
            Text(label, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
            Text(description, fontSize = 11.sp, color = Color.White.copy(alpha = 0.7f))
        }
    }
}