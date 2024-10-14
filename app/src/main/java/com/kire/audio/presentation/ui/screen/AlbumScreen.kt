package com.kire.audio.presentation.ui.screen

import androidx.activity.compose.BackHandler

import androidx.compose.animation.core.animateDpAsState

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp

import androidx.lifecycle.compose.collectAsStateWithLifecycle

import androidx.media3.session.MediaController

import com.kire.audio.presentation.navigation.transitions.AlbumScreenTransitions

import com.kire.audio.presentation.ui.details.album_screen_ui.AlbumInfoPanel
import com.kire.audio.presentation.ui.details.album_screen_ui.AlbumScreenCentralDraggableControls
import com.kire.audio.presentation.ui.details.common.AsyncImageWithLoading
import com.kire.audio.presentation.ui.details.common.BlurPanel
import com.kire.audio.presentation.ui.details.common.LazyListPattern
import com.kire.audio.presentation.ui.details.common.PanelNumber
import com.kire.audio.presentation.ui.screen.destinations.ListAlbumScreenDestination
import com.kire.audio.presentation.ui.screen.destinations.PlayerScreenDestination
import com.kire.audio.presentation.ui.theme.AudioExtendedTheme
import com.kire.audio.presentation.ui.theme.dimen.Dimens
import com.kire.audio.presentation.ui.theme.localization.LocalizationProvider
import com.kire.audio.presentation.util.modifier.dynamicPadding
import com.kire.audio.presentation.viewmodel.TrackViewModel

import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

/**
 * Экран отдельного альбома. Содержит список всех треков данного альбома.
 *
 * @param trackViewModel VievModel, содержащая все необходимые для работы с треками поля и методы
 * @param mediaController для управления воспроизведением
 * @param navigator для навигации между экранами
 *
 * @author Михаил Гонтарев (KiREHwYE)
 * */
@Destination(style = AlbumScreenTransitions::class)
@Composable
fun AlbumScreen(
    trackViewModel: TrackViewModel,
    mediaController: MediaController? = null,
    navigator: DestinationsNavigator
) {
    /** Переопределяем жест назад */
    BackHandler {
        navigator.popBackStack()
        return@BackHandler
    }
    /** Экземпляр TrackState. Содержит информацию о состоянии воспроизведения */
    val trackState by trackViewModel.trackState.collectAsStateWithLifecycle()

    /** Текущая плотность пикселей */
    val localDensity = LocalDensity.current

    /** Высота верхнего бара в пикселях*/
    val topBarHeightPx = remember { mutableStateOf(0f) }
    /** Свдиг верхнего бара по высоте */
    val topBarOffsetHeightPx = remember { mutableStateOf(0f) }

    /** Динамический отступ для списка */
    val spaceHeight = remember {
        derivedStateOf {
            (topBarHeightPx.value + topBarOffsetHeightPx.value) / localDensity.density
        }
    }

    /** Флаг того, что центральная панель сдвинута в крайнее верхнее положение */
    var isCentralBlockShifted by rememberSaveable {
        mutableStateOf(true)
    }

    /** Анимированный отступ от верхнего края экрана */
    val animatedTopPad by animateDpAsState(targetValue = if (isCentralBlockShifted) 0.dp else WindowInsets.displayCutout.asPaddingValues().calculateTopPadding())

    /** Область выполнения корутин */
    val coroutineScope = rememberCoroutineScope()

    /** Поднимет вверх облажку альбома за границы экрана */
    fun shiftImageUp() {
        coroutineScope.launch {
            while (-topBarOffsetHeightPx.value < topBarHeightPx.value) {
                 val newTopBarOffset = (topBarOffsetHeightPx.value - 5).coerceIn(
                    minimumValue = -topBarHeightPx.value,
                    maximumValue = 0f
                )
                topBarOffsetHeightPx.value = newTopBarOffset
                delay(1)
            }
            isCentralBlockShifted = false
        }
    }
    /** Опускает вниз облажку альбома */
    fun shiftImageDown() {
        coroutineScope.launch {
            while (topBarOffsetHeightPx.value < 0) {
                val newTopBarOffset = (topBarOffsetHeightPx.value + 5).coerceIn(
                    minimumValue = -topBarHeightPx.value,
                    maximumValue = 0f
                )
                topBarOffsetHeightPx.value = newTopBarOffset
                delay(1)
            }
            isCentralBlockShifted = true
        }
    }

    /** Основной контент */
    BlurPanel(
        onTopOfBlurPanel1 = {
            AlbumInfoPanel(
                trackState = trackViewModel.trackState,
                onEvent = trackViewModel::onEvent
            )
        }
    ) { expandPanelByNumber ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(AudioExtendedTheme.extendedColors.roseAccent)
                .pointerInput(Unit) {
                    detectVerticalDragGestures { _, dragAmount ->
                        if (dragAmount > 60) {
                            navigator.popBackStack()
                        }
                    }
                }
        ) {

            /** Обложка альбома */
            AsyncImageWithLoading(
                imageUri = trackState.currentList[0].imageUri,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f / 1f)
                    .clip(
                        RoundedCornerShape(
                            bottomStart = Dimens.universalRoundedCorners,
                            bottomEnd = Dimens.universalRoundedCorners
                        )
                    )
                    .onGloballyPositioned {
                        topBarHeightPx.value = it.size.height.toFloat()
                    }
                    .offset { IntOffset(x = 0, y = topBarOffsetHeightPx.value.roundToInt()) }
            )

            Column(
                modifier = Modifier
                    .dynamicPadding(top = { spaceHeight.value.dp })
                    .fillMaxSize()
            ) {
                /** Центральная часть экрана, содержащая название альбома,
                 * кнопку возвращения назад и кнопку открытия панели информации об альбоме */
                AlbumScreenCentralDraggableControls(
                    albumTitle = { trackState.currentList[0].album ?: LocalizationProvider.strings.nothingWasFound },
                    animatedTopPad = { animatedTopPad },
                    onArrowBackClick = {
                        navigator.popBackStack()
                    },
                    onInfoClick = {
                        expandPanelByNumber(PanelNumber.FIRST)
                    },
                    onDrag = { deltaY ->
                        val newTopBarOffset =
                            (topBarOffsetHeightPx.value + deltaY).coerceIn(
                                minimumValue = -topBarHeightPx.value,
                                maximumValue = 0f
                            )
                        topBarOffsetHeightPx.value = newTopBarOffset
                    },
                    onDragEnd = {
                        coroutineScope.launch {
                            if (topBarOffsetHeightPx.value >= (-topBarHeightPx.value / 2))
                                shiftImageDown()
                            else
                                shiftImageUp()
                        }
                    }
                )

                /** Треки данного альбома */
                LazyListPattern(
                    trackStateFlow = trackViewModel.trackState,
                    onEvent = trackViewModel::onEvent,
                    mediaController = mediaController,
                    navigateToPlayerScreen = {
                        navigator.navigate(PlayerScreenDestination)
                    },
                    list = { trackState.currentList },
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(
                            RoundedCornerShape(
                                topStart = Dimens.universalRoundedCorners,
                                topEnd = Dimens.universalRoundedCorners
                            )
                        )
                        .background(AudioExtendedTheme.extendedColors.background)
                        .padding(horizontal = Dimens.universalPad)
                )
            }
        }
    }
}