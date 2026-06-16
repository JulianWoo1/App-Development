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
import com.example.realitycheck.ui.game.RulesMode
import com.example.realitycheck.audio.LocalSoundManager

private val BgDeep       = Color(0xFF050505)
private val CardBg       = Color(0xFF0D0D0D)
private val ButtonPurple = Color(0xFF5B2EFF)
private val TextMuted    = Color(0xFFA0A0A0)
private val WelcomeGray  = Color(0xFFB0B0B0)
private val SubtitleGray = Color(0xFF9A9A9A)
private val ToggleBg     = Color(0xFF12122A)

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onStartGame: (GameMode, RulesMode) -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    var selectedRules by remember { mutableStateOf(RulesMode.CLASSIC) }
    val sound = LocalSoundManager.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDeep)
            .padding(horizontal = 24.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = state.displayName,
            color = Color.White,
            fontSize = 28.sp,
            fontWeight = FontWeight.ExtraBold
        )
        Text(
            text = "Can you differentiate real from AI?",
            color = SubtitleGray,
            fontSize = 14.sp,
            modifier = Modifier.padding(top = 6.dp)
        )

        // ── XP card ──────────────────────────────────────────────────────────
        Card(
            modifier = Modifier.fillMaxWidth().padding(top = 24.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = CardBg)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = state.profile?.username ?: "Player",
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Highest streak: ${state.highScoreStreak}",
                        color = TextMuted,
                        fontSize = 12.sp
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                if (state.isLoading) {
                    LinearProgressIndicator(
                        modifier = Modifier.fillMaxWidth().height(10.dp),
                        color = ButtonPurple
                    )
                } else {
                    XpProgressBar(
                        totalXp  = state.profile?.totalXp ?: 0,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ── Rules mode toggle ─────────────────────────────────────────────────
        Text(
            text = "MODE",
            color = WelcomeGray,
            fontSize = 12.sp,
            modifier = Modifier.align(Alignment.Start).padding(bottom = 8.dp)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(ToggleBg, RoundedCornerShape(12.dp))
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            RulesMode.entries.forEach { mode ->
                val selected = mode == selectedRules
                Button(
                    onClick  = {
                        sound.playClick()
                        selectedRules = mode
                               },
                    modifier = Modifier.weight(1f),
                    shape    = RoundedCornerShape(10.dp),
                    colors   = ButtonDefaults.buttonColors(
                        containerColor = if (selected) ButtonPurple else Color.Transparent,
                        contentColor   = if (selected) Color.White  else TextMuted
                    ),
                    elevation = ButtonDefaults.buttonElevation(0.dp)
                ) {
                    Text(
                        text       = if (mode == RulesMode.CLASSIC) "Classic" else "Mixed",
                        fontSize   = 13.sp,
                        fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // ── Game mode cards ───────────────────────────────────────────────────
        Text(
            text = "GAME MODES",
            color = WelcomeGray,
            fontSize = 12.sp,
            modifier = Modifier.align(Alignment.Start).padding(bottom = 12.dp)
        )

        GameModeCard(
            icon      = "\uD83D\uDDBC️",
            title     = "Image Mode",
            subtitle  = "Identify which image is real",
            badge     = "Popular",
            badgeColor = Color(0xFF1DB954),
            onClick   = {
                sound.playClick()
                onStartGame(GameMode.IMAGE, selectedRules)
            }
        )
        Spacer(modifier = Modifier.height(12.dp))
        GameModeCard(
            icon      = "\u270D️",
            title     = "Text Mode",
            subtitle  = "Detect AI-written text",
            badge     = "New",
            badgeColor = ButtonPurple,
            onClick   = { onStartGame(GameMode.TEXT, selectedRules) }
        )
        Spacer(modifier = Modifier.height(12.dp))
        GameModeCard(
            icon      = "\u26A1",
            title     = "Speed Run",
            subtitle  = "Quickly spot real vs AI before images vanish",
            badge     = null,
            badgeColor = null,
            onClick   = {
                sound.playClick()
                onStartGame(GameMode.SPEED, selectedRules)
            }
        )

        Spacer(modifier = Modifier.weight(1f))

        state.error?.let {
            Text(text = it, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
        }
    }
}

@Composable
private fun GameModeCard(
    icon: String,
    title: String,
    subtitle: String,
    badge: String?,
    badgeColor: Color?,
    onClick: () -> Unit
) {
    Button(
        onClick  = onClick,
        modifier = Modifier.fillMaxWidth().height(72.dp),
        shape    = RoundedCornerShape(16.dp),
        colors   = ButtonDefaults.buttonColors(containerColor = Color(0xFF0D0D0D))
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(Color(0xFF1A1A2E), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) { Text(icon) }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(title,    color = Color.White,   fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                Text(subtitle, color = TextMuted,     fontSize = 12.sp)
            }

            if (badge != null && badgeColor != null) {
                Box(
                    modifier = Modifier
                        .background(badgeColor.copy(alpha = 0.15f), RoundedCornerShape(10.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) { Text(badge, color = badgeColor, fontSize = 11.sp) }
                Spacer(modifier = Modifier.width(8.dp))
            }

            Text("›", color = TextMuted, fontSize = 22.sp)
        }
    }
}