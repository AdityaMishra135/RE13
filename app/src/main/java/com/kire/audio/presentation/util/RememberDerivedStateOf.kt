package com.kire.audio.presentation.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisallowComposableCalls
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember

/**
 * Обычный remember c derivedStateOf внутри
 *
 * @param compute функция, результат которой должен быть запомнен
 *
 * @author Михаил Гонтарев (KiREHwYE)
 */
@Composable
inline fun <T> rememberDerivedStateOf(
    crossinline compute: @DisallowComposableCalls () -> T
): State<T> =
    remember {
        derivedStateOf {
            compute()
        }
    }