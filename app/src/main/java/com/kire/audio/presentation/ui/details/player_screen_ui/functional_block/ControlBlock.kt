package com.kire.audio.presentation.ui.details.player_screen_ui.functional_block

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.PlaylistPlay
import androidx.compose.material.icons.rounded.PauseCircle
import androidx.compose.material.icons.rounded.PlayCircle
import androidx.compose.material.icons.rounded.Repeat
import androidx.compose.material.icons.rounded.RepeatOn
import androidx.compose.material.icons.rounded.RepeatOne

import androidx.compose.material3.Icon

import androidx.compose.runtime.Composable

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

import androidx.media3.session.MediaController

import com.kire.audio.device.audio.util.RepeatMode
import com.kire.audio.presentation.util.modifier.bounceClick
import com.kire.audio.presentation.model.state.TrackState
import com.kire.audio.presentation.ui.theme.AudioExtendedTheme
import com.kire.audio.presentation.model.event.TrackUiEvent
import com.kire.audio.presentation.ui.details.common.MediaControls
import com.kire.audio.presentation.ui.theme.dimen.Dimens

/**
 * Кнопки переключения треков,
 * постановки на паузу/воспроизведение,
 * переключения режима воспроизведения и открытия панели любимых треков.
 *
 * @param modifierToExpandFavouritePanel модификатор для открытия панели любимых треков
 * @param trackState состояние воспроизведения
 * @param onEvent обработчик UI событий
 * @param mediaController для управления воспроизведением
 * @param saveRepeatMode действие для сохранения режима воспроизведения
 *
 * @author Михаил Гонтарев (KiREHwYE)
 */
@Composable
fun ControlBlock(
    trackState: TrackState,
    onEvent: (TrackUiEvent) -> Unit,
    mediaController: MediaController?,
    saveRepeatMode: (Int) -> Unit,
    modifierToExpandFavouritePanel: Modifier = Modifier,
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ){

        /** Кнопка для переключения режима воспроизведения:
         * однократный повтор, двойной повтор, циклический повтор
         * */
        Icon(
            when (trackState.trackRepeatMode) {
                RepeatMode.REPEAT_ONCE -> Icons.Rounded.Repeat
                RepeatMode.REPEAT_TWICE -> Icons.Rounded.RepeatOne
                RepeatMode.REPEAT_CYCLED -> Icons.Rounded.RepeatOn
            },
            contentDescription = "RepeatMode",
            tint = AudioExtendedTheme.extendedColors.playerScreenButton,
            modifier = Modifier
                .size(Dimens.playerScreenRepeatAndPlaylistIconSize)
                .bounceClick {
                    onEvent(
                        TrackUiEvent.updateTrackState(
                            trackState
                                .copy(
                                    trackRepeatMode = RepeatMode
                                        .entries[
                                        ((trackState.trackRepeatMode.ordinal + 1) % 3)
                                            .also { rep ->
                                                saveRepeatMode(rep)
                                            }
                                    ]
                                )
                        )
                    )
                }
        )

        Box(
           modifier = Modifier
               .weight(1f)
        ) {
            /**
             * Кнопки для управления воспроизведением:
             * переход к следующему/ предыдущему треку, постановка на паузу и проигрывание
             * */
            MediaControls(
                trackState = trackState,
                mediaController = mediaController,
                modifier = Modifier.align(Alignment.Center),
                playIcon = Icons.Rounded.PlayCircle,
                pauseIcon = Icons.Rounded.PauseCircle,
                skipIconsSize = Dimens.playerScreenSkipIconsSize,
                iconsTint = AudioExtendedTheme.extendedColors.playerScreenButton,
                playPauseIconSize = Dimens.playerScreenPlayPauseIconSize,
                horizontalArrangement = Arrangement.spacedBy(Dimens.columnAndRowUniversalSpacedBy)
            )
        }

        /** Кнопка для открытия панели любимых треков */
        Icon(
            Icons.AutoMirrored.Rounded.PlaylistPlay,
            contentDescription = "Playlist",
            modifier = modifierToExpandFavouritePanel
                .size(Dimens.playerScreenRepeatAndPlaylistIconSize),
            tint = AudioExtendedTheme.extendedColors.playerScreenButton,
        )
    }
}