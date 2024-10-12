package com.kire.audio.presentation.ui.details.album_screen_ui.list_item_album_wrapper

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

import androidx.lifecycle.compose.collectAsStateWithLifecycle

import androidx.media3.session.MediaController

import com.kire.audio.device.audio.media_controller.performPlayMedia
import com.kire.audio.presentation.model.PlayerStateParams
import com.kire.audio.presentation.model.event.TrackUiEvent
import com.kire.audio.presentation.model.state.TrackState
import com.kire.audio.presentation.ui.theme.AudioExtendedTheme
import com.kire.audio.presentation.ui.theme.animation.Animation
import com.kire.audio.presentation.ui.theme.dimen.Dimens
import com.kire.audio.presentation.util.rememberDerivedStateOf

import kotlinx.coroutines.flow.StateFlow

/**
 * Список с треками из альбома для быстрого доступа к ним без перехода на AlbumScreen
 *
 * @param trackStateFlow состояние воспроизведения
 * @param mediaController для управления воспроизведением
 * @param onEvent обработчик UI событий
 *
 * @author Михаил Гонтарев (KiREHwYE)
 */
@Composable
fun AlbumFastAccessBar(
    trackStateFlow: StateFlow<TrackState>,
    mediaController: MediaController? = null,
    onEvent: (TrackUiEvent) -> Unit = {}
) {

    val trackState by trackStateFlow.collectAsStateWithLifecycle()

    /** Состояние LazyRow */
    val listState = rememberLazyListState()

    LazyRow(
        state = listState,
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        contentPadding = PaddingValues(horizontal = Dimens.universalPad),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Dimens.universalColumnAndRowSpacedBy)
    ) {
        itemsIndexed(trackState.currentList, key = {_, track -> track.id}) { listIndex, track ->

            val isItCurrentTrackNow by rememberDerivedStateOf {
                trackState.currentTrackPlaying?.id == track.id
            }

            /** Анимирует цвет плитки с названием трека в зависимости от того он ли сейчас играет или нет */
            val animatedBackgroundColor by animateColorAsState(
                targetValue = if (isItCurrentTrackNow) AudioExtendedTheme.extendedColors.orangeAccent
                    else Color.White,
                animationSpec = Animation.universalFiniteSpring()
            )

            /** Анимирует цвет текста названия трека в зависимости от того он ли сейчас играет или нет */
            val animatedTextColor by animateColorAsState(
                targetValue = if (isItCurrentTrackNow) Color.White
                    else Color.Black,
                animationSpec = Animation.universalFiniteSpring()
            )

            val onClickStable = remember {
                {
                    PlayerStateParams.isPlaying =
                        if (track.path == trackState.currentTrackPlaying?.path)
                            !PlayerStateParams.isPlaying
                        else true
                    /** Обновляем играющий трек */
                    onEvent(
                        TrackUiEvent.updateTrackState(
                            trackState.copy(
                                currentTrackPlaying = track,
                                currentTrackPlayingIndex = listIndex,
                            )
                        )
                    )
                    /** Начинаем воспроизведение или ставим на паузу*/
                    mediaController?.apply {
                        if (PlayerStateParams.isPlaying && trackState.currentTrackPlaying?.path == track.path)
                            pause()
                        else if (!PlayerStateParams.isPlaying && trackState.currentTrackPlaying?.path == track.path) {
                            prepare()
                            play()

                        } else
                            performPlayMedia(track)
                    }
                    Unit
                }
            }

            /** Пролистывает до текущего выбранного трека, если он оказывается за пределами поля зрения */
            LaunchedEffect(isItCurrentTrackNow) {
                if (isItCurrentTrackNow)
                    listState.animateScrollToItem(index = listIndex)
            }

            /** Плитка, представляющая некоторый трек из альбома его названием */
            AlbumTrackFastAccessItem(
                trackTitle = track.title,
                animatedBackgroundColor = { animatedBackgroundColor },
                animatedTextColor = { animatedTextColor },
                onClick = onClickStable
            )
        }
    }
}