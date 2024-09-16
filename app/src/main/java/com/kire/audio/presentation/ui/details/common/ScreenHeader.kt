package com.kire.audio.presentation.ui.details.common

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.isSystemInDarkTheme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue

import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextMotion
import androidx.compose.ui.unit.sp

import com.kire.audio.presentation.ui.theme.AudioExtendedTheme
import com.kire.audio.presentation.ui.theme.animation.Animation

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
    isClicked: Boolean,
    onTitleClick: () -> Unit,
    modifier: Modifier = Modifier
){

    /** Размер текста экрана, который изменяется в зависимости от значения isClicked */
    val textSize by animateDpAsState(
        targetValue =
            if (isClicked)
                with(LocalDensity.current) { 28.sp.toDp() }
            else
                with (LocalDensity.current) { 52.sp.toDp() },
        animationSpec = Animation.universalSpring()
    )

    /** Цвет текста экрана, который изменяется в зависимости от значения isClicked и текущей темы */
    val animatedColor by animateColorAsState(
        targetValue = if (isClicked && !isSystemInDarkTheme()) Color.White else AudioExtendedTheme.extendedColors.primaryText,
        label = "color"
    )

    // Название экрана
    AnimatedContent(
        targetState = screenTitle,
        label = "",
        modifier = modifier
    ) {
        RubikFontText(
            text = it,
            style = TextStyle(
                fontSize = textSize.value.sp,
                lineHeight = textSize.value.sp,
                fontWeight = FontWeight.SemiBold,
                color = animatedColor,
                textMotion = TextMotion.Animated
            ),
            modifier = Modifier
                .pointerInput(Unit) {
                    detectTapGestures {
                        onTitleClick()
                    }
                }
        )
    }
}