package com.example.realitycheck.ui.nointernet

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.realitycheck.R

// ===== COLORS =====
private val Bg = Color(0xFF050505)
private val CardBg = Color(0xFF0D0D0D)
private val Purple = Color(0xFF5B2EFF)
private val Gray = Color(0xFFA0A0A0)
private val Stroke = Color(0xFF1F1F1F)

@Composable
fun NoInternetScreen(
    onRetrySuccess: () -> Unit
)
{
    // ===== FULL SCREEN BACKGROUND =====
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Bg)
    )
    {

        // ===== CENTER CONTENT CONTAINER =====
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        )
        {

            // ===== TOP SPACING =====
            Spacer(modifier = Modifier.height(60.dp))

            // ===== WIFI ICON WITH RADIAL GLOW BACKGROUND =====
            Box(
                modifier = Modifier
                    .size(170.dp)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color(0xFF301C8A),
                                Color(0xFF101010)
                            ),
                            radius = 200f
                        ),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            )
            {
                Image(
                    painter = painterResource(id = R.drawable.ic_no_wifi),
                    contentDescription = null,
                    modifier = Modifier.size(84.dp)
                )
            }

            // ===== TITLE =====
            Text(
                text = "No internet connection",
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 28.dp)
            )

            // ===== SUBTITLE =====
            Text(
                text = "Connection to the internet failed. Check your connection and try again.",
                color = Gray,
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                lineHeight = 20.sp,
                modifier = Modifier.padding(top = 14.dp)
            )

            // ===== SPACE BEFORE BUTTON =====
            Spacer(modifier = Modifier.height(48.dp))

            // ===== RETRY BUTTON =====
            Button(
                onClick = { onRetrySuccess() },
                colors = ButtonDefaults.buttonColors(containerColor = Purple),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(66.dp),
                contentPadding = PaddingValues(0.dp)
            )
            {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxSize()
                )
                {
                    Text(
                        text = "Try again",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.width(14.dp))

                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Retry",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // ===== SPACE BEFORE INFO CARD =====
            Spacer(modifier = Modifier.height(22.dp))

            // ===== INFO CARD =====
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = CardBg),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = androidx.compose.ui.graphics.SolidColor(Stroke)
                )
            )
            {

                // ===== CARD CONTENT =====
                Row(
                    modifier = Modifier.padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically
                )
                {

                    // INFO ICON CONTAINER
                    Box(
                        modifier = Modifier.size(52.dp),
                        contentAlignment = Alignment.Center
                    )
                    {
                        Image(
                            painter = painterResource(id = R.drawable.ic_info_outline),
                            contentDescription = null,
                            modifier = Modifier.size(26.dp)
                        )
                    }

                    // TEXT BLOCK
                    Column(
                        modifier = Modifier.padding(start = 16.dp)
                    )
                    {
                        Text(
                            text = "What can you do?",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = "Check your Wi-Fi or mobile data and try again.",
                            color = Gray,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }
        }
    }
}