package com.kire.audio.presentation.ui.screen

import androidx.activity.compose.BackHandler

import androidx.compose.foundation.gestures.detectHorizontalDragGestures
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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment

import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput

import androidx.lifecycle.compose.collectAsStateWithLifecycle

import androidx.media3.session.MediaController

import com.kire.audio.presentation.navigation.transitions.ListAlbumScreenTransitions

import com.kire.audio.presentation.ui.details.album_screen_ui.list_item_album_wrapper.ListItemAlbumWrapper
import com.kire.audio.presentation.ui.details.common.ListWithTopAndFab
import com.kire.audio.presentation.ui.details.common.ScreenHeader
import com.kire.audio.presentation.ui.screen.destinations.AlbumScreenDestination
import com.kire.audio.presentation.ui.screen.destinations.ListScreenDestination
import com.kire.audio.presentation.ui.theme.animation.Animation
import com.kire.audio.presentation.ui.theme.dimen.Dimens
import com.kire.audio.presentation.ui.theme.localization.LocalizationProvider

import com.kire.audio.presentation.viewmodel.TrackViewModel

import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@Destination(style = ListAlbumScreenTransitions::class)
@Composable
fun ListAlbumScreen(
    trackViewModel: TrackViewModel,
    shiftBottomBar: () -> Unit,
    mediaController: MediaController?,
    navigator: DestinationsNavigator
){
    val trackState by trackViewModel.trackState.collectAsStateWithLifecycle()

    val albumsWithTracks by trackViewModel.artistWithTracks.collectAsStateWithLifecycle()
    val albums by remember {
        derivedStateOf {
            albumsWithTracks.keys.toList()
        }
    }

    BackHandler {
        navigator.navigateUp()
        return@BackHandler
    }

    ListWithTopAndFab(
        listSize = albums.size,
        shiftBottomBar = shiftBottomBar,
        topBar = {
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
                    isClicked = false,
                    onTitleClick = {
                        navigator.popBackStack(ListScreenDestination, inclusive = false)
                    }
                )
            }
        }
    ) { modifier, state ->

        LazyColumn(
            state = state,
            modifier = modifier
                .pointerInput(Unit) {
                    detectHorizontalDragGestures { _, dragAmount ->
                        if (dragAmount > 60)
                            navigator.popBackStack(AlbumScreenDestination.route, inclusive = true)
                    }
                },
            contentPadding = PaddingValues(bottom = Dimens.columnUniversalVerticalContentPad),
            verticalArrangement = Arrangement.spacedBy(Dimens.columnAndRowUniversalSpacedBy)
        ) {

            itemsIndexed(albums, key = {_, title -> title}){ _, album ->
                ListItemAlbumWrapper(
                    modifier = Modifier
                        .animateItem(
                            Animation.universalFiniteSpring()
                        ),
                    trackState = trackState,
                    tracks = albumsWithTracks[album] ?: emptyList(),
                    onEvent = trackViewModel::onEvent,
                    mediaController = mediaController,
                    onImageClick = {
                        navigator.navigate(AlbumScreenDestination)
                    }
                )
            }
        }
    }
}