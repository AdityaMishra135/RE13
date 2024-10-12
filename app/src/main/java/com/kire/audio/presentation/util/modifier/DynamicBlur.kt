package com.kire.audio.presentation.util.modifier

import android.annotation.SuppressLint
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlurEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp

/**
 * Лямбда вариант стандартного blur.
 * Подходит в случаях, когда значение блюра
 * изменяется - например, анимируется.
 *
 * @param radius степень размытия
 *
 * @author Михаил Гонтарев
 */
@SuppressLint("SuspiciousModifierThen")
fun Modifier.dynamicBlur(
    radius: () -> Dp,
): Modifier = this.then(
    graphicsLayer {
        val blurPixels = radius().toPx()
        this.renderEffect =
            // С нулем кинет IllegalArgumentException !!!!!
            if (blurPixels > 0f)
                BlurEffect(blurPixels, blurPixels)
            else null
    }
)