package com.kire.audio.presentation.mapper

import com.kire.audio.domain.constants.LyricsRequestModeDomain
import com.kire.audio.presentation.constants.LyricsRequestMode

fun LyricsRequestMode.toDomain() =
    LyricsRequestModeDomain.valueOf(this.toString())