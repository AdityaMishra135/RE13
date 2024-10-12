package com.kire.audio.presentation.ui.details.list_screen_ui.top_block.action_bar.dropdown_menu

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.MaterialTheme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp

import androidx.lifecycle.compose.collectAsStateWithLifecycle

import com.kire.audio.presentation.model.SortOption
import com.kire.audio.presentation.ui.theme.AudioExtendedTheme
import com.kire.audio.presentation.constants.SortType
import com.kire.audio.presentation.model.event.TrackUiEvent
import com.kire.audio.presentation.ui.theme.dimen.Dimens
import com.kire.audio.presentation.ui.theme.localization.LocalizationProvider

import kotlinx.coroutines.flow.StateFlow

/**
 * Меню для сортировки списка треков
 *
 * @param isExpanded флаг открытия меню
 * @param onDismiss действие при закрытии меню
 * @param sortType тип сортировки
 * @param onEvent обработчик UI событий
 *
 * @author Михаил Гонтарев (KiREHwYE)
 */
@Composable
fun DropDownMenu(
    isExpanded: () -> Boolean,
    onDismiss: () -> Unit,
    sortType: StateFlow<SortType>,
    onEvent: (TrackUiEvent) -> Unit
){
    /** Получаем актуальный тип сортировки */
    val sortOption by sortType.collectAsStateWithLifecycle()

    /** Флаг возрастания или убывания сортировки */
    var isSortOptionAsc by remember { mutableIntStateOf(1) }

    /** Функция для обновления типа сортировки */
    val sortOptionFunc: (String, SortType, SortType)->Unit = { text, sortTypeASC, sortTypeDESC ->

        isSortOptionAsc =
            if (!sortOption.toString().take(text.length).equals(text, true)) {
                isSortOptionAsc
            } else (isSortOptionAsc + 1) % 2

        onEvent(
            TrackUiEvent.updateSortOptionAndSave(
                if (isSortOptionAsc == 0)
                    SortOption(sortTypeASC )
                else
                    SortOption(sortTypeDESC)
            )
        )
    }

    /** Делаем меню закругленные углы */
    MaterialTheme(
        shapes = MaterialTheme.shapes.copy(extraSmall = RoundedCornerShape(Dimens.universalRoundedCorners))
    ) {
        /** Само меню сортировки*/
        DropdownMenu(
            expanded = isExpanded(),
            onDismissRequest = { onDismiss() },
            offset = DpOffset(x = 0.dp, y = Dimens.universalPad),
            modifier = Modifier
                .wrapContentSize()
                .background(AudioExtendedTheme.extendedColors.controlElementsBackground)
                .padding(Dimens.universalPad)
        ) {

            CustomDropDownMenuItem(
                sortOption = sortOption,
                sortTypeASC = SortType.DATE_ASC_ORDER,
                sortTypeDESC = SortType.DATE_DESC_ORDER,
                title = LocalizationProvider.strings.dropdownDate,
                sortOptionFunc = sortOptionFunc
            )
            CustomDropDownMenuItem(
                sortOption = sortOption,
                sortTypeASC = SortType.TITLE_ASC_ORDER,
                sortTypeDESC = SortType.TITLE_DESC_ORDER,
                title = LocalizationProvider.strings.dropdownTitle,
                sortOptionFunc = sortOptionFunc
            )
            CustomDropDownMenuItem(
                sortOption = sortOption,
                sortTypeASC = SortType.ARTIST_ASC_ORDER,
                sortTypeDESC = SortType.ARTIST_DESC_ORDER,
                title = LocalizationProvider.strings.dropdownArtist,
                sortOptionFunc = sortOptionFunc
            )

            CustomDropDownMenuItem(
                sortOption = sortOption,
                sortTypeASC = SortType.DURATION_ASC_ORDER,
                sortTypeDESC = SortType.DURATION_DESC_ORDER,
                title = LocalizationProvider.strings.dropdownDuration,
                sortOptionFunc = sortOptionFunc
            )
        }
    }
}