package com.kire.audio.data.trackDatabase

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.kire.audio.data.model.TrackEntity
import com.kire.audio.data.trackDatabase.util.Converters

@Database(
    entities = [TrackEntity::class],
    version = 6
)
@TypeConverters(Converters::class)
abstract class TrackDatabase : RoomDatabase() {
    abstract val dao: TrackDao
}