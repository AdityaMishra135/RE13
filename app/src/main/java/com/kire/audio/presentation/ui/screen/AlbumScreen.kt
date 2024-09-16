package com.kire.audio.presentation.ui.screen

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.MarqueeAnimationMode
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowCircleLeft
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material3.Icon

import androidx.compose.material3.Text

import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import androidx.lifecycle.compose.collectAsStateWithLifecycle

import androidx.media3.session.MediaController

import com.kire.audio.presentation.navigation.transitions.AlbumScreenTransitions
import com.kire.audio.presentation.ui.details.album_screen_ui.dialog_album_info.AlbumInfoPanel
import com.kire.audio.presentation.ui.details.common.AsyncImageWithLoading
import com.kire.audio.presentation.ui.details.common.BlurPanel
import com.kire.audio.presentation.ui.details.common.LazyListMainAndAlbumPattern
import com.kire.audio.presentation.ui.details.common.RubikFontText
import com.kire.audio.presentation.ui.screen.destinations.ListAlbumScreenDestination
import com.kire.audio.presentation.ui.screen.destinations.PlayerScreenDestination
import com.kire.audio.presentation.ui.theme.AudioExtendedTheme
import com.kire.audio.presentation.ui.theme.dimen.Dimens
import com.kire.audio.presentation.ui.theme.localization.LocalizationProvider
import com.kire.audio.presentation.util.bounceClick
import com.kire.audio.presentation.viewmodel.TrackViewModel

import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@OptIn(ExperimentalFoundationApi::class)
@Destination(style = AlbumScreenTransitions::class)
@Composable
fun AlbumScreen(
    trackViewModel: TrackViewModel,
    mediaController: MediaController?,
    navigator: DestinationsNavigator
) {

    val trackState by trackViewModel.trackState.collectAsStateWithLifecycle()

    val localDensity = LocalDensity.current

    /** высота верхнего бара в пикселях*/
    val topBarHeightPx = remember { mutableStateOf(0f) }

    /** свдиг верхнего бара по высоте */
    val topBarOffsetHeightPx = remember { mutableStateOf(0f) }

    /** динамический отступ для списка */
    val spaceHeight = remember {
        derivedStateOf {
            (topBarHeightPx.value + topBarOffsetHeightPx.value) / localDensity.density
        }
    }

    var isTopBarExpanded by rememberSaveable {
        mutableStateOf(true)
    }

    val displayCutoutPad by animateDpAsState(targetValue = if (isTopBarExpanded) 0.dp else WindowInsets.displayCutout.asPaddingValues().calculateTopPadding())

    val coroutineScope = rememberCoroutineScope()

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
            isTopBarExpanded = false
        }
    }

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
            isTopBarExpanded = true
        }
    }

    BlurPanel(
        onTopOfBlurredPanel = {
            AlbumInfoPanel(
                trackState = trackState,
                onEvent = trackViewModel::onEvent
            )
        }
    ) { modifierToExpandBlurPanel ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(AudioExtendedTheme.extendedColors.roseAccent)
                .pointerInput(Unit) {
                    detectVerticalDragGestures { _, dragAmount ->
                        if (dragAmount > 60) {
                            navigator.popBackStack(
                                ListAlbumScreenDestination,
                                inclusive = false
                            )
                        }
                    }
                }
        ) {

            AsyncImageWithLoading(
                model =  trackState.currentList[0].imageUri,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f / 1f)
                    .clip(
                        RoundedCornerShape(
                            bottomStart = Dimens.universalRoundedCorner,
                            bottomEnd = Dimens.universalRoundedCorner
                        )
                    )
                    .onGloballyPositioned {
                        topBarHeightPx.value = it.size.height.toFloat()
                    }
                    .offset { IntOffset(x = 0, y = topBarOffsetHeightPx.value.roundToInt()) }
            )

            Column(
                modifier = Modifier
                    .padding(top = spaceHeight.value.dp)
                    .fillMaxSize()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .pointerInput(Unit) {
                            detectDragGestures(
                                onDrag = { change, dragAmount ->
                                    val deltaY = dragAmount.y

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
                        }
                        .padding(Dimens.universalPad)
                        .padding(top = displayCutoutPad),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Icon(
                        imageVector = Icons.Rounded.ArrowCircleLeft,
                        contentDescription = null,
                        tint = if (!isSystemInDarkTheme()) Color.White else AudioExtendedTheme.extendedColors.button,
                        modifier = Modifier
                            .size(Dimens.albumScreenIconSize)
                            .bounceClick {
                                navigator.popBackStack(
                                    ListAlbumScreenDestination,
                                    inclusive = false
                                )
                            }
                    )


                    RubikFontText(
                        text = trackState.currentList[0].album
                            ?: LocalizationProvider.strings.nothingWasFound,
                        style = TextStyle(
                            color = if (!isSystemInDarkTheme()) Color.White else AudioExtendedTheme.extendedColors.primaryText,
                            fontSize = 24.sp,
                            lineHeight = 24.sp,
                            fontWeight = FontWeight.Medium,
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = Dimens.universalPad)
                    )

                    Icon(
                        imageVector = Icons.Rounded.Info,
                        contentDescription = null,
                        tint = if (!isSystemInDarkTheme()) Color.White else AudioExtendedTheme.extendedColors.button,
                        modifier = modifierToExpandBlurPanel
                            .size(Dimens.albumScreenIconSize)
                    )
                }

                LazyListMainAndAlbumPattern(
                    trackState = trackViewModel.trackState,
                    onEvent = trackViewModel::onEvent,
                    list = trackState.currentList,
                    mediaController = mediaController,
                    goToPlayerScreen = {
                        navigator.navigate(PlayerScreenDestination)
                    },
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(
                            RoundedCornerShape(
                                topStart = Dimens.universalRoundedCorner,
                                topEnd = Dimens.universalRoundedCorner
                            )
                        )
                        .background(AudioExtendedTheme.extendedColors.background)
                        .padding(horizontal = Dimens.universalPad)
                )
            }
        }
    }
}