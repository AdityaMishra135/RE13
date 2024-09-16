package com.kire.audio.presentation.ui.details.player_screen_ui.image_lyrics_flip_block

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue

import androidx.compose.ui.Modifier

import androidx.lifecycle.compose.collectAsStateWithLifecycle

import com.kire.audio.presentation.model.ILyricsRequestState
import com.kire.audio.presentation.model.state.LyricsState
import com.kire.audio.presentation.model.state.TrackState
import com.kire.audio.presentation.constants.LyricsRequestMode
import com.kire.audio.presentation.model.event.TrackUiEvent

import kotlinx.coroutines.flow.StateFlow

@Composable
fun ImageLyricsFlipBlock(
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

    ImageCardSide(
        imageUri = trackState.currentTrackPlaying?.imageUri,
        modifierToExpandPopUpBar = modifierToExpandPopUpBar
    )
}