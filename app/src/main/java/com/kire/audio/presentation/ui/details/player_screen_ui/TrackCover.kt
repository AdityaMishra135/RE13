package com.kire.audio.presentation.ui.details.player_screen_ui

import androidx.compose.animation.Crossfade

import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.runtime.Composable

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip

import com.kire.audio.presentation.model.state.TrackState
import com.kire.audio.presentation.ui.details.common.AsyncImageWithLoading
import com.kire.audio.presentation.ui.theme.animation.Animation
import com.kire.audio.presentation.ui.theme.dimen.Dimens


/**
 * Обложка трека
 *
 * @param trackState состояние воспроизведения
 * @param lyricsState состояние загрузки текста песни
 * @param onEvent обработка UI событий
 *
 * @author Михаил Гонтарев (KiREHwYE)
 */
@Composable
fun TrackCover(
    trackState: TrackState,
    modifierToExpandPopUpBar: Modifier = Modifier
){

    /** Сама обложка */
    Crossfade(
        targetState = trackState.currentTrackPlaying?.imageUri,
        label = "Track Image in foreground",
        animationSpec = Animation.universalFiniteSpring()
    ) {
        AsyncImageWithLoading(
            model = it,
            modifier = modifierToExpandPopUpBar
                .fillMaxWidth()
                .aspectRatio(1f / 1f)
                .clip(RoundedCornerShape(Dimens.universalRoundedCorner))
        )
    }
}