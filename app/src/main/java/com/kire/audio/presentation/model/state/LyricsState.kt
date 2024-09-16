package com.kire.audio.presentation.model.state

import com.kire.audio.presentation.constants.LyricsRequestMode

data class LyricsState(
    val userInput: String = "",
    val isEditModeEnabled: Boolean = false,
    val lyricsRequestMode: LyricsRequestMode = LyricsRequestMode.AUTOMATIC
)
