package com.example.realitycheck.ui.game

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

@Composable
fun GameScreen(
    viewModel: GameViewModel,
    onGameOver: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val streak by viewModel.currentStreak.collectAsState()

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFF1a1a2e))) {
        if (!state.isGameOver) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Images section (takes remaining space)
                Column(modifier = Modifier.weight(1f)) {
                    // Top Image
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .background(Color(0xFF2d2d44))
                            .clickable(enabled = !state.isLoading && !state.showOverlay) { viewModel.onImageSelected(true) }
                    ) {
                        AsyncImage(
                            model = state.topImageUrl,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                        if (state.showOverlay && state.tappedTop != false) {
                            Box(modifier = Modifier.fillMaxSize().background(if (state.lastResultCorrect) Color.Green.copy(alpha = 0.5f) else Color.Red.copy(alpha = 0.5f)))
                        }
                    }

                    // Bottom Image
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .background(Color(0xFF2d2d44))
                            .clickable(enabled = !state.isLoading && !state.showOverlay) { viewModel.onImageSelected(false) }
                    ) {
                        AsyncImage(
                            model = state.bottomImageUrl,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                        if (state.showOverlay && state.tappedTop != true) {
                            Box(modifier = Modifier.fillMaxSize().background(if (state.lastResultCorrect) Color.Green.copy(alpha = 0.5f) else Color.Red.copy(alpha = 0.5f)))
                        }
                    }
                }

                // Both AI / Both Real buttons (always visible)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Button(
                        onClick = { viewModel.onBothAnswer(guessedAi = true) },
                        enabled = !state.isLoading && !state.showOverlay,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Both AI")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = { viewModel.onBothAnswer(guessedAi = false) },
                        enabled = !state.isLoading && !state.showOverlay,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Both Real")
                    }
                }
            }

            // Streak Display
            Text(
                text = streak.toString(),
                modifier = Modifier.align(Alignment.TopCenter).padding(top = 32.dp),
                style = MaterialTheme.typography.displayMedium,
                color = Color.White
            )

            if (state.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = Color.White
                )
            }
        }

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
