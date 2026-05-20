package com.example.realitycheck.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities

class NetworkMonitor(
    context: Context,
    private val onConnectionChanged: (Boolean) -> Unit
)
{
    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE)
                as ConnectivityManager

    private val networkCallback =
        object : ConnectivityManager.NetworkCallback()
        {
            override fun onAvailable(network: Network)
            {
                onConnectionChanged(true)
            }

            override fun onLost(network: Network)
            {
                onConnectionChanged(false)
            }

            override fun onUnavailable()
            {
                onConnectionChanged(false)
            }
        }

    fun startMonitoring()
    {
        onConnectionChanged(hasInternetConnection())

        connectivityManager.registerDefaultNetworkCallback(
            networkCallback
        )
    }

    fun stopMonitoring()
    {
        try
        {
            connectivityManager.unregisterNetworkCallback(
                networkCallback
            )
        }
        catch (_: Exception)
        {
        }
    }

    fun hasInternetConnection(): Boolean
    {
        val activeNetwork =
            connectivityManager.activeNetwork ?: return false

        val capabilities =
            connectivityManager.getNetworkCapabilities(
                activeNetwork
            ) ?: return false

        return capabilities.hasCapability(
            NetworkCapabilities.NET_CAPABILITY_INTERNET
        )
    }
}