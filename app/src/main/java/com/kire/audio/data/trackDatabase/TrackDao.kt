package com.kire.audio.data.trackDatabase

import androidx.room.*
import com.kire.audio.data.model.TrackEntity
import kotlinx.coroutines.flow.Flow

/**
 * Функционал, реализуемый в рамках базы данных,
 * хранящей информацию о треках, представленных на устройстве
 *
 * @author Михаил Гонтарев (KiREHwYE)
 */
@Dao
interface TrackDao {

    /** Обновляет трек, если он уже есть в базе, либо добавляет новый объект */
    @Upsert
    fun upsertTrack(track: TrackEntity)

    /** Обновляет поле isFavourite трека, соответственно убирая его из списка любимых треков*/
    @Update
    suspend fun updateIsFavourite(track: TrackEntity)

    /** Удаляет трек из базы данных */
    @Delete
    fun deleteTrack(track: TrackEntity)

    /** Список любимых треков (isFavoutite = true)*/
    @Query("SELECT * FROM track WHERE isFavourite LIKE :value")
    fun getFavouriteTracks(value: Boolean = true): Flow<List<TrackEntity>>

    /** Возвращает список треков отсортированный по дате добавления в порядке возрастания */
    @Query("SELECT * FROM track ORDER BY dateAdded ASC")
    fun getTracksOrderedByDateAddedASC(): Flow<List<TrackEntity>>

    /** Возвращает список треков отсортированный по дате добавления в порядке убывания */
    @Query("SELECT * FROM track ORDER BY dateAdded DESC")
    fun getTracksOrderedByDateAddedDESC(): Flow<List<TrackEntity>>

    /** Возвращает список треков отсортированный по названию в порядке возрастания */
    @Query("SELECT * FROM track ORDER BY title ASC")
    fun getTracksOrderedByTitleASC(): Flow<List<TrackEntity>>

    /** Возвращает список треков отсортированный по названию в порядке убывания */
    @Query("SELECT * FROM track ORDER BY title DESC")
    fun getTracksOrderedByTitleDESC(): Flow<List<TrackEntity>>

    /** Возвращает список треков отсортированный по имени исполнителя в порядке возрастания */
    @Query("SELECT * FROM track ORDER BY artist ASC")
    fun getTracksOrderedByArtistASC(): Flow<List<TrackEntity>>

    /** Возвращает список треков отсортированный по имени исполнителя в порядке убывания */
    @Query("SELECT * FROM track ORDER BY artist DESC")
    fun getTracksOrderedByArtistDESC(): Flow<List<TrackEntity>>

    /** Возвращает список треков отсортированный по длительности в порядке возрастания */
    @Query("SELECT * FROM track ORDER BY duration ASC")
    fun getTracksOrderedByDurationASC(): Flow<List<TrackEntity>>

    /** Возвращает список треков отсортированный по длительности в порядке убывания */
    @Query("SELECT * FROM track ORDER BY duration DESC")
    fun getTracksOrderedByDurationDESC(): Flow<List<TrackEntity>>

    /** Возвращает трек с указанным id */
    @Query("SELECT * FROM track WHERE id = (:id)")
    fun getTrack(id: String): TrackEntity

    /** Возвращает словарь с ключом - названием альбома, значением - списком треков, соответствующих данному альбому */
    @Query("SELECT * FROM track")
    fun getAlbumsWithTracks():
            Map<@MapColumn(columnName = "album") String, List<TrackEntity>>
}