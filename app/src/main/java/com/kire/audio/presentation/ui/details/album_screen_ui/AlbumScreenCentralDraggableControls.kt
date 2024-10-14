package com.kire.audio.presentation.ui.details.album_screen_ui

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowCircleLeft
import androidx.compose.material.icons.rounded.Info

import androidx.compose.material3.Icon

import androidx.compose.runtime.Composable

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp

import com.kire.audio.presentation.ui.details.common.RubikFontBasicText
import com.kire.audio.presentation.ui.theme.AudioExtendedTheme
import com.kire.audio.presentation.ui.theme.dimen.Dimens
import com.kire.audio.presentation.util.modifier.bounceClick
import com.kire.audio.presentation.util.modifier.dynamicPadding

/**
 * Центральная часть экрана альбома, содержащая название альбома,
 * кнопку возвращения назад и кнопку открытия панели информации об альбоме
 *
 * @param albumTitle Название альбома
 * @param animatedTopPad Анимированный отступ от верхней границы экрана
 * @param onArrowBackClick Действие при нажатии на кнопку возвращения назад
 * @param onInfoClick Действие при нажатии на кнопку открытия панели информации об албоме
 * @param onDrag Действие, когда тянут данный компонент
 * @param onDragEnd Действие, когда пользователь перестает тянуть данных компонент
 *
 * @author Михаил Гонтарев (KiREHwYE)
 */
@Composable
fun AlbumScreenCentralDraggableControls(
    albumTitle: () -> String,
    animatedTopPad: () -> Dp,
    onArrowBackClick: () -> Unit = {},
    onInfoClick: () -> Unit = {},
    onDrag: (Float) -> Unit = {},
    onDragEnd: () -> Unit = {}
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .pointerInput(Unit) {
                detectDragGestures(
                    onDrag = { _, dragAmount ->
                        val deltaY = dragAmount.y
                        onDrag(deltaY)
                    },
                    onDragEnd = onDragEnd
                )
            }
            .padding(Dimens.universalPad)
            .dynamicPadding(top = animatedTopPad),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {

        /** Кнопка возвращения назад */
        Icon(
            imageVector = Icons.Rounded.ArrowCircleLeft,
            contentDescription = null,
            tint = if (!isSystemInDarkTheme()) Color.White else AudioExtendedTheme.extendedColors.button,
            modifier = Modifier
                .size(Dimens.albumScreenIconSize)
                .bounceClick {
                    onArrowBackClick()
                }
        )

        /** Название альбома */
        RubikFontBasicText(
            text = albumTitle(),
            style = TextStyle(
                color = if (!isSystemInDarkTheme()) Color.White else AudioExtendedTheme.extendedColors.primaryText,
                fontSize = 24.sp,
                lineHeight = 24.sp,
                fontWeight = FontWeight.Medium,
            ),
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = Dimens.universalPad)
        )

        /** Кнопка открытия панели с информацией об альбоме */
        Icon(
            imageVector = Icons.Rounded.Info,
            contentDescription = null,
            tint = if (!isSystemInDarkTheme()) Color.White else AudioExtendedTheme.extendedColors.button,
            modifier = Modifier
                .size(Dimens.albumScreenIconSize)
                .pointerInput(Unit) {
                    detectTapGestures {
                        onInfoClick()
                    }
                }
        )
    }
}