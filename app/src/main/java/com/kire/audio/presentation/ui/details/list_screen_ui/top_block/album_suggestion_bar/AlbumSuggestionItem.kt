package com.kire.audio.presentation.ui.details.list_screen_ui.top_block.album_suggestion_bar

import android.net.Uri

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.runtime.Composable

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

import coil.compose.AsyncImage

import com.kire.audio.R
import com.kire.audio.presentation.ui.details.common.AsyncImageWithLoading
import com.kire.audio.presentation.ui.details.common.RubikFontText
import com.kire.audio.presentation.ui.details.common.TwoTextsInColumn
import com.kire.audio.presentation.ui.theme.AudioExtendedTheme
import com.kire.audio.presentation.ui.theme.dimen.Dimens
import com.kire.audio.presentation.util.bounceClick

/**
 * Компонент - элемент AlbumSuggestionBar. Представляет некоторый альбом.
 *
 * @param imageUri Uri изображения альбома
 * @param albumTitle название альбома
 * @param albumArtist исполнитель
 * @param onAlbumSuggestionClick действие при нажатии на компонент
 *
 * @author Michael Gontarev (KiREHwYE)
 */
@Composable
fun AlbumSuggestionItem(
    imageUri: Uri?,
    albumTitle: String,
    albumArtist: String,
    onAlbumSuggestionClick: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .width(Dimens.albumSuggestionItemWidth)
            .wrapContentHeight()
            .bounceClick {
                onAlbumSuggestionClick(albumTitle)
            },
        verticalArrangement = Arrangement.spacedBy(Dimens.columnAndRowUniversalSpacedBy),
        horizontalAlignment = Alignment.Start
    ) {

        AsyncImageWithLoading(
            model = imageUri,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f / 1f)
                .clip(RoundedCornerShape(Dimens.universalRoundedCorner))
        )

        TwoTextsInColumn(
            mainText = albumTitle,
            satelliteText = albumArtist,
            mainTextStyle = TextStyle(
                fontSize = 12.sp,
                lineHeight = 12.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = AudioExtendedTheme.extendedFonts.rubikFontFamily,
                color = AudioExtendedTheme.extendedColors.albumSuggestionItemText
            ),
            satelliteTextStyle = TextStyle(
                fontSize = 12.sp,
                lineHeight = 12.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = AudioExtendedTheme.extendedFonts.rubikFontFamily,
                color = AudioExtendedTheme.extendedColors.albumSuggestionItemText,
            )
        )
    }
}