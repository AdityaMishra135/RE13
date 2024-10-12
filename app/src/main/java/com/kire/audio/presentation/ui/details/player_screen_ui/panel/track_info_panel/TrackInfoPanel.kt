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
import androidx.lifecycle.compose.collectAsStateWithLifecycle

import com.kire.audio.presentation.model.event.TrackUiEvent
import com.kire.audio.presentation.model.state.TrackState
import com.kire.audio.presentation.ui.details.common.PanelHeader
import com.kire.audio.presentation.ui.details.player_screen_ui.panel.DialogGalleryOrPhoto
import com.kire.audio.presentation.ui.theme.dimen.Dimens
import com.kire.audio.presentation.ui.theme.localization.LocalizationProvider
import com.kire.audio.screen.functional.convertLongToTime
import kotlinx.coroutines.flow.StateFlow

import java.util.concurrent.TimeUnit

/**
 * Панель информации о треке
 *
 * @param trackStateFlow состояние воспроизведения
 * @param onEvent обработчик UI событий
 *
 * @author Михаил Гонтарев (KiREHwYE)
 * */
@Composable
fun TrackInfoPanel(
    trackStateFlow: StateFlow<TrackState>,
    onEvent: (TrackUiEvent) -> Unit = {}
) {
    val trackState by trackStateFlow.collectAsStateWithLifecycle()

    /** Флаг открытия диалога для смены обложки трека */
    var openPhotoChangingDialog by remember {
        mutableStateOf(false)
    }

    trackState.currentTrackPlaying.let { track ->

        /** Общая продолжительность трека в минутах */
        val minutesAll = TimeUnit.MILLISECONDS.toMinutes(track?.duration ?: 0L)
        /** Общая продолжительность трека в секундах */
        val secondsAll = TimeUnit.MILLISECONDS.toSeconds(track?.duration ?: 0L) % 60

        LocalizationProvider.strings.apply {

            /** Предствим всю информацию о треке в виде пар название поля-значение. Например, Title - Some title*/
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

            /** Список полей разрешенных для редактирования */
            val editableFields = arrayOf(
                infoDialogTitle,
                infoDialogArtist,
                infoDialogAlbum,
                infoDialogImageUri
            )

            /** Флаг активности редактирования */
            var isEnabled by rememberSaveable { mutableStateOf(false) }

            /* Текущие значения полей */
            var newTitle by rememberSaveable { mutableStateOf(track?.title) }
            var newArtist by rememberSaveable { mutableStateOf(track?.artist) }
            var newAlbum by rememberSaveable { mutableStateOf(track?.album) }


            Column(
                verticalArrangement = Arrangement.spacedBy(Dimens.dialogsHeaderBottomSpacer)
            ){
                PanelHeader(
                    isEnabled = { isEnabled },
                    onClick = {
                        /** Если кликнули на иконку сохранения,
                         * то обновляем данные о треке,
                         * если они реально изменились */
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

                /** Грид с парами название поля - значение. Например, Title - Some title */
                LazyVerticalGrid(
                    modifier = Modifier
                        .fillMaxWidth(),
                    columns = GridCells.Fixed(count = 2),
                    verticalArrangement = Arrangement.spacedBy(Dimens.dialogsSpacedBy)
                ) {

                    for(element in map){
                        item {
                            /** Заголовок поля */
                            GridElementTitle(title = element.key)
                        }
                        item {
                            /** Информация о треке, соответствующая данному заголовку */
                            GridElementInfo(
                                text = element.value ?: nothingWasFound,
                                isEnabled = { isEnabled },
                                isImageURI = element.key == LocalizationProvider.strings.infoDialogImageUri,
                                isEditable = element.key in editableFields,
                                updateText = { newText ->
                                    when(element.key){
                                        infoDialogTitle -> newTitle = newText
                                        infoDialogArtist -> newArtist = newText
                                        infoDialogAlbum -> newAlbum = newText
                                    }
                                },
                                openImageChangingDialog = { isIt ->
                                    openPhotoChangingDialog = isIt
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    /** Диалог для смены обложки трека */
    trackState.currentTrackPlaying?.apply {
        if (openPhotoChangingDialog) {
            DialogGalleryOrPhoto(
                imageUri = imageUri,
                defaultImageUri = defaultImageUri,
                openPhotoChangingDialog = { isIt ->
                    openPhotoChangingDialog = isIt
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