package com.example.realitycheck

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.realitycheck.data.model.ContentItem
import com.example.realitycheck.data.repository.ContentRepository
import com.example.realitycheck.databinding.ActivityGameScreenBinding
import com.example.realitycheck.RealityCheckApplication
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class GameActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGameScreenBinding
    private lateinit var contentRepository: ContentRepository

    private var streakMenuItem: MenuItem? = null

    private var isCorrectImageTop = true
    private var currentStreak = 0
    private var bestStreak = 0
    private var inputEnabled = true

    private var roundType = RoundType.ONE_REAL

    private var topItem: ContentItem? = null
    private var bottomItem: ContentItem? = null

    enum class RoundType {
        ONE_REAL,
        BOTH_AI,
        BOTH_REAL
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityGameScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.app_name)

        contentRepository = (application as RealityCheckApplication).contentRepository
        restoreState(savedInstanceState)

        binding.imageTop.setOnClickListener {
            if (inputEnabled) checkImageAnswer(isTop = true)
        }
        binding.imageBottom.setOnClickListener {
            if (inputEnabled) checkImageAnswer(isTop = false)
        }
        binding.btnBothAi.setOnClickListener {
            if (inputEnabled) checkBothAnswer(guessedAi = true)
        }
        binding.btnBothReal.setOnClickListener {
            if (inputEnabled) checkBothAnswer(guessedAi = false)
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
            android.R.id.home -> { finishAfterTransition(); true }
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
        handleResult(correct = isTop == isCorrectImageTop, tappedTop = isTop)
    }

    private fun checkBothAnswer(guessedAi: Boolean) {
        val correct = when (roundType) {
            RoundType.BOTH_AI   -> guessedAi
            RoundType.BOTH_REAL -> !guessedAi
            RoundType.ONE_REAL  -> false
        }
        handleResult(correct = correct, tappedTop = null)
    }

    private fun handleResult(correct: Boolean, tappedTop: Boolean?) {
        inputEnabled = false
        setButtonsEnabled(false)

        if (tappedTop != null) showOverlay(tappedTop, correct)
        else showBothOverlays(correct)

        if (correct) {
            currentStreak++
            bestStreak = maxOf(bestStreak, currentStreak)
            updateStreakDisplay()

            lifecycleScope.launch {
                delay(ROUND_DELAY)
                clearOverlays()
                inputEnabled = true
                setButtonsEnabled(true)
                loadNewRound()
            }
        } else {
            lifecycleScope.launch {
                delay(ROUND_DELAY)
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
        lifecycleScope.launch {
            val result = contentRepository.getNextPair()

            result.onFailure {
                return@launch
            }

            val (first, second) = result.getOrThrow()
            topItem = first
            bottomItem = second

            roundType = deriveRoundType(first, second)

            when (roundType) {
                RoundType.ONE_REAL -> {
                    isCorrectImageTop = !first.isAi
                    setButtonsEnabled(false)
                }
                RoundType.BOTH_AI, RoundType.BOTH_REAL -> {
                    setButtonsEnabled(true)
                }
            }

            loadImage(first.contentUrl, binding.imageTop)
            loadImage(second.contentUrl, binding.imageBottom)

            inputEnabled = true
        }
    }

    /**
     * Derive the round type from the pair of items returned by the repository.
     * The repository can return any combination; we classify it here so the
     * existing answer-checking logic doesn't need to change.
     */
    private fun deriveRoundType(a: ContentItem, b: ContentItem): RoundType = when {
        a.isAi && b.isAi   -> RoundType.BOTH_AI
        !a.isAi && !b.isAi -> RoundType.BOTH_REAL
        else               -> RoundType.ONE_REAL
    }


    private fun loadImage(url: String?, imageView: ImageView) {
        Glide.with(this)
            .load(url)
            .placeholder(android.R.drawable.ic_menu_gallery)
            .error(android.R.drawable.ic_delete)
            .into(imageView)
    }

    private fun showOverlay(tappedTop: Boolean, correct: Boolean) {
        val color = overlayColor(correct)
        if (tappedTop) {
            binding.overlayTop.setBackgroundColor(color)
            binding.overlayTop.visibility = View.VISIBLE
        } else {
            binding.overlayBottom.setBackgroundColor(color)
            binding.overlayBottom.visibility = View.VISIBLE
        }
    }

    private fun showBothOverlays(correct: Boolean) {
        val color = overlayColor(correct)
        binding.overlayTop.setBackgroundColor(color)
        binding.overlayBottom.setBackgroundColor(color)
        binding.overlayTop.visibility = View.VISIBLE
        binding.overlayBottom.visibility = View.VISIBLE
    }

    private fun clearOverlays() {
        binding.overlayTop.visibility = View.GONE
        binding.overlayBottom.visibility = View.GONE
    }

    private fun overlayColor(correct: Boolean) =
        if (correct) Color.parseColor("#8800FF00") else Color.parseColor("#88FF0000")

    private fun updateStreakDisplay() {
        streakMenuItem?.title = currentStreak.toString()
    }

    private fun setButtonsEnabled(enabled: Boolean) {
        binding.btnBothAi.isEnabled = enabled
        binding.btnBothReal.isEnabled = enabled
        val alpha = if (enabled) 1f else 0.4f
        binding.btnBothAi.alpha = alpha
        binding.btnBothReal.alpha = alpha
    }

    companion object {
        private const val ROUND_DELAY = 1200L
        private const val KEY_STREAK = "key_streak"
        private const val KEY_BEST_STREAK = "key_best_streak"
        private const val KEY_CORRECT_TOP = "key_correct_top"
        private const val KEY_ROUND_TYPE = "key_round_type"
    }
}