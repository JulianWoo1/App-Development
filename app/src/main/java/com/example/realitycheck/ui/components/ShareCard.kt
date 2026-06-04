package com.example.realitycheck.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val Bg     = Color(0xFF050505)
private val Purple = Color(0xFF5B2EFF)
private val Pink   = Color(0xFFFF4D8D)
private val Orange = Color(0xFFFF8A3D)
private val Gray   = Color(0xFFA0A0A0)
private val Stroke = Color(0xFF1F1F1F)

@Composable
fun ShareScoreCard(
    streak: Int,
    xpGained: Int,
    level: Int
) {
    Column(
        modifier = Modifier
            .width(360.dp)
            .background(Bg)
            .padding(28.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "REALITY CHECK",
            color = Purple,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 4.sp
        )

        Spacer(modifier = Modifier.height(24.dp))

        Box(
            modifier = Modifier
                .size(80.dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0xFF42124B), Color(0xFF101010)),
                        radius = 140f
                    ),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(Brush.linearGradient(colors = listOf(Pink, Orange))),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "✕",
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("Game Over", color = Gray, fontSize = 14.sp, letterSpacing = 2.sp)

        Spacer(modifier = Modifier.height(20.dp))

        Text("Your Streak", color = Gray, fontSize = 14.sp)

        Text(
            text = streak.toString(),
            color = Color.White,
            fontSize = 64.sp,
            fontWeight = FontWeight.ExtraBold
        )

        if (streak >= 3) {
            Spacer(modifier = Modifier.height(6.dp))
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(Color(0xFF4A1DFF), Color(0xFF2E146B))
                        )
                    )
            ) {
                Text(
                    text = when {
                        streak >= 20 -> "Legendary streak 🏆"
                        streak >= 10 -> "Hot streak 🔥"
                        else -> "Nice streak ⚡"
                    },
                    color = Color(0xFFBFA6FF),
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(horizontal = 18.dp, vertical = 6.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0xFF0A0A0A))
                .border(1.dp, Stroke, RoundedCornerShape(20.dp))
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            ShareStatItem("🎯", streak.toString(), "Streak")
            ShareStatItem("⚡", "+$xpGained", "XP earned")
            ShareStatItem("🏅", "Lv $level", "Level")
        }

        Spacer(modifier = Modifier.height(28.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(Color(0xFF1F1F1F))
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Think you can beat this?",
            color = Gray,
            fontSize = 15.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Download Reality Check",
            color = Purple,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )
    }
}

@Composable
private fun ShareStatItem(icon: String, value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(icon, fontSize = 20.sp)
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = value,
            color = Color.White,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(label, color = Gray, fontSize = 13.sp)
    }
}
