package com.kire.audio.data.repository

import android.annotation.SuppressLint

import com.kire.audio.data.mapper.toDomain
import com.kire.audio.data.mapper.toEntity
import com.kire.audio.data.model.TrackEntity
import com.kire.audio.data.repository.util.TracksLoading
import com.kire.audio.data.trackDatabase.TrackDao

import com.kire.audio.domain.repository.ITrackRepository
import com.kire.audio.domain.model.TrackDomain

import com.kire.audio.di.IoDispatcher

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

import java.io.File

import javax.inject.Inject

@SuppressLint("Range")
class TrackRepository @Inject constructor(
    private val trackDatabaseDao: TrackDao,
    private val tracksLoading: TracksLoading,
    @IoDispatcher private val coroutineDispatcher: CoroutineDispatcher
): ITrackRepository {

    override suspend fun getTrack(id: String): TrackEntity {
       return withContext(coroutineDispatcher) {
           trackDatabaseDao.getTrack(id)
       }
    }
    override suspend fun upsertTrack(track: TrackDomain) {
        return withContext(coroutineDispatcher) {
            trackDatabaseDao.upsertTrack(track.toEntity())
        }
    }
    override suspend fun deleteTrack(track: TrackEntity) =
        withContext(coroutineDispatcher) {
            trackDatabaseDao.deleteTrack(track)
        }

    override suspend fun updateIsLoved(track: TrackDomain) =
        withContext(coroutineDispatcher) {
            trackDatabaseDao.updateIsLoved(track.toEntity())
        }

    override suspend fun getFavouriteTracks(): Flow<List<TrackDomain>> =
        withContext(coroutineDispatcher) {
            trackDatabaseDao.getFavouriteTracks().toDomain()
        }

    override suspend fun getTracksOrderedByDateAddedASC(): Flow<List<TrackDomain>> =
        withContext(coroutineDispatcher) {
            trackDatabaseDao.getTracksOrderedByDateAddedASC().toDomain()
        }

    override suspend fun getTracksOrderedByDateAddedDESC(): Flow<List<TrackDomain>> =
        withContext(coroutineDispatcher) {
            trackDatabaseDao.getTracksOrderedByDateAddedDESC().toDomain()
        }

    override suspend fun getTracksOrderedByTitleASC(): Flow<List<TrackDomain>> =
        withContext(coroutineDispatcher) {
            trackDatabaseDao.getTracksOrderedByTitleASC().toDomain()
        }

    override suspend fun getTracksOrderedByTitleDESC(): Flow<List<TrackDomain>> =
        withContext(coroutineDispatcher) {
            trackDatabaseDao.getTracksOrderedByTitleDESC().toDomain()
        }

    override suspend fun getTracksOrderedByArtistASC(): Flow<List<TrackDomain>> =
        withContext(coroutineDispatcher) {
            trackDatabaseDao.getTracksOrderedByArtistASC().toDomain()
        }

    override suspend fun getTracksOrderedByArtistDESC(): Flow<List<TrackDomain>> =
        withContext(coroutineDispatcher) {
            trackDatabaseDao.getTracksOrderedByArtistDESC().toDomain()
        }

    override suspend fun getTracksOrderedByDurationASC(): Flow<List<TrackDomain>> =
        withContext(coroutineDispatcher) {
            trackDatabaseDao.getTracksOrderedByDurationASC().toDomain()
        }

    override suspend fun getTracksOrderedByDurationDESC(): Flow<List<TrackDomain>> =
        withContext(coroutineDispatcher) {
            trackDatabaseDao.getTracksOrderedByDurationDESC().toDomain()
        }

    override suspend fun getAlbumsWithTracks(): Map<String, List<TrackDomain>> =
        withContext(coroutineDispatcher) {
            trackDatabaseDao.getAlbumsWithTracks().toDomain()
        }

    @SuppressLint("Range")
    override suspend fun loadTracksToDatabase() {

        withContext(coroutineDispatcher) {
            tracksLoading.getTracksFromLocalStorage(
                getTrack = ::getTrack, upsertTrack = ::upsertTrack
            )
        }
    }

    override suspend fun deleteNoLongerExistingTracksFromDatabase() {

        withContext(coroutineDispatcher) {

            getTracksOrderedByDateAddedASC().collect { tracks ->
                tracks.forEach { track ->
                    if (!File(track.path).exists())
                        deleteTrack(track.toEntity())
                }
            }
        }
    }

    override suspend fun updateTracks() {
        withContext(coroutineDispatcher) {
            launch { loadTracksToDatabase() }
            launch { deleteNoLongerExistingTracksFromDatabase() }
        }
    }
}