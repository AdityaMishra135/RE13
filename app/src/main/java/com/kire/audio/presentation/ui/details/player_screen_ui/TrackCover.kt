package com.kire.audio.presentation.ui.details.player_screen_ui

import android.net.Uri
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.gestures.detectTapGestures

import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.runtime.Composable

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput

import com.kire.audio.presentation.ui.details.common.AsyncImageWithLoading
import com.kire.audio.presentation.ui.theme.animation.Animation
import com.kire.audio.presentation.ui.theme.dimen.Dimens


/**
 * Обложка трека
 *
 * @param imageUri URI обложки трека
 * @param expandPanelByNumber раскрывает панель с текстом песни
 *
 * @author Михаил Гонтарев (KiREHwYE)
 */
@Composable
fun TrackCover(
    imageUri: Uri? = null,
    expandPanelByNumber: () -> Unit
){

    /** Сама обложка */
    Crossfade(
        targetState = imageUri,
        label = "Track Cover",
        animationSpec = Animation.universalFiniteSpring()
    ) {
        AsyncImageWithLoading(
            imageUri = it,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f / 1f)
                .clip(RoundedCornerShape(Dimens.universalRoundedCorners))
                .pointerInput(Unit) {
                    detectTapGestures {
                        expandPanelByNumber()
                    }
                }
        )
    }
}