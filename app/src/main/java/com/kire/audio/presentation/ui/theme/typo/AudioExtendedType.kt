package com.kire.audio.presentation.ui.theme.typo

import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.TextStyle

@Immutable
data class AudioExtendedType(
    val screenTitle: TextStyle = TextStyle(),
    val mediumTitle: TextStyle = TextStyle(),
    val mediumTitleSatellite: TextStyle = TextStyle(),
    val smallTitle: TextStyle = TextStyle()
)
