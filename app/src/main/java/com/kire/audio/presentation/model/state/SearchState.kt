package com.kire.audio.presentation.model.state

import androidx.compose.runtime.Immutable

@Immutable
data class SearchState(
    val isExpanded: Boolean = false,
    val active: Boolean = false,
    val searchText: String = "",
)
