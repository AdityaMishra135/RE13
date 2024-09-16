package com.kire.audio.presentation.ui.details.list_screen_ui.top_block.action_bar

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateOffsetAsState
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.shrinkOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.animation.with
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
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.dp

import androidx.media3.session.MediaController

import com.kire.audio.presentation.model.event.TrackUiEvent
import com.kire.audio.presentation.ui.details.list_screen_ui.top_block.action_bar.dropdown_menu.DropDownMenu
import com.kire.audio.presentation.ui.theme.animation.Animation
import com.kire.audio.presentation.ui.theme.dimen.Dimens
import com.kire.audio.presentation.util.animatePlacement
import com.kire.audio.presentation.viewmodel.TrackViewModel

/**
 * Action bar of TopBlock composable that provides search functionality alongside with sorting and updating track list
 *
 * @param trackViewModel ViewModel
 * @param mediaController MediaController to control media playback
 * @param modifier Modifier
 *
 * @author Michael Gontarev (KiREHwYE)
 * */
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun ActionBar(
    trackViewModel: TrackViewModel,
    mediaController: MediaController?,
    navigateToPlayerScreen: () -> Unit,
    modifier: Modifier = Modifier
){

    var isSearchWidened by remember {
        mutableStateOf(false)
    }

    val spacedBy by animateDpAsState(targetValue = if (isSearchWidened) 0.dp else Dimens.columnAndRowUniversalSpacedBy)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(horizontal = Dimens.universalPad),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(spacedBy)
    ) {
        AnimatedContent(
            targetState = !isSearchWidened,
        ) {
            if (it)
                SortAndRefreshBar(
                    refreshAction = trackViewModel::updateTrackDataBase,
                    dropDownMenu = { isExpanded, onDismiss ->
                        DropDownMenu(
                            isExpanded = isExpanded,
                            onDismiss = onDismiss,
                            sortType = trackViewModel.sortType,
                            saveSortOption = trackViewModel::saveSortOption,
                            updateSortOption = { sortOption ->
                                trackViewModel.onEvent(TrackUiEvent.updateSortOption(sortOption))
                            },
                        )
                    }
                )
        }

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