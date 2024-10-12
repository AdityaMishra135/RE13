package com.kire.audio.presentation.ui.details.common

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.isSystemInDarkTheme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue

import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

import com.kire.audio.presentation.ui.theme.AudioExtendedTheme
import com.kire.audio.presentation.ui.theme.animation.Animation
import com.kire.audio.presentation.util.modifier.animatePlacement

/**
 * Название экрана
 *
 * @param screenTitle название текущего экрана
 * @param isClicked был ли клик по названию экрана
 * @param onTitleClick действие при клике по названию экрана
 * @param modifier модификатор
 *
 * @author Michael Gontarev (KiREHwYE)
 */
@Composable
fun ScreenHeader(
    screenTitle: String,
    modifier: Modifier = Modifier,
    isClicked: () -> Boolean = { false },
    onTitleClick: () -> Unit = {}
){

    /** Цвет текста экрана, который изменяется в зависимости от значения isClicked и текущей темы */
    val animatedColor by animateColorAsState(
        targetValue = if (isClicked() && !isSystemInDarkTheme()) Color.White
            else AudioExtendedTheme.extendedColors.primaryText,
        animationSpec = Animation.universalFiniteSpring()
    )

    val scale by animateFloatAsState(
        targetValue = if (!isClicked()) 1f else 0.549f,
        animationSpec = Animation.universalFiniteSpring()
    )
    
    /** Название экрана */
    AnimatedContent(
        targetState = screenTitle,
        label = "",
        modifier = modifier
            .animatePlacement()
    ) {

        RubikFontBasicText(
            text = it,
            color = { animatedColor },
            style = TextStyle(
                fontSize = 52.sp,
                lineHeight = 52.sp,
                fontWeight = FontWeight.SemiBold
            ),
            modifier = Modifier
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                    transformOrigin = TransformOrigin.Center
                }
                .pointerInput(Unit) {
                    detectTapGestures {
                        onTitleClick()
                    }
                }
        )
    }
}