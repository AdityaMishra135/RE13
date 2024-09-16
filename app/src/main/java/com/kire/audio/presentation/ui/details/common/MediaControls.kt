package com.kire.audio.presentation.ui.details.common

import androidx.annotation.DrawableRes

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateContentSize

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight

import androidx.compose.material3.Icon

import androidx.compose.runtime.Composable

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp

import androidx.media3.session.MediaController

import com.kire.audio.R
import com.kire.audio.device.audio.util.MediaCommands
import com.kire.audio.presentation.model.state.TrackState
import com.kire.audio.presentation.ui.theme.AudioExtendedTheme
import com.kire.audio.presentation.ui.theme.animation.Animation
import com.kire.audio.presentation.ui.theme.dimen.Dimens
import com.kire.audio.presentation.util.bounceClick

/**
 * Кнопки для управления воспроизведением: переход к следующему/предыдущему треку,
 * постановка на паузу и проигрывание
 *
 * @param trackState состояние воспроизведения
 * @param mediaController для управления воспроизведением
 * @param modifier модификатор
 * @param horizontalArrangement распределение по ширине
 * @param verticalAlignment распределение по высоте
 * @param skipPreviousIcon иконка перехода к предыдущему треку
 * @param playIcon иконка начала воспроизведения
 * @param pauseIcon иконка остановки воспроизведения
 * @param skipNextIcon иконка перехода к следующему треку
 * @param iconsTint цвет иконок
 * @param playPauseIconSize размер иконок начала и остановки проигрывания
 * @param skipIconsSize размер иконок перехода
 *
 * @author Michael Gontarev (KiREHwYE)
 */
@Composable
fun MediaControls(
    trackState: TrackState,
    mediaController: MediaController?,
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.SpaceEvenly,
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
    @DrawableRes skipPreviousIcon: Int = R.drawable.skip_previous_button_bottom_sheet,
    @DrawableRes playIcon: Int = R.drawable.play_button_bottom_sheet,
    @DrawableRes pauseIcon: Int = R.drawable.pause_button_bottom_sheet,
    @DrawableRes skipNextIcon: Int = R.drawable.skip_next_button_bottom_sheet,
    iconsTint: Color = AudioExtendedTheme.extendedColors.button,
    playPauseIconSize: Dp = Dimens.playPauseIconSize,
    skipIconsSize: Dp = Dimens.skipIconsSize
) {
    Row(
        modifier = Modifier
            .animateContentSize(
                Animation.universalFiniteSpring()
            )
            .fillMaxWidth()
            .wrapContentHeight()
            .let { modifier },
        horizontalArrangement = horizontalArrangement,
        verticalAlignment = verticalAlignment
    ) {
        Icon(
            painter = painterResource(id = skipPreviousIcon),
            contentDescription = null,
            tint = iconsTint,
            modifier = Modifier
                .size(skipIconsSize)
                .bounceClick {
                    MediaCommands.isPreviousTrackRequired.value = true
                }
        )

        AnimatedContent(targetState = trackState.isPlaying, label = "") {
            Icon(
                painter =
                    if (it)
                        painterResource(id = pauseIcon)
                    else
                        painterResource(id = playIcon),
                contentDescription = null,
                tint = iconsTint,
                modifier = Modifier
                    .size(playPauseIconSize)
                    .bounceClick {
                        mediaController?.apply {
                            if (trackState.isPlaying)
                                pause()
                            else {
                                prepare()
                                play()
                            }
                        }
                    }
            )
        }

        Icon(
            painter = painterResource(id = skipNextIcon),
            contentDescription = null,
            tint = iconsTint,
            modifier = Modifier
                .size(skipIconsSize)
                .bounceClick {
                    MediaCommands.isNextTrackRequired.value = true
                }
        )
    }
}