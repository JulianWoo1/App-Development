package com.example.realitycheck.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val BgDeep = Color(0xFF050505)
private val CardBg = Color(0xFF0D0D0D)
private val ButtonPurple = Color(0xFF5B2EFF)
private val TextMuted = Color(0xFFA0A0A0)
private val SectionLabel = Color(0xFFB0B0B0)

@Composable
fun SettingsScreen(
    username: String,
    email: String,
    onBackClick: () -> Unit = {},
    onUsernameClick: () -> Unit = {},
    onGuideClick: () -> Unit = {},
    onPrivacyClick: () -> Unit = {},
    onAboutClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {}
) {

    var showLogoutDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDeep)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 24.dp)
    ) {

        IconButton(
            onClick = onBackClick,
            modifier = Modifier.size(52.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.ArrowBack,
                contentDescription = null,
                tint = Color.White
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Settings",
            color = Color.White,
            fontSize = 28.sp,
            fontWeight = FontWeight.ExtraBold
        )

        Text(
            text = "Manage your account and preferences",
            color = TextMuted,
            fontSize = 16.sp,
            modifier = Modifier.padding(top = 4.dp)
        )

        Spacer(modifier = Modifier.height(36.dp))

        SectionHeader("PROFILE")

        Spacer(modifier = Modifier.height(12.dp))

        SettingsItemCard(
            icon = Icons.Outlined.Person,
            title = "Username",
            subtitle = "Change your username",
            trailingText = username,
            iconColor = ButtonPurple,
            onClick = onUsernameClick
        )

        Spacer(modifier = Modifier.height(12.dp))

        SettingsItemCard(
            icon = Icons.Outlined.Email,
            title = "Email Address",
            subtitle = email,
            iconColor = ButtonPurple,
            onClick = {},
            showChevron = false
        )

        Spacer(modifier = Modifier.height(32.dp))

        SectionHeader("OTHER")

        Spacer(modifier = Modifier.height(12.dp))

        SettingsItemCard(
            icon = Icons.Outlined.MenuBook,
            title = "Game Guide",
            subtitle = "Learn how to play and perform",
            iconColor = ButtonPurple,
            onClick = onGuideClick
        )

        Spacer(modifier = Modifier.height(12.dp))

        SettingsItemCard(
            icon = Icons.Outlined.Shield,
            title = "Privacy",
            subtitle = "Learn how we handle your data",
            iconColor = ButtonPurple,
            onClick = onPrivacyClick
        )

        Spacer(modifier = Modifier.height(12.dp))

        SettingsItemCard(
            icon = Icons.Outlined.Info,
            title = "App Information",
            subtitle = "Version, credits, and details",
            iconColor = ButtonPurple,
            onClick = onAboutClick
        )

        Spacer(modifier = Modifier.height(32.dp))

        SectionHeader("SESSION")

        Spacer(modifier = Modifier.height(12.dp))

        SettingsItemCard(
            icon = Icons.Outlined.Logout,
            title = "Log Out",
            subtitle = "Sign out of your account",
            iconColor = Color(0xFFFF5B5B),
            titleColor = Color(0xFFFF5B5B),
            onClick = {
                showLogoutDialog = true
            }
        )

        Spacer(modifier = Modifier.height(24.dp))

        AppInfoCard()

        Spacer(modifier = Modifier.height(32.dp))
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = {
                showLogoutDialog = false
            },
            title = {
                Text("Log Out")
            },
            text = {
                Text("Are you sure you want to log out?")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showLogoutDialog = false
                        onLogoutClick()
                    }
                ) {
                    Text("Log Out", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showLogoutDialog = false
                    }
                ) {
                    Text("Cancel", color = Color.White)
                }
            }
        )
    }
}

@Composable
private fun SectionHeader(text: String) {
    Text(
        text = text,
        color = SectionLabel,
        fontSize = 12.sp,
        letterSpacing = 2.sp
    )
}

@Composable
private fun SettingsItemCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    trailingText: String? = null,
    titleColor: Color = Color.White,
    iconColor: Color = ButtonPurple,
    onClick: () -> Unit,
    showChevron: Boolean = true
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(88.dp),
        shape = RoundedCornerShape(22.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = CardBg
        ),
        contentPadding = PaddingValues(horizontal = 18.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .size(52.dp)
                    .background(
                        iconColor.copy(alpha = 0.12f),
                        RoundedCornerShape(16.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    color = titleColor,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp
                )

                Text(
                    text = subtitle,
                    color = TextMuted,
                    fontSize = 14.sp
                )
            }

            if (trailingText != null) {
                Text(
                    text = trailingText,
                    color = ButtonPurple,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp
                )

                Spacer(modifier = Modifier.width(8.dp))
            }

            if (showChevron) {
                Icon(
                    imageVector = Icons.Outlined.ChevronRight,
                    contentDescription = null,
                    tint = TextMuted
                )
            }
        }
    }
}

@Composable
private fun AppInfoCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = CardBg
        )
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        ButtonPurple.copy(alpha = 0.15f),
                        RoundedCornerShape(14.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Settings,
                    contentDescription = null,
                    tint = ButtonPurple
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "RealityCheck",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )

            Text(
                text = "Version 1.0.0",
                color = TextMuted,
                fontSize = 14.sp
            )
        }
    }
}