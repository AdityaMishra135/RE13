package com.kire.audio.presentation.ui.details.list_screen_ui.top_block.action_bar

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandIn
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.shrinkOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.togetherWith

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.Interaction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.delete
import androidx.compose.foundation.text.input.rememberTextFieldState

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Search

import androidx.compose.material3.DockedSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle

import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import androidx.lifecycle.compose.collectAsStateWithLifecycle

import androidx.media3.session.MediaController
import com.kire.audio.device.audio.media_controller.performPlayMedia
import com.kire.audio.presentation.model.Track

import com.kire.audio.presentation.util.bounceClick
import com.kire.audio.presentation.model.state.SearchState
import com.kire.audio.presentation.model.state.TrackState
import com.kire.audio.presentation.ui.details.common.ListItem
import com.kire.audio.presentation.ui.theme.AudioExtendedTheme
import com.kire.audio.presentation.model.event.TrackUiEvent
import com.kire.audio.presentation.ui.details.common.RubikFontText
import com.kire.audio.presentation.ui.details.list_screen_ui.top_block.album_suggestion_bar.AlbumSuggestionItem
import com.kire.audio.presentation.ui.theme.animation.Animation
import com.kire.audio.presentation.ui.theme.dimen.Dimens
import com.kire.audio.presentation.ui.theme.localization.LocalizationProvider
import com.kire.audio.presentation.util.animatePlacement
import com.kire.audio.presentation.util.nonScaledSp

import kotlinx.coroutines.flow.StateFlow

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun SearchBar(
    trackState: StateFlow<TrackState>,
    searchState: StateFlow<SearchState>,
    onEvent: (TrackUiEvent) -> Unit,
    mediaController: MediaController?,
    modifier: Modifier = Modifier
){

    val trackState by trackState.collectAsStateWithLifecycle()
    val searchState by searchState.collectAsStateWithLifecycle()

    DockedSearchBar(
        query = searchState.searchText,
        onQueryChange = {
            onEvent(
                TrackUiEvent.updateSearchState(
                    searchState.copy(searchText = it)
                )
            )
        },
        onSearch = {
            onEvent(
                TrackUiEvent.updateSearchState(
                    searchState.copy(active = false)
                )
            )
        },
        active = searchState.active && !searchState.isExpanded,
        onActiveChange = {
            onEvent(
                TrackUiEvent.updateSearchState(
                    searchState.copy(active = it)
                )
            )
        },
        colors = SearchBarDefaults.colors(
            containerColor = AudioExtendedTheme.extendedColors.controlElementsBackground,
            dividerColor = AudioExtendedTheme.extendedColors.secondaryText,
            inputFieldColors = TextFieldDefaults.colors(
                focusedTextColor = AudioExtendedTheme.extendedColors.secondaryText,
                unfocusedTextColor = AudioExtendedTheme.extendedColors.secondaryText,
                disabledTextColor = AudioExtendedTheme.extendedColors.secondaryText,
                cursorColor = AudioExtendedTheme.extendedColors.secondaryText,
            )
        ),
        leadingIcon = {
            Icon(
                Icons.Rounded.Search,
                contentDescription = "Search",
                tint = AudioExtendedTheme.extendedColors.button,
                modifier = Modifier
                    .size(Dimens.universalIconSize)
            )
        },
        trailingIcon = {
            if (searchState.active) {
                Icon(
                    Icons.Rounded.Close,
                    contentDescription = "Close",
                    tint = AudioExtendedTheme.extendedColors.button,
                    modifier = Modifier
                        .size(Dimens.universalIconSize)
                        .bounceClick {
                            if (searchState.searchText.isNotEmpty())
                                onEvent(
                                    TrackUiEvent.updateSearchState(
                                        searchState.copy(searchText = "")
                                    )
                                )
                            else
                                onEvent(
                                    TrackUiEvent.updateSearchState(
                                        searchState.copy(active = false)
                                    )
                                )
                        }
                )
            }
        },
        placeholder = {
            Box(
                contentAlignment = Alignment.CenterStart,
                modifier = Modifier
                    .fillMaxSize()
            ) {
                RubikFontText(
                    text = LocalizationProvider.strings.listScreenSearchHint,
                    style = TextStyle(
                        fontSize = 14.sp.nonScaledSp,
                        lineHeight = 14.sp.nonScaledSp,
                        color = AudioExtendedTheme.extendedColors.secondaryText,
                        fontWeight = FontWeight.Light
                    )
                )
            }
        },
        modifier = modifier
            .shadow(
                elevation = Dimens.universalShadowElevation,
                spotColor = AudioExtendedTheme.extendedColors.shadow,
                shape = RoundedCornerShape(Dimens.universalRoundedCorner)
            )

    ) {

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.4f),
            contentPadding = PaddingValues(Dimens.universalPad),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Dimens.searchListSpacedBy)
        ) {

            itemsIndexed(
                trackState.currentList,
                key = { _, track ->
                    track.id
                }
            ) { listIndex, track ->

                ListItem(
                    mainText = track.title,
                    satelliteText = track.artist,
                    leadingImageUri = track.imageUri,
                    onClick = {
                        onEvent(
                            TrackUiEvent.updateTrackState(
                                trackState.copy(
                                    isPlaying = if (track.path == trackState.currentTrackPlaying?.path) !trackState.isPlaying else true,
                                    currentTrackPlaying = track,
                                    currentTrackPlayingIndex = listIndex
                                )
                            )
                        )
                        mediaController?.apply {
                            if (trackState.isPlaying && trackState.currentTrackPlaying?.path == track.path)
                                pause()
                            else if (!trackState.isPlaying && trackState.currentTrackPlaying?.path == track.path) {
                                prepare()
                                play()

                            } else
                                performPlayMedia(track)
                        }
                    },
                    modifier = Modifier.animateItemPlacement(
                        animationSpec = tween(
                            durationMillis = 400,
                            easing = LinearOutSlowInEasing
                        )
                    )
                )
            }
        }
    }
}

@Composable
fun keyboardAsState(): State<Boolean> {
    val isImeVisible = WindowInsets.ime.getBottom(LocalDensity.current) > 0
    return rememberUpdatedState(isImeVisible)
}

@Composable
fun SearchPanel(
    mediaController: MediaController?,
    trackState: StateFlow<TrackState>,
    searchState: StateFlow<SearchState>,
    searchResult: StateFlow<List<Track>>,
    onEvent: (TrackUiEvent) -> Unit,
    navigateToPlayerScreen: () -> Unit,
    modifier: Modifier = Modifier,
    widenSearchPanel: (Boolean) -> Unit
) {

    val searchState by searchState.collectAsStateWithLifecycle()
    val searchResult by searchResult.collectAsStateWithLifecycle()
    val trackState by trackState.collectAsStateWithLifecycle()

    val searchString = rememberTextFieldState(initialText = searchState.searchText)

    /** Прозрачность фона */
    val backgroundAlpha by animateFloatAsState(targetValue = if (searchString.text.isNotEmpty()) 1f else 0f)

    LaunchedEffect(searchString.text) {
        onEvent(TrackUiEvent
            .updateSearchState(
                searchState.copy(searchText = searchString.text.toString())
            )
        )
    }

    val interactionSource = remember {
        MutableInteractionSource()
    }

    val isFocused by interactionSource.collectIsFocusedAsState()

    val isKeyboardOpened by keyboardAsState()

    LaunchedEffect(key1 = isFocused, key2 = isKeyboardOpened, key3 = searchString.text) {
        if ((isFocused && isKeyboardOpened) || searchString.text.isNotEmpty())
            widenSearchPanel(true)
        else widenSearchPanel(false)
    }

    Column(
        modifier = modifier
            .wrapContentHeight(),
        verticalArrangement = Arrangement.spacedBy(Dimens.columnAndRowUniversalSpacedBy),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        BasicTextField(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .animateContentSize(animationSpec = Animation.universalFiniteSpring())
                .clip(RoundedCornerShape(Dimens.universalRoundedCorner))
                .background(AudioExtendedTheme.extendedColors.controlElementsBackground)
                .padding(Dimens.universalPad),
            state = searchString,
            interactionSource = interactionSource,
            textStyle = TextStyle.Default.copy(
                fontSize = 15.sp,
                lineHeight = 15.sp,
                fontFamily = AudioExtendedTheme.extendedFonts.rubikFontFamily,
                fontWeight = FontWeight.Medium,
                color = AudioExtendedTheme.extendedColors.primaryText
            ),
            lineLimits = TextFieldLineLimits.SingleLine,
            decorator = { innerTextField ->
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(Dimens.columnAndRowUniversalSpacedBy)
                    ) {
                        Icon(
                            Icons.Rounded.Search,
                            contentDescription = "Search",
                            tint = AudioExtendedTheme.extendedColors.button,
                            modifier = Modifier
                                .size(Dimens.universalIconSize)
                        )

                        if (searchString.text.isEmpty())
                            RubikFontText(
                                text = LocalizationProvider.strings.listScreenSearchHint,
                                style = TextStyle(
                                    fontWeight = FontWeight.Light,
                                    fontSize = 15.sp,
                                    lineHeight = 15.sp,
                                    color = AudioExtendedTheme.extendedColors.secondaryText
                                )
                            )
                        else
                            innerTextField()
                    }

                    AnimatedVisibility(visible = searchString.text.isNotEmpty()) {
                        Icon(
                            Icons.Rounded.Close,
                            contentDescription = "Close",
                            tint = AudioExtendedTheme.extendedColors.button,
                            modifier = Modifier
                                .size(Dimens.universalIconSize)
                                .bounceClick {
                                    searchString.clearText()
                                }
                        )
                    }
                }
            }
        )

        AnimatedVisibility(
            visible = searchResult.isNotEmpty()
        ) {
            LazyRow(
                modifier = Modifier
                    .animateContentSize(
                        Animation.universalFiniteSpring()
                    )
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .clip(RoundedCornerShape(Dimens.universalRoundedCorner))
                    .background(AudioExtendedTheme.extendedColors.roseAccent.copy(alpha = backgroundAlpha)),
                contentPadding = PaddingValues(Dimens.universalPad),
                horizontalArrangement = Arrangement.spacedBy(Dimens.columnAndRowUniversalSpacedBy),
                verticalAlignment = Alignment.CenterVertically
            ) {
                itemsIndexed(
                    searchResult
                ) { _, track ->

                    AlbumSuggestionItem(
                        imageUri = track.imageUri,
                        albumTitle = track.title,
                        albumArtist = track.artist,
                        onAlbumSuggestionClick = { _ ->
                            onEvent(
                                TrackUiEvent.updateTrackState(
                                    trackState.copy(
                                        currentList = searchResult,
                                        currentTrackPlaying = track,
                                        currentTrackPlayingIndex = 0,
                                        isPlaying = true
                                    )
                                )
                            )
                            mediaController?.performPlayMedia(track)
                            navigateToPlayerScreen()
                        }
                    )
                }
            }
        }
    }
}