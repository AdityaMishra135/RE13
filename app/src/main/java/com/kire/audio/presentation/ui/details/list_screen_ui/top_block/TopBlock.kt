package com.kire.audio.presentation.ui.details.list_screen_ui.top_block

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState

import androidx.compose.foundation.background
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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue

import androidx.compose.ui.Alignment

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
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
import com.kire.audio.presentation.util.modifier.animatePlacement

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
    mediaController: MediaController?,
    onSearchResulItemClick: () -> Unit,
    onAlbumSuggestionClick: (String) -> Unit,
    onTitleClick: () -> Unit
){

    /** Словарь с ключами - названиями альбомов, значениями - списками треков */
    val albumsWithTracks by trackViewModel.artistWithTracks.collectAsStateWithLifecycle()
    /** Список названий альбомов */
    val albums = albumsWithTracks.keys.toList()

    /** Флаг, определяющий, было ли нажатие на название экрана */
    var isClicked by rememberSaveable {
        mutableStateOf(false)
    }

    /** Отступ от выреза в экране */
    val displayCutoutPadDp = with(LocalDensity.current) {
        WindowInsets.displayCutout.asPaddingValues().calculateTopPadding().toPx().toDp()
    }

    /** Отступ от названия экрана до верха дисплея */
    val topPadding by animateDpAsState(targetValue = if (isClicked) displayCutoutPadDp else Dimens.screenTitleTopPad,
        animationSpec = Animation.universalSpring())

    /** Прозрачность фона */
    val backgroundAlpha by animateFloatAsState(targetValue = if (isClicked) 1f else 0f)

    /** Высота ActionBar */
    var actionBarHeight by rememberSaveable { mutableIntStateOf(0) }

    /** Цвет кнопки перехода на экран альбомов */
    val animatedColor by animateColorAsState(
        targetValue =
            if (isClicked && !isSystemInDarkTheme())
                Color.White
            else
                AudioExtendedTheme.extendedColors.button,
        label = "color"
    )

    /** Alignment для Header, который меняется при нажатии на название экрана */
    val alignment by remember {
        derivedStateOf {
            if (isClicked) Alignment.Center else Alignment.BottomStart
        }
    }

    /** Определяет закруглённые углы компонента */
    val roundedCorners = RoundedCornerShape(
        bottomStart = Dimens.universalRoundedCorner,
        bottomEnd = Dimens.universalRoundedCorner
    )

    Column(
        modifier = Modifier
            .animateContentSize(animationSpec = Animation.universalFiniteSpring())
            .wrapContentHeight()
            .fillMaxWidth()
            .clip(roundedCorners)
            .background(
                color = if (!isClicked) AudioExtendedTheme.extendedColors.background else AudioExtendedTheme.extendedColors.roseAccent.copy(alpha = backgroundAlpha),
                shape = roundedCorners
            )
            .padding(top = topPadding, bottom = Dimens.universalPad),
        verticalArrangement = Arrangement.spacedBy(Dimens.columnAndRowUniversalSpacedBy),
        horizontalAlignment = Alignment.Start
    ) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Dimens.universalPad)
        ) {
            /** Название экрана с возможностью перехода на экран альбомов */
            ScreenHeader(
                screenTitle =
                    if (!isClicked)
                        LocalizationProvider.strings.listScreenHeader
                    else
                        LocalizationProvider.strings.albumScreenHeader,
                isClicked = isClicked,
                onTitleClick = {
                    trackViewModel.onEvent(TrackUiEvent.updateArtistWithTracks())
                    isClicked = !isClicked
                },
                modifier = Modifier
                    .animatePlacement()
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
                    tint = animatedColor,
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

        /** Отображает список альбомов для быстрого доступа или
         * панель с поиском и сортировкой в зависимости от значения isClicked
         * */
        AnimatedContent(targetState = isClicked, label = "") { clicked ->
            if (clicked)
                AlbumSuggestionPanel(
                    albums = albums,
                    onAlbumSuggestionClick = onAlbumSuggestionClick,
                    getImageUri = { albumTitle ->
                        albumsWithTracks[albumTitle]?.get(0)?.imageUri
                    },
                    getAlbumArtist = { albumTitle ->
                        albumsWithTracks[albumTitle]?.get(0)?.artist
                            ?: LocalizationProvider.strings.unknownArtist
                    }
                )
            else
                ActionPanel(
                    trackViewModel = trackViewModel,
                    mediaController = mediaController,
                    navigateToPlayerScreen = onSearchResulItemClick,
                    modifier = Modifier
                        .onGloballyPositioned {
                            actionBarHeight = it.size.height
                        }
                )
        }
    }
}