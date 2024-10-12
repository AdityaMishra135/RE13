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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

import androidx.lifecycle.compose.collectAsStateWithLifecycle

import com.kire.audio.presentation.model.state.ILyricsRequestState
import com.kire.audio.presentation.model.state.LyricsState
import com.kire.audio.presentation.model.state.TrackState
import com.kire.audio.presentation.constants.LyricsRequestMode
import com.kire.audio.presentation.model.Track
import com.kire.audio.presentation.model.event.TrackUiEvent
import com.kire.audio.presentation.ui.theme.animation.Animation
import com.kire.audio.presentation.ui.theme.dimen.Dimens
import com.kire.audio.presentation.ui.theme.localization.LocalizationProvider
import com.kire.audio.presentation.util.modifier.animatePlacement
import com.kire.audio.presentation.util.rememberDerivedStateOf

import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * Панель с текстом песни, который вытягивается с Genius.
 * Предоставляет возможность автоматического вытягивания,
 * вытягивания по ссылке и по имени исполнителя и названию
 * трека
 *
 * @param trackStateFlow текущее состояние воспроизведения
 * @param lyricsStateFlow текущее состояние поиска текста песни
 * @param onEvent обработчик UI событий
 * @param getTrackLyricsFromGeniusAndUpdateTrack метод для получения текста песни
 * @param modifier модификатор
 *
 * @author Михаил Гонтарев (KiREHwYE)
 */
@Composable
fun LyricsPanel(
    trackStateFlow: StateFlow<TrackState>,
    lyricsStateFlow: StateFlow<LyricsState>,
    onEvent: (TrackUiEvent) -> Unit,
    getTrackLyricsFromGeniusAndUpdateTrack: suspend (
        track: Track,
        mode: LyricsRequestMode,
        title: String?,
        artist: String?,
        userInput: String?
    ) -> ILyricsRequestState,
    modifier: Modifier = Modifier
) {

    /** Текущее состояние воспроизведения */
    val trackState by trackStateFlow.collectAsStateWithLifecycle()
    /** Текущее состояние поиска текста песни */
    val lyricsState by lyricsStateFlow.collectAsStateWithLifecycle()

    /** Нужно ли очистить поле пользовательского ввода */
    var isClearNeeded by remember {
        mutableStateOf(false)
    }

    /** Текст, который необходимо вывести в соответствии ILyricsRequestState */
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

    /** Область выполнения корутин */
    val coroutineScope = rememberCoroutineScope()

    /** Делает запрос текста песни и обновляет соответствующее поле в данных о треке */
    val lyricsRequestWithUpdatingTrack: (lyricsRequestMode: LyricsRequestMode) -> Unit =
        { lyricsRequestMode ->
            trackState.currentTrackPlaying?.let { track ->
                coroutineScope.launch {
                    onEvent(TrackUiEvent
                        .upsertAndUpdateCurrentTrack(
                            track.copy(
                                lyrics = getTrackLyricsFromGeniusAndUpdateTrack(
                                    track,
                                    lyricsRequestMode,
                                    track.title,
                                    track.artist,
                                    lyricsState.userInput
                                )
                            )
                        )
                    )
                }
            }
    }

    LaunchedEffect(key1 = lyricsState.isEditModeEnabled, key2 = lyricsState.lyricsRequestMode) {
        /** Очищаем пользовательский ввод, если вышли из режима редактирования */
        if (!lyricsState.isEditModeEnabled)
            onEvent(TrackUiEvent
                .updateLyricsState(
                    lyricsState.copy(userInput = "")
                )
            )

        /** Если выбрали автоматический поиск песни, выключаем меню редактирования */
        if (lyricsState.lyricsRequestMode == LyricsRequestMode.AUTOMATIC)
            onEvent(TrackUiEvent
                .updateLyricsState(
                    lyricsState.copy(isEditModeEnabled = false)
                )
            )
    }

    /** Флаг отображения меню редактирования */
    val showLyricsEditOptions by rememberDerivedStateOf {
        lyricsState.isEditModeEnabled && lyricsState.lyricsRequestMode == LyricsRequestMode.SELECTOR_IS_VISIBLE
    }
    /** Флаг отображения поля ввода */
    val showLyricsPickedEditOption by rememberDerivedStateOf {
        lyricsState.isEditModeEnabled && lyricsState.lyricsRequestMode != LyricsRequestMode.AUTOMATIC
    }
    /** Флаг отображения результата запроса текста песни */
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
        verticalArrangement = Arrangement.spacedBy(Dimens.universalColumnAndRowSpacedBy),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            /** Заголовок панели */
            LyricsHeader(
                onEvent = onEvent,
                trackStateFlow = trackStateFlow,
                clearUserInput = {
                    isClearNeeded = true
                },
                lyricsRequestWithUpdatingTrack = lyricsRequestWithUpdatingTrack,
                lyricsStateFlow = lyricsStateFlow,
            )
        }
        item {
            AnimatedContent(targetState = showLyricsEditOptions) {
                if (it)
                    /** Меню редактирования */
                    LyricsEditOptions(
                        lyricsRequestWithUpdatingTrack = {
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
                    /** Поле ввода */
                    LyricsPickedEditOption(
                        isClearNeeded = { isClearNeeded },
                        lyricsRequestMode = { lyricsState.lyricsRequestMode },
                        lyrics = trackState.currentTrackPlaying?.lyrics
                            ?: ILyricsRequestState.OnRequest,
                        updateUserInput = {
                            onEvent(TrackUiEvent
                                .updateLyricsState(
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
            /** Результат запроса текста песни */
            AnimatedContent(targetState = showLyricsResult) {
                if (it)
                    LyricsResult(lyrics = { lyrics })
            }
        }
    }
}