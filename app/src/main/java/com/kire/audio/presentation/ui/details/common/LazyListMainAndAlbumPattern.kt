package com.kire.audio.presentation.ui.details.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

import androidx.lifecycle.compose.collectAsStateWithLifecycle

import androidx.media3.session.MediaController

import com.kire.audio.device.audio.media_controller.performPlayMedia
import com.kire.audio.presentation.model.Track
import com.kire.audio.presentation.model.event.TrackUiEvent
import com.kire.audio.presentation.model.state.TrackState
import com.kire.audio.presentation.ui.details.list_screen_ui.ListItemWrapper
import com.kire.audio.presentation.ui.theme.dimen.Dimens

import kotlinx.coroutines.flow.StateFlow

/**
 * Список для отображения треков
 *
 * @param trackState текущее состояние воспроизведения
 * @param onEvent обработчик событий
 * @param list список треков
 * @param mediaController для управления воспроизведением
 * @param modifier модификатор
 * @param navigateToPlayerScreen навигация на PlayerScreen
 * @param state состояние LazyColumn
 *
 * @author Michael Gontarev (KiREHwYE)
 */
@Composable
fun LazyListMainAndAlbumPattern(
    trackState: StateFlow<TrackState>,
    mediaController: MediaController?,
    modifier: Modifier = Modifier,
    onEvent: (TrackUiEvent) -> Unit = {},
    list: List<Track> = emptyList(),
    navigateToPlayerScreen: () -> Unit = {},
    state: LazyListState = rememberLazyListState()
) {

    val trackState by trackState.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = modifier,
        state = state,
        contentPadding = PaddingValues(vertical = Dimens.universalPad),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Dimens.columnAndRowUniversalSpacedBy),
    ) {

        itemsIndexed(list, key = { _, track -> track.id }) { listIndex, track ->

            ListItemWrapper(
                mediaController = mediaController,
                trackState = trackState,
                track = track,
                showBottomBar = { isShown ->
                    onEvent(
                        TrackUiEvent.updateTrackState(
                            trackState.copy(isPlayerBottomCardShown = isShown)
                        )
                    )
                },
                modifier = Modifier
                    .animateItem(),
                goToPlayerScreen = navigateToPlayerScreen
            ) { trackItemModifier, changeIsClicked ->

                ListItem(
                    modifier = trackItemModifier,
                    track = track,
                    onClick = {
                        changeIsClicked()
                        onEvent(
                            TrackUiEvent.updateTrackState(
                                trackState.copy(
                                    isPlaying = if (track.path == trackState.currentTrackPlaying?.path) !trackState.isPlaying else true,
                                    currentTrackPlaying = track,
                                    currentList = list,
                                    currentTrackPlayingIndex = listIndex
                                )
                            )
                        )
                        mediaController?.apply {
                            if (trackState.isPlaying && trackState.currentTrackPlaying?.path == track.path)
                                pause()
                            else if (!trackState.isPlaying && trackState.currentTrackPlaying?.path == track.path) {
                                prepare()
                                play()

                            } else
                                performPlayMedia(track)
                        }
                    }
                )
            }
        }
    }
}