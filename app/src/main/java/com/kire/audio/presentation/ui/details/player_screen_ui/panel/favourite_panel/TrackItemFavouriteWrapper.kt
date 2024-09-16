package com.kire.audio.presentation.ui.details.player_screen_ui.panel.favourite_panel

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Favorite

import androidx.compose.material3.Icon

import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment

import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

import com.kire.audio.presentation.ui.theme.dimen.Dimens
import com.kire.audio.presentation.util.bounceClick

@Composable
fun TrackItemFavouriteWrapper(
    trackItem: @Composable (Modifier) -> Unit,
    modifier: Modifier = Modifier,
    onHeartClick: () -> Unit = {}
) {

    Row(
        modifier = modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        trackItem(
            Modifier
                .weight(1f)
                .padding(end = Dimens.universalPad)
        )

        Icon(
            Icons.Rounded.Favorite,
            contentDescription = "Favourite",
            tint = Color.Red,
            modifier = Modifier
                .size(Dimens.universalIconSize)
                .bounceClick {
                    onHeartClick()
                }
        )
    }
}