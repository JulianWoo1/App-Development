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
    viewModel: GameViewModel,
    onGameOver: () -> Unit
) {
    val context = LocalContext.current
    val imageLoader = remember { ImageLoaderFactory.create(context) }

    val state by viewModel.uiState.collectAsState()
    val streak by viewModel.currentStreak.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1a1a2e))
    ) {

        if (!state.isGameOver) {

            Column(modifier = Modifier.fillMaxSize()) {

                // -------- TOP + BOTTOM --------
                Column(modifier = Modifier.weight(1f)) {

                    GameContentBox(
                        content = state.topContent,
                        isImage = state.isImageMode,
                        onClick = { viewModel.onSelect(true) },
                        enabled = !state.isLoading && !state.showOverlay,
                        showOverlay = state.showOverlay && state.tappedTop != false,
                        isCorrect = state.lastResultCorrect,
                        modifier = Modifier.weight(1f),
                        imageLoader = imageLoader
                    )

                    GameContentBox(
                        content = state.bottomContent,
                        isImage = state.isImageMode,
                        onClick = { viewModel.onSelect(false) },
                        enabled = !state.isLoading && !state.showOverlay,
                        showOverlay = state.showOverlay && state.tappedTop != true,
                        isCorrect = state.lastResultCorrect,
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
                        onClick = { viewModel.onBothAnswer(true) },
                        enabled = !state.isLoading && !state.showOverlay,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Both AI")
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = { viewModel.onBothAnswer(false) },
                        enabled = !state.isLoading && !state.showOverlay,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Both Real")
                    }
                }
            }

            // -------- STREAK --------
            Text(
                text = streak.toString(),
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 32.dp),
                style = MaterialTheme.typography.displayMedium,
                color = Color.White
            )

            // xp earned
            if (state.earnedXp > 0) {
                Text(
                    text = "+${state.earnedXp} XP",
                    color = Color.Yellow,
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier
                        .align(Alignment.Center)
                )
            }

            // -------- LOADING --------
            if (state.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = Color.White
                )
            }
        }

        // -------- GAME OVER --------
        if (state.isGameOver) {
            AlertDialog(
                onDismissRequest = {},
                title = { Text("You're out!") },
                text = { Text("Your streak: $streak") },
                confirmButton = {
                    TextButton(onClick = onGameOver) {
                        Text("OK")
                    }
                }
            )
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