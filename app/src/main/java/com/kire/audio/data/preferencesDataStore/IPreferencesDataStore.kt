package com.kire.audio.data.preferencesDataStore

import com.kire.audio.data.constants.SortTypeDataStore
import com.kire.audio.device.audio.util.RepeatMode
import kotlinx.coroutines.flow.Flow

interface IPreferencesDataStore {
    suspend fun saveSortOption(key: String, value: String)
    suspend fun readSortOption(key: String): Flow<SortTypeDataStore>
    suspend fun saveRepeatMode(key: String, value: String)
    suspend fun readRepeatMode(key: String): Flow<RepeatMode>
}