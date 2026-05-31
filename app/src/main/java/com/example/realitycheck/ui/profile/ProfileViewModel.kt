package com.example.realitycheck.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.realitycheck.data.model.Profile
import com.example.realitycheck.data.repository.ProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ProfileUiState(
    val profile: Profile? = null,
    val isLoading: Boolean = true,
    val error: String? = null
)

class ProfileViewModel(
    private val profileRepository: ProfileRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadProfile()
    }

    fun loadProfile() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            profileRepository.getCurrentUserProfile().fold(
                onSuccess = { profile ->
                    _uiState.value = ProfileUiState(profile = profile, isLoading = false)
                },
                onFailure = { e ->
                    _uiState.value = ProfileUiState(isLoading = false, error = e.message)
                }
            )
        }
    }
}