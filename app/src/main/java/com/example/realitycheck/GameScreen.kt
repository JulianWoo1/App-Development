package com.example.realitycheck

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.realitycheck.databinding.ActivityGameScreenBinding

class GameScreen : AppCompatActivity() {

    private lateinit var binding: ActivityGameScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityGameScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_game_screen)
                    as NavHostFragment

        val navController = navHostFragment.navController

        binding.navView.setupWithNavController(navController)
    }
}