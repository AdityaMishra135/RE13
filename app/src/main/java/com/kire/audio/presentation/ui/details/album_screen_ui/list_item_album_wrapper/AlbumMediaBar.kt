package com.kire.audio.presentation.ui.details.album_screen_ui.list_item_album_wrapper

import androidx.compose.animation.AnimatedContent

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.runtime.Composable

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color

import androidx.media3.session.MediaController

import com.kire.audio.presentation.model.state.TrackState
import com.kire.audio.presentation.ui.details.common.MediaControls
import com.kire.audio.presentation.ui.details.common.RubikFontBasicText
import com.kire.audio.presentation.ui.theme.AudioExtendedTheme
import com.kire.audio.presentation.ui.theme.dimen.Dimens
import com.kire.audio.presentation.ui.theme.localization.LocalizationProvider

/**
 * Панель для управления воспроизведением в пределах альбома.
 * Часть ListItemAlbumWrapper.
 * Область видимости - RowScope
 *
 * @param modifier модификатор
 * @param trackState состояние воспроизведения
 * @param mediaController для управления воспроизведением
 *
 * @author Михаил Гонтарев (KiREHwYE)
 */
@Composable
fun RowScope.AlbumMediaBar(
    trackState: TrackState,
    modifier: Modifier = Modifier,
    mediaController: MediaController? = null
) {

    Column(
        modifier = modifier
            .aspectRatio(1f / 1f)
            .weight(1f)
            .shadow(
                elevation = Dimens.universalShadowElevation,
                spotColor = AudioExtendedTheme.extendedColors.shadow,
                shape = RoundedCornerShape(Dimens.universalRoundedCorners)
            )
            .clip(RoundedCornerShape(Dimens.universalRoundedCorners))
            .background(color = Color.White)
            .padding(Dimens.universalPad),
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AnimatedContent(targetState = trackState.currentTrackPlaying?.title, label = "") {
            RubikFontBasicText(
                text = it ?: LocalizationProvider.strings.nothingWasFound,
                style = AudioExtendedTheme.extendedType.mediumTitle
                    .copy(color = Color.Black)
            )
        }

        MediaControls(
            mediaController = mediaController,
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            iconsTint = Color.Black
        )
    }
}