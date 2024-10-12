package com.kire.audio.presentation.ui.details.player_screen_ui.dialog.favourite_panel

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

import androidx.lifecycle.compose.collectAsStateWithLifecycle

import androidx.media3.session.MediaController

import com.kire.audio.device.audio.media_controller.performPlayMedia
import com.kire.audio.presentation.model.PlayerStateParams

import com.kire.audio.presentation.model.Track
import com.kire.audio.presentation.model.state.TrackState
import com.kire.audio.presentation.ui.details.common.ListItem
import com.kire.audio.presentation.model.event.TrackUiEvent
import com.kire.audio.presentation.ui.details.common.Divider
import com.kire.audio.presentation.ui.details.common.RubikFontBasicText
import com.kire.audio.presentation.ui.details.player_screen_ui.panel.favourite_panel.TrackItemFavouriteWrapper
import com.kire.audio.presentation.ui.theme.AudioExtendedTheme
import com.kire.audio.presentation.ui.theme.animation.Animation
import com.kire.audio.presentation.ui.theme.dimen.Dimens
import com.kire.audio.presentation.ui.theme.localization.LocalizationProvider

import kotlinx.coroutines.flow.StateFlow

/**
 * Панель со списком избранных треков
 *
 * @param trackState состояние воспроизведения
 * @param favouriteTracks список избранных треков
 * @param onEvent обработчик UI событий
 * @param mediaController для управления воспроизведением
 *
 * @return Михаил Гонтарев (KiREHwYE)
 */
@Composable
fun FavouritePanel(
    trackState: StateFlow<TrackState>,
    favouriteTracks: StateFlow<List<Track>>,
    onEvent: (TrackUiEvent) -> Unit = {},
    mediaController: MediaController? = null
) {
    /** Текущее значение списка избранных треков */
    val favouriteTracks by favouriteTracks.collectAsStateWithLifecycle()
    /** Текущее состояние воспроизведения */
    val trackState by trackState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .animateContentSize(animationSpec = Animation.universalFiniteSpring())
            .fillMaxWidth()
            .wrapContentHeight()
            .windowInsetsPadding(WindowInsets.displayCutout)
            .padding(horizontal = Dimens.universalPad),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            verticalArrangement = Arrangement.spacedBy(Dimens.universalColumnAndRowSpacedBy),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            /** Заголовок панели */
            RubikFontBasicText(
                text = LocalizationProvider.strings.favouriteDialogHeader,
                style = TextStyle(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 28.sp,
                    color = Color.White
                )
            )

            /** Декоративный разделитель */
            Divider()
        }

        /** Если избранных треков нет, отрисовываем соответствующую надпись */
        AnimatedVisibility(
            visible = favouriteTracks.isEmpty(),
            enter = scaleIn(animationSpec = Animation.universalFiniteSpring())
                    + fadeIn(animationSpec = Animation.universalFiniteSpring()),
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center,
                content = {
                    /** Надпись, уведомляющая о том, что список избранных треков пуст */
                    RubikFontBasicText(
                        text = LocalizationProvider.strings.nothingWasFound,
                        style = TextStyle(
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 28.sp,
                            color = Color.White
                        )
                    )
                }
            )
        }

        /** Список избранного */
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            contentPadding = PaddingValues(vertical = Dimens.universalPad),
            verticalArrangement = Arrangement.spacedBy(Dimens.universalColumnAndRowSpacedBy),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            itemsIndexed(
                items = favouriteTracks,
                key = { _, track ->
                    track.id
                }
            ) { listIndex, track ->

                /** Обертка для избранного трека */
                TrackItemFavouriteWrapper(
                    modifier = Modifier.animateItem(
                        placementSpec = Animation.universalFiniteSpring()
                    ),
                    onHeartClick = {
                        /** Добавляем в избранное или убираем оттуда */
                        trackState.currentTrackPlaying?.let {

                            val newTrack = track.copy(isFavourite = !track.isFavourite)

                            if (it.id == track.id)
                                onEvent(TrackUiEvent
                                    .upsertAndUpdateCurrentTrack(newTrack)
                                )
                            else onEvent(TrackUiEvent
                                .upsertTrack(newTrack)
                            )
                        }
                    },
                    trackItem = { modifier ->
                        /** Базовый элемент списка для отображения некоторой сущности */
                        ListItem(
                            track = track,
                            modifier = modifier,
                            mainTextColor = AudioExtendedTheme.extendedColors.favouritePanelMainTextColor,
                            satelliteTextColor = AudioExtendedTheme.extendedColors.favouritePanelSatelliteTextColor,
                            onClick = {
                                PlayerStateParams.isPlaying =
                                    if (track.id == trackState.currentTrackPlaying?.id)
                                        !PlayerStateParams.isPlaying
                                    else true
                                /** Обновляем информацию о текущем воспроизводимом треке */
                                onEvent(
                                    TrackUiEvent.updateTrackState(
                                        trackState.copy(
                                            currentTrackPlaying = track,
                                            currentTrackPlayingIndex = listIndex
                                        )
                                    )
                                )
                                /** Обрабатываем разные ситуации,
                                 * чтобы понять ставить на паузу/воспроизведение или воспроизводить иной трек */
                                mediaController?.apply {
                                    if (PlayerStateParams.isPlaying && trackState.currentTrackPlaying?.id == track.id)
                                        pause()
                                    else if (!PlayerStateParams.isPlaying && trackState.currentTrackPlaying?.id == track.id) {
                                        prepare()
                                        play()

                                    } else
                                        performPlayMedia(track)
                                }
                            }
                        )
                    }
                )
            }
        }
    }
}