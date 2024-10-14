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

/**
 * Репозиторий для базы данных,
 * содержащей информацию о треках,
 * сохраненных на устройстве
 *
 * @param trackDatabaseDao Функционал базы данных
 * @param tracksLoading Класс для загрузки информации о треках из памяти устройства
 *
 * @author Михаил Гонтарев (KiREHwYE)
 */
@SuppressLint("Range")
class TrackRepository @Inject constructor(
    private val trackDatabaseDao: TrackDao,
    private val tracksLoading: TracksLoading,
    @IoDispatcher private val coroutineDispatcher: CoroutineDispatcher
): ITrackRepository {

    /** Возвращает трек с указанным id */
    override suspend fun getTrack(id: String): TrackEntity =
       withContext(coroutineDispatcher) {
           trackDatabaseDao.getTrack(id)
       }
    /** Обновляет соответствующий трек в базе, либо добавляет, если его еще в ней нет */
    override suspend fun upsertTrack(track: TrackDomain) =
        withContext(coroutineDispatcher) {
            trackDatabaseDao.upsertTrack(track.toEntity())
        }
    /** Удаляет соответствующий трек из базы */
    override suspend fun deleteTrack(track: TrackEntity) =
        withContext(coroutineDispatcher) {
            trackDatabaseDao.deleteTrack(track)
        }

    /** Добавляет трек в избранное или убирает оттуда */
    override suspend fun updateIsFavourite(track: TrackDomain) =
        withContext(coroutineDispatcher) {
            trackDatabaseDao.updateIsFavourite(track.toEntity())
        }
    /** Список любимых треков (isFavoutite = true) */
    override suspend fun getFavouriteTracks(): Flow<List<TrackDomain>> =
        withContext(coroutineDispatcher) {
            trackDatabaseDao.getFavouriteTracks().toDomain()
        }

    /** Возвращает список треков отсортированный по дате добавления в порядке возрастания */
    override suspend fun getTracksOrderedByDateAddedASC(): Flow<List<TrackDomain>> =
        withContext(coroutineDispatcher) {
            trackDatabaseDao.getTracksOrderedByDateAddedASC().toDomain()
        }
    /** Возвращает список треков отсортированный по дате добавления в порядке убывания */
    override suspend fun getTracksOrderedByDateAddedDESC(): Flow<List<TrackDomain>> =
        withContext(coroutineDispatcher) {
            trackDatabaseDao.getTracksOrderedByDateAddedDESC().toDomain()
        }
    /** Возвращает список треков отсортированный по названию в порядке возрастания */
    override suspend fun getTracksOrderedByTitleASC(): Flow<List<TrackDomain>> =
        withContext(coroutineDispatcher) {
            trackDatabaseDao.getTracksOrderedByTitleASC().toDomain()
        }
    /** Возвращает список треков отсортированный по названию в порядке убывания */
    override suspend fun getTracksOrderedByTitleDESC(): Flow<List<TrackDomain>> =
        withContext(coroutineDispatcher) {
            trackDatabaseDao.getTracksOrderedByTitleDESC().toDomain()
        }
    /** Возвращает список треков отсортированный по имени исполнителя в порядке возрастания */
    override suspend fun getTracksOrderedByArtistASC(): Flow<List<TrackDomain>> =
        withContext(coroutineDispatcher) {
            trackDatabaseDao.getTracksOrderedByArtistASC().toDomain()
        }
    /** Возвращает список треков отсортированный по имени исполнителя в порядке убывания */
    override suspend fun getTracksOrderedByArtistDESC(): Flow<List<TrackDomain>> =
        withContext(coroutineDispatcher) {
            trackDatabaseDao.getTracksOrderedByArtistDESC().toDomain()
        }
    /** Возвращает список треков отсортированный по длительности в порядке возрастания */
    override suspend fun getTracksOrderedByDurationASC(): Flow<List<TrackDomain>> =
        withContext(coroutineDispatcher) {
            trackDatabaseDao.getTracksOrderedByDurationASC().toDomain()
        }
    /** Возвращает список треков отсортированный по длительности в порядке убывания */
    override suspend fun getTracksOrderedByDurationDESC(): Flow<List<TrackDomain>> =
        withContext(coroutineDispatcher) {
            trackDatabaseDao.getTracksOrderedByDurationDESC().toDomain()
        }

    /** Возвращает словарь с ключом - названием альбома и
     * значением - списком треков, ему соответствующих*/
    override suspend fun getAlbumsWithTracks(): Map<String, List<TrackDomain>> =
        withContext(coroutineDispatcher) {
            trackDatabaseDao.getAlbumsWithTracks().toDomain()
        }

    /** Обновляет базу данных, добавляя в нее те треки, которых в ней еще нет */
    @SuppressLint("Range")
    override suspend fun loadTracksToDatabase() {
        withContext(coroutineDispatcher) {
            tracksLoading(
                getTrack = ::getTrack,
                upsertTrack = ::upsertTrack
            )
        }
    }
    /** Обновляет базу данных, удаляя из нее те треки, которых на устройстве больше нет */
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
    /** Совмещает в себе добавление новых треков и удаление тех, которых на устройстве больше нет */
    override suspend fun updateTracks() {
        withContext(coroutineDispatcher) {
            launch { loadTracksToDatabase() }
            launch { deleteNoLongerExistingTracksFromDatabase() }
        }
    }
}