package com.example.realitycheck.ui.game

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.realitycheck.di.ImageLoaderFactory

@Composable
fun GameScreen(
    uiState: GameUiState,
    streak: Int,
    timeRemainingSeconds: Int?,
    scoreLabel: String = "streak",
    onSelect: (isTop: Boolean) -> Unit,
    onBothAnswer: (guessedAi: Boolean) -> Unit,
    onGameOverDismissed: () -> Unit
) {
    val context = LocalContext.current
    val imageLoader = remember { ImageLoaderFactory.create(context) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1a1a2e))
    ) {

        if (!uiState.isGameOver) {

            Column(modifier = Modifier.fillMaxSize()) {

                // -------- TOP + BOTTOM --------
                Column(modifier = Modifier.weight(1f)) {

                    GameContentBox(
                        content = uiState.topContent,
                        isImage = uiState.isImageMode,
                        onClick = { onSelect(true) },
                        enabled = !uiState.isLoading && !uiState.showOverlay,
                        showOverlay = uiState.showOverlay && uiState.tappedTop != false,
                        isCorrect = uiState.lastResultCorrect,
                        modifier = Modifier.weight(1f),
                        imageLoader = imageLoader
                    )

                    GameContentBox(
                        content = uiState.bottomContent,
                        isImage = uiState.isImageMode,
                        onClick = { onSelect(false) },
                        enabled = !uiState.isLoading && !uiState.showOverlay,
                        showOverlay = uiState.showOverlay && uiState.tappedTop != true,
                        isCorrect = uiState.lastResultCorrect,
                        modifier = Modifier.weight(1f),
                        imageLoader = imageLoader
                    )
                }

                // -------- BOTTOM BUTTONS --------
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {

                    Button(
                        onClick = { onBothAnswer(true) },
                        enabled = !uiState.isLoading && !uiState.showOverlay,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Both AI")
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = { onBothAnswer(false) },
                        enabled = !uiState.isLoading && !uiState.showOverlay,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Both Real")
                    }
                }
            }

            // -------- TIMER / STREAK --------
            if (timeRemainingSeconds != null) {
                Row(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 32.dp),
                    horizontalArrangement = Arrangement.spacedBy(32.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = timeRemainingSeconds.toString(),
                            style = MaterialTheme.typography.displayMedium,
                            color = Color.White
                        )
                        Text(
                            text = "time",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.6f)
                        )
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = streak.toString(),
                            style = MaterialTheme.typography.displayMedium,
                            color = Color.White
                        )
                        Text(
                            text = scoreLabel,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.6f)
                        )
                    }
                }
            } else {
                Text(
                    text = streak.toString(),
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 32.dp),
                    style = MaterialTheme.typography.displayMedium,
                    color = Color.White
                )
            }

            // -------- LOADING --------
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = Color.White
                )
            }
        }
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
            .background(Color(0xFF2d2d44))
            .clickable(enabled = enabled) { onClick() }
    ) {

        if (isImage) {
            AsyncImage(
                model = content,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                imageLoader = imageLoader
            )
        } else {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = content ?: "",
                    color = Color.White,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }

        if (showOverlay) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        if (isCorrect)
                            Color.Green.copy(alpha = 0.5f)
                        else
                            Color.Red.copy(alpha = 0.5f)
                    )
            )
        }
    }
}