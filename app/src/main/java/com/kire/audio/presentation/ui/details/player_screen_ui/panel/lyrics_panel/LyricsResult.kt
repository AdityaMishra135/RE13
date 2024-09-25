package com.kire.audio.presentation.ui.details.player_screen_ui.panel.lyrics_panel

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

import com.kire.audio.presentation.ui.theme.AudioExtendedTheme
import com.kire.audio.presentation.util.modifier.animatePlacement

@Composable
fun LyricsResult(
    lyrics: String
) {

    Box(
        modifier = Modifier
            .animatePlacement()
            .animateContentSize()
            .wrapContentHeight(),
        contentAlignment = Alignment.TopStart
    ){
        Text(
            text = lyrics,
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