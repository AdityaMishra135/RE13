package com.kire.audio.presentation.ui.details.player_screen_ui

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.MoreVert

import androidx.compose.material3.Icon

import androidx.compose.runtime.Composable

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput

import com.kire.audio.presentation.util.modifier.bounceClick

import com.kire.audio.presentation.ui.theme.AudioExtendedTheme
import com.kire.audio.presentation.ui.theme.dimen.Dimens

/**
 * Кнопки вверху экрана плеера:
 * сворачивание экрана и открытие меню дополнительной информации о треке
 *
 * @param navigateBack действие при нажатии на кнопку закрытия экрана
 * @param expandPanelByNumber модификатор для открытия меню дополнительной информации о треке
 *
 * @author Михаил Гонтарев (KiREHwYE)
 * */
@Composable
fun TopButtons(
    navigateBack: () -> Unit,
    expandPanelByNumber: () -> Unit
){

    Row(modifier = Modifier
        .fillMaxWidth()
        .wrapContentHeight(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {

        /** Кнопка закрытия экрана с плеером */
        Icon(
            Icons.Rounded.KeyboardArrowDown,
            contentDescription = "Close",
            modifier = Modifier
                .size(Dimens.universalIconSize)
                .bounceClick {
                    navigateBack()
                },
            tint = AudioExtendedTheme.extendedColors.playerScreenButton
        )

        /** Кнопка открытия меню дополнительной информации о треке*/
        Icon(
            Icons.Rounded.MoreVert,
            contentDescription = "Info",
            modifier = Modifier
                .size(Dimens.universalIconSize)
                .pointerInput(Unit) {
                    detectTapGestures {
                        expandPanelByNumber()
                    }
                },
            tint = AudioExtendedTheme.extendedColors.playerScreenButton
        )
    }
}