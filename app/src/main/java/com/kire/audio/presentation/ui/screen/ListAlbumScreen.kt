package com.kire.audio.presentation.ui.screen

import androidx.activity.compose.BackHandler

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment

import androidx.compose.ui.Modifier

import androidx.lifecycle.compose.collectAsStateWithLifecycle

import androidx.media3.session.MediaController
import com.kire.audio.presentation.model.PlayerStateParams

import com.kire.audio.presentation.navigation.transitions.ListAlbumScreenTransitions

import com.kire.audio.presentation.ui.details.album_screen_ui.list_item_album_wrapper.ListItemAlbumWrapper
import com.kire.audio.presentation.ui.details.common.ListWithTopAndFab
import com.kire.audio.presentation.ui.details.common.ScreenHeader
import com.kire.audio.presentation.ui.screen.destinations.AlbumScreenDestination
import com.kire.audio.presentation.ui.screen.destinations.ListScreenDestination
import com.kire.audio.presentation.ui.theme.dimen.Dimens
import com.kire.audio.presentation.ui.theme.localization.LocalizationProvider
import com.kire.audio.presentation.util.rememberDerivedStateOf

import com.kire.audio.presentation.viewmodel.TrackViewModel

import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

/**
 * Экран альбомов. Отображает список всех альбомов.
 *
 * @param trackViewModel VievModel, содержащая все необходимые для работы с треками поля и методы
 * @param shiftPlayerBottomBar функция, опускающая PlayerBottomBar за границы экрана
 * @param mediaController для управления воспроизведением
 * @param navigator для навигации между экранами
 *
 * @author Михаил Гонтарев (KiREHwYE)
 */
@Destination(style = ListAlbumScreenTransitions::class)
@Composable
fun ListAlbumScreen(
    trackViewModel: TrackViewModel,
    shiftPlayerBottomBar: () -> Unit,
    mediaController: MediaController? = null,
    navigator: DestinationsNavigator
){
    /** Словарь с ключами - альбомами и значениями - списками треков, им соответствующими */
    val albumsWithTracks by trackViewModel.artistWithTracks.collectAsStateWithLifecycle()
    /** Список названий альбомов */
    val albums by rememberDerivedStateOf {
        albumsWithTracks.keys.toList()
    }

    /** Флаг того, что список пуст и нужно уведомить об этом пользователя */
    val contentIsEmpty by rememberDerivedStateOf {
        { albums.isEmpty() }
    }

    /** Переопределяем жест назад */
    BackHandler {
        navigator.popBackStack(ListScreenDestination, inclusive = false)
        return@BackHandler
    }

    /** Отрисовываем контент экрана */
    ListWithTopAndFab(
        contentIsEmpty = contentIsEmpty,
        shiftBottomBar = shiftPlayerBottomBar,
        topBar = {
            /** Шапка с названием экрана, поиском, сортировкой треков и возможностью перехода на экран альбомов*/
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(top = Dimens.screenTitleTopPad, bottom = Dimens.universalPad)
                    .windowInsetsPadding(WindowInsets.displayCutout)
                    .padding(horizontal = Dimens.universalPad),
                contentAlignment = Alignment.BottomStart
            ) {
                ScreenHeader(
                    screenTitle = LocalizationProvider.strings.albumScreenHeader,
                    isClicked = { false },
                    onTitleClick = {
                        navigator.popBackStack(ListScreenDestination, inclusive = false)
                    }
                )
            }
        }
    ) { modifier, state ->

        /** Список альбомов */
        LazyColumn(
            state = state,
            modifier = modifier,
            contentPadding = PaddingValues(bottom = Dimens.universalColumnVerticalContentPad),
            verticalArrangement = Arrangement.spacedBy(Dimens.universalColumnAndRowSpacedBy)
        ) {

            itemsIndexed(albums, key = {_, title -> title}) { _, album ->

                /** Треки, соответствующие данному альбому */
                val tracks by rememberDerivedStateOf {
                    albumsWithTracks[album] ?: emptyList()
                }

                /** Обертка для элемента списка, предоставляющая быстрый доступ к трекам альбома */
                ListItemAlbumWrapper(
                    trackStateFlow = trackViewModel.trackState,
                    tracks = tracks,
                    onEvent = trackViewModel::onEvent,
                    mediaController = mediaController,
                    onImageClick = {
                        navigator.navigate(AlbumScreenDestination)
                        PlayerStateParams.isPlayerBottomBarShown = false
                    }
                )
            }
        }
    }
}