package com.kire.audio.presentation.ui.details.player_screen_ui.functional_block

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight

import androidx.compose.runtime.Composable

import androidx.compose.ui.Modifier

import androidx.media3.session.MediaController

import com.kire.audio.presentation.model.state.TrackState
import com.kire.audio.presentation.model.event.TrackUiEvent
import com.kire.audio.presentation.ui.details.common.SliderBlock
import com.kire.audio.presentation.ui.theme.dimen.Dimens

/**
 * Блок для управления воспроизведением.
 * Состоит из слайдера, кнопок переключения треков,
 * постановки на паузу/воспроизведение,
 * переключения режима воспроизведения и открытия панели любимых треков.
 *
 * @param modifierToExpandFavouritePanel модификатор для открытия панели любимых треков
 * @param trackState состояние воспроизведения
 * @param onEvent обработчик UI событий
 * @param saveRepeatMode lambda для сохранения режима воспроизведения
 * @param mediaController для управления воспроизведением
 *
 * @author Михаил Гонтарев (KiREHwYE)
 */
@Composable
fun FunctionalBlock(
    trackState: TrackState,
    onEvent: (TrackUiEvent) -> Unit,
    saveRepeatMode: (Int) -> Unit,
    mediaController: MediaController?,
    modifierToExpandFavouritePanel: Modifier = Modifier,
){

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        verticalArrangement = Arrangement.spacedBy(Dimens.functionalBlockSpacedBy)
    ) {

        /** Слайдер для перемотки трека */
        SliderBlock(
            isPlayerScreen = true,
            mediaController = mediaController
        )

        /** Кнопки */
        ControlBlock(
            modifierToExpandFavouritePanel = modifierToExpandFavouritePanel,
            trackState = trackState,
            onEvent = onEvent,
            saveRepeatMode = saveRepeatMode,
            mediaController = mediaController,
        )
    }
}