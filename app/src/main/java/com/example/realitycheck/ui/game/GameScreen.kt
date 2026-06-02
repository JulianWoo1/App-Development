package com.example.realitycheck.ui.game

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.realitycheck.di.ImageLoaderFactory

private val Purple    = Color(0xFF5B2EFF)
private val DarkBg    = Color(0xFF1a1a2e)
private val CardBg    = Color(0xFF2d2d44)
private val ToggleBg  = Color(0xFF12122A)
private val MutedText = Color(0xFFA0A0C0)

@Composable
fun GameScreen(
    uiState: GameUiState,
    streak: Int,
    timeRemainingSeconds: Int?,
    scoreLabel: String = "streak",
    onSelect: (isTop: Boolean) -> Unit,
    onBothAnswer: (guessedAi: Boolean) -> Unit,
    onGameOverDismissed: () -> Unit,
) {
    val context     = LocalContext.current
    val imageLoader = remember { ImageLoaderFactory.create(context) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBg)
    ) {
        if (!uiState.isGameOver) {
            Column(modifier = Modifier.fillMaxSize()) {

                // ── Content panels ───────────────────────────────────────────
                Column(modifier = Modifier.weight(1f)) {
                    GameContentBox(
                        content     = uiState.topContent,
                        isImage     = uiState.isImageMode,
                        onClick     = { onSelect(true) },
                        enabled     = !uiState.isLoading && !uiState.showOverlay,
                        showOverlay = uiState.showOverlay && uiState.tappedTop != false,
                        isCorrect   = uiState.lastResultCorrect,
                        modifier    = Modifier.weight(1f),
                        imageLoader = imageLoader
                    )
                    GameContentBox(
                        content     = uiState.bottomContent,
                        isImage     = uiState.isImageMode,
                        onClick     = { onSelect(false) },
                        enabled     = !uiState.isLoading && !uiState.showOverlay,
                        showOverlay = uiState.showOverlay && uiState.tappedTop != true,
                        isCorrect   = uiState.lastResultCorrect,
                        modifier    = Modifier.weight(1f),
                        imageLoader = imageLoader
                    )
                }

                // ── Both AI / Both Real — only in CHAOS mode ─────────────────
                if (uiState.rulesMode == RulesMode.CHAOS) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick  = { onBothAnswer(true) },
                            enabled  = !uiState.isLoading && !uiState.showOverlay,
                            modifier = Modifier.weight(1f),
                            colors   = ButtonDefaults.buttonColors(containerColor = Purple)
                        ) { Text("Both AI") }

                        Button(
                            onClick  = { onBothAnswer(false) },
                            enabled  = !uiState.isLoading && !uiState.showOverlay,
                            modifier = Modifier.weight(1f),
                            colors   = ButtonDefaults.buttonColors(containerColor = Purple)
                        ) { Text("Both Real") }
                    }
                } else {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            // ── Streak / timer overlay ───────────────────────────────────────
            val topPad = 32.dp
            if (timeRemainingSeconds != null) {
                Row(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = topPad),
                    horizontalArrangement = Arrangement.spacedBy(32.dp)
                ) {
                    ScoreColumn(timeRemainingSeconds.toString(), "time")
                    ScoreColumn(streak.toString(), scoreLabel)
                }
            } else {
                Text(
                    text     = streak.toString(),
                    modifier = Modifier.align(Alignment.TopCenter).padding(top = topPad),
                    style    = MaterialTheme.typography.displayMedium,
                    color    = Color.White
                )
            }

            // ── XP flash ────────────────────────────────────────────────────
            if (uiState.earnedXp > 0) {
                Text(
                    text     = "+${uiState.earnedXp} XP",
                    color    = Color.Yellow,
                    style    = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color    = Color.White
                )
            }
        }
    }
}


@Composable
private fun ScoreColumn(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, style = MaterialTheme.typography.displayMedium, color = Color.White)
        Text(label, style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.6f))
    }
}

@Composable
private fun GameContentBox(
    content: String?,
    isImage: Boolean,
    onClick: () -> Unit,
    enabled: Boolean,
    showOverlay: Boolean,
    isCorrect: Boolean,
    modifier: Modifier = Modifier,
    imageLoader: coil.ImageLoader
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(CardBg)
            .clickable(enabled = enabled) { onClick() }
    ) {
        if (isImage) {
            AsyncImage(
                model              = content,
                contentDescription = null,
                modifier           = Modifier.fillMaxSize(),
                contentScale       = ContentScale.Crop,
                imageLoader        = imageLoader
            )
        } else {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(content ?: "", color = Color.White, style = MaterialTheme.typography.bodyLarge)
            }
        }
        if (showOverlay) {
            Box(
                modifier = Modifier.fillMaxSize().background(
                    if (isCorrect) Color.Green.copy(alpha = 0.5f) else Color.Red.copy(alpha = 0.5f)
                )
            )
        }
    }
}