package com.kire.audio.presentation.ui.details.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Save

import androidx.compose.material3.Icon

import androidx.compose.runtime.Composable

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

import com.kire.audio.presentation.ui.theme.AudioExtendedTheme
import com.kire.audio.presentation.ui.theme.dimen.Dimens
import com.kire.audio.presentation.ui.theme.localization.LocalizationProvider
import com.kire.audio.presentation.util.modifier.bounceClick

/**
 * Заголовок с кнопкой редактирования/сохранения панелей, отрисовываемых поверх экрана
 *
 * @param isEnabled флаг активности режима редактирования
 * @param onClick действие при клике на кнопку редактирования/сохранения
 *
 * @author Михаил Гонтарев (KiREHwYE)
 * */
@Composable
fun PanelHeader(
    isEnabled: () -> Boolean,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.displayCutout),
        verticalArrangement = Arrangement.spacedBy(Dimens.universalPad),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
        ){
            /** Заголовок */
            RubikFontBasicText(
                text = LocalizationProvider.strings.infoDialogHeader,
                style = TextStyle(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 28.sp,
                    color = Color.White
                ),
                modifier = Modifier
                    .align(Alignment.Center)
            )

            /** Кнопка редактирования/сохранения */
            Icon(
                imageVector = if (!isEnabled()) Icons.Rounded.Edit else Icons.Rounded.Save,
                contentDescription = "",
                tint = AudioExtendedTheme.extendedColors.roseAccent,
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .size(Dimens.universalIconSize)
                    .bounceClick {
                        onClick()
                    }
            )
        }

        /** Декоративный разделитель */
        Divider()
    }
}