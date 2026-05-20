package com.example.realitycheck.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.realitycheck.data.repository.ConnectivityRepository
import kotlinx.coroutines.flow.StateFlow

class NetworkViewModel(application: Application) : AndroidViewModel(application)
{
    private val repository = ConnectivityRepository(application.applicationContext)

    val isOnline: StateFlow<Boolean> = repository.isOnline

    override fun onCleared()
    {
        repository.stop()
        super.onCleared()
    }
}