package com.kire.audio.presentation.ui.details.common

import android.net.Uri

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.runtime.Composable

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

import com.kire.audio.presentation.ui.theme.AudioExtendedTheme
import com.kire.audio.presentation.ui.theme.dimen.Dimens
import com.kire.audio.presentation.util.modifier.bounceClick

/**
 * Плитка рекомендации трека / альбома
 *
 * @param imageUri Uri изображения
 * @param mainText название
 * @param satelliteText исполнитель
 * @param onSuggestionClick действие при нажатии на компонент
 *
 * @author Михаил Гонтарев (KiREHwYE)
 */
@Composable
fun SuggestionItem(
    imageUri: Uri?,
    mainText: String,
    satelliteText: String,
    onSuggestionClick: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .width(Dimens.albumSuggestionItemWidth)
            .wrapContentHeight()
            .bounceClick {
                onSuggestionClick(mainText)
            },
        verticalArrangement = Arrangement.spacedBy(Dimens.columnAndRowUniversalSpacedBy),
        horizontalAlignment = Alignment.Start
    ) {

        /** Обложка трека или альбома */
        AsyncImageWithLoading(
            model = imageUri,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f / 1f)
                .clip(RoundedCornerShape(Dimens.universalRoundedCorner))
        )

        /** Тексты для отображения */
        TwoTextsInColumn(
            mainText = mainText,
            satelliteText = satelliteText,
            mainTextStyle = TextStyle(
                fontSize = 12.sp,
                lineHeight = 12.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = AudioExtendedTheme.extendedFonts.rubikFontFamily,
                color = AudioExtendedTheme.extendedColors.albumSuggestionItemText
            ),
            satelliteTextStyle = TextStyle(
                fontSize = 12.sp,
                lineHeight = 12.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = AudioExtendedTheme.extendedFonts.rubikFontFamily,
                color = AudioExtendedTheme.extendedColors.albumSuggestionItemText,
            )
        )
    }
}