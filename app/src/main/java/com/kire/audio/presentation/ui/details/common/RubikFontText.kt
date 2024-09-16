package com.kire.audio.presentation.ui.details.common

import androidx.compose.foundation.MarqueeAnimationMode
import androidx.compose.foundation.basicMarquee

import androidx.compose.material3.Text

import androidx.compose.runtime.Composable

import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle

import com.kire.audio.presentation.ui.theme.extendedFonts

/**
 * Текст со шрифтом Rubik и basicMarquee
 *
 * @param text текст
 * @param style стиль текста
 * @param modifier модификатор
 *
 * @author Michael Gontarev (KiREHwYE)
 */
@Composable
fun RubikFontText(
    text: String,
    style: TextStyle,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        style = style
            .copy(fontFamily = extendedFonts.rubikFontFamily),
        modifier = modifier
            .basicMarquee(
                iterations = Int.MAX_VALUE,
                animationMode = MarqueeAnimationMode.Immediately,
                repeatDelayMillis = 0
            )
    )
}