package com.kire.audio.presentation.ui.details.common.slider

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.kire.audio.presentation.ui.details.common.RubikFontBasicText

/** Текущая позиция воспроизведения и полная длительность трека
 *
 * @param currentPosition Текущая позиция воспроизведения
 * @param duration Полная длительность трека
 * @param textColor Цвет текста
 *
 * @author Михаил Гонтарев (KiREHwYE)
 * */
@Composable
fun SliderTexts(
    currentPosition: () -> String = { "" },
    duration: () -> String = { "" },
    textColor: Color = Color.Unspecified
) {
    /** Содержит текущую позицию и длительность трека */
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ) {

        /** Текущая позиция */
        /** Текущая позиция */
        RubikFontBasicText(
            text = currentPosition(),
            style = TextStyle(
                color = textColor,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold
            )
        )

        /** Длительность трека */

        /** Длительность трека */
        RubikFontBasicText(
            text = duration(),
            style = TextStyle(
                color = textColor,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
            )
        )
    }
}