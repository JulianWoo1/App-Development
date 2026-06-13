package com.example.realitycheck.audio

import androidx.compose.runtime.staticCompositionLocalOf

val LocalSoundManager =
    staticCompositionLocalOf<SoundManager> {
        error("SoundManager not provided")
    }