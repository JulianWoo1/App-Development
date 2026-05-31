package com.example.realitycheck.ui.gameover

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ===== COLORS =====
private val Bg = Color(0xFF050505)
private val CardBg = Color(0xFF0D0D0D)
private val Purple = Color(0xFF5B2EFF)
private val Pink = Color(0xFFFF4D8D)
private val Orange = Color(0xFFFF8A3D)
private val Gray = Color(0xFFA0A0A0)
private val Stroke = Color(0xFF1F1F1F)
private val ProgressBg = Color(0xFF222222)

@Composable
fun GameOverScreen(
    score: String = "2.340",
    accuracy: String = "74%",
    fastestTime: String = "1.4s",
    bestRank: String = "#18",
    level: Int = 23,
    currentXp: Int = 2820,
    maxXp: Int = 5000,
    gainedXp: Int = 230,
    onPlayAgain: () -> Unit,
    onHome: () -> Unit
)
{
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Bg)
    )
    {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
                .padding(bottom = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        )
        {

            Spacer(modifier = Modifier.height(22.dp))

            // ===== ICON =====
            Box(
                modifier = Modifier
                    .size(140.dp)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color(0xFF42124B),
                                Color(0xFF101010)
                            ),
                            radius = 220f
                        ),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            )
            {
                Box(
                    modifier = Modifier
                        .size(74.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(Pink, Orange)
                            )
                        ),
                    contentAlignment = Alignment.Center
                )
                {
                    Text(
                        text = "✕",
                        color = Color.White,
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Text(
                text = "Game over",
                fontSize = 36.sp,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center,
                color = Color.White,
                modifier = Modifier.padding(top = 10.dp)
            )

            Text(
                text = "Better luck next time!",
                color = Gray,
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 6.dp)
            )

            Spacer(modifier = Modifier.height(22.dp))

            // ===== SCORE CARD =====
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(26.dp),
                colors = CardDefaults.cardColors(containerColor = CardBg),
                border = BorderStroke(1.dp, Stroke)
            )
            {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                )
                {

                    Text("Your score", color = Gray, fontSize = 16.sp)

                    Text(
                        text = score,
                        color = Color.White,
                        fontSize = 48.sp,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.padding(top = 4.dp)
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(
                                        Color(0xFF4A1DFF),
                                        Color(0xFF2E146B)
                                    )
                                )
                            )
                    )
                    {
                        Text(
                            text = "New record 🏆",
                            color = Color(0xFFBFA6FF),
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(
                                horizontal = 18.dp,
                                vertical = 6.dp
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color(0xFF0A0A0A))
                            .border(1.dp, Stroke, RoundedCornerShape(20.dp))
                            .padding(vertical = 16.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    )
                    {
                        StatItem("🎯", accuracy, "Accuracy")
                        StatItem("⚡", fastestTime, "Fastest")
                        StatItem("🏆", bestRank, "Best rank")
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // ===== XP CARD =====
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = CardBg),
                border = BorderStroke(1.dp, Stroke)
            )
            {
                Column(modifier = Modifier.padding(20.dp))
                {

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    )
                    {
                        Text(
                            text = "Level $level",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )

                        Text(
                            text = "+$gainedXp XP",
                            color = Purple,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(50))
                            .background(ProgressBg)
                    )
                    {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(
                                    (currentXp.toFloat() / maxXp.toFloat()).coerceIn(0f, 1f)
                                )
                                .fillMaxHeight()
                                .background(
                                    Brush.horizontalGradient(
                                        colors = listOf(Purple, Color(0xFF7B61FF))
                                    )
                                )
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "$currentXp / $maxXp XP",
                        color = Gray,
                        fontSize = 13.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(22.dp))

            // ===== PLAY AGAIN BUTTON =====
            Button(
                onClick = onPlayAgain,
                colors = ButtonDefaults.buttonColors(containerColor = Purple),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp),
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
                        text = "Play again",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Retry",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // ===== HOME BUTTON =====
            OutlinedButton(
                onClick = onHome,
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(1.dp, Stroke),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color.White
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp)
            )
            {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                )
                {
                    Text(
                        text = "Back to home",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    Icon(
                        imageVector = Icons.Default.Home,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun StatItem(icon: String, value: String, label: String)
{
    Column(horizontalAlignment = Alignment.CenterHorizontally)
    {
        Text(icon, fontSize = 22.sp)

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = value,
            color = Color.White,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(3.dp))

        Text(label, color = Gray, fontSize = 14.sp)
    }
}