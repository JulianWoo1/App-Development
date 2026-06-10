package com.example.realitycheck.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Badge(
    val id: String,
    val name: String,
    val description: String,
    @SerialName("icon_url") val iconUrl: String,
    @SerialName("criteria_description") val criteriaDescription: String? = null,
    @SerialName("sort_order") val sortOrder: Int = 0
)
