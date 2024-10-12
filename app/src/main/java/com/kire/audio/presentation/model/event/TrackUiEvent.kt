package com.kire.audio.presentation.model.event

import com.kire.audio.presentation.model.SortOption
import com.kire.audio.presentation.model.Track
import com.kire.audio.presentation.model.state.AlbumState
import com.kire.audio.presentation.model.state.LyricsState
import com.kire.audio.presentation.model.state.SearchState
import com.kire.audio.presentation.model.state.TrackState

sealed class TrackUiEvent {

    data class upsertTrack(val track: Track): TrackUiEvent()
    data class updateTrackState(val trackState: TrackState): TrackUiEvent()
    data class upsertAndUpdateCurrentTrack(val track: Track): TrackUiEvent()
    data class updateLyricsState(val lyricsState: LyricsState): TrackUiEvent()
    data class updateSearchState(val searchState: SearchState): TrackUiEvent()
    data class updateArtistWithTracks(val albumState: AlbumState = AlbumState()): TrackUiEvent()
    data class updateSortOptionAndSave(val sortOption: SortOption): TrackUiEvent()
    data object updateTrackDataBase: TrackUiEvent()
}