package com.kire.audio.presentation.ui.details.player_screen_ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.MoreVert

import androidx.compose.material3.Icon

import androidx.compose.runtime.Composable

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

import com.kire.audio.presentation.util.bounceClick

import com.kire.audio.presentation.ui.theme.AudioExtendedTheme
import com.kire.audio.presentation.ui.theme.dimen.Dimens

@Composable
fun Header(
    navigateBack: () -> Unit,
    modifierToExpandBlurPanel: Modifier = Modifier
){

    Row(modifier = Modifier
        .fillMaxWidth()
        .wrapContentHeight(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {


        Icon(
            Icons.Rounded.KeyboardArrowDown,
            contentDescription = "Close",
            modifier = Modifier
                .size(Dimens.universalIconSize)
                .bounceClick {
                    navigateBack()
                },
            tint = AudioExtendedTheme.extendedColors.playerScreenButton
        )

        Icon(
            Icons.Rounded.MoreVert,
            contentDescription = "Info",
            modifier = modifierToExpandBlurPanel
                .size(Dimens.universalIconSize),
            tint = AudioExtendedTheme.extendedColors.playerScreenButton
        )
    }
}