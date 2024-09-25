package com.kire.audio.presentation.model.state

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import com.kire.audio.device.audio.util.RepeatMode
import com.kire.audio.presentation.model.Track

@Immutable
data class TrackState(
    val currentTrackPlaying: Track? = null,
    val currentTrackPlayingIndex: Int? = null,
    val trackRepeatMode: RepeatMode = RepeatMode.REPEAT_ONCE,
    val currentList: List<Track> = emptyList()
)