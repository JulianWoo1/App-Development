package com.example.realitycheck.ui.nointernet

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.realitycheck.R
import com.example.realitycheck.utils.NetworkMonitor

class NoInternetFragment : Fragment(R.layout.fragment_no_internet)
{
    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    )
    {
        super.onViewCreated(view, savedInstanceState)

        val retryButton =
            view.findViewById<View>(R.id.retryButton)

        retryButton.setOnClickListener {

            val monitor =
                NetworkMonitor(
                    requireContext(),
                    {}
                )

            if (monitor.hasInternetConnection())
            {
                findNavController().popBackStack()
            }
        }
    }
}