package com.example.realitycheck.ui.game

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.realitycheck.di.ImageLoaderFactory

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ZoomOutMap

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
    val context = LocalContext.current
    val imageLoader = remember { ImageLoaderFactory.create(context) }

    var fullscreenImage by remember { mutableStateOf<String?>(null) }

    // zoom state
    var scale by remember { mutableStateOf(1f) }
    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }

    LaunchedEffect(fullscreenImage) {
        scale = 1f
        offsetX = 0f
        offsetY = 0f
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBg)
    ) {

        // ───────────────────────────── GAME ─────────────────────────────
        if (!uiState.isGameOver) {
            Column(modifier = Modifier.fillMaxSize()) {

                Column(modifier = Modifier.weight(1f)) {

                    GameContentBox(
                        content = uiState.topContent,
                        isImage = uiState.isImageMode,
                        onClick = { onSelect(true) },
                        onImageClick = { fullscreenImage = it },
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
                        onImageClick = { fullscreenImage = it },
                        enabled = !uiState.isLoading && !uiState.showOverlay,
                        showOverlay = uiState.showOverlay && uiState.tappedTop != true,
                        isCorrect = uiState.lastResultCorrect,
                        modifier = Modifier.weight(1f),
                        imageLoader = imageLoader
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            // ───────────────────────── OVERLAYS ─────────────────────────

            if (timeRemainingSeconds != null) {
                Row(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 32.dp),
                    horizontalArrangement = Arrangement.spacedBy(32.dp)
                ) {
                    ScoreColumn(timeRemainingSeconds.toString(), "time")
                    ScoreColumn(streak.toString(), scoreLabel)
                }
            }

            if (uiState.earnedXp > 0) {
                Text(
                    text = "+${uiState.earnedXp} XP",
                    color = Color.Yellow,
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = Color.White
                )
            }
        }

// ───────────────────────── FULLSCREEN IMAGE ─────────────────────────
        fullscreenImage?.let { url ->

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black)
                    .pointerInput(Unit) {
                        detectTransformGestures { centroid, pan, zoom, _ ->

                            val newScale = (scale * zoom).coerceIn(1f, 5f)

                            val scaleFactor = newScale / scale

                            offsetX = (offsetX - centroid.x) * scaleFactor + centroid.x + pan.x
                            offsetY = (offsetY - centroid.y) * scaleFactor + centroid.y + pan.y

                            scale = newScale
                        }
                    }
                    .clickable { fullscreenImage = null }
            ) {

                AsyncImage(
                    model = url,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer {
                            scaleX = scale
                            scaleY = scale
                            translationX = offsetX
                            translationY = offsetY
                        },
                    contentScale = ContentScale.Fit,
                    imageLoader = imageLoader
                )

                // close button
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(12.dp)
                        .size(48.dp)
                        .background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                        .clickable { fullscreenImage = null },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.ZoomOutMap,
                        contentDescription = "Close",
                        tint = Color.White
                    )
                }
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
    onImageClick: (String) -> Unit,
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
            .clickable(enabled = enabled) {
                onClick()
            }
    ) {

        if (isImage) {

            Box(modifier = Modifier.fillMaxSize()) {

                AsyncImage(
                    model = content,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    imageLoader = imageLoader
                )

                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .size(40.dp)
                        .background(
                            color = Color.Black.copy(alpha = 0.45f),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .clickable(enabled = content != null) {
                            content?.let(onImageClick)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.ZoomOutMap,
                        contentDescription = "Fullscreen",
                        tint = Color.White,
                        modifier = Modifier.size(36.dp)
                    )
                }
            }

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
                        if (isCorrect) Color.Green.copy(alpha = 0.5f)
                        else Color.Red.copy(alpha = 0.5f)
                    )
            )
        }
    }
}