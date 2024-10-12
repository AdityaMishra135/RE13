package com.kire.audio.presentation.ui.details.common

import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState

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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp

import com.kire.audio.presentation.ui.theme.animation.Animation
import com.kire.audio.presentation.ui.theme.dimen.Dimens
import com.kire.audio.presentation.util.modifier.dynamicBlur

/** Соответствует номерам панелей,
 * открываемых поверх BlurPanel
 *
 * @author Михаил Гонтарев (KiREHwYE)
 * */
enum class PanelNumber {
    FIRST, SECOND, THIRD, IDLE
}

/**
 * Контейнер с плавным эффектом размытия заднего фона.
 * Отрисовывает только один из переданных
 *
 * @param onTopOfBlurPanel1 первый элемент, который может быть отрисовыван поверх размытого основного контента
 * @param onTopOfBlurPanel2 второй элемент, который может быть отрисовыван поверх размытого основного контента
 * @param onTopOfBlurPanel3 третий элемент, который может быть отрисовыван поверх размытого основного контента
 * @param content основной контент
 *
 * @author Михаил Гонтарев (KiREHwYE)
 */
@Composable
fun BlurPanel(
    onTopOfBlurPanel1: @Composable () -> Unit = {},
    onTopOfBlurPanel2: @Composable () -> Unit = {},
    onTopOfBlurPanel3: @Composable () -> Unit = {},
    content: @Composable (expand: (PanelNumber) -> Unit) -> Unit
) {

    /** Флаг размытия фона и открытия onTopOfBlurredPanel*/
    var onTopOfBlurPanelExpanded by remember {
        mutableStateOf(false)
    }

    /** Переопределяем жест назад */
    BackHandler(onTopOfBlurPanelExpanded) {
        onTopOfBlurPanelExpanded = false
        return@BackHandler
    }

    /** Порядковый номер панели подлежащей открытию */
    var panelNumber by remember {
        mutableStateOf(PanelNumber.IDLE)
    }

    /** Анимированный блюр основного контента экрана */
    val blur by animateDpAsState(
        targetValue = if (onTopOfBlurPanelExpanded) Dimens.universalBlur else 0.dp,
        animationSpec = Animation.universalFiniteSpring()
    )
    /** Анимированный инвертированный блюр основного контента
     *  для панели, отрисовываемой поверх него*/
    val blurReversed by animateDpAsState(
        targetValue = if (onTopOfBlurPanelExpanded) 0.dp else Dimens.universalBlur,
        animationSpec = Animation.universalFiniteSpring()
    )

    /** Цвет затемнения заднего фона */
    val darkColor = Color.Black.copy(alpha = 0.2f)
    /** Динамичный цвет заднего фона */
    val darkLayerColor by animateColorAsState(
        targetValue = if (onTopOfBlurPanelExpanded) darkColor else Color.Transparent,
        animationSpec = Animation.universalFiniteSpring()
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .dynamicBlur { blur }
    ) {
        /** Основной контент */
        content(expand = { panel: PanelNumber ->
            panelNumber = panel
            onTopOfBlurPanelExpanded = true
        })

        /** Затемняет задний фон, чтобы текст лучше читался */
        Box(
            modifier = Modifier
                .fillMaxSize()
                .drawBehind {
                    drawRect(color = darkLayerColor)
                }
        )
    }

    /** Элемент, который отрисовывается поверх размытого основного контента */
    if (onTopOfBlurPanelExpanded)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .dynamicBlur { blurReversed }
                .pointerInput(Unit) {
                    detectTapGestures {
                        onTopOfBlurPanelExpanded = false
                    }
                }
                .padding(horizontal = Dimens.universalPad),
            contentAlignment = Alignment.Center
        ) {
            when (panelNumber) {
                PanelNumber.FIRST -> onTopOfBlurPanel1()
                PanelNumber.SECOND -> onTopOfBlurPanel2()
                PanelNumber.THIRD -> onTopOfBlurPanel3()
                PanelNumber.IDLE -> {}
            }
        }
}