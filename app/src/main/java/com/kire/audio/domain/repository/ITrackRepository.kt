package com.kire.audio.domain.repository

import com.kire.audio.data.model.TrackEntity

import com.kire.audio.domain.model.TrackDomain

import kotlinx.coroutines.flow.Flow

/**
 * Интерфейс для репозитория базы данных треков
 *
 * @author Михаил Гонтарев (KiREHwYE)
 * */
interface ITrackRepository {

    // Базовые функции

    /** Возвращает трек с указанным id */
    suspend fun getTrack(id: String): TrackEntity
    /** Обновляет соответствующий трек в базе, либо добавляет, если его еще в ней нет */
    suspend fun upsertTrack(track: TrackDomain)
    /** Удаляет соответствующий трек из базы */
    suspend fun deleteTrack(track: TrackEntity)

    // Функции для работы с избранными треками

    /** Добавляет трек в избранное или убирает оттуда */
    suspend fun updateIsFavourite(track: TrackDomain)
    /** Список любимых треков (isFavoutite = true) */
    suspend fun getFavouriteTracks(): Flow<List<TrackDomain>>

    // Функции для возврата отсортрованного списка треков

    /** Возвращает список треков отсортированный по дате добавления в порядке возрастания */
    suspend fun getTracksOrderedByDateAddedASC(): Flow<List<TrackDomain>>
    /** Возвращает список треков отсортированный по дате добавления в порядке убывания */
    suspend fun getTracksOrderedByDateAddedDESC(): Flow<List<TrackDomain>>
    /** Возвращает список треков отсортированный по названию в порядке возрастания */
    suspend fun getTracksOrderedByTitleASC(): Flow<List<TrackDomain>>
    /** Возвращает список треков отсортированный по названию в порядке убывания */
    suspend fun getTracksOrderedByTitleDESC(): Flow<List<TrackDomain>>
    /** Возвращает список треков отсортированный по имени исполнителя в порядке возрастания */
    suspend fun getTracksOrderedByArtistASC(): Flow<List<TrackDomain>>
    /** Возвращает список треков отсортированный по имени исполнителя в порядке убывания */
    suspend fun getTracksOrderedByArtistDESC(): Flow<List<TrackDomain>>
    /** Возвращает список треков отсортированный по длительности в порядке возрастания */
    suspend fun getTracksOrderedByDurationASC(): Flow<List<TrackDomain>>
    /** Возвращает список треков отсортированный по длительности в порядке убывания */
    suspend fun getTracksOrderedByDurationDESC(): Flow<List<TrackDomain>>

    // Функция для получения альбомов

    /** Возвращает словарь с ключом - названием альбома и
     * значением - списком треков, ему соответствующих*/
    suspend fun getAlbumsWithTracks(): Map<String, List<TrackDomain>>

    // Функции для обновления базы данных

    /** Обновляет базу данных, добавляя в нее те треки, которых в ней еще нет */
    suspend fun loadTracksToDatabase()
    /** Обновляет базу данных, удаляя из нее те треки, которых на устройстве больше нет */
    suspend fun deleteNoLongerExistingTracksFromDatabase()
    /** Совмещает в себе добавление новых треков и удаление тех, которых на устройстве больше нет */
    suspend fun updateTracks()
}