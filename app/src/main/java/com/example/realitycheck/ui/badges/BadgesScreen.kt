package com.example.realitycheck.ui.badges

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.request.ImageRequest
import com.example.realitycheck.data.di.SupabaseModule
import com.example.realitycheck.di.ImageLoaderFactory

private val Background = Color(0xFF050505)
private val CardBg = Color(0xFF0D0D0D)
private val Purple = Color(0xFF5B2EFF)
private val SubtitleGray = Color(0xFF9A9A9A)

@Composable
fun BadgesScreen() {
    val viewModel: BadgesViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return BadgesViewModel(SupabaseModule.badgeRepository) as T
            }
        }
    )

    val state by viewModel.uiState.collectAsState()
    var selectedBadge by remember { mutableStateOf<BadgeUiItem?>(null) }

    if (selectedBadge != null) {
        BadgeDetailDialog(
            badge = selectedBadge!!,
            onDismiss = { selectedBadge = null }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Text(
            text = "Badges",
            color = Color.White,
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(4.dp))

        val earnedCount = state.badges.count { it.isUnlocked }
        Text(
            text = "$earnedCount / ${state.badges.size} earned",
            color = SubtitleGray,
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(20.dp))

        when {
            state.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Purple)
                }
            }
            state.error != null -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                        Text(
                            text = state.error ?: "An error occurred",
                            color = Color.Red,
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        TextButton(onClick = { viewModel.loadBadges() }) {
                            Text("Try again", color = Purple)
                        }
                    }
                }
            }
            else -> {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(
                        items = state.badges,
                        key = { it.id }
                    ) { badge ->
                        BadgeGridItem(
                            badge = badge,
                            onClick = { selectedBadge = badge }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun BadgeGridItem(badge: BadgeUiItem, onClick: () -> Unit) {
    var badgeBitmap by remember { mutableStateOf<ImageBitmap?>(null) }
    val ctx = LocalContext.current

    LaunchedEffect(badge.iconUrl) {
        val request = ImageRequest.Builder(ctx)
            .data(badge.iconUrl)
            .size(512)
            .crossfade(true)
            .build()

        val result = ImageLoaderFactory.create(ctx).execute(request)
        val drawable = result.drawable ?: return@LaunchedEffect

        val w = drawable.intrinsicWidth.coerceAtLeast(1)
        val h = drawable.intrinsicHeight.coerceAtLeast(1)

        val raw = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        val rawCanvas = android.graphics.Canvas(raw)
        drawable.setBounds(0, 0, w, h)
        drawable.draw(rawCanvas)

        if (badge.isUnlocked) {
            badgeBitmap = raw.asImageBitmap()
        } else {
            val tinted = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
            val tintCanvas = android.graphics.Canvas(tinted)
            val tintPaint = android.graphics.Paint().apply {
                colorFilter = android.graphics.ColorMatrixColorFilter(
                    floatArrayOf(
                        0.299f, 0.587f, 0.114f, 0f, 0f,
                        0.299f, 0.587f, 0.114f, 0f, 0f,
                        0.299f, 0.587f, 0.114f, 0f, 0f,
                        0f,     0f,     0f,     1f, 0f
                    )
                )
            }
            tintCanvas.drawBitmap(raw, 0f, 0f, tintPaint)
            raw.recycle()
            badgeBitmap = tinted.asImageBitmap()
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .then(
                if (!badge.isUnlocked) Modifier.alpha(0.35f) else Modifier
            )
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .background(
                    color = if (badge.isUnlocked) Purple.copy(alpha = 0.2f) else CardBg,
                    shape = CircleShape
                )
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            if (badgeBitmap != null) {
                Image(
                    painter = BitmapPainter(badgeBitmap!!),
                    contentDescription = badge.name ?: "Locked badge",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = if (badge.isUnlocked) (badge.name ?: "") else "???",
            color = if (badge.isUnlocked) Color.White else SubtitleGray,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
            maxLines = 2
        )
    }
}


