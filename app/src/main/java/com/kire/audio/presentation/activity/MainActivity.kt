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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize

import androidx.compose.material3.Scaffold

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.Density

import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

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
import com.kire.audio.presentation.model.PlayerStateParams
import com.kire.audio.presentation.model.Track
import com.kire.audio.presentation.model.event.TrackUiEvent
import com.kire.audio.presentation.model.state.TrackState
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

            CompositionLocalProvider(
                LocalDensity provides Density(
                    LocalDensity.current.density,
                    1f
                )
            ) {
                AudioExtendedTheme {

                    PlayerStateParams.isPlaying = MediaCommands.isPlayRequired

                    if (MediaCommands.isPreviousTrackRequired)
                        skipTrack(this@MainActivity, SkipTrackAction.PREVIOUS, trackViewModel)

                    if (MediaCommands.isNextTrackRequired)
                        skipTrack(this@MainActivity, SkipTrackAction.NEXT, trackViewModel)

                    if (MediaCommands.isRepeatRequired)
                        skipTrack(this@MainActivity, SkipTrackAction.REPEAT, trackViewModel)

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

                    NestedContainer { nestedScrollConnection, modifier, shiftPlayerBottomBarDown, shiftPlayerBottomBar ->
                        Scaffold(
                            modifier = Modifier
                                .nestedScroll(nestedScrollConnection),
                            bottomBar = {
                                Box(
                                    modifier = modifier
                                        .navigationBarsPadding()
                                        .wrapContentSize()
                                        .background(color = Color.Transparent)
                                        .padding(Dimens.universalPad),
                                    contentAlignment = Alignment.Center
                                ) {
                                    PlayerBottomBar(
                                        trackState = trackViewModel.trackState,
                                        mediaController = mediaController,
                                        navHostController = navHostController,
                                        onDragDown = shiftPlayerBottomBarDown
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
                                shiftPlayerBottomBar = shiftPlayerBottomBar,
                                mediaController = mediaController,
                                navHostController = navHostController,
                                navHostEngine = navHostEngine
                            )
                        }
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

private fun skipTrack(
    context: Context,
    skipTrackAction: SkipTrackAction,
    trackViewModel: TrackViewModel
){

    val mediaController = MediaControllerManager.getInstance(context)

    trackViewModel.apply {

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

            MediaCommands.isTrackRepeated = false
        }

        mediaController.controller.value?.performPlayMedia(trackState.value.currentList[newINDEX])

        MediaCommands.isRepeatRequired = false
    }

    if (skipTrackAction == SkipTrackAction.PREVIOUS)
        MediaCommands.isPreviousTrackRequired = false
    else
        MediaCommands.isNextTrackRequired = false
}

private fun Window.hideSystemUi(extraAction:(WindowInsetsControllerCompat.() -> Unit)? = null) {
    WindowInsetsControllerCompat(this, this.decorView).let { controller ->
        controller.hide(WindowInsetsCompat.Type.systemBars())
        extraAction?.invoke(controller)
    }
}

private fun Activity.setDisplayCutoutMode() {
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