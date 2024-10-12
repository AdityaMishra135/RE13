package com.kire.audio.presentation.ui.details.common

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.material3.HorizontalDivider

import androidx.compose.runtime.Composable

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color

import com.kire.audio.presentation.ui.theme.AudioExtendedTheme
import com.kire.audio.presentation.ui.theme.dimen.Dimens

/**
 * Разделитель на 1/4 доступной ширины с закругленными углами
 *
 * @param modifier модификатор
 * @param color цвет разделителя
 *
 * @author Михаил Гонтарев (KiREHwYE)
 * */
@Composable
fun Divider(
    modifier: Modifier = Modifier,
    color: Color = AudioExtendedTheme.extendedColors.roseAccent
) {
    HorizontalDivider(
        modifier = modifier
            .fillMaxWidth(0.25f)
            .clip(RoundedCornerShape(Dimens.universalRoundedCorners)),
        thickness = Dimens.horizontalDividerThickness,
        color = color
    )
}