package com.kire.audio.presentation.ui.details.list_screen_ui.top_block.action_bar

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

import androidx.media3.session.MediaController
import com.kire.audio.presentation.constants.SortType
import com.kire.audio.presentation.model.Track

import com.kire.audio.presentation.model.event.TrackUiEvent
import com.kire.audio.presentation.model.state.SearchState
import com.kire.audio.presentation.model.state.TrackState
import com.kire.audio.presentation.ui.details.list_screen_ui.top_block.action_bar.dropdown_menu.DropDownMenu
import com.kire.audio.presentation.ui.theme.dimen.Dimens
import kotlinx.coroutines.flow.StateFlow

/**
 * Панель с поиском, сортировкой и возможностью обновления списка треков
 *
 * @param sortType тип сортировки
 * @param trackState состояние воспроизведения
 * @param searchState состояние поиска
 * @param searchResult результат поиска
 * @param onEvent обработчик UI событий
 * @param mediaController для управления воспроизведением
 * @param navigateToPlayerScreen переход на экран плеера
 *
 * @author Михаил Гонтарев (KiREHwYE)
 * */
@Composable
fun ActionPanel(
    sortType: StateFlow<SortType>,
    trackState: StateFlow<TrackState>,
    searchState: StateFlow<SearchState>,
    searchResult: StateFlow<List<Track>>,
    onEvent: (TrackUiEvent) -> Unit,
    mediaController: MediaController? = null,
    navigateToPlayerScreen: () -> Unit = {}
){

    /** Флаг растягивания панели поиска на всю ширину экрана */
    var isSearchWidened by rememberSaveable {
        mutableStateOf(false)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(horizontal = Dimens.universalPad),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        AnimatedVisibility(visible = !isSearchWidened) {
            /** Меню сортировки + обновление списка треков */
            SortAndRefreshPanel(
                refreshAction = {
                    onEvent(TrackUiEvent.updateTrackDataBase)
                },
                dropDownMenu = { isExpanded, onDismiss ->
                    DropDownMenu(
                        isExpanded = isExpanded,
                        onDismiss = onDismiss,
                        sortType = sortType,
                        onEvent = onEvent,
                    )
                }
            )
        }

        /** Панель поиска */
        SearchPanel(
            mediaController = mediaController,
            isSearchWidened = { isSearchWidened },
            trackState = trackState,
            searchResult = searchResult,
            searchState = searchState,
            onEvent = onEvent,
            navigateToPlayerScreen = navigateToPlayerScreen,
            widenSearchPanel = { isWidened: Boolean ->
                isSearchWidened = isWidened
            }
        )
    }
}