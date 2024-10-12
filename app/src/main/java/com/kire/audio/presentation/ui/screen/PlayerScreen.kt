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
import androidx.compose.runtime.remember

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput

import androidx.lifecycle.compose.collectAsStateWithLifecycle

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf

import androidx.media3.session.MediaController
import com.kire.audio.presentation.constants.LyricsRequestMode
import com.kire.audio.presentation.model.state.ILyricsRequestState
import com.kire.audio.presentation.model.event.TrackUiEvent

import com.kire.audio.presentation.navigation.transitions.PlayerScreenTransitions
import com.kire.audio.presentation.ui.details.player_screen_ui.Background
import com.kire.audio.presentation.ui.details.player_screen_ui.TopButtons
import com.kire.audio.presentation.ui.details.player_screen_ui.TextAndHeart
import com.kire.audio.presentation.ui.details.player_screen_ui.functional_block.FunctionalBlock
import com.kire.audio.presentation.ui.details.player_screen_ui.TrackCover
import com.kire.audio.presentation.viewmodel.TrackViewModel
import com.kire.audio.presentation.ui.details.common.BlurPanel
import com.kire.audio.presentation.ui.details.common.PanelNumber
import com.kire.audio.presentation.ui.details.player_screen_ui.dialog.favourite_panel.FavouritePanel
import com.kire.audio.presentation.ui.details.player_screen_ui.panel.lyrics_panel.LyricsPanel
import com.kire.audio.presentation.ui.details.player_screen_ui.panel.track_info_panel.TrackInfoPanel
import com.kire.audio.presentation.ui.theme.dimen.Dimens

import com.ramcosta.composedestinations.annotation.Destination

/**
 * Экран для управления воспроизведением, добавления в избранное,
 * возможности посмотреть текст песни, вытянув его с Genius.
 * Также предоставляет функциональность для редактирования информации о треке
 *
 * @param trackViewModel ViewModel содержит все необходимые поля и методы для взаимодействия с треками
 * @param mediaController контроллер для управления воспроизведением
 * @param navigateBack метод для закрытия экрана
 *
 * @author Михаил Гонтарев (KiREHwYE)
 */
@Destination(style = PlayerScreenTransitions::class)
@Composable
fun PlayerScreen(
    trackViewModel: TrackViewModel,
    mediaController: MediaController? = null,
    navigateBack: () -> Unit = {}
){
    /** Текущее состояние воспроизведения*/
    val trackState by trackViewModel.trackState.collectAsStateWithLifecycle()

    /** Жест назад для закрытия данного экрана
     * и навигации на предыдущий экран */
    BackHandler {
        navigateBack()
        return@BackHandler
    }

    /** Запускает процесс "вытягивания" текста песни, если его еще нет */
    LaunchedEffect(trackState.currentTrackPlaying?.path) {
        trackState.currentTrackPlaying?.let { track ->

            if (track.lyrics !is ILyricsRequestState.Success || track.lyrics.lyrics.isEmpty()) {
                trackViewModel.onEvent(
                    TrackUiEvent.upsertAndUpdateCurrentTrack(
                        track = track.copy(
                            lyrics = trackViewModel.getTrackLyricsFromGeniusAndUpdateTrack(
                                track = track,
                                mode = LyricsRequestMode.AUTOMATIC,
                                title = track.title,
                                artist = track.artist
                            )
                        )
                    )
                )
            }
        }
    }

    /** Блюрит весь контент и отрисовывает поверх панель либо с информацией о треке,
     * либо со списком любимых треков, либо с текстом песни */
    BlurPanel(
        onTopOfBlurPanel1 = {
            /** Панель с информацией о треке */
            TrackInfoPanel(
                trackStateFlow = trackViewModel.trackState,
                onEvent = trackViewModel::onEvent
            )
        },
        onTopOfBlurPanel2 = {
            /** Панель любимых треков */
            FavouritePanel(
                favouriteTracks = trackViewModel.favouriteTracks,
                trackState = trackViewModel.trackState,
                onEvent = trackViewModel::onEvent,
                mediaController = mediaController
            )
        },
        onTopOfBlurPanel3 = {
            /** Панель с текстом песни */
            LyricsPanel(
                trackStateFlow = trackViewModel.trackState,
                lyricsStateFlow = trackViewModel.lyricsState,
                onEvent = trackViewModel::onEvent,
                getTrackLyricsFromGeniusAndUpdateTrack = trackViewModel::getTrackLyricsFromGeniusAndUpdateTrack
            )
        }
    ) { expandPanelByNumber ->

        val imageUri by remember(trackState.currentTrackPlaying?.imageUri) {
            mutableStateOf(trackState.currentTrackPlaying?.imageUri)
        }

        /** Задний фон в виде растянутой и заблюренной обложки трека */
        Background(imageUri = imageUri)

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

            /** Кнопки вверху экрана: для его сворачивания и для открытия панели с информацией о треке */
            TopButtons(
                navigateBack = navigateBack,
                expandPanelByNumber = { expandPanelByNumber(PanelNumber.FIRST) }
            )

            /** Обложка трека в виде большой картинки по центру экрана */
            TrackCover(
                imageUri = imageUri,
                expandPanelByNumber = { expandPanelByNumber(PanelNumber.THIRD) }
            )

            Column(
                modifier = Modifier
                    .wrapContentHeight()
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(Dimens.universalColumnAndRowSpacedBy)
            ) {

                /** Название трека, исполнитель
                 * и кнопка для добавления в избранное */
                TextAndHeart(
                    trackState = trackViewModel.trackState,
                    onEvent = trackViewModel::onEvent
                )

                /** Функциональная панель для управления воспроизведением
                 * + кнопка для открытия списка любимых композиций */
                FunctionalBlock(
                    expandPanelByNumber = { expandPanelByNumber(PanelNumber.SECOND) },
                    trackState = trackViewModel.trackState,
                    onEvent = trackViewModel::onEvent,
                    saveRepeatMode = trackViewModel::saveRepeatMode,
                    mediaController = mediaController
                )
            }
        }
    }
}