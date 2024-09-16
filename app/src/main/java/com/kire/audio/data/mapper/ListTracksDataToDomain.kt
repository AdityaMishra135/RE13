package com.kire.audio.data.mapper

import com.kire.audio.data.model.TrackEntity

fun List<TrackEntity>.toDomain() =
    this.map { track ->
        track.toDomain()
    }