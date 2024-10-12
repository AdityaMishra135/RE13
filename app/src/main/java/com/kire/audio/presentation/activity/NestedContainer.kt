package com.kire.audio.presentation.activity

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope

import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset

import com.kire.audio.presentation.ui.theme.dimen.Dimens

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

/**
 * Обертка над основным контентом, вызываемом в MainActivity
 *
 * @param content основной контент
 *
 * @author Михаил Гонтарев (KiREHwYE)
 */
@Composable
fun NestedContainer(
    content: @Composable (
        nestedScrollConnection: NestedScrollConnection,
        modifier: Modifier,
        shiftButtonDown: () -> Unit,
        shiftBottomBar: () -> Unit
    ) -> Unit
) {
    /** Измерения контейнера в пикселях */
    val localDensity = LocalDensity.current

    /** Высота плавающей кнопки в пикселях */
    val bottomBarHeightPx = remember { mutableFloatStateOf(0f) }

    /** Свдиг нижнего бара по высоте */
    val bottomBarOffsetHeightPx = remember { mutableFloatStateOf(0f) }

    /** Отступ нижней системной панели навигации */
    val bottomInsetPaddingPx = with(localDensity) {
        WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding().toPx()
    }

    /** Слушатель скролла экрана */
    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                val delta = available.y

                val newBottomBarOffset = bottomBarOffsetHeightPx.floatValue + delta
                bottomBarOffsetHeightPx.floatValue =
                    newBottomBarOffset.coerceIn(
                        minimumValue = -bottomBarHeightPx.floatValue - bottomInsetPaddingPx,
                        maximumValue = 0f
                    )

                return Offset.Zero
            }
        }
    }

    /** Область выполнения корутин */
    val coroutineScope = rememberCoroutineScope()

    /**
     * Опускает PlayerBottomBar за границы экрана
     *
     * @author Михаил Гонтарев (KiREHwYE)
     * */
    fun shiftPlayerBottomBarDown() {
        coroutineScope.launch {
            while (-bottomBarOffsetHeightPx.floatValue < bottomBarHeightPx.floatValue) {
                val newTopBarOffset = (bottomBarOffsetHeightPx.floatValue - 5).coerceIn(
                    minimumValue = -bottomBarHeightPx.floatValue,
                    maximumValue = 0f
                )
                bottomBarOffsetHeightPx.floatValue = newTopBarOffset
                delay(1)
            }
        }
    }

    /**
     * Поднимает PlayerBottomBar, делая видимым
     *
     * @author Михаил Гонтарев (KiREHwYE)
     * */
    fun shiftPlayerBottomBarUp() {
        coroutineScope.launch {
            while (bottomBarOffsetHeightPx.floatValue < 0) {
                bottomBarOffsetHeightPx.floatValue += 5
                delay(1)
            }
        }
    }

    /** Основной контент */
    content(
        nestedScrollConnection,
        Modifier
            .onGloballyPositioned {
                bottomBarHeightPx.floatValue =
                    it.size.height.toFloat() + with(localDensity) { 2 * Dimens.universalPad.toPx() }
            }
            .offset {
                IntOffset(
                    x = 0,
                    y = -bottomBarOffsetHeightPx.floatValue.roundToInt()
                )
            },
        ::shiftPlayerBottomBarDown
    ) {
        coroutineScope.launch {
            if (bottomBarOffsetHeightPx.floatValue <= ((-bottomBarHeightPx.floatValue + with(localDensity) { Dimens.universalPad.toPx() }) / 2))
                shiftPlayerBottomBarDown()
            else
                shiftPlayerBottomBarUp()
        }
    }
}
