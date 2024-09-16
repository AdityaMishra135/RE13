package com.kire.audio.presentation.mapper

import com.kire.audio.domain.model.TrackDomain

fun Map<String, List<TrackDomain>>.toPresentation() =
    this.mapValues { pair ->
        pair.value.toPresentation()
    }