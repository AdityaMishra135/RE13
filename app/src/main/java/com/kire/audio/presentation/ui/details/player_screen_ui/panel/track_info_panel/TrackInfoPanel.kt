package com.kire.audio.presentation.ui.details.player_screen_ui.panel.track_info_panel

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
import com.kire.audio.presentation.ui.theme.dimen.Dimens
import com.kire.audio.presentation.ui.theme.localization.LocalizationProvider
import com.kire.audio.screen.functional.convertLongToTime

import java.util.concurrent.TimeUnit

@Composable
fun TrackInfoPanel(
    trackState: TrackState,
    onEvent: (TrackUiEvent) -> Unit
) {

    var openDialog by remember {
        mutableStateOf(false)
    }

    trackState.currentTrackPlaying.let { track ->

        val minutesAll = TimeUnit.MILLISECONDS.toMinutes(track?.duration ?: 0L)
        val secondsAll = TimeUnit.MILLISECONDS.toSeconds(track?.duration ?: 0L) % 60

        LocalizationProvider.strings.apply {
            val map = mapOf(
                infoDialogTitle to track?.title,
                infoDialogArtist to track?.artist,
                infoDialogAlbum to (track?.album ?: "0"),
                infoDialogDuration to "$minutesAll:$secondsAll",
                infoDialogFavourite to if (track?.isFavourite == true) yes else no,
                infoDialogDateAdded to convertLongToTime(track?.dateAdded?.toLong() ?: 0),
                infoDialogAlbumId to track?.albumId.toString(),
                infoDialogImageUri to track?.imageUri.toString(),
                infoDialogPath to track?.path
            )

            val editableFields = arrayOf(
                infoDialogTitle,
                infoDialogArtist,
                infoDialogAlbum,
                infoDialogImageUri
            )

            var isEnabled by rememberSaveable { mutableStateOf(false) }

            var newTitle by rememberSaveable { mutableStateOf(track?.title) }
            var newArtist by rememberSaveable { mutableStateOf(track?.artist) }
            var newAlbum by rememberSaveable { mutableStateOf(track?.album) }


            Column(
                verticalArrangement = Arrangement.spacedBy(Dimens.dialogsHeaderBottomSpacer)
            ){
                PanelHeader(
                    isEnabled = isEnabled,
                    onClick = {
                        isEnabled = !isEnabled
                            .also {
                                track?.apply clickApply@{
                                    if (title != newTitle ||
                                        artist != newArtist ||
                                        !album.equals(newAlbum)
                                    )
                                    onEvent(TrackUiEvent
                                        .upsertAndUpdateCurrentTrack(
                                            this@clickApply.copy(
                                                title = newTitle ?: nothingWasFound,
                                                artist = newArtist
                                                    ?: nothingWasFound,
                                                album = newAlbum ?: nothingWasFound
                                            )
                                        ))
                                }
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
                            GridElementTitle(title = element.key)
                        }
                        item {
                            GridElementInfo(
                                text = element.value ?: nothingWasFound,
                                isEnabled = isEnabled,
                                isImageURI = element.key == LocalizationProvider.strings.infoDialogImageUri,
                                isEditable = element.key in editableFields,
                                updateText = { newText ->
                                    when(element.key){
                                        infoDialogTitle -> newTitle = newText
                                        infoDialogArtist -> newArtist = newText
                                        infoDialogAlbum -> newAlbum = newText
                                    }
                                },
                                changeOpenDialog = {isIt ->
                                    openDialog = isIt
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    trackState.currentTrackPlaying?.apply {
        if (openDialog) {
            DialogGalleryOrPhoto(
                imageUri = imageUri,
                defaultImageUri = defaultImageUri,
                changeOpenDialog = {isIt ->
                    openDialog = isIt
                },
                updateUri = { imageUri ->
                    onEvent(TrackUiEvent
                        .upsertAndUpdateCurrentTrack(copy(imageUri = imageUri))
                    )
                },
            )
        }
    }
}