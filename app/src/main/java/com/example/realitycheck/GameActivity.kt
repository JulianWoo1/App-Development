package com.example.realitycheck

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.realitycheck.databinding.ActivityGameScreenBinding
import kotlin.random.Random

class GameActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGameScreenBinding
    private val handler = Handler(Looper.getMainLooper())
    private var streakMenuItem: MenuItem? = null
    private var isCorrectImageTop = true
    private var currentStreak = 0
    private var bestStreak = 0
    private var inputEnabled = true
    private var roundType = RoundType.ONE_REAL

    enum class RoundType { ONE_REAL, BOTH_AI, BOTH_REAL }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityGameScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.app_name)

        restoreState(savedInstanceState)

        binding.imageTop.setOnClickListener {
            if (inputEnabled) {
                checkImageAnswer(isTop = true)
            }
        }

        binding.imageBottom.setOnClickListener {
            if (inputEnabled) {
                checkImageAnswer(isTop = false)
            }
        }

        binding.btnBothAi.setOnClickListener {
            if (inputEnabled) {
                checkBothAnswer(guessedAi = true)
            }
        }

        binding.btnBothReal.setOnClickListener {
            if (inputEnabled) {
                checkBothAnswer(guessedAi = false)
            }
        }

        loadNewRound()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_game, menu)
        streakMenuItem = menu.findItem(R.id.action_streak_game)
        updateStreakDisplay()
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finishAfterTransition()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(KEY_STREAK, currentStreak)
        outState.putInt(KEY_BEST_STREAK, bestStreak)
        outState.putBoolean(KEY_CORRECT_TOP, isCorrectImageTop)
        outState.putString(KEY_ROUND_TYPE, roundType.name)
    }

    override fun onDestroy() {
        handler.removeCallbacksAndMessages(null)
        super.onDestroy()
    }

    private fun restoreState(savedInstanceState: Bundle?) {
        if (savedInstanceState == null) return
        currentStreak = savedInstanceState.getInt(KEY_STREAK)
        bestStreak = savedInstanceState.getInt(KEY_BEST_STREAK)
        isCorrectImageTop = savedInstanceState.getBoolean(KEY_CORRECT_TOP)
        roundType = RoundType.valueOf(
            savedInstanceState.getString(KEY_ROUND_TYPE, RoundType.ONE_REAL.name)!!
        )
    }

    private fun checkImageAnswer(isTop: Boolean) {
        val correct = isTop == isCorrectImageTop
        handleResult(correct = correct, tappedTop = isTop)
    }

    private fun checkBothAnswer(guessedAi: Boolean) {
        val correct = when (roundType) {
            RoundType.BOTH_AI -> guessedAi
            RoundType.BOTH_REAL -> !guessedAi
            RoundType.ONE_REAL -> false
        }
        handleResult(correct = correct, tappedTop = null)
    }

    private fun handleResult(correct: Boolean, tappedTop: Boolean?) {
        inputEnabled = false
        setButtonsEnabled(false)

        if (correct) {
            currentStreak++
            bestStreak = maxOf(bestStreak, currentStreak)
            updateStreakDisplay()

            if (tappedTop != null) showOverlay(tappedTop, correct = true)
            else showBothOverlays(correct = true)

            runDelayed {
                clearOverlays()
                inputEnabled = true
                setButtonsEnabled(true)
                loadNewRound()
            }

        } else {
            if (tappedTop != null) showOverlay(tappedTop, correct = false)
            else showBothOverlays(correct = false)

            runDelayed {
                val resultIntent = Intent().apply {
                    putExtra(MainActivity.EXTRA_FINAL_STREAK, currentStreak)
                }
                setResult(RESULT_OK, resultIntent)
                finishAfterTransition()
            }
        }
    }

    private fun loadNewRound() {
        inputEnabled = false

        roundType = RoundType.entries.random()

        val randomId = (1..MAX_IMAGE_ID - 3).random()
        val realUrl1 = "https://picsum.photos/id/$randomId/$IMAGE_SIZE/$IMAGE_SIZE"
        val realUrl2 = "https://picsum.photos/id/${randomId + 2}/$IMAGE_SIZE/$IMAGE_SIZE"
        val aiUrl1   = "https://picsum.photos/id/${randomId + 1}/$IMAGE_SIZE/$IMAGE_SIZE"
        val aiUrl2   = "https://picsum.photos/id/${randomId + 3}/$IMAGE_SIZE/$IMAGE_SIZE"

        when (roundType) {
            RoundType.ONE_REAL -> {
                isCorrectImageTop = Random.nextBoolean()
                val topUrl    = if (isCorrectImageTop) realUrl1 else aiUrl1
                val bottomUrl = if (isCorrectImageTop) aiUrl1 else realUrl1
                loadImage(topUrl, binding.imageTop)
                loadImage(bottomUrl, binding.imageBottom)
                binding.imageTop.isClickable = true
                binding.imageBottom.isClickable = true
                setButtonsEnabled(false)
            }
            RoundType.BOTH_AI -> {
                loadImage(aiUrl1, binding.imageTop)
                loadImage(aiUrl2, binding.imageBottom)
                binding.imageTop.isClickable = true
                binding.imageBottom.isClickable = true
                setButtonsEnabled(true)
            }
            RoundType.BOTH_REAL -> {
                loadImage(realUrl1, binding.imageTop)
                loadImage(realUrl2, binding.imageBottom)
                binding.imageTop.isClickable = true
                binding.imageBottom.isClickable = true
                setButtonsEnabled(true)
            }
        }

        inputEnabled = true
    }

    private fun loadImage(url: String, imageView: ImageView) {
        Glide.with(binding.root)
            .load(url)
            .placeholder(android.R.drawable.ic_menu_gallery)
            .error(android.R.drawable.ic_delete)
            .into(imageView)
    }

    private fun showOverlay(tappedTop: Boolean, correct: Boolean) {
        val color = if (correct) Color.parseColor("#8800FF00") else Color.parseColor("#88FF0000")
        if (tappedTop) {
            binding.overlayTop.setBackgroundColor(color)
            binding.overlayTop.visibility = View.VISIBLE
        } else {
            binding.overlayBottom.setBackgroundColor(color)
            binding.overlayBottom.visibility = View.VISIBLE
        }
    }

    private fun showBothOverlays(correct: Boolean) {
        val color = if (correct) Color.parseColor("#8800FF00") else Color.parseColor("#88FF0000")
        binding.overlayTop.setBackgroundColor(color)
        binding.overlayBottom.setBackgroundColor(color)
        binding.overlayTop.visibility = View.VISIBLE
        binding.overlayBottom.visibility = View.VISIBLE
    }

    private fun clearOverlays() {
        binding.overlayTop.visibility = View.GONE
        binding.overlayBottom.visibility = View.GONE
    }

    private fun updateStreakDisplay() {
        streakMenuItem?.title = "$currentStreak"
    }

    private fun setButtonsEnabled(enabled: Boolean) {
        binding.btnBothAi.isEnabled = enabled
        binding.btnBothReal.isEnabled = enabled
        val alpha = if (enabled) 1f else 0.4f
        binding.btnBothAi.alpha = alpha
        binding.btnBothReal.alpha = alpha
    }

    private fun runDelayed(action: () -> Unit) {
        handler.postDelayed(action, ROUND_DELAY)
    }

    companion object {
        private const val ROUND_DELAY = 1200L
        private const val IMAGE_SIZE = 600
        private const val MAX_IMAGE_ID = 1000
        private const val KEY_STREAK = "key_streak"
        private const val KEY_BEST_STREAK = "key_best_streak"
        private const val KEY_CORRECT_TOP = "key_correct_top"
        private const val KEY_ROUND_TYPE = "key_round_type"
    }
}