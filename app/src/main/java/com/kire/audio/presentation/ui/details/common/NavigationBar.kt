package com.kire.audio.presentation.ui.details.common

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.kire.audio.presentation.ui.screen.NavGraphs
import com.kire.audio.presentation.ui.screen.appCurrentDestinationAsState
import com.kire.audio.presentation.ui.screen.destinations.AlbumScreenDestination
import com.kire.audio.presentation.ui.screen.destinations.Destination
import com.kire.audio.presentation.ui.screen.destinations.ListAlbumScreenDestination
import com.kire.audio.presentation.ui.screen.destinations.ListScreenDestination
import com.kire.audio.presentation.ui.screen.startAppDestination
import com.kire.audio.presentation.ui.theme.AudioExtendedTheme
import com.kire.audio.presentation.ui.theme.AudioTheme
import com.kire.audio.presentation.ui.theme.dimen.Dimens
import com.kire.audio.presentation.util.animatePlacement

@Composable
fun NavigationBar(
   navHostController: NavHostController
) {

    val currentDestination: Destination = navHostController.appCurrentDestinationAsState().value
        ?: NavGraphs.root.startAppDestination

    val interactionSource = remember {
        MutableInteractionSource()
    }

    Row(
       modifier = Modifier
           .padding(Dimens.universalPad)
           .fillMaxWidth()
           .wrapContentHeight()
           .shadow(
               elevation = Dimens.universalShadowElevation + 6.dp,
               shape = RoundedCornerShape(Dimens.universalRoundedCorner),
               spotColor = AudioExtendedTheme.extendedColors.shadow,
               ambientColor = Color.Transparent
           )
           .clip(RoundedCornerShape(Dimens.universalRoundedCorner))
           .pointerInput(Unit) {
               detectTapGestures {  }
           }
           .background(color = AudioExtendedTheme.extendedColors.controlElementsBackground)
           .padding(Dimens.universalPad),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceAround
    ) {

        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clickable(
                    interactionSource = interactionSource,
                    indication = null
                ) {
                    if (currentDestination != ListScreenDestination)
                        navHostController.navigate(ListScreenDestination.route) {
                            navHostController.popBackStack()
                        }
                }
        ) {
            Icon(
                imageVector = Icons.Default.LibraryMusic,
                contentDescription = null,
                tint = if (currentDestination == ListScreenDestination) AudioExtendedTheme.extendedColors.roseAccent else AudioExtendedTheme.extendedColors.primaryText,
                modifier = Modifier
                    .animatePlacement()
                    .size(Dimens.universalIconSize + 10.dp)
            )
            AnimatedVisibility(visible = currentDestination == ListScreenDestination) {
                RubikFontText(
                    text = "Main",
                    style = TextStyle(
                        fontWeight = FontWeight.Medium,
                        fontSize = 19.sp,
                        color = AudioExtendedTheme.extendedColors.primaryText
                    )
                )
            }
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clickable(
                    interactionSource = interactionSource,
                    indication = null
                ) {
                    if (currentDestination != ListAlbumScreenDestination)
                        navHostController.navigate(ListAlbumScreenDestination.route) {
                            navHostController.popBackStack()
                        }
                }
        ) {
            Icon(
                imageVector = Icons.Default.Album,
                contentDescription = null,
                tint = if (currentDestination == ListAlbumScreenDestination) AudioExtendedTheme.extendedColors.roseAccent else AudioExtendedTheme.extendedColors.primaryText,
                modifier = Modifier
                    .animatePlacement()
                    .size(Dimens.universalIconSize + 10.dp)
            )
            AnimatedVisibility(visible = currentDestination == ListAlbumScreenDestination) {
                RubikFontText(
                    text = "Albums",
                    style = TextStyle(
                        fontWeight = FontWeight.Medium,
                        fontSize = 19.sp,
                        color = AudioExtendedTheme.extendedColors.primaryText
                    )
                )
            }
        }
    }
}