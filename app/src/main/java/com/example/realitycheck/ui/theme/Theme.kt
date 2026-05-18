package com.example.realitycheck.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = AuthPrimary,
    onPrimary = AuthOnPrimary,
    background = AuthBackground,
    onBackground = AuthOnSurface,
    surface = AuthSurface,
    onSurface = AuthOnSurface
)

@Composable
fun RealityCheckTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // For now, we only support dark theme as per requirements
    MaterialTheme(
        colorScheme = DarkColorScheme,
        content = content
    )
}
