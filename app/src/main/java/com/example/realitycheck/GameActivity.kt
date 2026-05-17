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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityGameScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.app_name)

        restoreState(savedInstanceState)

        binding.imageTop.setOnClickListener {
            if (inputEnabled) {
                checkAnswer(isTop = true)
            }
        }

        binding.imageBottom.setOnClickListener {
            if (inputEnabled) {
                checkAnswer(isTop = false)
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
    }

    private fun checkAnswer(isTop: Boolean) {

        inputEnabled = false

        val isCorrect = isTop == isCorrectImageTop

        if (isCorrect) {

            currentStreak++
            bestStreak = maxOf(bestStreak, currentStreak)

            showOverlay(
                tappedTop = isTop,
                correct = true
            )

            updateStreakDisplay()

            runDelayed {

                clearOverlays()

                inputEnabled = true

                loadNewRound()
            }

        } else {

            showOverlay(
                tappedTop = isTop,
                correct = false
            )

            runDelayed {

                val resultIntent = Intent().apply {
                    putExtra(
                        MainActivity.EXTRA_FINAL_STREAK,
                        currentStreak
                    )
                }

                setResult(RESULT_OK, resultIntent)

                finishAfterTransition()
            }
        }
    }

    private fun loadNewRound() {

        inputEnabled = false

        isCorrectImageTop = Random.nextBoolean()

        val randomId = (1..MAX_IMAGE_ID).random()

        val realUrl =
            "https://picsum.photos/id/$randomId/$IMAGE_SIZE/$IMAGE_SIZE"

        val aiUrl =
            "https://picsum.photos/id/${randomId + 1}/$IMAGE_SIZE/$IMAGE_SIZE"

        val topUrl =
            if (isCorrectImageTop) realUrl else aiUrl

        val bottomUrl =
            if (isCorrectImageTop) aiUrl else realUrl

        loadImage(topUrl, binding.imageTop)

        loadImage(bottomUrl, binding.imageBottom)

        inputEnabled = true
    }

    private fun loadImage(
        url: String,
        imageView: ImageView
    ) {

        Glide.with(binding.root)
            .load(url)
            .placeholder(android.R.drawable.ic_menu_gallery)
            .error(android.R.drawable.ic_delete)
            .into(imageView)
    }

    private fun showOverlay(
        tappedTop: Boolean,
        correct: Boolean
    ) {

        val overlayColor =
            if (correct) {
                Color.parseColor("#8800FF00")
            } else {
                Color.parseColor("#88FF0000")
            }

        if (tappedTop) {

            binding.overlayTop.setBackgroundColor(overlayColor)
            binding.overlayTop.visibility = View.VISIBLE

        } else {

            binding.overlayBottom.setBackgroundColor(overlayColor)
            binding.overlayBottom.visibility = View.VISIBLE
        }
    }

    private fun clearOverlays() {

        binding.overlayTop.visibility = View.GONE
        binding.overlayBottom.visibility = View.GONE
    }

    private fun updateStreakDisplay() {
        streakMenuItem?.title = currentStreak.toString()
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
    }
}