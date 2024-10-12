package com.kire.audio.presentation.ui.details.album_screen_ui.list_item_album_wrapper

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle

import androidx.media3.session.MediaController

import com.kire.audio.device.audio.media_controller.performPlayMedia
import com.kire.audio.presentation.model.PlayerStateParams
import com.kire.audio.presentation.model.Track
import com.kire.audio.presentation.model.event.TrackUiEvent
import com.kire.audio.presentation.model.state.TrackState
import com.kire.audio.presentation.ui.details.common.AsyncImageWithLoading
import com.kire.audio.presentation.ui.details.common.ListItem
import com.kire.audio.presentation.ui.theme.AudioExtendedTheme
import com.kire.audio.presentation.ui.theme.animation.Animation
import com.kire.audio.presentation.ui.theme.dimen.Dimens
import com.kire.audio.presentation.ui.theme.localization.LocalizationProvider
import com.kire.audio.presentation.util.modifier.dynamicPadding
import com.kire.audio.presentation.util.rememberDerivedStateOf

import kotlinx.coroutines.flow.StateFlow

/**
 * Обертка для ListItem, раскрывает панель с альбомным функционалом вокруг ListItem
 *
 * @param trackStateFlow состояние воспроизведения
 * @param tracks треки из соответствующего альбома, который соответствует данному ListItem в списке альбомов
 * @param onEvent обработчик UI событий
 * @param mediaController для управления воспроизведением
 * @param modifier модификатор
 * @param showBottomBar поднимает PlayerBottomBar при скрытии ListItemWrapper из поля зрения
 * @param onImageClick определяет действие при нажатии на обложку альбома
 *
 * @author Михаил Гонтарев (KiREHwYE)
 */
@Composable
fun ListItemAlbumWrapper(
    trackStateFlow: StateFlow<TrackState>,
    modifier: Modifier = Modifier,
    tracks: List<Track> = emptyList(),
    mediaController: MediaController? = null,
    onEvent: (TrackUiEvent) -> Unit = {},
    showBottomBar: (Boolean) -> Unit = {},
    onImageClick: () -> Unit= {}
) {

    /** Текущее состояние воспроизведения */
    val trackState by trackStateFlow.collectAsStateWithLifecycle()

    /** Поток взаимодействий */
    val interactionSource = remember {
        MutableInteractionSource()
    }

    /** Флаг клика на ListItem */
    var isClicked by rememberSaveable {
        mutableStateOf(false)
    }

    /** Определяет какой альбом сейчас играет, то есть должен отрисовывать обертку вокруг себя */
    LaunchedEffect(trackState.currentList) {
        isClicked = (trackState.currentList == tracks)
            .also { if (it) showBottomBar(false) }
    }

    /** При исчезновении из поля зрения / закрытия ListItemWrapper скрывает BottomBar */
    DisposableEffect(Unit) {
        onDispose {
            if (isClicked)
                showBottomBar(true)
        }
    }
    val padFromBorders by animateDpAsState(
        targetValue = if (isClicked) Dimens.universalPad else 0.dp,
        animationSpec = Animation.universalFiniteSpring()
    )
    val backgroundColor by animateColorAsState(
        targetValue = if (isClicked) AudioExtendedTheme.extendedColors.roseAccent
            else Color.Transparent,
        animationSpec = Animation.universalFiniteSpring()
    )

    /** Первый трек из альбома, из которого будут вытянуты название альбома, автор и обложка */
    val firstTrackInAlbum by rememberDerivedStateOf {
        tracks[0]
    }

    AnimatedContent(
        targetState = isClicked,
        label = "",
        modifier = modifier
    ) { clicked ->

        if (!clicked)
        /** Базовый элемент */
            ListItem(
                mainText = firstTrackInAlbum.album ?: LocalizationProvider.strings.nothingWasFound,
                satelliteText = firstTrackInAlbum.artist,
                leadingImageUri = firstTrackInAlbum.imageUri,
                onClick = {
                    /** Разворачиваем/сворачиваем обертку при клике */
                    isClicked = !isClicked
                    PlayerStateParams.isPlaying = true
                    /** Обновляем состояние играющего на данный мемент трека */
                    onEvent(TrackUiEvent
                        .updateTrackState(
                            trackState.copy(
                                currentList = tracks,
                                currentTrackPlaying = firstTrackInAlbum,
                                currentTrackPlayingIndex = 0
                            )
                        )
                    )
                    /** Начинаем воспроизведение или ставим на паузу */
                    mediaController?.performPlayMedia(firstTrackInAlbum)
                }
            )
        else
        /** Обертка */
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .clip(RoundedCornerShape(Dimens.universalRoundedCorners))
                    .drawBehind {
                        drawRect(color = backgroundColor)
                    }
                    .clickable(
                        indication = null,
                        interactionSource = interactionSource
                    ) { isClicked = !isClicked }
                    .dynamicPadding(top = { padFromBorders }, bottom = { padFromBorders }),
                verticalArrangement = Arrangement.spacedBy(Dimens.listItemAlbumWrapperPadding)
            ) {

                Row(
                    modifier = Modifier
                        .wrapContentSize()
                        .dynamicPadding(start = { padFromBorders }, end = { padFromBorders }),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(Dimens.listItemAlbumWrapperPadding),
                ) {

                    /** Обложка трека */
                    AsyncImageWithLoading(
                        imageUri = firstTrackInAlbum.imageUri,
                        modifier = Modifier
                            .shadow(
                                elevation = Dimens.universalShadowElevation,
                                spotColor = AudioExtendedTheme.extendedColors.shadow,
                                shape = RoundedCornerShape(Dimens.universalRoundedCorners)
                            )
                            .weight(1f)
                            .aspectRatio(1f / 1f)
                            .clip(RoundedCornerShape(Dimens.universalRoundedCorners))
                            .pointerInput(Unit) {
                                detectTapGestures {
                                    onImageClick()
                                }
                            }
                    )

                    /** Кнопки управления воспроизведением + название играющего трека */
                    AlbumMediaBar(
                        trackState = trackState,
                        mediaController = mediaController
                    )
                }

                /** Список с треками из альбома для быстрого доступа к ним без перехода на AlbumScreen */
                AlbumFastAccessBar(
                    trackStateFlow = trackStateFlow,
                    mediaController = mediaController,
                    onEvent = onEvent
                )
            }
    }
}