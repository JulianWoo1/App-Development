package com.example.realitycheck.ui.onboarding

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.realitycheck.R

class OnboardingFragment : Fragment(R.layout.fragment_onboarding)
{
    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<View>(R.id.skipButton).setOnClickListener {
            findNavController().navigate(R.id.action_global_home)
        }

        view.findViewById<View>(R.id.continueButton).setOnClickListener {
            findNavController().navigate(R.id.action_step1_to_step2)
        }
    }
}