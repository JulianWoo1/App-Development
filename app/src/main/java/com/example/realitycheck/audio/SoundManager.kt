package com.example.realitycheck.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import com.example.realitycheck.R

class SoundManager(context: Context) {

    private val soundPool: SoundPool

    private val clickId: Int
    private val correctId: Int
    private val wrongId: Int
    private val levelUpId: Int

    var isSoundEnabled: Boolean = true
    var isHapticsEnabled: Boolean = true

    init {
        val attributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        soundPool = SoundPool.Builder()
            .setAudioAttributes(attributes)
            .setMaxStreams(5)
            .build()

        clickId = soundPool.load(context, R.raw.click, 1)
        correctId = soundPool.load(context, R.raw.correct, 1)
        wrongId = soundPool.load(context, R.raw.wrong, 1)
        levelUpId = soundPool.load(context, R.raw.level_up, 1)
    }

    fun playClick() {
        if (!isSoundEnabled) return
        soundPool.play(clickId, 1f, 1f, 1, 0, 1f)
    }

    fun playCorrect() {
        if (!isSoundEnabled) return
        soundPool.play(correctId, 1f, 1f, 1, 0, 1f)
    }

    fun playWrong() {
        if (!isSoundEnabled) return
        soundPool.play(wrongId, 1f, 1f, 1, 0, 1f)
    }

    fun playLevelUp() {
        if (!isSoundEnabled) return
        soundPool.play(levelUpId, 1f, 1f, 1, 0, 1f)
    }

    fun vibrate(haptic: androidx.compose.ui.hapticfeedback.HapticFeedback?) {
        if (!isHapticsEnabled) return
        haptic?.performHapticFeedback(
            androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress
        )
    }

    fun release() {
        soundPool.release()
    }
}