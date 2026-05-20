package com.example.realitycheck.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.realitycheck.data.repository.ProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(private val profileRepository: ProfileRepository) : ViewModel() {
    private val _bestStreak = MutableStateFlow(0)
    val bestStreak: StateFlow<Int> = _bestStreak.asStateFlow()

    init {
        loadStreak()
    }

    private fun loadStreak() {
        viewModelScope.launch {
            profileRepository.getCurrentUserProfile().onSuccess { profile ->
                _bestStreak.value = profile.highScoreStreak
            }
        }
    }
}
