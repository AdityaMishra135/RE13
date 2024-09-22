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

import com.kire.audio.presentation.model.ILyricsRequestState
import com.kire.audio.presentation.model.state.LyricsState
import com.kire.audio.presentation.model.state.TrackState
import com.kire.audio.presentation.constants.LyricsRequestMode
import com.kire.audio.presentation.model.event.TrackUiEvent
import com.kire.audio.presentation.ui.theme.animation.Animation
import com.kire.audio.presentation.ui.theme.dimen.Dimens
import com.kire.audio.presentation.util.modifier.animatePlacement

import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@Composable
fun LyricsPanel(
    trackState: StateFlow<TrackState>,
    lyricsState: StateFlow<LyricsState>,
    onEvent: (TrackUiEvent) -> Unit,
    getTrackLyricsFromGenius: suspend (LyricsRequestMode, String?, String?, String?) -> ILyricsRequestState,
    modifier: Modifier = Modifier
) {

    val trackState by trackState.collectAsStateWithLifecycle()
    val lyricsState by lyricsState.collectAsStateWithLifecycle()

    val coroutineScope = rememberCoroutineScope()

    lyricsState.apply {

        var isClearNeeded by remember {
            mutableStateOf(false)
        }

        val lyricsRequest: (lyricsRequestMode: LyricsRequestMode) -> Unit = {

            onEvent(
                TrackUiEvent.upsertAndUpdateCurrentTrack(
                    trackState.currentTrackPlaying!!.copy(
                        lyrics = ILyricsRequestState.OnRequest
                    )
                )
            )

            coroutineScope.launch {
                onEvent(
                    TrackUiEvent.upsertAndUpdateCurrentTrack(
                        trackState.currentTrackPlaying!!.copy(
                            lyrics = getTrackLyricsFromGenius(
                                lyricsRequestMode,
                                trackState.currentTrackPlaying?.title,
                                trackState.currentTrackPlaying?.artist,
                                userInput
                            )
                        )
                    )
                )
            }
        }

        LaunchedEffect(key1 = isEditModeEnabled) {
            if (!isEditModeEnabled)
                onEvent(
                    TrackUiEvent.updateLyricsState(
                        this@apply.copy(userInput = "")
                    )
                )
        }

        LaunchedEffect(key1 = lyricsRequestMode) {
            if (lyricsRequestMode == LyricsRequestMode.AUTOMATIC)
                onEvent(
                    TrackUiEvent.updateLyricsState(
                        this@apply.copy(isEditModeEnabled = false)
                    )
                )
        }

        LazyColumn(
            modifier = modifier
                .animatePlacement()
                .animateContentSize(
                    animationSpec = Animation.universalFiniteSpring()
                )
                .wrapContentSize()
                .padding(horizontal = Dimens.universalPad),
            contentPadding = PaddingValues(bottom = Dimens.universalPad),
            verticalArrangement = Arrangement.spacedBy(Dimens.columnAndRowUniversalSpacedBy),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                LyricsHeader(
                    onEvent = onEvent,
                    trackState = trackState,
                    clearUserInput = {
                        isClearNeeded = true
                    },
                    lyricsRequest = lyricsRequest,
                    lyricsState = lyricsState,
                )
            }
            item {
                AnimatedContent(targetState = isEditModeEnabled && lyricsRequestMode == LyricsRequestMode.SELECTOR_IS_VISIBLE) {
                    if (it)
                        LyricsEditOptions(
                            lyricsRequest = {
                                lyricsRequest(LyricsRequestMode.AUTOMATIC)
                            },
                            updateLyricsRequestMode = {
                                onEvent(
                                    TrackUiEvent.updateLyricsState(
                                        lyricsState.copy(lyricsRequestMode = it)
                                    )
                                )
                            }
                        )
                    else if (isEditModeEnabled && lyricsRequestMode != LyricsRequestMode.AUTOMATIC)
                        LyricsPickedEditOption(
                            isClearNeeded = isClearNeeded,
                            lyricsRequestMode = lyricsRequestMode,
                            lyrics = trackState.currentTrackPlaying?.lyrics ?: ILyricsRequestState.OnRequest,
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
                AnimatedContent(targetState = !isEditModeEnabled && lyricsRequestMode != LyricsRequestMode.SELECTOR_IS_VISIBLE) {
                    if (it)
                        LyricsResult(
                            lyrics = trackState.currentTrackPlaying?.lyrics ?: ILyricsRequestState.OnRequest,
                        )
                }
            }
        }
    }
}