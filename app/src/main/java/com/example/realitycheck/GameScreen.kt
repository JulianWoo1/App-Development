package com.example.realitycheck

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.realitycheck.databinding.ActivityGameScreenBinding

class GameScreen : AppCompatActivity() {

    private lateinit var binding: ActivityGameScreenBinding
    private var correctIsTop = true
    private var streak = 0
    private var bestStreak = 0
    private var isClickable = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "RealityCheck"

        binding.imageTop.setOnClickListener {
            if (isClickable) checkAnswer(isTop = true)
        }
        binding.imageBottom.setOnClickListener {
            if (isClickable) checkAnswer(isTop = false)
        }

        loadNewRound()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_game, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        menu.findItem(R.id.action_streak_game)?.title = "$streak"
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun checkAnswer(isTop: Boolean) {
        isClickable = false
        val correct = isTop == correctIsTop

        if (correct) {
            streak++
            if (streak > bestStreak) bestStreak = streak
            showOverlay(isTop, correct = true)
            updateStreakDisplay()

            Handler(Looper.getMainLooper()).postDelayed({
                clearOverlays()
                isClickable = true
                loadNewRound()
            }, 1200)

        } else {
            showOverlay(isTop, correct = false)

            Handler(Looper.getMainLooper()).postDelayed({
                val intent = Intent()
                intent.putExtra("FINAL_STREAK", streak)
                setResult(RESULT_OK, intent)
                finish()
            }, 1200)
        }
    }

    private fun showOverlay(tappedTop: Boolean, correct: Boolean) {
        val color = if (correct)
            Color.parseColor("#8800FF00")
        else
            Color.parseColor("#88FF0000")

        if (tappedTop) {
            binding.overlayTop.setBackgroundColor(color)
            binding.overlayTop.visibility = View.VISIBLE
        } else {
            binding.overlayBottom.setBackgroundColor(color)
            binding.overlayBottom.visibility = View.VISIBLE
        }
    }

    private fun clearOverlays() {
        binding.overlayTop.visibility = View.GONE
        binding.overlayBottom.visibility = View.GONE
    }

    private fun updateStreakDisplay() {
        invalidateOptionsMenu()
    }

    private fun loadNewRound() {
        correctIsTop = (0..1).random() == 0
        isClickable = false

        val randomId = (1..1000).random()
        val realUrl = "https://picsum.photos/id/$randomId/600/600"
        val aiUrl = "https://picsum.photos/id/${randomId + 1}/600/600"

        val topUrl = if (correctIsTop) realUrl else aiUrl
        val bottomUrl = if (correctIsTop) aiUrl else realUrl

        Glide.with(this)
            .load(topUrl)
            .placeholder(android.R.drawable.ic_menu_gallery)
            .into(binding.imageTop)

        Glide.with(this)
            .load(bottomUrl)
            .placeholder(android.R.drawable.ic_menu_gallery)
            .into(binding.imageBottom)

        isClickable = true
    }
}