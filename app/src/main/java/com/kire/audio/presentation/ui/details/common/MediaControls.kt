package com.kire.audio.presentation.ui.details.common

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateContentSize

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.SkipNext
import androidx.compose.material.icons.rounded.SkipPrevious

import androidx.compose.material3.Icon

import androidx.compose.runtime.Composable

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp

import androidx.media3.session.MediaController

import com.kire.audio.device.audio.util.MediaCommands
import com.kire.audio.presentation.model.state.TrackState
import com.kire.audio.presentation.ui.theme.AudioExtendedTheme
import com.kire.audio.presentation.ui.theme.animation.Animation
import com.kire.audio.presentation.ui.theme.dimen.Dimens
import com.kire.audio.presentation.util.modifier.bounceClick

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
 * @author Михаил Гонтарев (KiREHwYE)
 */
@Composable
fun MediaControls(
    trackState: TrackState,
    mediaController: MediaController?,
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.SpaceEvenly,
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
    skipPreviousIcon: ImageVector = Icons.Rounded.SkipPrevious,
    playIcon: ImageVector = Icons.Rounded.PlayArrow,
    pauseIcon: ImageVector = Icons.Rounded.Pause,
    skipNextIcon: ImageVector = Icons.Rounded.SkipNext,
    iconsTint: Color = AudioExtendedTheme.extendedColors.button,
    playPauseIconSize: Dp = Dimens.playPauseIconSize,
    skipIconsSize: Dp = Dimens.skipIconsSize
) {

    Row(
        modifier = Modifier
            .animateContentSize(Animation.universalFiniteSpring())
            .fillMaxWidth()
            .wrapContentHeight()
            .let { modifier },
        horizontalArrangement = horizontalArrangement,
        verticalAlignment = verticalAlignment
    ) {
        Icon(
            imageVector = skipPreviousIcon,
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
                imageVector =
                    if (it) pauseIcon
                    else playIcon,
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
            imageVector = skipNextIcon,
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