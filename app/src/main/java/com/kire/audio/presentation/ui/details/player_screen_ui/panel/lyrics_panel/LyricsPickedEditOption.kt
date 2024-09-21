package com.kire.audio.presentation.ui.details.player_screen_ui.panel.lyrics_panel

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.BasicTextField

import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

import com.kire.audio.presentation.model.ILyricsRequestState
import com.kire.audio.presentation.constants.LyricsRequestMode
import com.kire.audio.presentation.ui.details.common.RubikFontText
import com.kire.audio.presentation.ui.theme.localization.LocalizationProvider

@Composable
fun LyricsPickedEditOption(
    isClearNeeded: Boolean,
    changeIsClearNeeded: () -> Unit,
    lyricsRequestMode: LyricsRequestMode,
    lyrics: ILyricsRequestState,
    updateUserInput: (String) -> Unit
) {

    var input by rememberSaveable {
        mutableStateOf(
            if (lyricsRequestMode == LyricsRequestMode.EDIT_CURRENT_TEXT && lyrics is ILyricsRequestState.Success)
                lyrics.lyrics.also { updateUserInput(it) }
            else "".also { updateUserInput(it) }
        )
    }

    LaunchedEffect(key1 = isClearNeeded) {
        if (isClearNeeded)
            input = "".also {
                updateUserInput(it)
                changeIsClearNeeded()
            }
    }

    BasicTextField(
        modifier = Modifier
            .background(
                Color.Transparent,
                MaterialTheme.shapes.small,
            )
            .fillMaxWidth(),
        value = input,
        onValueChange = { newText ->
            input = newText.also { updateUserInput(it) }
        },
        enabled = true,
        cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
        textStyle = LocalTextStyle.current.copy(
            color = Color.White,
            fontSize = 19.sp,
            lineHeight = 19.sp,
            fontWeight = FontWeight.Medium
        ),
        decorationBox = { innerTextField ->

            Box(
                modifier = Modifier,
                contentAlignment = Alignment.TopStart
            ) {

                if (input.isEmpty())
                    if (lyricsRequestMode == LyricsRequestMode.BY_LINK)
                        RubikFontText(
                            text = LocalizationProvider.strings.byGeniusLinkModeTextExample,
                            style = TextStyle(
                                color = Color.White,
                                fontSize = 19.sp,
                                lineHeight = 19.sp,
                                fontWeight = FontWeight.Normal
                            )
                        )
                    else if (lyricsRequestMode == LyricsRequestMode.BY_TITLE_AND_ARTIST)
                        RubikFontText(
                            text = LocalizationProvider.strings.byArtistAndTitleModeTextExample,
                            style = TextStyle(
                                color = Color.White,
                                fontSize = 19.sp,
                                lineHeight = 19.sp,
                                fontWeight = FontWeight.Normal
                            )
                        )

                innerTextField()
            }
        }
    )
}