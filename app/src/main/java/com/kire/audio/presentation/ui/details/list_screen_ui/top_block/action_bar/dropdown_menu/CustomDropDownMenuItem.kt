package com.kire.audio.presentation.ui.details.list_screen_ui.top_block.action_bar.dropdown_menu

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight

import androidx.compose.runtime.Composable

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

import com.kire.audio.presentation.ui.theme.AudioExtendedTheme
import com.kire.audio.presentation.constants.SortType
import com.kire.audio.presentation.ui.details.common.RubikFontBasicText
import com.kire.audio.presentation.ui.theme.dimen.Dimens
import com.kire.audio.presentation.util.modifier.bounceClick

/**
 * Элемент меню сортировки
 *
 * @param sortOption текущий тип сортировки
 * @param sortTypeASC соответстыующий вариант сортировки по возрастанию
 * @param sortTypeDESC соответстыующий вариант сортировки по убыванию
 * @param title название типа сортировки, понятное пользователю
 * @param sortOptionFunc действие при нажатии на элемент
 * @param modifier модификатор
 *
 * @author Михаил Гонтарев (KiREHwYE)
 */
@Composable
fun CustomDropDownMenuItem(
    sortOption: SortType,
    sortTypeASC: SortType,
    sortTypeDESC: SortType,
    title: String,
    sortOptionFunc: (String, SortType, SortType)->Unit,
    modifier: Modifier = Modifier
){

    Row(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .bounceClick {
                sortOptionFunc(
                    title,
                    sortTypeASC,
                    sortTypeDESC
                )
            },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {

        /** Название типа сортировки */
        RubikFontBasicText(
            text = title,
            style = TextStyle(
                fontSize = 19.sp,
                fontWeight = FontWeight.Medium,
                color = if (sortOption == sortTypeASC || sortOption == sortTypeDESC)
                    AudioExtendedTheme.extendedColors.roseAccent
                else
                    AudioExtendedTheme.extendedColors.secondaryText
            ),
            modifier = Modifier.padding(end = Dimens.universalPad)
        )

        /** Хвостовая иконка */
        DropdownMenuItemTrailingIcon(
            sortOption = sortOption,
            sortTypeASC = sortTypeASC,
            sortTypeDESC = sortTypeDESC
        )
    }
}