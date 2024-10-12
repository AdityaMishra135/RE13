package com.kire.audio.presentation.model.state

import androidx.compose.runtime.Immutable
import com.kire.audio.presentation.constants.LyricsRequestMode

@Immutable
data class LyricsState(
    val userInput: String = "",
    val isEditModeEnabled: Boolean = false,
    val lyricsRequestMode: LyricsRequestMode = LyricsRequestMode.AUTOMATIC
)
