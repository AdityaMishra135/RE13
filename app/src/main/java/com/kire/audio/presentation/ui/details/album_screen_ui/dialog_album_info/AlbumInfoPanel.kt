package com.kire.audio.presentation.ui.details.album_screen_ui.dialog_album_info

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue

import androidx.compose.ui.Modifier

import com.kire.audio.presentation.model.event.TrackUiEvent
import com.kire.audio.presentation.model.state.TrackState
import com.kire.audio.presentation.ui.details.common.PanelHeader
import com.kire.audio.presentation.ui.details.player_screen_ui.panel.DialogGalleryOrPhoto
import com.kire.audio.presentation.ui.details.player_screen_ui.panel.track_info_panel.GridElementInfo
import com.kire.audio.presentation.ui.details.player_screen_ui.panel.track_info_panel.GridElementTitle
import com.kire.audio.presentation.ui.theme.dimen.Dimens
import com.kire.audio.presentation.ui.theme.localization.LocalizationProvider

import com.kire.audio.screen.functional.convertLongToTime

import java.util.concurrent.TimeUnit

@Composable
fun AlbumInfoPanel(
    trackState: TrackState,
    onEvent: (TrackUiEvent) -> Unit
) {
    var openDialog by remember {
        mutableStateOf(false)
    }

    val firstTrackInAlbum = trackState.currentList[0]

    trackState.apply {

        val summaryDuration = currentList.sumOf { it.duration }

        val hoursAll = TimeUnit.MILLISECONDS.toHours(summaryDuration)
        val minutesAll = TimeUnit.MILLISECONDS.toMinutes(summaryDuration)
        val secondsAll = TimeUnit.MILLISECONDS.toSeconds(summaryDuration) % 60

        LocalizationProvider.strings.apply {

            val map = mapOf(
                infoDialogAlbum to firstTrackInAlbum.album,
                infoDialogArtist to firstTrackInAlbum.artist,
                infoDialogDuration to "$hoursAll:$minutesAll:$secondsAll",
                infoDialogDateAdded to convertLongToTime(firstTrackInAlbum.dateAdded?.toLong() ?: 0),
                infoDialogAlbumId to currentList[0].albumId.toString(),
                infoDialogImageUri to firstTrackInAlbum.imageUri.toString(),
                infoDialogPath to firstTrackInAlbum.path
            )

            val editableFields = arrayOf(
                infoDialogArtist,
                infoDialogAlbum,
                infoDialogImageUri
            )

            var isEnabled by rememberSaveable { mutableStateOf(false) }

            var newArtist by rememberSaveable { mutableStateOf(firstTrackInAlbum.artist) }
            var newAlbum by rememberSaveable { mutableStateOf(firstTrackInAlbum.album) }

            Column(
                verticalArrangement = Arrangement.spacedBy(Dimens.dialogsHeaderBottomSpacer)
            ){
                PanelHeader(
                    isEnabled = isEnabled,
                    onClick = {
                        isEnabled = !isEnabled.also {

//                            if (it && firstTrackInAlbum.artist != newArtist || !firstTrackInAlbum.album.equals(newAlbum)) {
                                currentList.forEach { track ->
                                    if (trackState.currentTrackPlaying?.id == track.id) {
                                        onEvent(TrackUiEvent
                                            .upsertAndUpdateCurrentTrack(track.copy(
                                                artist = newArtist,
                                                album = newAlbum ?: nothingWasFound
                                            ))
                                        )
                                    } else
                                        onEvent(
                                            TrackUiEvent.upsertTrack(
                                                track.copy(
                                                    artist = newArtist,
                                                    album = newAlbum ?: nothingWasFound
                                                ).also {
                                                    onEvent(TrackUiEvent.updateTrackState(
                                                        trackState.copy(
                                                                currentList = currentList
                                                                    .map { listTrack -> if (it.id == listTrack.id) it else listTrack }
                                                                )
                                                        )
                                                    )
                                                }
                                            )
                                        )
                                }
                                onEvent(TrackUiEvent.updateArtistWithTracks())
//                            }
                        }
                    }
                )

                LazyVerticalGrid(
                    modifier = Modifier
                        .fillMaxWidth(),
                    columns = GridCells.Fixed(count = 2),
                    verticalArrangement = Arrangement.spacedBy(Dimens.dialogsSpacedBy)
                ) {

                    for(element in map){
                        item {
                            GridElementTitle(
                                title = element.key
                            )
                        }
                        item {
                            GridElementInfo(
                                text = element.value ?: nothingWasFound,
                                isEnabled = isEnabled,
                                isImageURI = element.key == infoDialogImageUri,
                                isEditable = element.key in editableFields,
                                updateText = { newText ->
                                    when(element.key){
                                        infoDialogArtist -> newArtist = newText
                                        infoDialogAlbum -> newAlbum = newText
                                    }
                                },
                                openImageChangingDialog = { isIt ->
                                    openDialog = isIt
                                }
                            )
                        }
                    }
                }
            }
        }

        if (openDialog) {
            DialogGalleryOrPhoto(
                imageUri = firstTrackInAlbum.imageUri,
                defaultImageUri = firstTrackInAlbum.defaultImageUri,
                openPhotoChangingDialog = { isIt ->
                    openDialog = isIt
                },
                updateUri = { imageUri ->

//                    if (firstTrackInAlbum.imageUri != imageUri)

                    trackState.currentList.forEach { track ->
                        if (trackState.currentTrackPlaying?.id == track.id) {
                            onEvent(TrackUiEvent
                                .upsertAndUpdateCurrentTrack(track.copy(imageUri = imageUri))
                            )
                        } else {
                            onEvent(
                                TrackUiEvent.upsertTrack(
                                    track.copy(imageUri = imageUri)
                                        .also {
                                            onEvent(
                                                TrackUiEvent.updateTrackState(
                                                    trackState.copy(
                                                        currentList = currentList
                                                            .map { listTrack -> if (it.id == listTrack.id) it else listTrack }
                                                    )
                                                )
                                            )
                                        }
                                )
                            )
                        }
                    }
                    onEvent(TrackUiEvent.updateArtistWithTracks())
                },
            )
        }
    }
}