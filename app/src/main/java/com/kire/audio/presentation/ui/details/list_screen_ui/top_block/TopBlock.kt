package com.kire.audio.presentation.ui.details.list_screen_ui.top_block

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize

import androidx.compose.animation.core.animateDpAsState

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowForwardIos

import androidx.compose.material3.Icon

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue

import androidx.compose.ui.Alignment

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity

import androidx.lifecycle.compose.collectAsStateWithLifecycle

import androidx.media3.session.MediaController

import com.kire.audio.presentation.model.event.TrackUiEvent
import com.kire.audio.presentation.ui.details.common.ScreenHeader
import com.kire.audio.presentation.ui.details.list_screen_ui.top_block.action_bar.ActionPanel
import com.kire.audio.presentation.ui.theme.AudioExtendedTheme
import com.kire.audio.presentation.ui.theme.animation.Animation
import com.kire.audio.presentation.ui.theme.dimen.Dimens
import com.kire.audio.presentation.ui.theme.localization.LocalizationProvider
import com.kire.audio.presentation.util.modifier.dynamicPadding
import com.kire.audio.presentation.util.rememberDerivedStateOf
import com.kire.audio.presentation.viewmodel.TrackViewModel

/**
 * Шапка ListScreen, которая отображает название экрана
 * и предоставляет функциональность для сортировки списка треков, поиска и быстрого доступа к альбомам
 *
 * @param trackViewModel ViewModel
 * @param mediaController для управления воспроизведением
 * @param onSearchResulItemClick определяет действие при клике на трек, найденный в поиске
 * @param onAlbumSuggestionClick определяет действие при нажатии на AlbumSuggestionItem - плитку альбома
 * @param onTitleClick определяет действие при нажатии на название экрана
 *
 * @author Михаил Гонтарев (KiREHwYE)
 */
@Composable
fun TopBlock(
    trackViewModel: TrackViewModel,
    mediaController: MediaController? = null,
    onSearchResulItemClick: () -> Unit = {},
    onAlbumSuggestionClick: (String) -> Unit = {},
    onTitleClick: () -> Unit = {}
){

    /** Словарь с ключами - названиями альбомов, значениями - списками треков */
    val albumsWithTracks by trackViewModel.artistWithTracks.collectAsStateWithLifecycle()
    /** Список названий альбомов */
    val albums by rememberDerivedStateOf {
        albumsWithTracks.keys.toList()
    }
    /** Флаг, определяющий, было ли нажатие на название экрана */
    var isClicked by rememberSaveable {
        mutableStateOf(false)
    }

    LaunchedEffect(key1 = isClicked) {
        if (isClicked)
            trackViewModel.onEvent(TrackUiEvent.updateArtistWithTracks())
    }

    /** Отступ от выреза в экране */
    val displayCutoutPadDp = with(LocalDensity.current) {
        WindowInsets.displayCutout.asPaddingValues().calculateTopPadding().toPx().toDp()
    }

    val topPadding by animateDpAsState(
        targetValue = if (isClicked) displayCutoutPadDp else Dimens.screenTitleTopPad,
        animationSpec = Animation.universalFiniteSpring()
    )
    val backgroundColor by animateColorAsState(
        targetValue = if (!isClicked) AudioExtendedTheme.extendedColors.background
            else AudioExtendedTheme.extendedColors.roseAccent,
        animationSpec = Animation.universalFiniteSpring()
    )
    val iconTint by animateColorAsState(
        targetValue = if (isClicked && !isSystemInDarkTheme()) Color.White
            else AudioExtendedTheme.extendedColors.button,
        animationSpec = Animation.universalFiniteSpring()
    )

    val alignment by rememberDerivedStateOf {
        if (isClicked) Alignment.Center else Alignment.BottomStart
    }

    val screenTitle by rememberDerivedStateOf {
        if (!isClicked)
            LocalizationProvider.strings.listScreenHeader
        else
            LocalizationProvider.strings.albumScreenHeader
    }

    Column(
        modifier = Modifier
            .animateContentSize(animationSpec = Animation.universalFiniteSpring())
            .wrapContentHeight()
            .fillMaxWidth()
            .clip(
                RoundedCornerShape(
                    bottomStart = Dimens.universalRoundedCorners,
                    bottomEnd = Dimens.universalRoundedCorners
                )
            )
            .drawBehind { drawRect(color = backgroundColor) }
            .dynamicPadding(top = { topPadding })
            .padding(bottom = Dimens.universalPad),
        verticalArrangement = Arrangement.spacedBy(Dimens.universalColumnAndRowSpacedBy),
        horizontalAlignment = Alignment.Start
    ) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Dimens.universalPad)
        ) {
            /** Название экрана с возможностью перехода на экран альбомов */
            ScreenHeader(
                screenTitle = screenTitle,
                isClicked = { isClicked },
                onTitleClick = {
                    isClicked = !isClicked
                },
                modifier = Modifier
                    .align(alignment)
            )

            /** Иконка-стрелка для перехода на экран альбомов */
            androidx.compose.animation.AnimatedVisibility(
                visible = isClicked,
                modifier = Modifier
                    .align(Alignment.CenterEnd)
            ) {
                Icon(
                    imageVector = Icons.Rounded.ArrowForwardIos,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier
                        .size(Dimens.universalIconSize)
                        .pointerInput(Unit) {
                            detectTapGestures {
                                onTitleClick()
                            }
                        }
                )
            }
        }

        val getImageUri = remember(albumsWithTracks) {
            { albumTitle: String -> albumsWithTracks[albumTitle]?.get(0)?.imageUri }
        }
        val getAlbumArtist = remember(albumsWithTracks) {
            { albumTitle: String ->
                albumsWithTracks[albumTitle]?.get(0)?.artist
                    ?: LocalizationProvider.strings.unknownArtist
            }
        }

        /** Отображает список альбомов для быстрого доступа или
         * панель с поиском и сортировкой в зависимости от значения isClicked
         * */
        AnimatedContent(targetState = isClicked, label = "") { clicked ->
            if (clicked)
                AlbumSuggestionPanel(
                    albums = { albums },
                    onAlbumSuggestionClick = onAlbumSuggestionClick,
                    getImageUri = getImageUri,
                    getAlbumArtist = getAlbumArtist
                )
            else
                ActionPanel(
                    onEvent = trackViewModel::onEvent,
                    searchResult = trackViewModel.searchResult,
                    trackState = trackViewModel.trackState,
                    searchState = trackViewModel.searchState,
                    sortType = trackViewModel.sortType,
                    mediaController = mediaController,
                    navigateToPlayerScreen = onSearchResulItemClick
                )
        }
    }
}