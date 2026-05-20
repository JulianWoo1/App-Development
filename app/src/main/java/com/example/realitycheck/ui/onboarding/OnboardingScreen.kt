package com.example.realitycheck.ui.onboarding

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

private val CardBg = Color(0xFF0D0D0D)
private val ButtonPurple = Color(0xFF5B2EFF)
private val SubtitleGray = Color(0xFFA0A0A0)
private val ProgressInactive = Color(0xFF1F1F1F)
private val CardStroke = Color(0xFF1F1F1F)

private data class OnboardingStep(
    val number: Int,
    val cardTitle: String,
    val cardDescription: String
)

private val steps = listOf(
    OnboardingStep(1, "Kijk goed", "Je krijgt twee contentstukken te zien. Bijvoorbeeld foto's, teksten of audio."),
    OnboardingStep(2, "Kies je antwoord", "Wat denk jij dat echt is? Kies A, B of één van de andere opties."),
    OnboardingStep(3, "Ontvang direct feedback", "Je ziet meteen of je het goed hebt en waarom."),
    OnboardingStep(4, "Verdien punten & verbeter", "Speel meer, bouw je streak op en klim op de ranglijsten.")
)

@Composable
fun OnboardingScreen(
    onComplete: () -> Unit
) {
    var currentStep by remember { mutableIntStateOf(0) }

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

                Spacer(modifier = Modifier.width(12.dp))

                // Skip button
                Card(
                    modifier = Modifier.clickable { onComplete() },
                    colors = CardDefaults.cardColors(containerColor = CardBg),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 22.dp, vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Overslaan",
                            color = Color.White,
                            style = MaterialTheme.typography.bodyMedium
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
                text = "Hoe het werkt \uD83D\uDE80",
                color = Color.White,
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier.padding(top = 40.dp)
            )

            // Subtitle
            Text(
                text = "Test je intuïtie. Kun jij echt van AI onderscheiden?",
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
                    text = if (currentStep < 3) "Volgende" else "Laten we gaan!",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White
                )
            }
        }
    }
}
