package com.kire.audio.presentation.ui.details.player_screen_ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder

import androidx.compose.material3.Icon

import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp

import androidx.lifecycle.compose.collectAsStateWithLifecycle

import com.kire.audio.presentation.util.modifier.bounceClick
import com.kire.audio.presentation.model.event.TrackUiEvent
import com.kire.audio.presentation.model.state.TrackState
import com.kire.audio.presentation.ui.details.common.TwoTextsInColumn
import com.kire.audio.presentation.ui.theme.AudioExtendedTheme
import com.kire.audio.presentation.ui.theme.dimen.Dimens
import com.kire.audio.presentation.util.rememberDerivedStateOf

import kotlinx.coroutines.flow.StateFlow

/**
 * Название трека, имя исполнителя + кнопка для добавления в список избранных треков
 *
 * @param trackState состояние воспроизведения
 * @param onEvent обработчик UI событий
 *
 * @author Михаил Гонтарев (KiREHwYE)
 */
@Composable
fun TextAndHeart(
    trackState: StateFlow<TrackState>,
    onEvent: (TrackUiEvent) -> Unit,
){
    val trackState by trackState.collectAsStateWithLifecycle()

    /** Вид сердечка - иконки-кнопки для добавления трека в избранное */
    val heartIcon by remember {
        derivedStateOf {
            if (trackState.currentTrackPlaying?.isFavourite == true)
                Icons.Rounded.Favorite
            else
                Icons.Rounded.FavoriteBorder
        }
    }

    /** Цвет сердечка в нажатом состоянии */
    val heartPressedColor = AudioExtendedTheme.extendedColors.heartPressed
    /** Цвет сердечка в неактивном состоянии */
    val heartIconTintIdle = AudioExtendedTheme.extendedColors.playerScreenButton
    /** Цвет сердечка в зависимости того, находится трек в избранном или нет */
    val heartIconTint by rememberDerivedStateOf {
        if (trackState.currentTrackPlaying?.isFavourite == true)
            heartPressedColor
        else
            heartIconTintIdle
    }

    Row(modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {

        /** Название трека и исполнитель */
        TwoTextsInColumn(
            modifier = Modifier.weight(1f),
            mainText = trackState.currentTrackPlaying?.title,
            satelliteText = trackState.currentTrackPlaying?.artist,
            mainTextStyle = TextStyle(
                fontSize = 24.sp,
                lineHeight = 24.sp,
                color = Color.White
            ),
            satelliteTextStyle = TextStyle(
                fontSize = 16.sp,
                lineHeight = 16.sp,
                color = Color.LightGray
            )
        )

        /** Иконка-кнопка для добавления трека в избранное */
        Icon(
            imageVector = heartIcon,
            contentDescription = "Favourite Button",
            tint = heartIconTint,
            modifier = Modifier
                .padding(start = Dimens.textEndPadding)
                .size(Dimens.universalIconSize)
                .bounceClick {
                    trackState.currentTrackPlaying?.let { track ->
                        onEvent(TrackUiEvent
                            .upsertAndUpdateCurrentTrack(
                                track.copy(isFavourite = !track.isFavourite)
                            )
                        )
                    }
                }
        )
    }
}