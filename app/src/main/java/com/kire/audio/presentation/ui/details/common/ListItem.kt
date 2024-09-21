package com.kire.audio.presentation.ui.details.common

import android.net.Uri

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp

import com.kire.audio.presentation.model.Track
import com.kire.audio.presentation.ui.theme.AudioExtendedTheme
import com.kire.audio.presentation.ui.theme.dimen.Dimens

/**
 * Элемент с картинкой и колонкой из двух текстов справа от нее
 *
 * @param mainText основной текст в колонке
 * @param satelliteText доп. текст в колонке
 * @param leadingImageUri uri картинки слева от текста
 * @param modifier модификатор
 * @param onClick действие при клике на элемент
 *
 * @author Michael Gontarev (KiREHwYE)
 */
@Composable
fun ListItem(
    mainText: String,
    satelliteText: String,
    leadingImageUri: Uri?,
    modifier: Modifier = Modifier,
    mainTextColor: Color = AudioExtendedTheme.extendedColors.primaryText,
    satelliteTextColor: Color = AudioExtendedTheme.extendedColors.secondaryText,
    onClick: () -> Unit = {},
){

    val interactionSource = remember {
        MutableInteractionSource()
    }

    Row(
        modifier = modifier
            .wrapContentHeight()
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) {
                onClick()
            },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Dimens.listItemStartPadding)
    ){

        AsyncImageWithLoading(
            model = leadingImageUri,
            modifier = Modifier
                .size(Dimens.listItemImageSize)
                .clip(CircleShape)
        )


        TwoTextsInColumn(
            modifier = Modifier
                .weight(1f),
            mainText = mainText,
            satelliteText = satelliteText,
            mainTextStyle = TextStyle(
                color = mainTextColor,
                fontSize = 19.sp
            ),
            satelliteTextStyle = TextStyle(
                color = satelliteTextColor,
                fontSize = 15.sp
            )
        )
    }
}

/**
 * Элемент с картинкой и колонкой из двух текстов справа от нее. Перегруженная версия: берет необходимые данные из track
 *
 * @param track трек, из которого берутся mainText, satelliteText, leadingImageUri
 * @param modifier модификатор
 * @param onClick действие при клике на элемент
 *
 * @author Michael Gontarev (KiREHwYE)
 */
@Composable
fun ListItem(
    track: Track,
    modifier: Modifier = Modifier,
    mainTextColor: Color = AudioExtendedTheme.extendedColors.primaryText,
    satelliteTextColor: Color = AudioExtendedTheme.extendedColors.secondaryText,
    onClick: () -> Unit = {}
){
    ListItem(
        mainText = track.title,
        satelliteText = track.artist,
        leadingImageUri = track.imageUri,
        onClick = onClick,
        mainTextColor = mainTextColor,
        satelliteTextColor = satelliteTextColor,
        modifier = modifier
    )
}