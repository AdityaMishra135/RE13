package com.kire.audio.presentation.model.state

import com.kire.audio.presentation.model.Track

data class AlbumState(
    val tracks: List<Track> = emptyList(),
    val albumTitle: String = "No title"
)