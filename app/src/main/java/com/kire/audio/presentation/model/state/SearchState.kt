package com.kire.audio.presentation.model.state

data class SearchState(
    val isExpanded: Boolean = false,
    val active: Boolean = false,
    val searchText: String = "",
)
