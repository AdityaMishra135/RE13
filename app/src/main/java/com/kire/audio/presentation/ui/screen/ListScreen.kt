package com.kire.audio.presentation.ui.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember

import androidx.lifecycle.compose.collectAsStateWithLifecycle

import com.kire.audio.presentation.viewmodel.TrackViewModel

import androidx.compose.runtime.setValue

import androidx.media3.session.MediaController
import com.kire.audio.device.audio.media_controller.performPlayMedia

import com.kire.audio.device.audio.util.PlayerState
import com.kire.audio.device.audio.util.state

import com.kire.audio.presentation.navigation.transitions.ListScreenTransitions
import com.kire.audio.presentation.model.event.TrackUiEvent
import com.kire.audio.presentation.ui.details.common.LazyListMainAndAlbumPattern
import com.kire.audio.presentation.ui.details.common.ListWithTopAndFab
import com.kire.audio.presentation.ui.details.list_screen_ui.top_block.TopBlock
import com.kire.audio.presentation.ui.screen.destinations.AlbumScreenDestination
import com.kire.audio.presentation.ui.screen.destinations.ListAlbumScreenDestination
import com.kire.audio.presentation.ui.screen.destinations.PlayerScreenDestination

import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@RootNavGraph(start = true)
@Destination(style = ListScreenTransitions::class)
@Composable
fun ListScreen(
    trackViewModel: TrackViewModel,
    shiftBottomBar: () -> Unit,
    navigator: DestinationsNavigator,
    mediaController: MediaController?
) {

    var playerState: PlayerState? by remember {
        mutableStateOf(mediaController?.state())
    }

    DisposableEffect(key1 = mediaController) {
        mediaController?.run {
            playerState = state()
        }
        onDispose {
            playerState?.dispose()
        }
    }

    val trackState by trackViewModel.trackState.collectAsStateWithLifecycle()

    val allTracks by trackViewModel.tracks.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        trackViewModel.onEvent(TrackUiEvent.updateArtistWithTracks())
    }

    ListWithTopAndFab(
        listSize = allTracks.size,
        shiftBottomBar = shiftBottomBar,
        topBar = {
            TopBlock(
                trackViewModel = trackViewModel,
                mediaController = mediaController,
                navigateToPlayerScreen = {
                    navigator.navigate(PlayerScreenDestination)
                },
                onTitleClick = {
                    navigator.navigate(ListAlbumScreenDestination)
                    trackViewModel.onEvent(TrackUiEvent.updateArtistWithTracks())
                },
                onAlbumSuggestionClick = { albumTitle ->
                    val album = trackViewModel.artistWithTracks.value[albumTitle]

                    album?.let {
                        trackViewModel.onEvent(
                            TrackUiEvent.updateTrackState(
                                trackState.copy(
                                    currentList = album,
                                    currentTrackPlaying = try {
                                        album[0]
                                    } catch (e: Exception) { null },
                                    currentTrackPlayingIndex = 0,
                                    isPlaying = true
                                )
                            )
                        )
                        navigator.navigate(AlbumScreenDestination)
                        try {
                            mediaController?.performPlayMedia(album[0])
                        } catch (_: Exception) { }
                    }
                }
            )
        }
    ) { modifier, state ->

        LazyListMainAndAlbumPattern(
            trackState = trackViewModel.trackState,
            onEvent = trackViewModel::onEvent,
            list = allTracks,
            mediaController = mediaController,
            state = state,
            goToPlayerScreen = {
                navigator.navigate(PlayerScreenDestination)
            },
            modifier = modifier
        )
    }
}