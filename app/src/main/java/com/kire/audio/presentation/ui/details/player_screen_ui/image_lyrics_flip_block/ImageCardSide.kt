package com.kire.audio.presentation.ui.details.player_screen_ui.image_lyrics_flip_block

import android.net.Uri

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.runtime.Composable

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource

import coil.compose.AsyncImage
import com.kire.audio.R
import com.kire.audio.presentation.ui.details.common.AsyncImageWithLoading
import com.kire.audio.presentation.ui.theme.dimen.Dimens

@Composable
fun ImageCardSide(
    imageUri: Uri?,
    modifierToExpandPopUpBar: Modifier = Modifier
) {
    Crossfade(
        targetState = imageUri,
        label = "Track Image in foreground"
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