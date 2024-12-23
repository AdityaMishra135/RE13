package com.kire.audio.presentation.ui.details.player_screen_ui.panel.lyrics_panel

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.EditNote
import androidx.compose.material.icons.rounded.Link
import androidx.compose.material.icons.rounded.Lyrics

import androidx.compose.material3.Icon
import androidx.compose.material3.Text

import androidx.compose.runtime.Composable

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

import com.kire.audio.presentation.util.modifier.bounceClick
import com.kire.audio.presentation.ui.theme.AudioExtendedTheme
import com.kire.audio.presentation.constants.LyricsRequestMode
import com.kire.audio.presentation.ui.theme.dimen.Dimens
import com.kire.audio.presentation.ui.theme.localization.LocalizationProvider

/**
 * Меню редактирования текста песни
 *
 * @param updateLyricsRequestMode Изменяет режим редактирования
 * @param lyricsRequestWithUpdatingTrack Запускает процесс поиска текста песни
 * @param modifier Модификатор
 *
 * @author Михаил Гонтарев (KiREHwYE)
 */
@Composable
fun LyricsEditOptions(
    updateLyricsRequestMode: (LyricsRequestMode) -> Unit,
    lyricsRequestWithUpdatingTrack: () -> Unit,
    modifier: Modifier = Modifier
) {

    Box(
        modifier = modifier
            .wrapContentHeight()
            .fillMaxWidth(),
        contentAlignment = Alignment.Center
    ){

        Column(
            modifier = Modifier
                .wrapContentHeight()
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(Dimens.universalColumnAndRowSpacedBy),
            horizontalAlignment = Alignment.Start
        ){

            /** Поиск по ссылке */
            EditOption(
                leadingIcon = Icons.Rounded.Link,
                text = LocalizationProvider.strings.byGeniusLinkModeText,
                onClick = {
                    updateLyricsRequestMode(LyricsRequestMode.BY_LINK)
                }
            )

            /** Поиск по имени исполнителя и названию трека,
             * введенным пользователем */
            EditOption(
                leadingIcon = Icons.Rounded.Lyrics,
                text = LocalizationProvider.strings.byArtistAndTitleModeText,
                onClick = {
                    updateLyricsRequestMode(LyricsRequestMode.BY_TITLE_AND_ARTIST)
                }
            )

            /** Редактирование текущего текста */
            EditOption(
                leadingIcon = Icons.Rounded.EditNote,
                text = LocalizationProvider.strings.editModeText,
                onClick = {
                    updateLyricsRequestMode(LyricsRequestMode.EDIT_CURRENT_TEXT)
                }
            )

            /** Поиск в автоматическом режиме */
            EditOption(
                leadingIcon = Icons.Rounded.AutoAwesome,
                text = LocalizationProvider.strings.automaticModeText,
                onClick = {
                    updateLyricsRequestMode(LyricsRequestMode.AUTOMATIC)
                    lyricsRequestWithUpdatingTrack()
                }
            )
        }
    }
}

/**
 * Вариант редактирования текста песни
 *
 * @param leadingIcon Иконка
 * @param text Название варианта
 * @param onClick Действие при нажатии
 *
 * @author Михаил Гонтарев (KiREHwYE)
 */
@Composable
private fun EditOption(
    leadingIcon: ImageVector,
    text: String,
    onClick: () -> Unit
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .bounceClick { onClick() },
        horizontalArrangement = Arrangement.spacedBy(Dimens.universalColumnAndRowSpacedBy),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Icon(
            imageVector = leadingIcon,
            contentDescription = null,
            tint = AudioExtendedTheme.extendedColors.roseAccent,
            modifier = Modifier
                .size(Dimens.universalIconSize)
        )

        Text(
            text = text,
            style = TextStyle(
                color = Color.White,
                fontSize = 19.sp,
                lineHeight = 19.sp,
                fontFamily = AudioExtendedTheme.extendedFonts.rubikFontFamily,
                fontWeight = FontWeight.Medium
            )
        )
    }
}