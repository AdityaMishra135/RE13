package com.kire.audio.presentation.ui.details.common

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp

import com.kire.audio.presentation.ui.theme.animation.Animation
import com.kire.audio.presentation.ui.theme.dimen.Dimens
import com.kire.audio.presentation.util.modifier.bounceClick

/**
 * Контейнер с плавным эффектом размытия заднего фона
 *
 * @param onTopOfBlurredPanel элемент, который отрисовывается поверх размытого основного контента
 * @param content основной контент
 *
 * @author Michael Gontarev (KiREHwYE)
 */
@Composable
fun BlurPanel(
    onTopOfBlurredPanel: @Composable () -> Unit,
    content: @Composable (modifierToExpandPopUpBar: Modifier) -> Unit
) {
    /** Флаг размытия фона и открытия onTopOfBlurredPanel*/
    var onTopOfBlurredPanelExpanded by remember {
        mutableStateOf(false)
    }

    /** Степень размытия основного контента */
    val blur by animateDpAsState(
        targetValue = if (onTopOfBlurredPanelExpanded) Dimens.universalBlur else 0.dp,
        animationSpec = Animation.universalSpring()
    )

    /** Степень размытия onTopOfBlurredPanel */
    val blurReversed by animateDpAsState(
        targetValue = if (onTopOfBlurredPanelExpanded) 0.dp else Dimens.universalBlur,
        animationSpec = Animation.universalSpring()
    )

    val darkLayerAlpha by animateFloatAsState(targetValue = if (onTopOfBlurredPanelExpanded) 0.2f else 0f)

    // Основной контент
    Box(
        modifier = Modifier
            .fillMaxSize()
            .blur(radius = blur)
    ) {
        content(
            Modifier
                .bounceClick {
                    onTopOfBlurredPanelExpanded = true
                }
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = darkLayerAlpha))
        )
    }

    // Элемент, который отрисовывается поверх размытого основного контента
    if (onTopOfBlurredPanelExpanded)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .blur(blurReversed)
                .pointerInput(Unit) {
                    detectTapGestures {
                        onTopOfBlurredPanelExpanded = false
                    }
                }
                .padding(horizontal = Dimens.universalPad),
            contentAlignment = Alignment.Center
        ) {
            onTopOfBlurredPanel()
        }
}

/**
 * Контейнер с плавным эффектом размытия заднего фона. Перегруженная версия
 *
 * @param onTopOfBlurredPanel1 первый элемент, который отрисовывается поверх размытого основного контента
 * @param onTopOfBlurredPanel2 второй элемент, который отрисовывается поверх размытого основного контента
 * @param content основной контент
 *
 * @author Michael Gontarev (KiREHwYE)
 */
@Composable
fun BlurPanel(
    onTopOfBlurredPanel1: @Composable () -> Unit,
    onTopOfBlurredPanel2: @Composable () -> Unit,
    content: @Composable (
        modifierToExpandPopUpBar1: Modifier,
        modifierToExpandPopUpBar2: Modifier
    ) -> Unit
) {
    /** Флаг размытия фона и открытия onTopOfBlurredPanel2*/
    var isPopUpBar2Expanded by remember {
        mutableStateOf(false)
    }

    /** Степень размытия основного контента */
    val blur by animateDpAsState(
        targetValue = if (isPopUpBar2Expanded) Dimens.universalBlur else 0.dp,
        animationSpec = Animation.universalSpring()
    )

    /** Степень размытия onTopOfBlurredPanel */
    val blurReversed by animateDpAsState(
        targetValue = if (isPopUpBar2Expanded) 0.dp else Dimens.universalBlur,
        animationSpec = Animation.universalSpring()
    )

    val darkLayerAlpha by animateFloatAsState(targetValue = if (isPopUpBar2Expanded) 0.2f else 0f)

    // Основной контент - тоже BlurPanel
    BlurPanel(
        onTopOfBlurredPanel = onTopOfBlurredPanel1
    ) { modifierToExpandPopUpBar1 ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .blur(radius = blur)
        ) {
            content(
                modifierToExpandPopUpBar1,
                Modifier.bounceClick { isPopUpBar2Expanded = true }
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = darkLayerAlpha))
            )
        }
    }

    // Второй элемент, который отрисовывается поверх размытого основного контента
    if (isPopUpBar2Expanded)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .blur(blurReversed)
                .pointerInput(Unit) {
                    detectTapGestures {
                        isPopUpBar2Expanded = false
                    }
                }
                .padding(horizontal = Dimens.universalPad),
            contentAlignment = Alignment.Center
        ) {
            onTopOfBlurredPanel2()
        }
}

/**
 * Контейнер с плавным эффектом размытия заднего фона. Перегруженная версия
 *
 * @param onTopOfBlurredPanel1 первый элемент, который отрисовывается поверх размытого основного контента
 * @param onTopOfBlurredPanel2 второй элемент, который отрисовывается поверх размытого основного контента
 * @param onTopOfBlurredPanel3 третий элемент, который отрисовывается поверх размытого основного контента
 * @param content основной контент
 *
 * @author Michael Gontarev (KiREHwYE)
 */
@Composable
fun BlurPanel(
    onTopOfBlurredPanel1: @Composable () -> Unit,
    onTopOfBlurredPanel2: @Composable () -> Unit,
    onTopOfBlurredPanel3: @Composable () -> Unit,
    content: @Composable (
        modifierToExpandPopUpBar1: Modifier,
        modifierToExpandPopUpBar2: Modifier,
        modifierToExpandPopUpBar3: Modifier
    ) -> Unit
) {
    /** Флаг размытия фона и открытия onTopOfBlurredPanel3*/
    var isPopUpBar3Expanded by remember {
        mutableStateOf(false)
    }

    /** Степень размытия основного контента */
    val blur by animateDpAsState(
        targetValue = if (isPopUpBar3Expanded) Dimens.universalBlur else 0.dp,
        animationSpec = Animation.universalSpring()
    )

    /** Степень размытия onTopOfBlurredPanel */
    val blurReversed by animateDpAsState(
        targetValue = if (isPopUpBar3Expanded) 0.dp else Dimens.universalBlur,
        animationSpec = Animation.universalSpring()
    )

    val darkLayerAlpha by animateFloatAsState(targetValue = if (isPopUpBar3Expanded) 0.2f else 0f)

    // Основной контент - тоже BlurPanel
    BlurPanel(
        onTopOfBlurredPanel1 = onTopOfBlurredPanel1,
        onTopOfBlurredPanel2 = onTopOfBlurredPanel2
    ) { modifierToExpandPopUpBar1, modifierToExpandPopUpBar2 ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .blur(radius = blur)
        ) {
            content(
                modifierToExpandPopUpBar1,
                modifierToExpandPopUpBar2,
                Modifier.bounceClick { isPopUpBar3Expanded = true }
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = darkLayerAlpha))
            )
        }
    }

    // Третий элемент, который отрисовывается поверх размытого основного контента
    if (isPopUpBar3Expanded)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .blur(blurReversed)
                .pointerInput(Unit) {
                    detectTapGestures {
                        isPopUpBar3Expanded = false
                    }
                }
                .padding(horizontal = Dimens.universalPad),
            contentAlignment = Alignment.Center
        ) {
            onTopOfBlurredPanel3()
        }
}