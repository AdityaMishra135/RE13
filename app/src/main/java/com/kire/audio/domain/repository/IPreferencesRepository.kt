package com.kire.audio.domain.repository

import com.kire.audio.device.audio.util.RepeatMode
import com.kire.audio.domain.constants.SortTypeDomain
import kotlinx.coroutines.flow.Flow

interface IPreferencesRepository {
    suspend fun saveSortOption(key: String, value: String)
    suspend fun readSortOption(key: String): Flow<SortTypeDomain>
    suspend fun saveRepeatMode(key: String, value: String)
    suspend fun readRepeatMode(key: String): Flow<RepeatMode>
}