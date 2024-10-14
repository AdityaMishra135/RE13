package com.kire.audio.presentation.ui.details.common.slider

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight

import androidx.compose.material3.ExperimentalMaterial3Api

import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

import androidx.media3.session.MediaController

import com.kire.audio.presentation.ui.theme.AudioExtendedTheme

import kotlinx.coroutines.delay

import java.util.concurrent.TimeUnit

import kotlin.time.Duration.Companion.seconds

/** Время в минутах и секундах в формате 00:00 */
@Immutable
private data class TimePosition(
    val minutes: Long,
    val seconds: Long
) {
    fun getFormattedTime(): String =
        "${minutes.coerceAtLeast(0L)}:${seconds.coerceAtLeast(0L)}"
}

/**
 * Слайдер для перемотки трека, а также отображения его длительности и текущей позиции в ней
 *
 * @param isPlayerScreen Флаг того, открыт ли сейчас экран плеера или нет
 * @param mediaController Для управления воспроизведением
 *
 * @author Михаил Гонтарев (KiREHwYE)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SliderWithDurationAndCurrentPosition(
    isPlayerScreen: Boolean = false,
    mediaController: MediaController? = null
){
    /** Длительность трека*/
    var duration by rememberSaveable { mutableFloatStateOf(0f) }
    /** Текущая позиция слайдера */
    var sliderPosition by rememberSaveable {
        mutableFloatStateOf(mediaController?.currentPosition?.toFloat() ?: 0f)
    }
    /** Поток взаимодействий */
    val interactionSource = remember { MutableInteractionSource() }

    /** Текущее позиция воспроизведения в минутах и секундах */
    var currentTime by remember { mutableStateOf(TimePosition(0L, 0L)) }
    /** Полная длительность трека в минутах и секундах */
    var totalTime by remember { mutableStateOf(TimePosition(0L, 0L)) }

    /** Обновление полной длительности трека */
    LaunchedEffect(mediaController?.duration) {
        val totalDuration = mediaController?.duration ?: 0L
        duration = totalDuration.toFloat().coerceAtLeast(0f)
        totalTime = TimePosition(
            minutes = TimeUnit.MILLISECONDS.toMinutes(totalDuration),
            seconds = TimeUnit.MILLISECONDS.toSeconds(totalDuration) % 60
        )
    }

    /** Обновление текущей позиции слайдера */
    LaunchedEffect(Unit) {
        while (true) {
            val currentPosition = mediaController?.currentPosition ?: 0L
            sliderPosition = currentPosition.toFloat()

            currentTime = TimePosition(
                minutes = TimeUnit.MILLISECONDS.toMinutes(currentPosition),
                seconds = TimeUnit.MILLISECONDS.toSeconds(currentPosition) % 60
            )

            delay(1.seconds / 30)
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
                mediaController?.seekTo(sliderPosition.toLong())
            },
            interactionSource = interactionSource,
            valueRange = 0f..duration,
            colors = SliderDefaults.colors(
                inactiveTrackColor = if (isPlayerScreen) AudioExtendedTheme.extendedColors.inactiveTrackPlayerScreen else AudioExtendedTheme.extendedColors.inactiveTrack,
                activeTrackColor = if (isPlayerScreen) Color.White else AudioExtendedTheme.extendedColors.activeTrack
            ),
            thumb = {
                /** Ползунок */
                Thumb(
                    currentPosition = currentTime::getFormattedTime,
                    interactionSource = interactionSource,
                    thumbColor = if (isPlayerScreen) AudioExtendedTheme.extendedColors.thumbPlayerScreen else AudioExtendedTheme.extendedColors.thumb,
                    thumbBorderColor = if (isPlayerScreen) Color.White else AudioExtendedTheme.extendedColors.thumbBorder
                )
            }
        )

        /** Текущая позиция воспроизведения и полная длительность трека */
        SliderTexts(
            currentPosition = currentTime::getFormattedTime,
            duration = totalTime::getFormattedTime,
            textColor = if (isPlayerScreen) Color.White else AudioExtendedTheme.extendedColors.sliderDurationAndDivider
        )
    }
}



