package com.kire.audio.presentation.ui.details.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

import androidx.lifecycle.compose.collectAsStateWithLifecycle

import androidx.media3.session.MediaController

import com.kire.audio.device.audio.util.MediaCommands
import com.kire.audio.device.audio.util.RepeatMode
import com.kire.audio.presentation.model.state.TrackState

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.isActive

import java.util.concurrent.TimeUnit

import kotlin.time.Duration.Companion.seconds

/**
 * Автоматический переход к следующему треку или повтор текущего в зависимости от RepeatMode
 *
 * @param trackState состояние воспроизведения, включает в себя trackRepeatMode - режим повтора
 * @param mediaController для управления воспроизведением
 *
 * @author Michael Gontarev (KiREHwYE)
 */
@Composable
fun AutoSkipOnRepeatMode(
    trackState: StateFlow<TrackState>,
    mediaController: MediaController?
){
    val trackState by trackState.collectAsStateWithLifecycle()

    /** Текущая позиция слайдера в минутах */
    var minutesCurrent by remember { mutableLongStateOf(0L)}
    /** Текущая позиция слайдера в секундах */
    var secondsCurrent by  remember { mutableLongStateOf(0L) }
    /** Длительность трека в минутах */
    var minutesAll by remember { mutableLongStateOf(0L) }
    /** Длительность трека в секундах */
    var secondsAll by remember { mutableLongStateOf(0L) }

    LaunchedEffect(key1 = trackState.currentTrackPlaying?.path) {

        minutesAll = TimeUnit.MILLISECONDS.toMinutes(trackState.currentTrackPlaying?.duration ?: 0L)
        secondsAll = TimeUnit.MILLISECONDS.toSeconds(trackState.currentTrackPlaying?.duration ?: 0L) % 60

        // Определяет был ли уже осуществлен повтор трека
        MediaCommands.isTrackRepeated.value = false

        // Вычисляется текущая позиция слайдера и если она равна длительности трека,
        // осуществляется переход к следующему треку или повтор текущего в зависимости от trackRepeatMode
        while (isActive) {
            minutesCurrent = TimeUnit.MILLISECONDS.toMinutes(mediaController?.currentPosition ?: 0L)
            secondsCurrent = TimeUnit.MILLISECONDS.toSeconds(mediaController?.currentPosition ?: 0L) % 60

            if ((minutesCurrent >= minutesAll && secondsCurrent >= secondsAll)
                && !(minutesAll == 0L && secondsAll == 0L)
            ) {
                when (trackState.trackRepeatMode) {
                    RepeatMode.REPEAT_ONCE ->
                        MediaCommands.isNextTrackRequired.value = true

                    RepeatMode.REPEAT_TWICE -> {
                        if (!MediaCommands.isTrackRepeated.value) {
                            MediaCommands.isTrackRepeated.value = true
                            MediaCommands.isRepeatRequired.value = true
                        } else
                            MediaCommands.isNextTrackRequired.value = true
                    }
                    RepeatMode.REPEAT_CYCLED ->
                        MediaCommands.isRepeatRequired.value = true
                }
            }

            delay(1.seconds / 70)
        }
    }
}