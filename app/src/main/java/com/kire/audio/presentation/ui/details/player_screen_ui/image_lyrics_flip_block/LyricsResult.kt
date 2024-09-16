package com.kire.audio.presentation.ui.details.player_screen_ui.image_lyrics_flip_block

import androidx.compose.animation.animateContentSize

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.wrapContentHeight

import androidx.compose.material3.Text

import androidx.compose.runtime.Composable

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

import com.kire.audio.presentation.model.ILyricsRequestState
import com.kire.audio.presentation.ui.theme.AudioExtendedTheme
import com.kire.audio.presentation.ui.theme.localization.LocalizationProvider
import com.kire.audio.presentation.util.animatePlacement

@Composable
fun LyricsResult(
    lyrics: ILyricsRequestState
) {

    Box(
        modifier = Modifier
            .animatePlacement()
            .animateContentSize()
            .wrapContentHeight(),
        contentAlignment = Alignment.TopStart
    ){
        Text(
            text = when(lyrics) {
                is ILyricsRequestState.Success -> {
                    if (lyrics.lyrics.isNotEmpty())
                        lyrics.lyrics
                    else LocalizationProvider.strings.lyricsDialogUnsuccessfulMessage
                }
                is ILyricsRequestState.Unsuccessful -> LocalizationProvider.strings.lyricsDialogUnsuccessfulMessage
                is ILyricsRequestState.OnRequest -> LocalizationProvider.strings.lyricsDialogWaitingMessage
            },
            style = TextStyle(
                color = Color.White,
                fontSize = 19.sp,
                lineHeight = 19.sp,
                fontFamily = AudioExtendedTheme.extendedFonts.rubikFontFamily,
                fontWeight = FontWeight.Medium
            )
        )
    }
}