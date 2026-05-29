package com.example.realitycheck.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

private val Background = Color(0xFF050505)
private val CardBg = Color(0xFF0D0D0D)
private val Purple = Color(0xFF5B2EFF)
private val SubtitleGray = Color(0xFF9A9A9A)
private val WelcomeGray = Color(0xFF8E8E93)
private val ProgressBg = Color(0xFF252525)
private val Gold = Color(0xFFD4AF37)
private val Green = Color(0xFF6DDC6D)
private val Orange = Color(0xFFFF8A3D)

@Composable
fun ProfileScreen()
{
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {

            // Top section
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {

                // Avatar
                Box(
                    modifier = Modifier
                        .size(74.dp)
                        .background(
                            Color(0xFF120D2E),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "\uD83E\uDD16",
                        style = MaterialTheme.typography.headlineMedium
                    )

                    // Level badge
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .offset(x = 4.dp, y = 4.dp)
                            .size(28.dp)
                            .background(
                                Gold,
                                RoundedCornerShape(10.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "23",
                            color = Color.White,
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    Text(
                        text = "Player",
                        color = Color.White,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "AI Detection Specialist",
                        color = WelcomeGray,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(top = 4.dp)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {

                        ProfileBadge(
                            emoji = "\uD83D\uDD25",
                            text = "7 streak",
                            color = Orange
                        )

                        ProfileBadge(
                            emoji = "\uD83C\uDFC6",
                            text = "#14 ranked",
                            color = Gold
                        )
                    }
                }

                // Settings button
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .background(
                            CardBg,
                            RoundedCornerShape(14.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Settings,
                        contentDescription = null,
                        tint = WelcomeGray
                    )
                }
            }

            Spacer(modifier = Modifier.height(26.dp))

            // XP Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(
                    containerColor = CardBg
                )
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {

                        Text(
                            text = "Level 23 → 24",
                            color = Color.White,
                            style = MaterialTheme.typography.titleMedium
                        )

                        Text(
                            text = "2.820 / 5.000 XP",
                            color = Purple,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(18.dp))

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
                                .fillMaxWidth(0.56f)
                                .fillMaxHeight()
                                .background(
                                    Brush.horizontalGradient(
                                        colors = listOf(
                                            Purple,
                                            Color(0xFF7B61FF)
                                        )
                                    )
                                )
                        )
                    }

                    Text(
                        text = "2.180 XP until level 24",
                        color = WelcomeGray,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(top = 14.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Stats grid
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    ProfileStatCard(
                        emoji = "\uD83C\uDFAE",
                        value = "87",
                        label = "Played",
                        valueColor = Purple,
                        modifier = Modifier.weight(1f)
                    )

                    ProfileStatCard(
                        emoji = "\uD83C\uDFC6",
                        value = "64%",
                        label = "Win rate",
                        valueColor = Green,
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    ProfileStatCard(
                        emoji = "\uD83D\uDD25",
                        value = "12",
                        label = "Best streak",
                        valueColor = Orange,
                        modifier = Modifier.weight(1f)
                    )

                    ProfileStatCard(
                        emoji = "\uD83C\uDFC5",
                        value = "6",
                        label = "Badges",
                        valueColor = Gold,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Weekly activity
            Text(
                text = "ACTIVITY THIS WEEK",
                color = WelcomeGray,
                style = MaterialTheme.typography.labelLarge
            )

            Spacer(modifier = Modifier.height(14.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = CardBg
                )
            ) {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 22.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {

                    ActivityBar(40.dp, "Ma", false)
                    ActivityBar(62.dp, "Di", false)
                    ActivityBar(28.dp, "Wo", false)
                    ActivityBar(78.dp, "Do", false)
                    ActivityBar(50.dp, "Vr", false)
                    ActivityBar(70.dp, "Za", false)
                    ActivityBar(58.dp, "Zo", true)
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Recent games
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                Text(
                    text = "RECENT GAMES",
                    color = WelcomeGray,
                    style = MaterialTheme.typography.labelLarge
                )

                Text(
                    text = "View all ›",
                    color = Purple,
                    style = MaterialTheme.typography.titleMedium
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            RecentGameCard(
                title = "Classic",
                subtitle = "7/10 correct answers · Today",
                score = "810"
            )

            Spacer(modifier = Modifier.height(12.dp))

            RecentGameCard(
                title = "Speed Run",
                subtitle = "18/24 correct answers · Yesterday",
                score = "2340"
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun ProfileBadge(
    emoji: String,
    text: String,
    color: Color
)
{
    Box(
        modifier = Modifier
            .background(
                color.copy(alpha = 0.12f),
                RoundedCornerShape(14.dp)
            )
            .padding(horizontal = 14.dp, vertical = 8.dp)
    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(text = emoji)

            Spacer(modifier = Modifier.width(6.dp))

            Text(
                text = text,
                color = color,
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}

@Composable
private fun ProfileStatCard(
    emoji: String,
    value: String,
    label: String,
    valueColor: Color,
    modifier: Modifier = Modifier
)
{
    Card(
        modifier = modifier.height(120.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = CardBg
        )
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(18.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {

            Text(
                text = emoji,
                style = MaterialTheme.typography.titleMedium
            )

            Column {

                Text(
                    text = value,
                    color = valueColor,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = label,
                    color = WelcomeGray,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
private fun ActivityBar(
    height: Dp,
    day: String,
    active: Boolean
)
{
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Box(
            modifier = Modifier
                .width(30.dp)
                .height(height)
                .background(
                    if (active) Purple else Color(0xFF1B1B1B),
                    RoundedCornerShape(10.dp)
                )
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = day,
            color = if (active) Purple else WelcomeGray,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun RecentGameCard(
    title: String,
    subtitle: String,
    score: String
)
{
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = CardBg
        )
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .size(10.dp)
                    .background(
                        Color(0xFF6DDC6D),
                        CircleShape
                    )
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = title,
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium
                )

                Text(
                    text = subtitle,
                    color = WelcomeGray,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            Text(
                text = score,
                color = WelcomeGray,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}