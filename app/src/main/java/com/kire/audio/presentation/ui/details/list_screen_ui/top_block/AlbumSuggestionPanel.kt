package com.kire.audio.presentation.ui.details.list_screen_ui.top_block

import android.net.Uri

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items

import androidx.compose.runtime.Composable

import androidx.compose.ui.Modifier
import com.kire.audio.presentation.ui.details.common.SuggestionItem

import com.kire.audio.presentation.ui.theme.dimen.Dimens

/**
 * Список с альбомами для отрисовки в TopBlock
 *
 * @param albums список альбомов
 * @param onAlbumSuggestionClick lambda для обработки нажатия на альбом
 * @param getImageUri lambda для получения Uri изображения альбома
 * @param getAlbumArtist lambda для получения исполнителя альбома
 *
 * @author Michael Gontarev (KiREHwYE)
 */
@Composable
fun AlbumSuggestionPanel(
    albums: List<String>,
    onAlbumSuggestionClick: (String) -> Unit,
    getImageUri: (String) -> Uri?,
    getAlbumArtist: (String) -> String
) {
    /** Список с альбомами */
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        contentPadding = PaddingValues(horizontal = Dimens.universalPad),
        horizontalArrangement = Arrangement.spacedBy(Dimens.columnAndRowUniversalSpacedBy)
    ) {
        items(albums) { album ->

            /** Элемент списка в виде плитки с обложкой и 2-мя текстами */
            SuggestionItem(
                imageUri = getImageUri(album),
                mainText = album,
                satelliteText = getAlbumArtist(album),
                onSuggestionClick = onAlbumSuggestionClick,
            )
        }
    }
}