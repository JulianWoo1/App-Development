package com.example.realitycheck.data.repository

import android.content.Context
import com.example.realitycheck.utils.NetworkMonitor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ConnectivityRepository(context: Context)
{
    private val _isOnline = MutableStateFlow(true)
    val isOnline: StateFlow<Boolean> = _isOnline

    private val monitor = NetworkMonitor(context) { connected ->
        _isOnline.value = connected
    }

    init {
        monitor.startMonitoring()
    }

    fun stop()
    {
        monitor.stopMonitoring()
    }
}