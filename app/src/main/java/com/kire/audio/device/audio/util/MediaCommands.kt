package com.kire.audio.device.audio.util

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

object MediaCommands {
    var isPlayRequired by mutableStateOf(true)
    var isNextTrackRequired by mutableStateOf(false)
    var isPreviousTrackRequired by mutableStateOf(false)
    var isRepeatRequired by mutableStateOf(false)
    var isTrackRepeated by mutableStateOf(false)
}