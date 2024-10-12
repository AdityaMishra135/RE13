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
import com.kire.audio.presentation.ui.details.common.slider.SliderWithDurationAndCurrentPosition
import com.kire.audio.presentation.ui.theme.dimen.Dimens
import kotlinx.coroutines.flow.StateFlow

/**
 * Блок для управления воспроизведением.
 * Состоит из слайдера, кнопок переключения треков,
 * постановки на паузу/воспроизведение,
 * переключения режима воспроизведения и открытия панели любимых треков.
 *
 * @param trackState Состояние воспроизведения
 * @param onEvent Обработчик UI событий
 * @param saveRepeatMode Lambda для сохранения режима воспроизведения
 * @param mediaController Для управления воспроизведением
 * @param expandPanelByNumber Раскрывает панель любимых треков
 *
 * @author Михаил Гонтарев (KiREHwYE)
 */
@Composable
fun FunctionalBlock(
    trackState: StateFlow<TrackState>,
    onEvent: (TrackUiEvent) -> Unit,
    saveRepeatMode: (Int) -> Unit,
    mediaController: MediaController? = null,
    expandPanelByNumber: () -> Unit,
){

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        verticalArrangement = Arrangement.spacedBy(Dimens.functionalBlockSpacedBy)
    ) {

        /** Слайдер для перемотки трека */
        SliderWithDurationAndCurrentPosition(
            isPlayerScreen = true,
            mediaController = mediaController
        )

        /** Кнопки */
        ControlBlock(
            expandPanelByNumber = expandPanelByNumber,
            trackState = trackState,
            onEvent = onEvent,
            saveRepeatMode = saveRepeatMode,
            mediaController = mediaController,
        )
    }
}