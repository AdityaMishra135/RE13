package com.kire.audio.presentation.activity

import android.annotation.SuppressLint
import android.app.Activity

import android.content.ComponentName
import android.content.Context
import android.content.res.Configuration

import android.os.Build
import android.os.Bundle

import android.view.Window
import android.view.WindowManager

import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels

import androidx.compose.animation.ExperimentalAnimationApi

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize

import androidx.compose.material3.Scaffold

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntOffset

import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle

import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken

import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi

import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors

import com.kire.audio.device.audio.AudioPlayerService
import com.kire.audio.device.audio.media_controller.MediaControllerManager
import com.kire.audio.device.audio.util.SkipTrackAction
import com.kire.audio.device.audio.media_controller.performPlayMedia
import com.kire.audio.device.audio.util.MediaCommands
import com.kire.audio.device.audio.media_controller.rememberManagedMediaController
import com.kire.audio.device.audio.util.PlayerState
import com.kire.audio.device.audio.util.state
import com.kire.audio.presentation.model.event.TrackUiEvent
import com.kire.audio.presentation.navigation.NavigationUI
import com.kire.audio.presentation.ui.details.common.AutoSkipOnRepeatMode
import com.kire.audio.presentation.ui.details.common.PlayerBottomBar

import com.kire.audio.presentation.ui.theme.AudioExtendedTheme
import com.kire.audio.presentation.ui.theme.dimen.Dimens
import com.kire.audio.presentation.ui.theme.localization.LocalizationProvider
import com.kire.audio.presentation.viewmodel.TrackViewModel
import com.kire.audio.screen.functional.GetPermissions

import com.ramcosta.composedestinations.animations.rememberAnimatedNavHostEngine

import dagger.hilt.android.AndroidEntryPoint

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@UnstableApi
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val trackViewModel: TrackViewModel by viewModels()

    private var factory: ListenableFuture<MediaController>? = null

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @OptIn(ExperimentalMaterialNavigationApi::class, ExperimentalAnimationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        LocalizationProvider.updateLocalization(this)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.hideSystemUi(extraAction = {
            systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        })
        setDisplayCutoutMode()

        setContent {

            val navHostEngine = rememberAnimatedNavHostEngine(navHostContentAlignment = Alignment.TopCenter)
            val navHostController = navHostEngine.rememberNavController()

            val mediaController by rememberManagedMediaController()

            /** измерения контейнера в пикселях */
            val localDensity = LocalDensity.current

            /** высота плавающей кнопки в пикселях */
            val bottomBarHeightPx = remember { mutableStateOf(0f) }

            /** свдиг нижнего бара по высоте */
            val bottomBarOffsetHeightPx = remember { mutableStateOf(0f) }

            /** отступ нижней системной панели навигации */
            val bottomInsetPaddingPx = with(localDensity) {
                WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding().toPx()
            }

            /** слушатель скролла экрана */
            val nestedScrollConnection = remember {
                object : NestedScrollConnection {
                    override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                        val delta = available.y

                        val newBottomBarOffset = bottomBarOffsetHeightPx.value + delta
                        bottomBarOffsetHeightPx.value =
                            newBottomBarOffset.coerceIn(
                                minimumValue = -bottomBarHeightPx.value - bottomInsetPaddingPx,
                                maximumValue = 0f
                            )

                        return Offset.Zero
                    }
                }
            }

            val coroutineScope = rememberCoroutineScope()

            fun shiftButtonDown() {
                coroutineScope.launch {
                    while (-bottomBarOffsetHeightPx.value < bottomBarHeightPx.value) {
                        val newTopBarOffset = (bottomBarOffsetHeightPx.value - 5).coerceIn(
                            minimumValue = -bottomBarHeightPx.value,
                            maximumValue = 0f
                        )
                        bottomBarOffsetHeightPx.value = newTopBarOffset
                        delay(1)
                    }
                }
            }

            fun shiftBarUp() {
                coroutineScope.launch {
                    while (bottomBarOffsetHeightPx.value < 0) {
                        bottomBarOffsetHeightPx.value += 5
                        delay(1)
                    }
                }
            }

            CompositionLocalProvider(
                LocalDensity provides Density(
                    LocalDensity.current.density,
                    1f // дефолтный скейл текста
                )
            ) {
                AudioExtendedTheme {

                    /** Текущее состояние плеера */
                    var playerState: PlayerState? by remember {
                        mutableStateOf(mediaController?.state())
                    }

                    /** Создаем экземпляр состояния плеера */
                    DisposableEffect(key1 = mediaController) {
                        mediaController?.run {
                            playerState = state()
                        }
                        onDispose {
                            playerState?.dispose()
                        }
                    }

                    Scaffold(
                        modifier = Modifier
                            .nestedScroll(nestedScrollConnection),
                        bottomBar = {
                            Box(
                                modifier = Modifier
                                    .navigationBarsPadding()
                                    .wrapContentSize()
                                    .background(color = Color.Transparent)
                                    .padding(Dimens.universalPad)
                                    .onGloballyPositioned {
                                        bottomBarHeightPx.value =
                                            it.size.height.toFloat() + with(localDensity) { 2 * Dimens.universalPad.toPx() }
                                    }
                                    .offset {
                                        IntOffset(
                                            x = 0,
                                            y = -bottomBarOffsetHeightPx.value.roundToInt()
                                        )
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                PlayerBottomBar(
                                    trackState = trackViewModel.trackState,
                                    mediaController = mediaController,
                                    changeTrackUiState = {
                                        trackViewModel.onEvent(
                                            TrackUiEvent.updateTrackState(it)
                                        )
                                    },
                                    navHostController = navHostController,
                                    onDragDown = ::shiftButtonDown
                                )
                            }
                        }
                    ) { _ ->

                        GetPermissions(
                            lifecycleOwner = LocalLifecycleOwner.current,
                            updateTrackDataBase = trackViewModel::updateTrackDataBase
                        )

                        AutoSkipOnRepeatMode(
                            trackState = trackViewModel.trackState,
                            mediaController = mediaController
                        )

                        NavigationUI(
                            trackViewModel = trackViewModel,
                            shiftBottomBar = {
                                coroutineScope.launch {
                                    if (bottomBarOffsetHeightPx.value <= ((-bottomBarHeightPx.value + with(localDensity) { Dimens.universalPad.toPx() }) / 2))
                                        shiftButtonDown()
                                    else
                                        shiftBarUp()
                                }
                            },
                            mediaController = mediaController,
                            navHostController = navHostController,
                            navHostEngine = navHostEngine
                        )
                    }
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                launch {
                    MediaCommands.isPlayRequired.collect {
                        trackViewModel.onEvent(
                            TrackUiEvent.updateTrackState(
                                trackViewModel.trackState.value.copy(isPlaying = it)
                            )
                        )
                    }
                }
                launch {
                    MediaCommands.isPreviousTrackRequired.collect {
                        if (it)
                            skipTrack(this@MainActivity, SkipTrackAction.PREVIOUS, trackViewModel)
                    }
                }
                launch {
                    MediaCommands.isNextTrackRequired.collect {
                        if (it)
                            skipTrack(this@MainActivity, SkipTrackAction.NEXT, trackViewModel)
                    }
                }
                launch {
                    MediaCommands.isRepeatRequired.collect {
                        if (it)
                            skipTrack(this@MainActivity, SkipTrackAction.REPEAT, trackViewModel)
                    }
                }
            }
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        LocalizationProvider.updateLocalization(this)
    }

    override fun onStart() {
        super.onStart()

        factory = MediaController.Builder(
            this,
            SessionToken(this, ComponentName(this, AudioPlayerService::class.java))
        ).buildAsync()

        factory?.addListener(
            {
                // MediaController is available here with controllerFuture.get()
                factory?.let {
                    if (it.isDone)
                        it.get()
                    else null
                }
            },
            MoreExecutors.directExecutor()
        )
    }

    override fun onDestroy() {
        super.onDestroy()

        factory?.let {
            MediaController.releaseFuture(it)
            mediaController = null
        }
        factory = null
    }
}


private fun skipTrack(context: Context, skipTrackAction: SkipTrackAction, viewModel: TrackViewModel){

    val mediaController = MediaControllerManager.getInstance(context)

    viewModel.apply {

        val newINDEX =
            skipTrackAction.action(
                trackState.value.currentTrackPlayingIndex!!,
                trackState.value.currentList.size
            )

        if (skipTrackAction == SkipTrackAction.NEXT || skipTrackAction == SkipTrackAction.PREVIOUS) {
            onEvent(
                TrackUiEvent.updateTrackState(
                    trackState.value.copy(
                        currentTrackPlaying = trackState.value.currentList[newINDEX],
                        currentTrackPlayingIndex = newINDEX
                    )
                )
            )

            MediaCommands.isTrackRepeated.value = false
        }

        mediaController.controller.value?.performPlayMedia(trackState.value.currentList[newINDEX])

        MediaCommands.isRepeatRequired.value = false
    }

    if (skipTrackAction == SkipTrackAction.PREVIOUS)
        MediaCommands.isPreviousTrackRequired.value = false
    else
        MediaCommands.isNextTrackRequired.value = false
}

private fun Window.hideSystemUi(extraAction:(WindowInsetsControllerCompat.() -> Unit)? = null) {
    WindowInsetsControllerCompat(this, this.decorView).let { controller ->
        controller.hide(WindowInsetsCompat.Type.systemBars())
        extraAction?.invoke(controller)
    }
}

internal fun Activity.setDisplayCutoutMode() {
    when {
        Build.VERSION.SDK_INT < Build.VERSION_CODES.R -> {
            window.attributes.layoutInDisplayCutoutMode =
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
        }
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.R -> {
            window.attributes.layoutInDisplayCutoutMode =
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_ALWAYS
        }
    }
}