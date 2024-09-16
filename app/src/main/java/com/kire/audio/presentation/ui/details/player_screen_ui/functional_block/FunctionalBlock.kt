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

@Composable
fun FunctionalBlock(
    modifierToExpandPopUpBar: Modifier = Modifier,
    trackState: TrackState,
    onEvent: (TrackUiEvent) -> Unit,
    saveRepeatMode: (Int) -> Unit,
    durationGet: () -> Float,
    mediaController: MediaController?
){

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        verticalArrangement = Arrangement.spacedBy(Dimens.functionalBlockSpacedBy)
    ) {

        SliderBlock(
            isPlayerScreen = true,
            durationGet = durationGet,
            mediaController = mediaController
        )

        ControlBlock(
            modifierToExpandPopUpBar = modifierToExpandPopUpBar,
            trackState = trackState,
            onEvent = onEvent,
            saveRepeatMode = saveRepeatMode,
            mediaController = mediaController,
        )
    }
}