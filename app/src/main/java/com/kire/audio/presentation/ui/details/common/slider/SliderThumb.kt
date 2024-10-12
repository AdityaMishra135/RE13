package com.kire.audio.presentation.ui.details.common.slider

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Label
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.kire.audio.presentation.ui.details.common.RubikFontBasicText
import com.kire.audio.presentation.ui.theme.AudioExtendedTheme
import com.kire.audio.presentation.ui.theme.dimen.Dimens

/** Ползунок для слайдера
 *
 * @param currentPosition текущая позиция воспроизведения
 * @param interactionSource Поток взаимодействия
 * @param thumbColor Цвет ползунка
 * @param thumbBorderColor Цвет обводки ползунка
 *
 * @author Михаил Гонтарев (KiREHwYE)
 * */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Thumb(
    currentPosition: () -> String = { "" },
    interactionSource: MutableInteractionSource = MutableInteractionSource(),
    thumbColor: Color = Color.Unspecified,
    thumbBorderColor: Color = Color.Unspecified
) {
    /** Парящее облачко текста с текущей
     * позицией воспроизведения над ползунком */
    Label(
        label = {
            PlainTooltip(
                shape = RoundedCornerShape(Dimens.universalRoundedCorners),
                containerColor = AudioExtendedTheme.extendedColors.roseAccent,
                modifier = Modifier
                    .wrapContentSize()
            ) {
                /** Текущая позиция слайдера */
                /** Текущая позиция слайдера */
                RubikFontBasicText(
                    text = currentPosition(),
                    style = TextStyle(
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium
                    )
                )
            }
        },
        interactionSource = interactionSource
    ) {
        /** Ползунок */
        /** Ползунок */
        Spacer(
            modifier = Modifier
                .size(Dimens.sliderThumbSize)
                .indication(
                    interactionSource = interactionSource,
                    indication = ripple(
                        bounded = false,
                        radius = Dimens.sliderThumbRippleRadius
                    )
                )
                .hoverable(interactionSource = interactionSource)
                .background(
                    color = thumbColor,
                    shape = CircleShape
                )
                .border(
                    width = Dimens.sliderThumbBorderWidth,
                    color = thumbBorderColor,
                    shape = CircleShape
                )
        )
    }
}