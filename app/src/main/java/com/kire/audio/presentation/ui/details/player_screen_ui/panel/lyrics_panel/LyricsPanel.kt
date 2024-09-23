package com.kire.audio.presentation.ui.details.player_screen_ui.panel.lyrics_panel

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateContentSize

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

import androidx.lifecycle.compose.collectAsStateWithLifecycle

import com.kire.audio.presentation.model.ILyricsRequestState
import com.kire.audio.presentation.model.state.LyricsState
import com.kire.audio.presentation.model.state.TrackState
import com.kire.audio.presentation.constants.LyricsRequestMode
import com.kire.audio.presentation.model.event.TrackUiEvent
import com.kire.audio.presentation.ui.theme.animation.Animation
import com.kire.audio.presentation.ui.theme.dimen.Dimens
import com.kire.audio.presentation.ui.theme.localization.LocalizationProvider
import com.kire.audio.presentation.util.modifier.animatePlacement
import com.kire.audio.presentation.util.rememberDerivedStateOf

import kotlinx.coroutines.flow.StateFlow

/**
 * Панель с текстом песни, который вытягивается с Genius.
 * Предоставляет возможность автоматического вытягивания,
 * вытягивания по ссылке и по имени исполнителя и названию
 * трека
 *
 * @param trackStateFlow текущее состояние воспроизведения
 * @param lyricsStateFlow текущее состояние поиска текста песни
 * @param onEvent обработчик UI событий
 * @param modifier модификатор
 *
 * @author Михаил Гонтарев (KiREHwYE)
 */
@Composable
fun LyricsPanel(
    trackStateFlow: StateFlow<TrackState>,
    lyricsStateFlow: StateFlow<LyricsState>,
    onEvent: (TrackUiEvent) -> Unit,
    modifier: Modifier = Modifier
) {

    val trackState by trackStateFlow.collectAsStateWithLifecycle()
    val lyricsState by lyricsStateFlow.collectAsStateWithLifecycle()

    var isClearNeeded by remember {
        mutableStateOf(false)
    }

    val lyrics by rememberDerivedStateOf {

        LocalizationProvider.strings.run {

            when(trackState.currentTrackPlaying?.lyrics) {
                is ILyricsRequestState.Success ->
                    (trackState.currentTrackPlaying?.lyrics as ILyricsRequestState.Success).lyrics
                        .ifEmpty { lyricsDialogUnsuccessfulMessage }
                is ILyricsRequestState.Unsuccessful -> lyricsDialogUnsuccessfulMessage
                is ILyricsRequestState.OnRequest -> lyricsDialogWaitingMessage
                null -> lyricsDialogUnsuccessfulMessage
            }
        }
    }

    val lyricsRequestWithUpdatingTrack: (lyricsRequestMode: LyricsRequestMode) -> Unit = { lyricsRequestMode ->
        trackState.currentTrackPlaying?.let { track ->
            onEvent(
                TrackUiEvent.getTrackLyricsFromGeniusAndUpdateTrack(
                    track = track,
                    mode = lyricsRequestMode,
                    title = track.title,
                    artist = track.artist,
                    userInput = lyricsState.userInput
                )
            )
        }
    }

    LaunchedEffect(key1 = lyricsState.isEditModeEnabled, key2 = lyricsState.lyricsRequestMode) {
        if (!lyricsState.isEditModeEnabled)
            onEvent(
                TrackUiEvent.updateLyricsState(
                    lyricsState.copy(userInput = "")
                )
            )

        if (lyricsState.lyricsRequestMode == LyricsRequestMode.AUTOMATIC)
            onEvent(
                TrackUiEvent.updateLyricsState(
                    lyricsState.copy(isEditModeEnabled = false)
                )
            )
    }

    val showLyricsEditOptions by rememberDerivedStateOf {
        lyricsState.isEditModeEnabled && lyricsState.lyricsRequestMode == LyricsRequestMode.SELECTOR_IS_VISIBLE
    }
    val showLyricsPickedEditOption by rememberDerivedStateOf {
        lyricsState.isEditModeEnabled && lyricsState.lyricsRequestMode != LyricsRequestMode.AUTOMATIC
    }
    val showLyricsResult by rememberDerivedStateOf {
        !lyricsState.isEditModeEnabled && lyricsState.lyricsRequestMode != LyricsRequestMode.SELECTOR_IS_VISIBLE
    }

    LazyColumn(
        modifier = modifier
            .animatePlacement()
            .animateContentSize(animationSpec = Animation.universalFiniteSpring())
            .wrapContentSize()
            .padding(horizontal = Dimens.universalPad),
        contentPadding = PaddingValues(bottom = Dimens.universalPad),
        verticalArrangement = Arrangement.spacedBy(Dimens.columnAndRowUniversalSpacedBy),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            LyricsHeader(
                onEvent = onEvent,
                trackState = trackStateFlow,
                clearUserInput = {
                    isClearNeeded = true
                },
                lyricsRequest = lyricsRequestWithUpdatingTrack,
                lyricsState = lyricsStateFlow,
            )
        }
        item {
            AnimatedContent(targetState = showLyricsEditOptions) {
                if (it)
                    LyricsEditOptions(
                        lyricsRequest = {
                            lyricsRequestWithUpdatingTrack(LyricsRequestMode.AUTOMATIC)
                        },
                        updateLyricsRequestMode = {
                            onEvent(
                                TrackUiEvent.updateLyricsState(
                                    lyricsState.copy(lyricsRequestMode = it)
                                )
                            )
                        }
                    )
                else if (showLyricsPickedEditOption)
                    LyricsPickedEditOption(
                        isClearNeeded = isClearNeeded,
                        lyricsRequestMode = lyricsState.lyricsRequestMode,
                        lyrics = trackState.currentTrackPlaying?.lyrics
                            ?: ILyricsRequestState.OnRequest,
                        updateUserInput = {
                            onEvent(
                                TrackUiEvent.updateLyricsState(
                                    lyricsState.copy(userInput = it)
                                )
                            )
                        },
                        changeIsClearNeeded = {
                            isClearNeeded  = true
                        }
                    )

            }
        }
        item {
            AnimatedContent(targetState = showLyricsResult) {
                if (it)
                    LyricsResult(lyrics = lyrics)
            }
        }
    }
}