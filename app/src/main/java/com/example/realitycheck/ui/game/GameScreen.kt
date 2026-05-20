package com.example.realitycheck.ui.game

import android.content.Context
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
import coil.compose.AsyncImagePainter
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

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFF1a1a2e))) {
        if (!state.isGameOver) {
            Column(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.weight(1f)) {
                GameImageBox(
                    imageUrl = state.topImageUrl,
                    onClick = { viewModel.onImageSelected(true) },
                    enabled = !state.isLoading && !state.showOverlay,
                    showOverlay = state.showOverlay && state.tappedTop != false,
                    isCorrect = state.lastResultCorrect,
                    isTop = true,
                    modifier = Modifier.weight(1f),
                    imageLoader = imageLoader,
                    onImageLoadSuccess = { viewModel.onImageLoadSuccess(true) },
                    onImageLoadError = { viewModel.onImageLoadError(true) }
                )

                GameImageBox(
                    imageUrl = state.bottomImageUrl,
                    onClick = { viewModel.onImageSelected(false) },
                    enabled = !state.isLoading && !state.showOverlay,
                    showOverlay = state.showOverlay && state.tappedTop != true,
                    isCorrect = state.lastResultCorrect,
                    isTop = false,
                    modifier = Modifier.weight(1f),
                    imageLoader = imageLoader,
                    onImageLoadSuccess = { viewModel.onImageLoadSuccess(false) },
                    onImageLoadError = { viewModel.onImageLoadError(false) }
                )
            }

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

@Composable
private fun GameImageBox(
    imageUrl: String?,
    onClick: () -> Unit,
    enabled: Boolean,
    showOverlay: Boolean,
    isCorrect: Boolean,
    isTop: Boolean,
    modifier: Modifier = Modifier,
    imageLoader: coil.ImageLoader,
    onImageLoadSuccess: () -> Unit,
    onImageLoadError: () -> Unit
) {
    var loadState by remember { mutableStateOf<AsyncImagePainter.State>(AsyncImagePainter.State.Empty) }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xFF2d2d44))
            .clickable(enabled = enabled) { onClick() }
    ) {
        AsyncImage(
            model = imageUrl,
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            imageLoader = imageLoader,
            onState = { state ->
                loadState = state
                when (state) {
                    is AsyncImagePainter.State.Success -> onImageLoadSuccess()
                    is AsyncImagePainter.State.Error -> onImageLoadError()
                    else -> {}
                }
            }
        )

        if (loadState is AsyncImagePainter.State.Loading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = Color.White.copy(alpha = 0.7f)
            )
        }

        if (loadState is AsyncImagePainter.State.Error) {
            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.5f),
                    modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Failed to load",
                    color = Color.White.copy(alpha = 0.5f),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        if (showOverlay) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(if (isCorrect) Color.Green.copy(alpha = 0.5f) else Color.Red.copy(alpha = 0.5f))
            )
        }
    }
}
