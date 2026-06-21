package com.example.realitycheck.ui.badges

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import coil.request.ImageRequest
import com.example.realitycheck.di.ImageLoaderFactory

private val CardBg = Color(0xFF0D0D0D)
private val SubtitleGray = Color(0xFF9A9A9A)
private val Purple = Color(0xFF5B2EFF)

@Composable
fun BadgeDetailDialog(badge: BadgeUiItem, onDismiss: () -> Unit) {
    var badgeBitmap by remember { mutableStateOf<ImageBitmap?>(null) }
    val ctx = LocalContext.current

    LaunchedEffect(badge.iconUrl) {
        val request = ImageRequest.Builder(ctx)
            .data(badge.iconUrl)
            .size(512)
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

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = CardBg,
        titleContentColor = Color.White,
        textContentColor = Color.White,
        title = {
            Text(
                text = if (badge.isUnlocked) (badge.name ?: "") else "???",
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (badgeBitmap != null) {
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .background(
                                color = if (badge.isUnlocked) Purple.copy(alpha = 0.2f) else CardBg,
                                shape = CircleShape
                            )
                            .padding(12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = BitmapPainter(badgeBitmap!!),
                            contentDescription = badge.name ?: "Locked badge",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Fit
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
                if (badge.isUnlocked && badge.description != null) {
                    Text(
                        text = badge.description,
                        color = Color.White,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
                if (badge.criteriaDescription != null) {
                    Text(
                        text = "How to unlock:",
                        color = Purple,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = badge.criteriaDescription,
                        color = SubtitleGray,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close", color = Purple)
            }
        }
    )
}
