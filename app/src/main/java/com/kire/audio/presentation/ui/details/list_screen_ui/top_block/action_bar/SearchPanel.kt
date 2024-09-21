package com.kire.audio.presentation.ui.details.list_screen_ui.top_block.action_bar

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.rememberTextFieldState

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Search

import androidx.compose.material3.Icon

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle

import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

import androidx.lifecycle.compose.collectAsStateWithLifecycle

import androidx.media3.session.MediaController

import com.kire.audio.device.audio.media_controller.performPlayMedia
import com.kire.audio.presentation.model.Track
import com.kire.audio.presentation.util.modifier.bounceClick
import com.kire.audio.presentation.model.state.SearchState
import com.kire.audio.presentation.model.state.TrackState
import com.kire.audio.presentation.ui.theme.AudioExtendedTheme
import com.kire.audio.presentation.model.event.TrackUiEvent
import com.kire.audio.presentation.ui.details.common.RubikFontText
import com.kire.audio.presentation.ui.details.common.SuggestionItem
import com.kire.audio.presentation.ui.theme.dimen.Dimens
import com.kire.audio.presentation.ui.theme.localization.LocalizationProvider

import kotlinx.coroutines.flow.StateFlow

/**
 * Возвращает состояние клавиатуры: поднята или опущена.
 *
 * @return True или False как State
 *
 * @author Михаил Гонтарев (KiREHwYE)
 */
@Composable
private fun keyboardAsState(): State<Boolean> {
    val isImeVisible = WindowInsets.ime.getBottom(LocalDensity.current) > 0
    return rememberUpdatedState(isImeVisible)
}


/**
 * Панель поиска
 *
 * @param mediaController для управления воспроизведением
 * @param trackState текущее состояние воспроизведения
 * @param searchState текущее состояние поиска
 * @param searchResult результат поиска
 * @param onEvent для обработки UI событий
 * @param navigateToPlayerScreen для перехода на экран плеера
 * @param modifier модификатор
 * @param widenSearchPanel флаг растягивания панели поиска на всю ширину экрана
 *
 * @author Михаил Гонтарев (KiREHwYE)
 */
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

    /** Текущее состояние поиска */
    val searchState by searchState.collectAsStateWithLifecycle()
    /** Результат поиска */
    val searchResult by searchResult.collectAsStateWithLifecycle()
    /** Текущее состояние воспроизведения */
    val trackState by trackState.collectAsStateWithLifecycle()

    /** Поисковый запрос */
    val searchString = rememberTextFieldState(initialText = searchState.searchText)

    /** Прозрачность фона */
    val backgroundAlpha by animateFloatAsState(targetValue = if (searchString.text.isNotEmpty()) 1f else 0f)

    /** Обновляем SearchState при изменении поискового запроса */
    LaunchedEffect(searchString.text) {
        onEvent(TrackUiEvent
            .updateSearchState(
                searchState.copy(searchText = searchString.text.toString())
            )
        )
    }
    /** Поток взаимодействия */
    val interactionSource = remember {
        MutableInteractionSource()
    }

    /** Флаг фокуса */
    val isFocused by interactionSource.collectIsFocusedAsState()

    /** Флаг открытия клавиатуры */
    val isKeyboardOpened by keyboardAsState()

    /** Изменяем ширину панели поиска */
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

        /** Поле ввода поискового запроса */
        BasicTextField(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
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
                        /** Иконка поиска в виде лупы */
                        Icon(
                            Icons.Rounded.Search,
                            contentDescription = "Search",
                            tint = AudioExtendedTheme.extendedColors.button,
                            modifier = Modifier
                                .size(Dimens.universalIconSize)
                        )

                        /** Текст-подсказка для отображения, когда поиск в состоянии покоя */
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

                    /** Иконка очищения поискового запроса */
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

        /** Список удовлетворяющих поисковому запросу треков */
        AnimatedVisibility(visible = searchResult.isNotEmpty()) {
            LazyRow(
                modifier = Modifier
                    .animateContentSize()
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .clip(RoundedCornerShape(Dimens.universalRoundedCorner))
                    .background(AudioExtendedTheme.extendedColors.roseAccent.copy(alpha = backgroundAlpha)),
                contentPadding = PaddingValues(Dimens.universalPad),
                horizontalArrangement = Arrangement.spacedBy(Dimens.columnAndRowUniversalSpacedBy),
                verticalAlignment = Alignment.CenterVertically
            ) {
                itemsIndexed(
                    searchResult,
                    key = { _, track -> track.id }
                ) { _, track ->

                    /** Плитка для отображения результата запроса - трека */
                    SuggestionItem(
                        imageUri = track.imageUri,
                        mainText = track.title,
                        satelliteText = track.artist,
                        onSuggestionClick = { _ ->
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