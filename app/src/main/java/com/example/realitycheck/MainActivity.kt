package com.example.realitycheck

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.realitycheck.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var streakMenuItem: MenuItem? = null
    private var lastStreak = 0

    private val gameLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->

        if (result.resultCode == RESULT_OK) {
            lastStreak = result.data?.getIntExtra(EXTRA_FINAL_STREAK, 0) ?: 0
            updateStreakDisplay()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.startButton.setOnClickListener {
            val intent = Intent(this, GameActivity::class.java)
            gameLauncher.launch(intent)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)

        streakMenuItem = menu.findItem(R.id.action_streak)
        updateStreakDisplay()

        return true
    }

    private fun updateStreakDisplay() {
        streakMenuItem?.title = lastStreak.toString()
    }

    companion object {
        const val EXTRA_FINAL_STREAK = "FINAL_STREAK"
    }
}