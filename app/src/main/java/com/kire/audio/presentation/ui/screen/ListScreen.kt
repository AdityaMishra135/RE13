package com.kire.audio.presentation.ui.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue

import androidx.lifecycle.compose.collectAsStateWithLifecycle

import com.kire.audio.presentation.viewmodel.TrackViewModel

import androidx.media3.session.MediaController
import com.kire.audio.device.audio.media_controller.performPlayMedia
import com.kire.audio.presentation.model.PlayerStateParams

import com.kire.audio.presentation.navigation.transitions.ListScreenTransitions
import com.kire.audio.presentation.model.event.TrackUiEvent
import com.kire.audio.presentation.ui.details.common.LazyListPattern
import com.kire.audio.presentation.ui.details.common.ListWithTopAndFab
import com.kire.audio.presentation.ui.details.list_screen_ui.top_block.TopBlock
import com.kire.audio.presentation.ui.screen.destinations.AlbumScreenDestination
import com.kire.audio.presentation.ui.screen.destinations.ListAlbumScreenDestination
import com.kire.audio.presentation.ui.screen.destinations.PlayerScreenDestination
import com.kire.audio.presentation.util.rememberDerivedStateOf

import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

/**
 * Главный экран приложения. Отображает список всех треков, сохраненных на устройстве.
 *
 * @param trackViewModel VievModel, содержащая все необходимые для работы с треками поля и методы
 * @param shiftPlayerBottomBar функция, опускающая PlayerBottomBar за границы экрана
 * @param navigator для навигации между экранами
 * @param mediaController для управления воспроизведением
 *
 * @author Михаил Гонтарев (KiREHwYE)
 */
@RootNavGraph(start = true)
@Destination(style = ListScreenTransitions::class)
@Composable
fun ListScreen(
    navigator: DestinationsNavigator,
    trackViewModel: TrackViewModel,
    shiftPlayerBottomBar: () -> Unit = {},
    mediaController: MediaController? = null
) {
    /** Экземпляр TrackState. Содержит информацию о состоянии воспроизведения */
    val trackState by trackViewModel.trackState.collectAsStateWithLifecycle()
    /** Список всех треков */
    val allTracks by trackViewModel.tracks.collectAsStateWithLifecycle()

    /** Заранее обновляем список альбомов */
    LaunchedEffect(Unit) {
        trackViewModel.onEvent(TrackUiEvent.updateArtistWithTracks())
    }

    /** Флаг того, что список пуст и нужно уведомить об этом пользователя */
    val contentIsEmpty by rememberDerivedStateOf {
        { allTracks.isEmpty() }
    }

    /** Действие при клике на плитку альбома в с списке быстрого доступа в TopBlock */
    val onAlbumSuggestionClick by rememberDerivedStateOf {
        { albumTitle: String ->
            val album = trackViewModel.artistWithTracks.value[albumTitle]

            album?.let {
                PlayerStateParams.isPlaying = true
                trackViewModel.onEvent(
                    TrackUiEvent.updateTrackState(
                        trackState.copy(
                            currentList = album,
                            currentTrackPlaying = try {
                                album[0]
                            } catch (_: Exception) { null },
                            currentTrackPlayingIndex = 0
                        )
                    )
                )
                navigator.navigate(AlbumScreenDestination)
                try {
                    mediaController?.performPlayMedia(album[0])
                } catch (_: Exception) { }
            } ?: Unit
        }
    }

    /** Отрисовываем контент экрана */
    ListWithTopAndFab(
        contentIsEmpty = contentIsEmpty,
        shiftBottomBar = shiftPlayerBottomBar,
        topBar = {
            /** Шапка с названием экрана, поиском, сортировкой треков и возможностью перехода на экран альбомов*/
            TopBlock(
                trackViewModel = trackViewModel,
                mediaController = mediaController,
                onSearchResulItemClick = {
                    navigator.navigate(PlayerScreenDestination)
                },
                onTitleClick = {
                    navigator.navigate(ListAlbumScreenDestination)
                },
                onAlbumSuggestionClick = onAlbumSuggestionClick
            )
        }
    ) { modifier, listState ->

        /** Список треков */
        LazyListPattern(
            trackStateFlow = trackViewModel.trackState,
            onEvent = trackViewModel::onEvent,
            list = { allTracks },
            mediaController = mediaController,
            state = listState,
            navigateToPlayerScreen = {
                navigator.navigate(PlayerScreenDestination)
            },
            modifier = modifier
        )
    }
}