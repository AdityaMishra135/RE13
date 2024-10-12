package com.kire.audio.presentation.model.state

import androidx.compose.runtime.Immutable
import com.kire.audio.presentation.model.Track

@Immutable
data class AlbumState(
    val tracks: List<Track> = emptyList(),
    val albumTitle: String = "No title"
)