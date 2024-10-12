package com.kire.audio.presentation.ui.details.list_screen_ui.top_block.action_bar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Sort
import androidx.compose.material.icons.rounded.Refresh

import androidx.compose.material3.Icon

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip

import com.kire.audio.presentation.util.modifier.bounceClick
import com.kire.audio.presentation.ui.theme.AudioExtendedTheme
import com.kire.audio.presentation.ui.theme.dimen.Dimens

/**
 * Панель сортировки и обновления списка треков
 *
 * @param refreshAction действие обновления списка треков
 * @param dropDownMenu меню сортировки
 * @param modifier модификатор
 *
 * @author Михаил Гонтарев (KiREHwYE)
 */
@Composable
fun SortAndRefreshPanel(
    refreshAction: () -> Unit,
    modifier: Modifier = Modifier,
    dropDownMenu: @Composable (
        isExpanded: () -> Boolean,
        onDismiss: () -> Unit
    ) -> Unit = { _, _ -> }
){

    /** Флаг открытия меню сортировки */
    var expanded by rememberSaveable { mutableStateOf(false) }

    Box(
        modifier = modifier
            .wrapContentHeight(),
        contentAlignment = Alignment.TopStart
    ) {
        Row(
            modifier = Modifier
                .wrapContentSize()
                .clip(RoundedCornerShape(Dimens.universalRoundedCorners))
                .background(color = AudioExtendedTheme.extendedColors.controlElementsBackground)
                .padding(Dimens.universalPad),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Dimens.sortAndRefreshBarSpacedBy)

        ) {
            /** Иконка-кнопка для сортировки списка треков */
            Icon(
                Icons.AutoMirrored.Rounded.Sort,
                contentDescription = null,
                tint = AudioExtendedTheme.extendedColors.button,
                modifier = Modifier
                    .size(Dimens.universalIconSize)
                    .bounceClick {
                        expanded = !expanded
                    }
            )

            /** Иконка-кнопка для обновления списка треков */
            Icon(
                Icons.Rounded.Refresh,
                contentDescription = "Refresh",
                tint = AudioExtendedTheme.extendedColors.button,
                modifier = Modifier
                    .size(Dimens.universalIconSize)
                    .bounceClick {
                        refreshAction()
                    }
            )
        }

        /** Меню сортировки */
        dropDownMenu(
            isExpanded = {
                expanded
            },
            onDismiss = {
                expanded = false
            }
        )
    }
}