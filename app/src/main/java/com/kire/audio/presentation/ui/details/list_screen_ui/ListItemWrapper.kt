package com.kire.audio.presentation.ui.details.list_screen_ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle

import androidx.media3.session.MediaController

import com.kire.audio.presentation.model.state.TrackState
import com.kire.audio.presentation.ui.details.common.Divider
import com.kire.audio.presentation.ui.details.common.MediaControls
import com.kire.audio.presentation.ui.details.common.slider.SliderWithDurationAndCurrentPosition
import com.kire.audio.presentation.ui.theme.AudioExtendedTheme
import com.kire.audio.presentation.ui.theme.animation.Animation
import com.kire.audio.presentation.ui.theme.dimen.Dimens
import com.kire.audio.presentation.util.modifier.dynamicPadding
import kotlinx.coroutines.flow.StateFlow

/**
 * Обертка для ListItem, раскрывает панель с дополнительным функционалом вокруг ListItem
 *
 * @param id id трека, соответствующего данному элементу списка
 * @param trackState состояние воспроизведения
 * @param mediaController для управления воспроизведением
 * @param modifier модификатор
 * @param showBottomBar действие при скрытии ListItemWrapper из поля зрения
 * @param goToPlayerScreen навигация на PlayerScreen
 * @param listItem сам ListItem
 *
 * @author Михаил Гонтарев (KiREHwYE)
 */
@Composable
fun ListItemWrapper(
    id: String,
    trackState: StateFlow<TrackState>,
    modifier: Modifier = Modifier,
    mediaController: MediaController? = null,
    showBottomBar: (Boolean) -> Unit = {},
    goToPlayerScreen: () -> Unit = {},
    listItem: @Composable (Modifier, () -> Unit) -> Unit,
) {
    /** Текущее состояние воспроизведения */
    val trackState by trackState.collectAsStateWithLifecycle()

    /** Флаг клика на ListItem */
    var isClicked by rememberSaveable {
        mutableStateOf(false)
    }

    /** Определяет какой трек сейчас играет,
     * то есть должен отрисовывать обертку вокруг себя.
     * Опускает PlayerBottomBar, если обертка в пределах экрана.
     * */
    LaunchedEffect(trackState.currentTrackPlaying?.id) {
        isClicked = (trackState.currentTrackPlaying?.id == id)
            .also { if (it) showBottomBar(false) }
    }

     /** При исчезновении из поля зрения / закрытия ListItemWrapper скрывает PlayerBottomBar */
    DisposableEffect(Unit) {
        onDispose {
            if (isClicked)
                showBottomBar(true)
        }
    }

    /** Отступ от границы обертки внутреннего контента*/
    val padFromBorders by animateDpAsState(
        targetValue = if (isClicked) Dimens.universalPad else 0.dp,
        animationSpec = Animation.universalFiniteSpring()
    )

    val backgroundColor by animateColorAsState(
        targetValue = if (isClicked) AudioExtendedTheme.extendedColors.controlElementsBackground else Color.Transparent,
        animationSpec = Animation.universalFiniteSpring()
    )

    Column(
        modifier = modifier
            .wrapContentSize()
            .clip(RoundedCornerShape(Dimens.universalRoundedCorners))
            .drawBehind { drawRoundRect(color = backgroundColor) }
            .dynamicPadding { padFromBorders }
    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            /** Базовый компонент, представляющий отдельный трек его обложкой, названием и иссполнителем */
            listItem(
                Modifier
                    .weight(1f)
                    .padding(end = Dimens.universalPad)
            ) { isClicked = !isClicked }

            /** Кнопки управления воспроизведением */
            AnimatedVisibility(visible = isClicked) {
                MediaControls(
                    mediaController = mediaController,
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(Dimens.universalColumnAndRowSpacedBy)
                )
            }
        }

        AnimatedVisibility(visible = isClicked) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(Dimens.sliderAndDividerSpacedBy)
            ) {
                /** Слайдер для перемотки трека */
                SliderWithDurationAndCurrentPosition(mediaController = mediaController)

                /** Служит как кнопка для открытия экрана плеера */
                Divider(
                    color = AudioExtendedTheme.extendedColors.sliderDurationAndDivider,
                    modifier = Modifier
                        .pointerInput(Unit) {
                            detectTapGestures {
                                goToPlayerScreen()
                            }
                        }
                )
            }
        }
    }
}