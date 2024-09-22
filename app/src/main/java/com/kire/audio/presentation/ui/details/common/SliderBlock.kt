package com.kire.audio.presentation.ui.details.common

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Label
import androidx.compose.material3.PlainTooltip

import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.ripple

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

import androidx.media3.session.MediaController

import com.kire.audio.presentation.ui.theme.AudioExtendedTheme
import com.kire.audio.presentation.ui.theme.dimen.Dimens

import kotlinx.coroutines.delay

import java.util.concurrent.TimeUnit

import kotlin.time.Duration.Companion.seconds

/**
 * Слайдер для перемотки трека, а также отображения его длительности и текущей позиции в ней
 *
 * @param isPlayerScreen флаг того, открыт ли сейчас экран плеера или нет
 * @param mediaController для управления воспроизведением
 *
 * @author Михаил Гонтарев (KiREHwYE)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SliderBlock(
    isPlayerScreen: Boolean = false,
    mediaController: MediaController?
){

    /** Текущая позиция слайдера */
    var sliderPosition by rememberSaveable {
        mutableFloatStateOf(mediaController?.currentPosition?.toFloat() ?: 0f)
    }

    /** Поток взаимодействий */
    val interactionSource = remember {
        MutableInteractionSource()
    }

    val isFocused by interactionSource.collectIsFocusedAsState()

    /** Текущая позиция слайдера в минутах */
    val minutesCur = TimeUnit.MILLISECONDS.toMinutes(mediaController?.currentPosition ?: 0L)
    /** Текущая позиция слайдера в секундах */
    val secondsCur = TimeUnit.MILLISECONDS.toSeconds(mediaController?.currentPosition ?: 0L) % 60
    /** Длительность трека в минутах */
    val minutesAll = TimeUnit.MILLISECONDS.toMinutes(mediaController?.duration ?: 0L)
    /** Длительность трека в секундах */
    val secondsAll = TimeUnit.MILLISECONDS.toSeconds(mediaController?.duration ?: 0L) % 60

    /** Запуск корутины для обновления позиции слайдера */
    LaunchedEffect(key1 = Unit) {
        while(true) {
            sliderPosition = mediaController?.currentPosition?.toFloat() ?: 0f
            delay(1.seconds / 70)
        }
    }

    /** Колонка со слайдером и информацией о текущей позиции и длительности трека */
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        /** Слайдер */
        Slider(
            modifier = Modifier
                .fillMaxWidth(),
            value = sliderPosition,
            onValueChange = {
                sliderPosition = it
                if (!isFocused) {
                    mediaController?.seekTo(sliderPosition.toLong())
                    mediaController?.play()
                }
            },
            interactionSource = interactionSource,
            valueRange = 0f..(mediaController?.duration?.toFloat() ?: 0f).coerceAtLeast(0f),
            colors = SliderDefaults.colors(
                inactiveTrackColor = if (isPlayerScreen) Color(0xFFACACAC) else AudioExtendedTheme.extendedColors.inactiveTrack,
                activeTrackColor = if (isPlayerScreen) Color.White else AudioExtendedTheme.extendedColors.activeTrack
            ),
            thumb = {
                Label(
                    label = {
                        PlainTooltip(
                            shape = RoundedCornerShape(Dimens.universalRoundedCorner),
                            containerColor = AudioExtendedTheme.extendedColors.roseAccent,
                            modifier = Modifier
                                .wrapContentSize()
                        ) {
                            /** Текущая позиция слайдера */
                            RubikFontText(
                                text = "$minutesCur:$secondsCur",
                                style = TextStyle(
                                    color = Color.White,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            )
                        }
                    },
                    interactionSource = interactionSource
                ) {
                    /** Форма для ползунка */
                    val circleShape = CircleShape

                    /** Ползунок */
                    Spacer(
                        modifier = Modifier
                            .size(Dimens.sliderThumbSize)
                            .indication(
                                interactionSource = interactionSource,
                                indication = ripple(
                                    bounded = false,
                                    radius = Dimens.sliderThumbRippleRadius
                                )
                            )
                            .hoverable(interactionSource = interactionSource)
                            .background(
                                if (isPlayerScreen) Color(0xFFEBEBEB) else AudioExtendedTheme.extendedColors.thumb,
                                circleShape
                            )
                            .border(
                                width = Dimens.sliderThumbBorderWidth,
                                color = if (isPlayerScreen) Color.White else AudioExtendedTheme.extendedColors.thumbBorder,
                                shape = circleShape
                            )
                    )
                }
            }
        )

        /** Содержит текущую позицию и длительность трека */
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
        ) {

            /** Текущая позиция */
            RubikFontText(
                text = "$minutesCur:$secondsCur",
                style = TextStyle(
                    color = if (isPlayerScreen) Color.White else AudioExtendedTheme.extendedColors.sliderDurationAndDivider,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold
                )
            )

            /** Длительность трека */
            RubikFontText(
                text = "${if (minutesAll >= 0) minutesAll else 0}:${if (secondsAll >= 0) secondsAll else 0}",
                style = TextStyle(
                    color = if (isPlayerScreen) Color.White else AudioExtendedTheme.extendedColors.sliderDurationAndDivider,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                )
            )
        }
    }
}