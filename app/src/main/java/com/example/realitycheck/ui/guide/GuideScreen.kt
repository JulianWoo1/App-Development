package com.example.realitycheck.ui.guide

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.realitycheck.audio.LocalSoundManager

private val CardBg = Color(0xFF0D0D0D)
private val ButtonPurple = Color(0xFF5B2EFF)
private val SubtitleGray = Color(0xFFA0A0A0)
private val ProgressInactive = Color(0xFF1F1F1F)
private val CardStroke = Color(0xFF1F1F1F)

private data class GuideStep(
    val number: Int,
    val cardTitle: String,
    val cardDescription: String
)

private val steps = listOf(
    GuideStep(
        1,
        "Choose a game mode",
        "Pick how you want to play: Classic, Speedrun, Image Mode or Mixed Mode, each with different rules."
    ),
    GuideStep(
        2,
        "Look carefully",
        "You will see two images shown one below the other. Study both carefully and compare the details."
    ),
    GuideStep(
        3,
        "Tap the real image",
        "Tap the image you think is real. You’ll instantly see if you were right or wrong, and earn XP based on your answer."
    ),
    GuideStep(
        4,
        "Earn XP & improve",
        "Keep playing to build streaks, gain XP, and climb the leaderboards."
    )
)

@Composable
fun GuideScreen(
    onComplete: () -> Unit
) {
    var currentStep by remember { mutableIntStateOf(0) }
    val sound = LocalSoundManager.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF050505))
            .padding(24.dp)
    ) {
        Column {
            // Top row: step indicator + skip
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Step indicator
                Card(
                    colors = CardDefaults.cardColors(containerColor = CardBg),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${currentStep + 1} / 4",
                            color = Color.White,
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }
            }

            // Progress bars
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                repeat(4) { index ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(6.dp)
                            .clip(RoundedCornerShape(100.dp))
                            .background(if (index == currentStep) ButtonPurple else ProgressInactive)
                    )
                }
            }

            // Title
            Text(
                text = "How it works 🚀",
                color = Color.White,
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier.padding(top = 40.dp)
            )

            // Subtitle
            Text(
                text = "Test your intuition. Can you really distinguish it from AI?",
                color = SubtitleGray,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(top = 16.dp)
            )

            // Main card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(top = 32.dp, bottom = 24.dp),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = CardBg)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    // Purple number circle
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .background(ButtonPurple, RoundedCornerShape(21.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${currentStep + 1}",
                            color = Color.White,
                            style = MaterialTheme.typography.labelLarge
                        )
                    }

                    Text(
                        text = steps[currentStep].cardTitle,
                        color = Color.White,
                        style = MaterialTheme.typography.headlineMedium,
                        modifier = Modifier.padding(top = 24.dp)
                    )

                    Text(
                        text = steps[currentStep].cardDescription,
                        color = Color(0xFFB0B0B0),
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(top = 16.dp)
                    )
                }
            }

            // Continue button
            Button(
                onClick = {
                    sound.playClick()
                    if (currentStep < 3) {
                        currentStep++
                    } else {
                        onComplete()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ButtonPurple),
                shape = RoundedCornerShape(20.dp)
            ) {
                Text(
                    text = if (currentStep < 3) "Next" else "Back to settings",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White
                )
            }
        }
    }
}