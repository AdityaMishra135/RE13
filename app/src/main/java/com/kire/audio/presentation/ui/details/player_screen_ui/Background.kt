package com.kire.audio.presentation.ui.details.player_screen_ui

import android.net.Uri

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.isSystemInDarkTheme

import androidx.compose.foundation.layout.fillMaxSize

import androidx.compose.runtime.Composable

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix

import com.kire.audio.presentation.ui.details.common.AsyncImageWithLoading
import com.kire.audio.presentation.ui.theme.animation.Animation
import com.kire.audio.presentation.ui.theme.dimen.Dimens

/**
 * Картинка, которая отображается в качестве бэкграунда PlayerScreen
 *
 * @param imageUri изображение для отрисовки
 *
 * @author Michael Gontarev (KiREHwYE)
 */
@Composable
fun Background(
    imageUri: Uri?
){
    Crossfade(
        targetState = imageUri,
        label = "Background Image",
        animationSpec = Animation.universalFiniteSpring()
    ) {

        AsyncImageWithLoading(
            model = it,
            colorFilter = ColorFilter.colorMatrix(ColorMatrix().apply {
                if (isSystemInDarkTheme())
                    setToScale(0.35f,0.35f,0.35f,1f)
                else setToScale(0.7f,0.7f,0.7f,1f)
            }),
            modifier = Modifier
                .fillMaxSize()
                .blur(Dimens.backgroundBlur)
//                .alpha(1f)
        )
    }
}