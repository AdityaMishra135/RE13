package com.kire.audio.presentation.ui.details.list_screen_ui.top_block.action_bar

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

import androidx.media3.session.MediaController

import com.kire.audio.presentation.model.event.TrackUiEvent
import com.kire.audio.presentation.ui.details.list_screen_ui.top_block.action_bar.dropdown_menu.DropDownMenu
import com.kire.audio.presentation.ui.theme.dimen.Dimens
import com.kire.audio.presentation.viewmodel.TrackViewModel

/**
 * Панель с поиском, сортировкой и возможностью обновления списка треков
 *
 * @param trackViewModel ViewModel, содержащая все необходимые поля и методы для работы с треками
 * @param mediaController для управления воспроизведением
 * @param modifier модификатор
 *
 * @author Михаил Гонтарев (KiREHwYE)
 * */
@Composable
fun ActionPanel(
    trackViewModel: TrackViewModel,
    mediaController: MediaController?,
    navigateToPlayerScreen: () -> Unit,
    modifier: Modifier = Modifier
){

    /** Флаг растягивания панели поиска на всю ширину экрана */
    var isSearchWidened by remember {
        mutableStateOf(false)
    }

    /** Определяет расстояние между поиском и кнопками обновления и сортировки.
     * Делает плавным растяжением панели поиска на всю ширину экрана.
     * Без него при исчезновении SortAndRefreshBar происходит резкий "скачок".
     * */
    val spacedBy by animateDpAsState(targetValue = if (isSearchWidened) 0.dp else Dimens.columnAndRowUniversalSpacedBy)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(horizontal = Dimens.universalPad),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(spacedBy)
    ) {
        AnimatedVisibility(visible = !isSearchWidened,) {
            /** Меню сортировки + обновление списка треков */
            SortAndRefreshPanel(
                refreshAction = trackViewModel::updateTrackDataBase,
                dropDownMenu = { isExpanded, onDismiss ->
                    DropDownMenu(
                        isExpanded = isExpanded,
                        onDismiss = onDismiss,
                        sortType = trackViewModel.sortType,
                        saveSortOption = trackViewModel::saveSortOption,
                        updateSortOption = { sortOption ->
                            trackViewModel.onEvent(TrackUiEvent.updateSortOption(sortOption))
                        }
                    )
                }
            )
        }

        /** Панель поиска */
        SearchPanel(
            modifier = Modifier
                .weight(1f, fill = false),
            mediaController = mediaController,
            trackState = trackViewModel.trackState,
            searchResult = trackViewModel.searchResult,
            searchState = trackViewModel.searchState,
            onEvent = trackViewModel::onEvent,
            navigateToPlayerScreen = navigateToPlayerScreen,
            widenSearchPanel = { isWidened ->
                isSearchWidened = isWidened
            }
        )
    }
}