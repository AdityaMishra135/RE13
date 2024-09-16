package com.kire.audio.domain.repository

import com.kire.audio.data.model.TrackEntity

import com.kire.audio.domain.model.TrackDomain

import kotlinx.coroutines.flow.Flow

interface ITrackRepository {

    // Base functions
    suspend fun getTrack(id: String): TrackEntity
    suspend fun upsertTrack(track: TrackDomain)
    suspend fun deleteTrack(track: TrackEntity)

    // Favourite tracks handling funcs
    suspend fun updateIsLoved(track: TrackDomain)
    suspend fun getFavouriteTracks(): Flow<List<TrackDomain>>

    // Sorted tracks
    suspend fun getTracksOrderedByDateAddedASC(): Flow<List<TrackDomain>>
    suspend fun getTracksOrderedByDateAddedDESC(): Flow<List<TrackDomain>>
    suspend fun getTracksOrderedByTitleASC(): Flow<List<TrackDomain>>
    suspend fun getTracksOrderedByTitleDESC(): Flow<List<TrackDomain>>
    suspend fun getTracksOrderedByArtistASC(): Flow<List<TrackDomain>>
    suspend fun getTracksOrderedByArtistDESC(): Flow<List<TrackDomain>>
    suspend fun getTracksOrderedByDurationASC(): Flow<List<TrackDomain>>
    suspend fun getTracksOrderedByDurationDESC(): Flow<List<TrackDomain>>

    // Get albums
    suspend fun getAlbumsWithTracks(): Map<String, List<TrackDomain>>

    // Tracks storing funcs
    suspend fun loadTracksToDatabase()
    suspend fun deleteNoLongerExistingTracksFromDatabase()
    suspend fun updateTracks()
}