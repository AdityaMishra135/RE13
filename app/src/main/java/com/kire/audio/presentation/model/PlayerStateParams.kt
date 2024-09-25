package com.kire.audio.presentation.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

object PlayerStateParams {
    var isPlayerBottomBarShown by mutableStateOf(false)
    var isPlaying by mutableStateOf(false)
}