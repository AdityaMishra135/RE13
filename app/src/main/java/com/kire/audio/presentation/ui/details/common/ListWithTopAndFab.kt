package com.kire.audio.presentation.ui.details.common

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState

import androidx.compose.material3.CircularProgressIndicator

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex

import com.kire.audio.presentation.ui.theme.AudioExtendedTheme
import com.kire.audio.presentation.ui.theme.animation.Animation
import com.kire.audio.presentation.ui.theme.dimen.Dimens
import com.kire.audio.presentation.util.modifier.dynamicPadding
import com.kire.audio.presentation.util.rememberDerivedStateOf

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

import kotlin.math.roundToInt

/**
 * Контейнер с верхним баром и плавающей кнопкой
 *
 * @param contentIsEmpty флаг того, что ничего нет и нужно оповестить пользователя об этом
 * @param shiftBottomBar сдвигает нижний бар вниз (сам бар за пределами данного контейнера)
 * @param topBar верхний бар
 * @param floatingButton плавающая кнопка
 * @param content основной контент
 *
 * @author Михаил Гонтарев (KiREHwYE)
 */
@Composable
fun ListWithTopAndFab(
    contentIsEmpty: () -> Boolean = { true },
    shiftBottomBar: () -> Unit = {},
    topBar: @Composable () -> Unit = {},
    floatingButton: @Composable () -> Unit = {},
    content: @Composable (Modifier, LazyListState) -> Unit = { _, _ -> },
) {
    val coroutineScope = rememberCoroutineScope()

    /** измерения контейнера в пикселях */
    val localDensity = LocalDensity.current

    /** высота верхнего бара в пикселях*/
    val topBarHeightPx = remember { mutableStateOf(0f) }
    /** свдиг верхнего бара по высоте */
    val topBarOffsetHeightPx = remember { mutableStateOf(0f) }
    /** динамический отступ для списка */
    val spaceHeight = rememberDerivedStateOf {
        (topBarHeightPx.value + topBarOffsetHeightPx.value) / localDensity.density
    }
    /** отступ верхней системной панели навигации */
    val topInsetPaddingPx = with(LocalDensity.current) {
        WindowInsets.navigationBars.asPaddingValues().calculateTopPadding().toPx()
    }

    /** высота плавающей кнопки в пикселях */
    val fabWidthPx = remember { mutableStateOf(0f) }
    /** свдиг нижнего бара по высоте */
    val fabOffsetWidthPx = remember { mutableStateOf(0f) }

    /** отступ нижней системной панели навигации */
    val bottomInsetPaddingPx = with(localDensity) {
        WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding().toPx()
    }

    /** Поднимает верхний бар, скрывая его из области видимости */
    fun shiftTopBarUp() {
        coroutineScope.launch {
            while (-topBarOffsetHeightPx.value < topBarHeightPx.value) {
                val newTopBarOffset = (topBarOffsetHeightPx.value - 5).coerceIn(
                    minimumValue = -topBarHeightPx.value,
                    maximumValue = 0f
                )
                topBarOffsetHeightPx.value = newTopBarOffset
                delay(1)
            }
        }
    }

    /** Опускает верхний бар, делая его полностью видимым */
    fun shiftTopBarDown() {
        coroutineScope.launch {
            while (topBarOffsetHeightPx.value < 0) {
                topBarOffsetHeightPx.value += 5
                delay(1)
            }
        }
    }

    /** слушатель скролла экрана */
    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                val deltaY = available.y

                val newBottomBarOffset = fabOffsetWidthPx.value + deltaY
                fabOffsetWidthPx.value =
                    newBottomBarOffset.coerceIn(
                        minimumValue = -fabWidthPx.value,
                        maximumValue = 0f
                    )

                var consumed: Offset

                val newTopBarOffset = (topBarOffsetHeightPx.value + deltaY).coerceIn(
                    minimumValue = -topBarHeightPx.value,
                    maximumValue = 0f
                )
                consumed = Offset(0f, newTopBarOffset - topBarOffsetHeightPx.value)
                topBarOffsetHeightPx.value = newTopBarOffset

                return consumed
            }
        }
    }

    /** Состояние списка */
    val state = rememberLazyListState()
    /** Флаг активности скролла */
    val scrollInProgress: Boolean by rememberDerivedStateOf {
        state.isScrollInProgress
    }

    /** Поднимаем/опускаем нижний бар по окончании скролла */
    LaunchedEffect(scrollInProgress) {
        if (!scrollInProgress) {
            coroutineScope.launch {
                shiftBottomBar()
                if (topBarOffsetHeightPx.value >= (-topBarHeightPx.value / 2))
                    shiftTopBarDown()
                else
                    shiftTopBarUp()
            }
        }
    }

    val noElementsMessageTopPadding by rememberDerivedStateOf {
        (topBarHeightPx.value / localDensity.density).dp
    }

    ///////////////////////////////////////////////////////////////////////////////////////////
    // Основной контент
    //////////////////////////////////////////////////////////////////////////////////////////
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = AudioExtendedTheme.extendedColors.background)
            .nestedScroll(nestedScrollConnection)
    ) {
        when (contentIsEmpty()) {
            true -> {
                /** Если список пустой, показываем индикатор загрузки */
                AnimatedVisibility(
                    visible = true,
                    enter = scaleIn(animationSpec = Animation.universalFiniteSpring())
                            + fadeIn(animationSpec = Animation.universalFiniteSpring()),
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    Box(
                        modifier = Modifier
                            .zIndex(0f)
                            .dynamicPadding(top = { noElementsMessageTopPadding })
                            .padding(horizontal = Dimens.universalPad)
                            .fillMaxSize(),
                        contentAlignment = Alignment.Center,
                        content = {
                            CircularProgressIndicator(
                                modifier = Modifier.size(52.dp),
                                color = AudioExtendedTheme.extendedColors.primaryText,
                                strokeWidth = 6.dp,
                            )
                        }
                    )
                }
            }
            false -> {
                /** Контент */
                content(
                    Modifier
                        .zIndex(0f)
                        .dynamicPadding(top = { spaceHeight.value.dp })
                        .padding(horizontal = Dimens.universalPad)
                        .fillMaxSize(),
                    state
                )
            }
        }

        ///////////////////////////////////////////////////////////////////////////////////////////
        // Верхний бар
        //////////////////////////////////////////////////////////////////////////////////////////
        Box(
            modifier = Modifier
                .zIndex(1f)
                .wrapContentSize()
                .background(color = Color.Transparent)
                .onGloballyPositioned {
                    topBarHeightPx.value = it.size.height.toFloat()
                }
                .align(alignment = Alignment.TopCenter)
                .offset {
                    IntOffset(
                        x = 0,
                        y = (topBarOffsetHeightPx.value + topInsetPaddingPx).roundToInt()
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            topBar()
        }

        ///////////////////////////////////////////////////////////////////////////////////////////
        // Плавающая кнопка
        //////////////////////////////////////////////////////////////////////////////////////////
        Box(
            modifier = Modifier
                .navigationBarsPadding()
                .wrapContentSize()
                .background(color = Color.Transparent)
                .padding(bottom = 128.dp)
                .padding(horizontal = Dimens.universalPad)
                .onGloballyPositioned {
                    fabWidthPx.value =
                        it.size.width.toFloat() + with(localDensity) { Dimens.universalPad.toPx() }
                }
                .align(alignment = Alignment.BottomEnd)
                .offset {
                    IntOffset(
                        x = -(fabOffsetWidthPx.value + bottomInsetPaddingPx).roundToInt(),
                        y = 0
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            floatingButton()
        }
    }
}