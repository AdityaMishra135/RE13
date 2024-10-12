package com.kire.audio.presentation.ui.details.player_screen_ui.panel.lyrics_panel

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Save

import androidx.compose.material3.Icon

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

import androidx.lifecycle.compose.collectAsStateWithLifecycle

import com.kire.audio.presentation.util.modifier.bounceClick
import com.kire.audio.presentation.model.state.ILyricsRequestState
import com.kire.audio.presentation.model.state.LyricsState
import com.kire.audio.presentation.model.state.TrackState
import com.kire.audio.presentation.ui.theme.AudioExtendedTheme
import com.kire.audio.presentation.constants.LyricsRequestMode
import com.kire.audio.presentation.model.event.TrackUiEvent
import com.kire.audio.presentation.ui.details.common.Divider
import com.kire.audio.presentation.ui.details.common.RubikFontBasicText
import com.kire.audio.presentation.ui.theme.dimen.Dimens
import com.kire.audio.presentation.ui.theme.localization.LocalizationProvider
import com.kire.audio.presentation.util.modifier.animatePlacement
import com.kire.audio.presentation.util.rememberDerivedStateOf

import kotlinx.coroutines.flow.StateFlow

/**
 * Заголовок панели текста песни
 *
 * @param trackStateFlow Текущее состояние воспроизведения
 * @param lyricsStateFlow Текущее состояние поиска текста песни
 * @param onEvent Обработчик UI событий
 * @param clearUserInput Очищает поле ввода
 * @param lyricsRequestWithUpdatingTrack Осуществляет запрос текста песни
 *
 * @author Михаил Гонтарев (KiREHwYE)
 */
@Composable
fun LyricsHeader(
    trackStateFlow: StateFlow<TrackState>,
    lyricsStateFlow: StateFlow<LyricsState>,
    onEvent: (TrackUiEvent) -> Unit,
    clearUserInput: () -> Unit,
    lyricsRequestWithUpdatingTrack: (LyricsRequestMode) -> Unit,
) {
    /** Текущее состояние воспроизведения */
    val trackState by trackStateFlow.collectAsStateWithLifecycle()
    /** Текущее состояние поиска текста песни */
    val lyricsState by lyricsStateFlow.collectAsStateWithLifecycle()

    /** Флаг видимости кнопки удаления/очищения пользовательского ввода */
    val showDelete by rememberDerivedStateOf {
        lyricsState.isEditModeEnabled && lyricsState.lyricsRequestMode == LyricsRequestMode.EDIT_CURRENT_TEXT
    }

    /** Флаг видимости кнопки обновления текста песни в автоматическом режиме*/
    val showRefresh by rememberDerivedStateOf {
        lyricsState.lyricsRequestMode != LyricsRequestMode.SELECTOR_IS_VISIBLE
                || trackState.currentTrackPlaying?.lyrics !is ILyricsRequestState.Success
    }

    /** Флаг необходимости запроса текста песни */
    val shouldMakeLyricsRequest by rememberDerivedStateOf {
        (lyricsState.lyricsRequestMode == LyricsRequestMode.BY_LINK || lyricsState.lyricsRequestMode == LyricsRequestMode.BY_TITLE_AND_ARTIST)
                && lyricsState.userInput.isNotEmpty()
    }

    /** Иконка справа от заголовка панели */
    val editOrSaveIcon by rememberDerivedStateOf {
        if (!lyricsState.isEditModeEnabled)
            Icons.Rounded.Edit
        else Icons.Rounded.Save
    }

    /** Тип запроса, который должен быть применен */
    val newLyricsRequestMode by rememberDerivedStateOf {
        if (!lyricsState.isEditModeEnabled)
            LyricsRequestMode.SELECTOR_IS_VISIBLE
        else LyricsRequestMode.AUTOMATIC
    }

    Column(
        modifier = Modifier
            .clipToBounds()
            .animatePlacement()
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.displayCutout),
        verticalArrangement = Arrangement.spacedBy(Dimens.universalPad),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ){

            /** */
            AnimatedContent(
                targetState = showDelete,
                modifier = Modifier
                    .size(Dimens.universalIconSize)
            ) {
                /** Иконка удаления/очищения пользовательского ввода */
                if (it)
                    Icon(
                        imageVector = Icons.Rounded.Delete,
                        contentDescription = "Delete",
                        tint = AudioExtendedTheme.extendedColors.roseAccent,
                        modifier = Modifier
                            .fillMaxSize()
                            .bounceClick {
                                clearUserInput()
                            }
                    )
                else if (showRefresh)
                    /** Иконка обновления текста песни в автоматическом режиме */
                    Icon(
                        imageVector = Icons.Rounded.Refresh,
                        contentDescription = "Refresh",
                        tint = AudioExtendedTheme.extendedColors.roseAccent,
                        modifier = Modifier
                            .fillMaxSize()
                            .bounceClick {
                                lyricsRequestWithUpdatingTrack(LyricsRequestMode.AUTOMATIC)
                            }
                    )
            }

            /** Заголовок - название панели */
            RubikFontBasicText(
                text = LocalizationProvider.strings.lyricsDialogHeader,
                style = TextStyle(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 28.sp,
                    color = Color.White
                )
            )

            /** Иконка начала редактирования или сохранения
             * в зависимости от lyricsState.isEditModeEnabled */
            Icon(
                imageVector = editOrSaveIcon,
                contentDescription = "",
                tint = AudioExtendedTheme.extendedColors.roseAccent,
                modifier = Modifier
                    .size(Dimens.universalIconSize)
                    .bounceClick {
                        onEvent(
                            TrackUiEvent.updateLyricsState(
                                lyricsState.copy(
                                    lyricsRequestMode = newLyricsRequestMode,
                                    isEditModeEnabled = !lyricsState.isEditModeEnabled
                                        .also { isEnabled ->

                                            if (shouldMakeLyricsRequest && isEnabled)
                                                lyricsRequestWithUpdatingTrack(lyricsState.lyricsRequestMode)

                                            else if (lyricsState.lyricsRequestMode == LyricsRequestMode.EDIT_CURRENT_TEXT && isEnabled)

                                                trackState.currentTrackPlaying?.let { track ->
                                                    onEvent(TrackUiEvent
                                                        .upsertAndUpdateCurrentTrack(
                                                            track = track.copy(
                                                                lyrics = ILyricsRequestState.Success(
                                                                    lyricsState.userInput
                                                                )
                                                            )
                                                        )
                                                    )
                                                }
                                        }
                                )
                            )
                        )
                    }
            )
        }

        /** Декоративный разделитель */
        Divider()
    }
}