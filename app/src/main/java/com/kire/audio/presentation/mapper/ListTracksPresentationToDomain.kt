package com.kire.audio.presentation.mapper

import com.kire.audio.presentation.model.Track

fun List<Track>.toDomain() =
    this.map { track ->
        track.toDomain()
    }