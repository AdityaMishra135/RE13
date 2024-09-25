package com.kire.audio.presentation.ui.details.album_screen_ui.list_item_album_wrapper

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp

import androidx.media3.session.MediaController

import com.kire.audio.device.audio.media_controller.performPlayMedia
import com.kire.audio.presentation.model.PlayerStateParams
import com.kire.audio.presentation.model.Track
import com.kire.audio.presentation.model.event.TrackUiEvent
import com.kire.audio.presentation.model.state.TrackState
import com.kire.audio.presentation.ui.details.common.AsyncImageWithLoading
import com.kire.audio.presentation.ui.details.common.ListItem
import com.kire.audio.presentation.ui.theme.AudioExtendedTheme
import com.kire.audio.presentation.ui.theme.dimen.Dimens
import com.kire.audio.presentation.ui.theme.localization.LocalizationProvider

/**
 * Обертка для ListItem, раскрывает панель с альбомным функционалом вокруг ListItem
 *
 * @param trackState состояние воспроизведения
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
    trackState: TrackState,
    tracks: List<Track>,
    onEvent: (TrackUiEvent) -> Unit,
    mediaController: MediaController?,
    modifier: Modifier = Modifier,
    showBottomBar: (Boolean) -> Unit = {},
    onImageClick: () -> Unit= {}
) {

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

    /** Степень прозрачности фона */
    val backgroundAlpha by animateFloatAsState(targetValue = if (isClicked) 1f else 0f)
    /** Отступ от границы обертки внутреннего контента*/
    val padFromBorders by animateDpAsState(targetValue = if (isClicked) Dimens.universalPad else 0.dp)

    /** Первый трек из альбома, из которого будут вытянуты название альбома, автор и обложка */
    val firstTrackInAlbum = tracks[0]

    AnimatedContent(targetState = isClicked, label = "", modifier = modifier) { clicked ->

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
                    onEvent(
                        TrackUiEvent.updateTrackState(
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
                    .clip(RoundedCornerShape(Dimens.universalRoundedCorner))
                    .background(
                        color = AudioExtendedTheme.extendedColors.roseAccent
                            .copy(alpha = backgroundAlpha),
                        shape = RoundedCornerShape(Dimens.universalRoundedCorner)
                    )
                    .clickable(
                        indication = null,
                        interactionSource = interactionSource
                    ) { isClicked = !isClicked }
                    .padding(vertical = padFromBorders),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                Row(
                    modifier = Modifier
                        .wrapContentSize()
                        .padding(horizontal = padFromBorders),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {

                    /** Обложка трека */
                    AsyncImageWithLoading(
                        model = firstTrackInAlbum.imageUri,
                        modifier = Modifier
                            .shadow(
                                elevation = Dimens.universalShadowElevation,
                                spotColor = AudioExtendedTheme.extendedColors.shadow,
                                shape = RoundedCornerShape(Dimens.universalRoundedCorner)
                            )
                            .weight(1f)
                            .aspectRatio(1f / 1f)
                            .clip(RoundedCornerShape(Dimens.universalRoundedCorner))
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
                    trackState = trackState,
                    mediaController = mediaController,
                    onEvent = onEvent
                )
            }
    }
}