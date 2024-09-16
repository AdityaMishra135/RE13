package com.kire.audio.presentation.ui.details.player_screen_ui.image_lyrics_flip_block

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Save

import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text

import androidx.compose.runtime.Composable

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.kire.audio.presentation.util.bounceClick
import com.kire.audio.presentation.model.ILyricsRequestState
import com.kire.audio.presentation.model.state.LyricsState
import com.kire.audio.presentation.model.state.TrackState
import com.kire.audio.presentation.ui.theme.AudioExtendedTheme
import com.kire.audio.presentation.constants.LyricsRequestMode
import com.kire.audio.presentation.model.event.TrackUiEvent
import com.kire.audio.presentation.ui.details.common.RubikFontText
import com.kire.audio.presentation.ui.theme.dimen.Dimens
import com.kire.audio.presentation.ui.theme.localization.LocalizationProvider
import com.kire.audio.presentation.util.animatePlacement
import com.kire.audio.presentation.util.nonScaledSp

@Composable
fun LyricsHeader(
    onEvent: (TrackUiEvent) -> Unit,
    trackState: TrackState,
    clearUserInput: () -> Unit,
    lyricsRequest: (LyricsRequestMode) -> Unit,
    lyricsState: LyricsState,
) {

    lyricsState.apply {

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

                Box(
                    modifier = Modifier
                        .size(Dimens.universalIconSize)
                ) {

                    if (isEditModeEnabled && lyricsRequestMode == LyricsRequestMode.EDIT_CURRENT_TEXT)
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
                    else if (lyricsRequestMode != LyricsRequestMode.SELECTOR_IS_VISIBLE
                        && trackState.currentTrackPlaying?.lyrics is ILyricsRequestState.Unsuccessful) {

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
                    imageVector = if (!isEditModeEnabled) Icons.Rounded.Edit else Icons.Rounded.Save,
                    contentDescription = "",
                    tint = AudioExtendedTheme.extendedColors.roseAccent,
                    modifier = Modifier
                        .size(Dimens.universalIconSize)
                        .bounceClick {
                            onEvent(
                                TrackUiEvent.updateLyricsState(
                                    this@apply.copy(
                                        lyricsRequestMode =
                                        if (!isEditModeEnabled)
                                            LyricsRequestMode.SELECTOR_IS_VISIBLE
                                        else
                                            LyricsRequestMode.AUTOMATIC,
                                        isEditModeEnabled = !isEditModeEnabled
                                            .also {
                                                if (userInput.isNotEmpty()) {

                                                    if ((lyricsRequestMode == LyricsRequestMode.BY_LINK || lyricsRequestMode == LyricsRequestMode.BY_TITLE_AND_ARTIST) && it)
                                                        lyricsRequest(lyricsRequestMode)
                                                    else if (lyricsRequestMode == LyricsRequestMode.EDIT_CURRENT_TEXT && it)

                                                        trackState.currentTrackPlaying?.copy(lyrics = ILyricsRequestState.Success(userInput))
                                                            .also { track ->
                                                                onEvent(
                                                                    TrackUiEvent.updateTrackState(
                                                                        trackState.copy(currentTrackPlaying = track)
                                                                    )
                                                                )
                                                            }?.let {
                                                                onEvent(TrackUiEvent.upsertTrack(it))
                                                            }
                                                }
                                            }
                                    )
                                )
                            )
                        }
                )
            }

            HorizontalDivider(
                modifier = Modifier
                    .fillMaxWidth(0.25f)
                    .clip(RoundedCornerShape(Dimens.universalRoundedCorner)),
                thickness = Dimens.horizontalDividerThickness,
                color = AudioExtendedTheme.extendedColors.roseAccent
            )
        }
    }
}