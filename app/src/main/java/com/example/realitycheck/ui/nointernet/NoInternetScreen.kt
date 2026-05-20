package com.example.realitycheck.ui.nointernet

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.realitycheck.utils.NetworkMonitor

private val CardBg = Color(0xFF0D0D0D)
private val ButtonPurple = Color(0xFF5B2EFF)
private val SubtitleGray = Color(0xFFA0A0A0)
private val IconCircleBg = Color(0xFF161122)

@Composable
fun NoInternetScreen(
    onRetrySuccess: () -> Unit
) {
    val context = LocalContext.current
    var isChecking by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF050505)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Wifi icon circle
            Box(
                modifier = Modifier
                    .size(180.dp)
                    .background(
                        brush = androidx.compose.ui.graphics.Brush.radialGradient(
                            colors = listOf(Color(0xFF301C8A), Color(0xFF101010)),
                            radius = 220f
                        ),
                        shape = androidx.compose.foundation.shape.CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "\uD83D\uDCF6",
                    style = MaterialTheme.typography.displayLarge,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }

            // Title
            Text(
                text = "Geen internetverbinding",
                color = Color.White,
                style = MaterialTheme.typography.headlineLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 36.dp)
            )

            // Subtitle
            Text(
                text = "Verbinding met internet mislukt. Controleer je verbinding en probeer het opnieuw.",
                color = SubtitleGray,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 18.dp)
            )

            // Retry button
            Button(
                onClick = {
                    isChecking = true
                    val monitor = NetworkMonitor(context) { }
                    if (monitor.hasInternetConnection()) {
                        onRetrySuccess()
                    }
                    isChecking = false
                },
                enabled = !isChecking,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(72.dp)
                    .padding(top = 42.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ButtonPurple),
                shape = RoundedCornerShape(28.dp)
            ) {
                if (isChecking) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = "Opnieuw proberen",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White
                    )
                }
            }

            // Info card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 28.dp),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = CardBg)
            ) {
                Row(
                    modifier = Modifier.padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .background(IconCircleBg, androidx.compose.foundation.shape.CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "\u2139\uFE0F",
                            style = MaterialTheme.typography.titleLarge
                        )
                    }

                    Column(modifier = Modifier.padding(start = 18.dp)) {
                        Text(
                            text = "Wat kun je doen?",
                            color = Color.White,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = "Controleer je wifi of mobiele data en probeer het opnieuw.",
                            color = SubtitleGray,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(top = 6.dp)
                        )
                    }
                }
            }
        }
    }
}
