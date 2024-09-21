package com.kire.audio.presentation.util.modifier

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector2D
import androidx.compose.animation.core.VectorConverter

import androidx.compose.foundation.layout.offset

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue

import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.round

import com.kire.audio.presentation.ui.theme.animation.Animation

import kotlinx.coroutines.launch

/**
 * Анимация смещения компонента.
 *
 * @author Михаил Гонтарев (KiREHwYE)
 */
fun Modifier.animatePlacement(): Modifier = composed {

    /** Область выполнения корутин */
    val scope = rememberCoroutineScope()

    /** Текущее смещение компонента */
    var targetOffset by remember { mutableStateOf(IntOffset.Zero) }

    /** Анимируемое смещение компонента */
    var animatable by remember {
        mutableStateOf<Animatable<IntOffset, AnimationVector2D>?>(null)
    }
    this
        .onPlaced {
            // Вычисляет позицию в родительском компоненте
            targetOffset = it
                .positionInParent()
                .round()
        }
        .offset {
            /** Создаем объект анимации */
            val anim = animatable ?: Animatable(targetOffset, IntOffset.VectorConverter)
                .also {
                    animatable = it
                }

            /** Смещаем компонент в сторону targetOffset */
            if (anim.targetValue != targetOffset) {
                scope.launch {
                    anim.animateTo(targetOffset, Animation.universalSpring())
                }
            }

            animatable?.let { it.value - targetOffset } ?: IntOffset.Zero
        }
}