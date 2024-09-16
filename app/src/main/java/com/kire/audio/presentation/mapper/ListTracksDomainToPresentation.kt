package com.kire.audio.presentation.mapper

import com.kire.audio.domain.model.TrackDomain

fun List<TrackDomain>.toPresentation() =
    this.map { track ->
        track.toPresentation()
    }