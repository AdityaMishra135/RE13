package com.kire.audio.presentation.ui.details.player_screen_ui.panel.lyrics_panel

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import com.kire.audio.presentation.model.ILyricsRequestState
import com.kire.audio.presentation.model.state.LyricsState
import com.kire.audio.presentation.model.state.TrackState
import com.kire.audio.presentation.ui.theme.AudioExtendedTheme
import com.kire.audio.presentation.constants.LyricsRequestMode
import com.kire.audio.presentation.model.event.TrackUiEvent
import com.kire.audio.presentation.ui.details.common.Divider
import com.kire.audio.presentation.ui.details.common.RubikFontText
import com.kire.audio.presentation.ui.theme.dimen.Dimens
import com.kire.audio.presentation.ui.theme.localization.LocalizationProvider
import com.kire.audio.presentation.util.modifier.animatePlacement
import com.kire.audio.presentation.util.rememberDerivedStateOf

import kotlinx.coroutines.flow.StateFlow

@Composable
fun LyricsHeader(
    onEvent: (TrackUiEvent) -> Unit,
    trackState: StateFlow<TrackState>,
    clearUserInput: () -> Unit,
    lyricsRequest: (LyricsRequestMode) -> Unit,
    lyricsState: StateFlow<LyricsState>,
) {

    val lyricsState by lyricsState.collectAsStateWithLifecycle()
    val trackState by trackState.collectAsStateWithLifecycle()

    val showDelete by rememberDerivedStateOf {
        lyricsState.isEditModeEnabled && lyricsState.lyricsRequestMode == LyricsRequestMode.EDIT_CURRENT_TEXT
    }

    val showRefresh by rememberDerivedStateOf {
        lyricsState.lyricsRequestMode != LyricsRequestMode.SELECTOR_IS_VISIBLE
                || trackState.currentTrackPlaying?.lyrics !is ILyricsRequestState.Success
    }

    val shouldMakeLyricsRequest by rememberDerivedStateOf {
        (lyricsState.lyricsRequestMode == LyricsRequestMode.BY_LINK || lyricsState.lyricsRequestMode == LyricsRequestMode.BY_TITLE_AND_ARTIST)
                && lyricsState.userInput.isNotEmpty()
    }

    val editOrSaveIcon by rememberDerivedStateOf {
        if (!lyricsState.isEditModeEnabled)
            Icons.Rounded.Edit
        else Icons.Rounded.Save
    }

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

            AnimatedContent(
                targetState = showDelete,
                modifier = Modifier
                    .size(Dimens.universalIconSize)
            ) {
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
                    Icon(
                        imageVector = Icons.Rounded.Refresh,
                        contentDescription = "Refresh",
                        tint = AudioExtendedTheme.extendedColors.roseAccent,
                        modifier = Modifier
                            .fillMaxSize()
                            .bounceClick {
                                lyricsRequest(LyricsRequestMode.AUTOMATIC)
                            }
                    )
            }

            RubikFontText(
                text = LocalizationProvider.strings.lyricsDialogHeader,
                style = TextStyle(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 28.sp,
                    color = Color.White
                )
            )

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
                                                lyricsRequest(lyricsState.lyricsRequestMode)
                                            else if (lyricsState.lyricsRequestMode == LyricsRequestMode.EDIT_CURRENT_TEXT && isEnabled)

                                                trackState.currentTrackPlaying?.let { track ->
                                                    onEvent(
                                                        TrackUiEvent.upsertAndUpdateCurrentTrack(
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

        Divider()
    }
}