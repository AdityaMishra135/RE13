package com.kire.audio.presentation.ui.details.list_screen_ui.top_block.action_bar.dropdown_menu

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size

import androidx.compose.material.icons.Icons

import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon

import androidx.compose.runtime.Composable

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

import com.kire.audio.presentation.ui.theme.AudioExtendedTheme
import com.kire.audio.presentation.constants.SortType
import com.kire.audio.presentation.ui.theme.dimen.Dimens

/**
 * Хвостовая иконка элемента сортировки
 *
 * @param sortOption текущий тип сортировки
 * @param sortTypeASC соответстыующий вариант сортировки по возрастанию
 * @param sortTypeDESC соответстыующий вариант сортировки по убыванию
 *
 * @author Михаил Гонтарев (KiREHwYE)
 */
@Composable
fun DropdownMenuItemTrailingIcon(
    sortOption: SortType,
    sortTypeASC: SortType,
    sortTypeDESC: SortType
){
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy((-6).dp)
    ) {
        /** Соответствует сортировке по возрастанию */
        Icon(
            Icons.Filled.KeyboardArrowUp,
            contentDescription = null,
            tint = if (sortOption == sortTypeASC) AudioExtendedTheme.extendedColors.roseAccent else AudioExtendedTheme.extendedColors.primaryText,
            modifier = Modifier
                .size(Dimens.universalIconSize)
        )
        /** Соответствует сортировке по убыванию */
        Icon(
            Icons.Filled.KeyboardArrowDown,
            contentDescription = null,
            tint = if (sortOption == sortTypeDESC) AudioExtendedTheme.extendedColors.roseAccent else AudioExtendedTheme.extendedColors.primaryText,
            modifier = Modifier
                .size(Dimens.universalIconSize)
        )
    }
}