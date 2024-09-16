package com.kire.audio.data.mapper

import com.kire.audio.data.model.TrackEntity

fun Map<String, List<TrackEntity>>.toDomain() =
    this.mapValues { pair ->
        pair.value.toDomain()
    }