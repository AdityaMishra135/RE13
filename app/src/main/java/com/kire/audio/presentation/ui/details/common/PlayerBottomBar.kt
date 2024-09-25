package com.kire.audio.presentation.ui.details.common

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned

import androidx.lifecycle.compose.collectAsStateWithLifecycle

import androidx.media3.session.MediaController

import androidx.navigation.NavHostController
import com.kire.audio.presentation.model.PlayerStateParams

import com.kire.audio.presentation.model.state.TrackState
import com.kire.audio.presentation.ui.screen.NavGraphs
import com.kire.audio.presentation.ui.screen.appCurrentDestinationAsState
import com.kire.audio.presentation.ui.screen.destinations.AlbumScreenDestination
import com.kire.audio.presentation.ui.screen.destinations.Destination
import com.kire.audio.presentation.ui.screen.destinations.PlayerScreenDestination
import com.kire.audio.presentation.ui.screen.startAppDestination
import com.kire.audio.presentation.ui.theme.AudioExtendedTheme
import com.kire.audio.presentation.ui.theme.dimen.Dimens

import com.ramcosta.composedestinations.navigation.navigate

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

/**
 * Небольшая плавающая панель внизу экрана с информацией о текущем треке и кнопками управления
 *
 * @param trackState состояние воспроизведения
 * @param mediaController для управления воспроизведением
 * @param navHostController контроллер навигации
 * @param onDragDown действие при свайпе вниз
 *
 * @author Михаил Гонтарев (KiREHwYE)
 */
@Composable
fun PlayerBottomBar(
    trackState: StateFlow<TrackState>,
    mediaController: MediaController?,
    navHostController: NavHostController,
    onDragDown: () -> Unit,
) {
    /** Область видимости корутин */
    val coroutineScope = rememberCoroutineScope()

    /** Состояние воспроизведения */
    val trackState by trackState.collectAsStateWithLifecycle()

    val currentDestination: Destination = navHostController.appCurrentDestinationAsState().value
        ?: NavGraphs.root.startAppDestination

    /** Высота PlayerBottomBar + отступ от низа экрана */
    var height by remember {
        mutableStateOf(0)
    }

    AnimatedVisibility(
        visible = PlayerStateParams.isPlayerBottomBarShown &&
                currentDestination != PlayerScreenDestination && currentDestination != AlbumScreenDestination,
        enter = slideInVertically(
            initialOffsetY = { height },
            animationSpec = tween(durationMillis = 450, easing = LinearOutSlowInEasing)
        ) + fadeIn(animationSpec = tween(durationMillis = 100)),
        exit = slideOutVertically(
            targetOffsetY = { height },
            animationSpec = tween(durationMillis = 450, easing = LinearOutSlowInEasing)
        ) + fadeOut(animationSpec = tween(durationMillis = 100))
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .onGloballyPositioned {
                    height = it.size.height + Dimens.universalPad.value.roundToInt()
                }
                .shadow(
                    elevation = Dimens.universalShadowElevation,
                    spotColor = AudioExtendedTheme.extendedColors.shadow,
                    shape = RoundedCornerShape(Dimens.universalRoundedCorner)
                )
                .clip(RoundedCornerShape(Dimens.universalRoundedCorner))
                .pointerInput(Unit) {
                    detectVerticalDragGestures { _, dragAmount ->

                        if (dragAmount > 10)
                            coroutineScope.launch(Dispatchers.IO) {
                                onDragDown()
                            }
                    }
                }
                .background(AudioExtendedTheme.extendedColors.controlElementsBackground)
                .padding(Dimens.universalPad),
            horizontalArrangement = Arrangement.spacedBy(Dimens.columnAndRowUniversalSpacedBy),
            verticalAlignment = Alignment.CenterVertically
        ) {

            /** Обложка трека, его название и исполнитель */
            ListItem(
                track = trackState.currentTrackPlaying!!,
                modifier = Modifier.weight(1f),
                onClick = {
                    navHostController.navigate(PlayerScreenDestination)
                    PlayerStateParams.isPlayerBottomBarShown = false
                }
            )

            /** Кнопки для управления воспроизведением */
            MediaControls(
                trackState = trackState,
                mediaController = mediaController,
                modifier = Modifier.wrapContentSize(),
                horizontalArrangement = Arrangement.spacedBy(Dimens.columnAndRowUniversalSpacedBy)
            )
        }
    }
}