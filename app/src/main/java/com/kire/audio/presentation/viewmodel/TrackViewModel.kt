package com.kire.audio.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.kire.audio.domain.use_case.util.ITrackUseCases
import com.kire.audio.presentation.mapper.toPresentation
import com.kire.audio.presentation.constants.SortType
import com.kire.audio.presentation.mapper.toDomain
import com.kire.audio.presentation.model.state.LyricsState
import com.kire.audio.presentation.model.state.SearchState
import com.kire.audio.presentation.model.Track
import com.kire.audio.presentation.model.state.TrackState
import com.kire.audio.presentation.constants.LyricsRequestMode
import com.kire.audio.presentation.model.state.ILyricsRequestState
import com.kire.audio.presentation.model.event.TrackUiEvent
import com.kire.audio.presentation.util.search.onSearchRequestChange

import dagger.hilt.android.lifecycle.HiltViewModel

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

import javax.inject.Inject

@HiltViewModel
class TrackViewModel @Inject constructor(
    private val trackUseCases: ITrackUseCases
) : ViewModel(){

    /*
    * Tracks-providing params and funcs
    * */
    private val _sortType = MutableStateFlow(SortType.DATE_DESC_ORDER)
    val sortType : StateFlow<SortType>
        get() = _sortType.asStateFlow()

    private val _tracks: MutableStateFlow<List<Track>> = MutableStateFlow(emptyList())
    val tracks: StateFlow<List<Track>> = _tracks.asStateFlow()

    private val _favouriteTracks: MutableStateFlow<List<Track>> = MutableStateFlow(emptyList())
    val favouriteTracks: StateFlow<List<Track>> = _favouriteTracks.asStateFlow()

    private val _artistWithTracks: MutableStateFlow<Map<String, List<Track>>> = MutableStateFlow(emptyMap())
    val artistWithTracks: StateFlow<Map<String, List<Track>>> = _artistWithTracks.asStateFlow()

    /*
    TrackState params and funcs
    * */
    private val _trackState: MutableStateFlow<TrackState> = MutableStateFlow(TrackState())
    val trackState: StateFlow<TrackState> = _trackState.asStateFlow()

    suspend fun getTrackLyricsFromGeniusAndUpdateTrack(
        track: Track,
        mode: LyricsRequestMode,
        title: String? = "",
        artist: String? = "",
        userInput: String? = ""
    ): ILyricsRequestState {
        onEvent(
            TrackUiEvent.updateTrackState(
                _trackState.value.copy(
                    currentTrackPlaying = track.copy(
                        lyrics = ILyricsRequestState.OnRequest
                    )
                )
            )
        )

        return trackUseCases.getTrackLyricsFromGeniusUseCase(
            mode = mode.toDomain(),
            title = title,
            artist = artist,
            userInput = userInput
        ).toPresentation()
    }

    /*
    LyricsUiState params and funcs
    * */
    private val _lyricsState: MutableStateFlow<LyricsState> = MutableStateFlow(LyricsState())
    val lyricsState: StateFlow<LyricsState> = _lyricsState


    /*
     * DataStore funcs
     * */
    fun saveSortOption(value: SortType) =
        viewModelScope.launch {
            trackUseCases.saveSortOptionUseCase(value.toDomain())
        }

    fun saveRepeatMode(value: Int) =
        viewModelScope.launch {
            trackUseCases.saveRepeatModeUseCase(value)
        }


    /*
    * Database funcs
    * */
    fun updateTrackDataBase() =
        viewModelScope.launch {
            trackUseCases.updateTrackDataBaseUseCase()
        }


    /*
    * Search params and funcs
    * */
    private val _searchState = MutableStateFlow(SearchState())
    val searchState: StateFlow<SearchState> = _searchState.asStateFlow()


    var searchResult = searchState
        .combine(_tracks) { searchState: SearchState, tracks ->
            onSearchRequestChange(tracks, searchState.searchText)
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    /**
     * Handle Ui events
     *
     * @param event Event triggered by user
     *
     * @return Unit
     *
     * @author Michael Gontarev (KiREHwYE)
    * */
    fun onEvent(event: TrackUiEvent) {
        when(event) {
            is TrackUiEvent.updateTrackState -> {
                Log.d("MINE","QWEGQW")
                _trackState.update { _ ->
                    event.trackState
                }
            }
            is TrackUiEvent.upsertTrack -> {
                viewModelScope.launch {
                    trackUseCases.upsertTrackUseCase(event.track.toDomain())
                }
            }

            is TrackUiEvent.updateLyricsState -> {
                _lyricsState.update { _ ->
                    event.lyricsState
                }
            }

            is TrackUiEvent.updateSearchState -> {
                _searchState.update { _ ->
                    event.searchState
                }
            }

            is TrackUiEvent.updateArtistWithTracks -> {
                viewModelScope.launch {
                    _artistWithTracks.value = trackUseCases.getAlbumsWithTracksUseCase().toPresentation()
                }
            }

            is TrackUiEvent.updateSortOptionAndSave -> {
                _sortType.value = event.sortOption.sortType.also { saveSortOption(it) }
            }

            is TrackUiEvent.upsertAndUpdateCurrentTrack -> {
                onEvent(
                    TrackUiEvent.upsertTrack(event.track
                        .also { newTrack ->
                            _trackState.update { currentState ->
                                currentState.copy(
                                    currentTrackPlaying = newTrack,
                                    currentList = currentState.currentList.map { listTrack ->
                                        if (listTrack.id == newTrack.id) event.track else listTrack
                                    }
                                )
                            }
                        })
                )
            }

            is TrackUiEvent.updateTrackDataBase -> updateTrackDataBase()

//            is TrackUiEvent.saveSortOption -> saveSortOption(event.sortType.also {  })
        }
    }


    /*
    * Initialization block
    * */
    init {
        viewModelScope.launch {
            launch {
                trackUseCases.getSortedTracksUseCase().collect {
                    _tracks.value = it.toPresentation()
                }
            }
            launch {
                trackUseCases.readSortOptionUseCase().collect { sortTypeDomain ->
                    _sortType.value = sortTypeDomain.toPresentation()
                }

            }
            launch {
                trackUseCases.readRepeatModeUseCase().collect {
                    _trackState.update { currentState ->
                        currentState.copy(
                            trackRepeatMode = it
                        )
                    }
                }
            }
            launch {
                trackUseCases.getFavouriteTracksUseCase().collect {
                    _favouriteTracks.value = it.toPresentation()
                }
            }
        }
    }
}