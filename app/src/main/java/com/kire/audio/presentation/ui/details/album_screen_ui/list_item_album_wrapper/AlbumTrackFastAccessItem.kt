package com.kire.audio.presentation.ui.details.album_screen_ui.list_item_album_wrapper

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

import com.kire.audio.presentation.ui.details.common.RubikFontText
import com.kire.audio.presentation.ui.theme.AudioExtendedTheme
import com.kire.audio.presentation.ui.theme.dimen.Dimens

/**
 * Плитка, представляющая некоторый трек из альбома его названием
 *
 * @param trackTitle название трека
 * @param animatedColor анимированный фон плитки
 * @param onClick действие при нажатии на весь компонент
 *
 * @author Michael Gontarev (KiREHwYE)
 */
@Composable
fun AlbumTrackFastAccessItem(
    trackTitle: String,
    animatedColor: Color,
    onClick: () -> Unit
) {

    val interactionSource = remember {
        MutableInteractionSource()
    }

    Box(
        modifier = Modifier
            .wrapContentSize()
            .clip(RoundedCornerShape(Dimens.universalRoundedCorner))
            .shadow(
                elevation = Dimens.universalShadowElevation,
                spotColor = AudioExtendedTheme.extendedColors.shadow,
                shape = RoundedCornerShape(Dimens.universalRoundedCorner)
            )
            .drawBehind {
                drawRoundRect(
                    color = animatedColor,
                    cornerRadius = CornerRadius(
                        x = Dimens.universalRoundedCorner.value,
                        y = Dimens.universalRoundedCorner.value
                    )
                )
                drawRect(animatedColor)
            }
            .clickable(
                indication = null,
                interactionSource = interactionSource
            ) { onClick() }
            .padding(Dimens.universalPad),
        contentAlignment = Alignment.Center
    ) {
        RubikFontText(
            text = trackTitle,
            style = TextStyle(
                fontWeight = FontWeight.Medium,
                fontSize = 15.sp,
                lineHeight = 15.sp,
                color = AudioExtendedTheme.extendedColors.primaryText
            )
        )
    }
}