package com.kire.audio.presentation.ui.details.common

import androidx.compose.animation.AnimatedContent

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.wrapContentHeight

import androidx.compose.runtime.Composable

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight

import com.kire.audio.presentation.ui.theme.dimen.Dimens
import com.kire.audio.presentation.ui.theme.localization.LocalizationProvider

/**
 * Два текста в колонке. Тексты обернуты AnimatedContent для плавного изменения
 *
 * @param mainText основной текст (сверху)
 * @param satelliteText доп. текст (снизу)
 * @param mainTextStyle стиль основного текста
 * @param satelliteTextStyle стиль доп. текста
 * @param modifier модификатор
 *
 * @author Михаил Гонтарев (KiREHwYE)
 */
@Composable
fun TwoTextsInColumn(
    mainText: String?,
    satelliteText: String?,
    mainTextStyle: TextStyle,
    satelliteTextStyle: TextStyle,
    modifier: Modifier = Modifier
) {

    /** Колонка с текстами */
    Column(
        modifier = modifier
            .wrapContentHeight(),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(Dimens.columnUniversalTextSpacedBy)
    ) {

        /** Основной текст */
        AnimatedContent(targetState = mainText, label = "") {
            RubikFontText(
                text = it ?: LocalizationProvider.strings.nothingWasFound,
                style = TextStyle(fontWeight = FontWeight.Medium).merge(mainTextStyle)
            )
        }

        /** Дополнительный текст */
        AnimatedContent(targetState = satelliteText, label = "") {
            RubikFontText(
                text = it ?: LocalizationProvider.strings.nothingWasFound,
                style = TextStyle(fontWeight = FontWeight.Light).merge(satelliteTextStyle)
            )
        }
    }
}