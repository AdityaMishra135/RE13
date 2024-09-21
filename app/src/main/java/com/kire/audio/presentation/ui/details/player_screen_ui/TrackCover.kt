package com.kire.audio.presentation.ui.details.player_screen_ui

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip

import androidx.lifecycle.compose.collectAsStateWithLifecycle

import com.kire.audio.presentation.model.ILyricsRequestState
import com.kire.audio.presentation.model.state.LyricsState
import com.kire.audio.presentation.model.state.TrackState
import com.kire.audio.presentation.constants.LyricsRequestMode
import com.kire.audio.presentation.model.event.TrackUiEvent
import com.kire.audio.presentation.ui.details.common.AsyncImageWithLoading
import com.kire.audio.presentation.ui.theme.dimen.Dimens

import kotlinx.coroutines.flow.StateFlow

@Composable
fun TrackCover(
    trackState: TrackState,
    lyricsState: StateFlow<LyricsState>,
    onEvent: (TrackUiEvent) -> Unit,
    getTrackLyricsFromGenius: suspend (LyricsRequestMode, String?, String?, String?) -> ILyricsRequestState,
    modifierToExpandPopUpBar: Modifier = Modifier
){

    val lyricsUiState by lyricsState.collectAsStateWithLifecycle()

    LaunchedEffect(trackState.currentTrackPlaying?.path) {
        if (trackState.currentTrackPlaying?.lyrics !is ILyricsRequestState.Success
            || trackState.currentTrackPlaying?.lyrics.lyrics.isEmpty())

            trackState.currentTrackPlaying?.let { track ->
                onEvent(
                    TrackUiEvent.updateTrackState(
                        trackState.copy(currentTrackPlaying = track.copy(lyrics = ILyricsRequestState.OnRequest))
                    )
                )
                onEvent(
                    TrackUiEvent.updateTrackState(
                        trackState.copy(
                            currentTrackPlaying =
                            track.copy(
                                lyrics = getTrackLyricsFromGenius(
                                    LyricsRequestMode.AUTOMATIC,
                                    trackState.currentTrackPlaying.title,
                                    trackState.currentTrackPlaying.artist,
                                    lyricsUiState.userInput
                                )
                            ).also {
                                if (it.lyrics is ILyricsRequestState.Success)
                                    onEvent(TrackUiEvent.upsertTrack(it))
                            }
                        )
                    )
                )
            }
    }

    Crossfade(
        targetState = trackState.currentTrackPlaying?.imageUri,
        label = "Track Image in foreground"
    ) {
        AsyncImageWithLoading(
            model = it,
            modifier = modifierToExpandPopUpBar
                .fillMaxWidth()
                .aspectRatio(1f / 1f)
                .clip(RoundedCornerShape(Dimens.universalRoundedCorner))
        )
    }
}