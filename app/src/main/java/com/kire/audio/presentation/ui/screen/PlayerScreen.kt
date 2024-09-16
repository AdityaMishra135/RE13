package com.kire.audio.presentation.ui.screen

import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput

import androidx.lifecycle.compose.collectAsStateWithLifecycle

import androidx.activity.compose.BackHandler

import androidx.media3.session.MediaController

import com.kire.audio.presentation.navigation.transitions.PlayerScreenTransitions
import com.kire.audio.presentation.ui.details.player_screen_ui.Background
import com.kire.audio.presentation.ui.details.player_screen_ui.Header
import com.kire.audio.presentation.ui.details.player_screen_ui.TextAndHeart
import com.kire.audio.presentation.ui.details.player_screen_ui.functional_block.FunctionalBlock
import com.kire.audio.presentation.ui.details.player_screen_ui.image_lyrics_flip_block.ImageLyricsFlipBlock
import com.kire.audio.presentation.viewmodel.TrackViewModel
import com.kire.audio.presentation.ui.details.common.BlurPanel
import com.kire.audio.presentation.ui.details.player_screen_ui.dialog.favourite_panel.FavouritePanel
import com.kire.audio.presentation.ui.details.player_screen_ui.image_lyrics_flip_block.LyricsCardSide
import com.kire.audio.presentation.ui.details.player_screen_ui.panel.track_info_panel.TrackInfoPanel
import com.kire.audio.presentation.ui.theme.dimen.Dimens

import com.ramcosta.composedestinations.annotation.Destination

@Destination(style = PlayerScreenTransitions::class)
@Composable
fun PlayerScreen(
    trackViewModel: TrackViewModel,
    mediaController: MediaController?,
    navigateBack: () -> Unit
){

    val trackState by trackViewModel.trackState.collectAsStateWithLifecycle()

    var duration: Float by remember { mutableFloatStateOf(0f) }

    trackState.currentTrackPlaying?.let {
        duration = it.duration.toFloat()
    } ?: 0f

    BackHandler {
        navigateBack()
        return@BackHandler
    }

    BlurPanel(
        onTopOfBlurredPanel1 = {
            TrackInfoPanel(
                trackState = trackState,
                onEvent = trackViewModel::onEvent
            )
        },
        onTopOfBlurredPanel2 = {
            FavouritePanel(
                favouriteTracks = trackViewModel.favouriteTracks,
                trackState = trackState,
                onEvent = trackViewModel::onEvent,
                mediaController = mediaController
            )
        },
        onTopOfBlurredPanel3 = {
            LyricsCardSide(
                trackState = trackViewModel.trackState,
                lyricsState = trackViewModel.lyricsState,
                onEvent = trackViewModel::onEvent,
                getTrackLyricsFromGenius = trackViewModel::getTrackLyricsFromGenius
            )
        }
    ) { modifierToExpandPopUpBar1, modifierToExpandPopUpBar2, modifierToExpandPopUpBar3 ->

        Background(imageUri = trackState.currentTrackPlaying?.imageUri)

        Column(
            modifier = Modifier
                .padding(horizontal = Dimens.universalPad)
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectVerticalDragGestures { _, dragAmount ->
                        if (dragAmount > 50)
                            navigateBack()
                    }
                },
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceAround
        ) {

            Header(
                navigateBack = navigateBack,
                modifierToExpandBlurPanel = modifierToExpandPopUpBar1
            )

            ImageLyricsFlipBlock(
                trackState = trackState,
                lyricsState = trackViewModel.lyricsState,
                getTrackLyricsFromGenius = trackViewModel::getTrackLyricsFromGenius,
                onEvent = trackViewModel::onEvent,
                modifierToExpandPopUpBar = modifierToExpandPopUpBar3
            )

            Column(
                modifier = Modifier
                    .wrapContentHeight()
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(Dimens.columnAndRowUniversalSpacedBy)
            ) {

                TextAndHeart(
                    trackState = trackViewModel.trackState,
                    onEvent = trackViewModel::onEvent
                )

                FunctionalBlock(
                    modifierToExpandPopUpBar = modifierToExpandPopUpBar2,
                    trackState = trackState,
                    onEvent = trackViewModel::onEvent,
                    saveRepeatMode = trackViewModel::saveRepeatMode,
                    mediaController = mediaController,
                    durationGet = { duration },
                )
            }
        }
    }
}