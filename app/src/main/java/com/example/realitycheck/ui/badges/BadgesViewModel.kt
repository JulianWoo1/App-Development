package com.example.realitycheck.ui.badges

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.realitycheck.data.repository.BadgeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class BadgeUiItem(
    val id: String,
    val iconUrl: String,
    val name: String?,
    val description: String?,
    val criteriaDescription: String?,
    val isUnlocked: Boolean,
    val earnedAt: String? = null
)

data class BadgesUiState(
    val badges: List<BadgeUiItem> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)

class BadgesViewModel(
    private val badgeRepository: BadgeRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(BadgesUiState())
    val uiState: StateFlow<BadgesUiState> = _uiState.asStateFlow()

    fun loadBadges() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            badgeRepository.getAllBadges().fold(
                onSuccess = { allBadges ->
                    badgeRepository.getCurrentUserBadges().fold(
                        onSuccess = { userBadges ->
                            val earnedIds = userBadges.map { it.badgeId }.toSet()
                            val items = allBadges.map { badge ->
                                BadgeUiItem(
                                    id = badge.id,
                                    iconUrl = badge.iconUrl,
                                    name = if (badge.id in earnedIds) badge.name else null,
                                    description = if (badge.id in earnedIds) badge.description else null,
                                    criteriaDescription = badge.criteriaDescription,
                                    isUnlocked = badge.id in earnedIds,
                                    earnedAt = userBadges.find { it.badgeId == badge.id }?.earnedAt
                                )
                            }
                            _uiState.value = _uiState.value.copy(
                                badges = items,
                                isLoading = false
                            )
                        },
                        onFailure = { e ->
                            val items = allBadges.map { badge ->
                                BadgeUiItem(
                                    id = badge.id,
                                    iconUrl = badge.iconUrl,
                                    name = null,
                                    description = null,
                                    criteriaDescription = badge.criteriaDescription,
                                    isUnlocked = false
                                )
                            }
                            _uiState.value = _uiState.value.copy(
                                badges = items,
                                isLoading = false,
                                error = null
                            )
                        }
                    )
                },
                onFailure = { e ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = e.message
                    )
                }
            )
        }
    }
}
