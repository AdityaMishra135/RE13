package com.kire.audio.presentation.ui.details.common

import androidx.compose.foundation.MarqueeAnimationMode
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.text.BasicText

import androidx.compose.runtime.Composable

import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorProducer
import androidx.compose.ui.text.TextStyle

import com.kire.audio.presentation.ui.theme.extendedFonts

/**
 * Текст со шрифтом Rubik и basicMarquee
 *
 * @param text текст
 * @param modifier модификатор
 * @param color цвет текста
 * @param style стиль текста
 *
 * @author Michael Gontarev (KiREHwYE)
 */
@Composable
fun RubikFontBasicText(
    text: String,
    modifier: Modifier = Modifier,
    color: ColorProducer? = null,
    style: TextStyle
) {
    BasicText(
        text = text,
        color = color,
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