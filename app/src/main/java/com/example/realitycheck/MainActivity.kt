package com.example.realitycheck

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.realitycheck.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity()
{
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment =
            supportFragmentManager.findFragmentById(
                R.id.nav_host_fragment_activity_main
            ) as NavHostFragment

        val navController = navHostFragment.navController

        binding.bottomNav.setupWithNavController(navController)

        setBottomNavVisible(false)

        navController.addOnDestinationChangedListener { _, destination, _ ->

            when (destination.id)
            {
                R.id.navigation_home,
                R.id.navigation_scores,
                R.id.navigation_badges,
                R.id.navigation_profile ->
                {
                    setBottomNavVisible(true)
                }

                else ->
                {
                    setBottomNavVisible(false)
                }
            }
        }
    }

    private fun setBottomNavVisible(visible: Boolean)
    {
        binding.bottomNav.visibility =
            if (visible) View.VISIBLE else View.GONE
    }
}