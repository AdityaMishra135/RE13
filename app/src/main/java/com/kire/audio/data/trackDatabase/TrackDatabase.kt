package com.kire.audio.data.trackDatabase

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

import com.kire.audio.data.model.TrackEntity
import com.kire.audio.data.trackDatabase.util.Converters

/**
 * База данных для треков
 *
 * @author Михаил Гонтарев (KiREHwYE)
 * */
@Database(
    entities = [TrackEntity::class],
    version = 6
)
@TypeConverters(Converters::class)
abstract class TrackDatabase : RoomDatabase() {
    abstract val dao: TrackDao
}