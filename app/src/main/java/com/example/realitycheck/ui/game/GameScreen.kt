package com.example.realitycheck.ui.game

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.realitycheck.di.ImageLoaderFactory

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ZoomOutMap

private val Purple = Color(0xFF5B2EFF)
private val Gold = Color(0xFFD4AF37)

private val DarkBg = Color(0xFF0B0B12)
private val CardBg = Color(0xFF151520)

private val PillBg = Color(0xCC1F1F2B)
private val LabelBg = Color(0xCC2A2A35)

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

    var xpVisible by remember { mutableStateOf(false) }
    val showXp = uiState.earnedXp > 0 && xpVisible

    val xpScale by animateFloatAsState(
        targetValue = if (showXp) 1.2f else 0.8f,
        animationSpec = spring(dampingRatio = 0.5f),
        label = ""
    )

    val xpAlpha by animateFloatAsState(
        targetValue = if (showXp) 1f else 0f,
        animationSpec = tween(300),
        label = ""
    )

    val xpOffsetY by animateFloatAsState(
        targetValue = if (showXp) -80f else 0f,
        animationSpec = tween(600),
        label = ""
    )

    LaunchedEffect(uiState.earnedXp) {
        if (uiState.earnedXp > 0) {
            xpVisible = true
            kotlinx.coroutines.delay(1000)
            xpVisible = false
        }
    }

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

                    if (timeRemainingSeconds != null) {

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {

                            SpeedRunBar(
                                timeRemainingSeconds = timeRemainingSeconds,
                                totalTime = 3, // equal to speedrun duration
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = "🔥 Streak: $streak",
                                color = Color.White,
                                modifier = Modifier.align(Alignment.CenterHorizontally),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                    ) {
                        Text(
                            text = "Which photo is real?",
                            color = Color.White,
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                    }

                    GameContentBox(
                        content = uiState.topContent,
                        label = "Photo A",
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
                        label = "Photo B",
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

                if (uiState.rulesMode == RulesMode.CHAOS) {
                    Text(
                        text = "or",
                        color = Color.White.copy(alpha = 0.7f),
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    )
                    {
                        ChaosAnswerCard(
                            text = "Both AI",
                            emoji = "🤖",
                            modifier = Modifier.weight(1f)
                        ) {
                            onBothAnswer(true)
                        }

                        ChaosAnswerCard(
                            text = "Both real",
                            emoji = "📷",
                            modifier = Modifier.weight(1f)
                        ) {
                            onBothAnswer(false)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            // ───────────────────────── OVERLAYS ─────────────────────────

            if (uiState.earnedXp > 0) {

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .wrapContentSize(Alignment.Center)
                ) {
                    Box(
                        modifier = Modifier
                            .graphicsLayer {
                                scaleX = xpScale
                                scaleY = xpScale
                                translationY = xpOffsetY
                                this.alpha = xpAlpha
                            }
                            .background(
                                Color.Black.copy(alpha = 0.75f),
                                RoundedCornerShape(16.dp)
                            )
                            .padding(horizontal = 18.dp, vertical = 10.dp)
                    ) {
                        Text(
                            text = "+${uiState.earnedXp} XP",
                            color = Gold,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.headlineSmall
                        )
                    }
                }
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
private fun GameContentBox(
    content: String?,
    label: String,
    isImage: Boolean,
    onClick: () -> Unit,
    onImageClick: (String) -> Unit = {},
    enabled: Boolean,
    showOverlay: Boolean,
    isCorrect: Boolean,
    modifier: Modifier = Modifier,
    imageLoader: coil.ImageLoader
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(CardBg)
            .clickable(enabled = enabled) { onClick() }
    ) {
        if (isImage && content != null) {
            Box(modifier = Modifier.fillMaxSize()) {
                AsyncImage(
                    model              = content,
                    contentDescription = null,
                    modifier           = Modifier.fillMaxSize(),
                    contentScale       = ContentScale.Crop,
                    imageLoader        = imageLoader
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
                        .clickable(enabled = true) { onImageClick(content) },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.ZoomOutMap,
                        contentDescription = "Fullscreen",
                        tint = Color.White,
                        modifier = Modifier.size(36.dp)
                    )
                }

                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 12.dp)
                        .background(
                            PillBg,
                            RoundedCornerShape(16.dp)
                        )
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                )
                {
                    Text(
                        text = "Tap if real",
                        color = Color.White,
                        fontSize = 12.sp
                    )
                }
            }
        } else if (isImage) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text      = "?",
                    color     = Color.White.copy(alpha = 0.3f),
                    fontSize  = 64.sp,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(content ?: "", color = Color.White, style = MaterialTheme.typography.bodyLarge)
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(12.dp)
                .background(
                    LabelBg,
                    RoundedCornerShape(12.dp)
                )
                .padding(horizontal = 10.dp, vertical = 4.dp)
        )
        {
            Text(
                text = label,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
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

@Composable
private fun ChaosAnswerCard(
    text: String,
    emoji: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
)
{
    Box(
        modifier = modifier
            .height(90.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(CardBg)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    )
    {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        )
        {
            Text(
                text = emoji,
                fontSize = 24.sp
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = text,
                color = Color.White
            )
        }
    }
}

@Composable
private fun SpeedRunBar(
    timeRemainingSeconds: Int,
    totalTime: Int,
    modifier: Modifier = Modifier
) {
    val progress = (timeRemainingSeconds.toFloat() / totalTime.toFloat())
        .coerceIn(0f, 1f)

    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(900),
        label = ""
    )

    Column(modifier = modifier) {

        Box(
            modifier = Modifier
                .height(10.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(50))
                .background(Color.White.copy(alpha = 0.12f))
        ) {

            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(animatedProgress)
                    .background(Gold)
            )
        }
    }
}