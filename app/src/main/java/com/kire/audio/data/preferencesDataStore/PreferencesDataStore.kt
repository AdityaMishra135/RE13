package com.kire.audio.data.preferencesDataStore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.kire.audio.data.constants.SortTypeDataStore
import com.kire.audio.data.constants.StorageConstants
import com.kire.audio.device.audio.util.RepeatMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * База данных настроек, выбранных пользователем
 *
 * @param dataStore сама база данных
 *
 * @author Михаил Гонтарев (KiREHwYE)
 * */
class PreferencesDataStore @Inject constructor(
    private val dataStore: DataStore<Preferences>
): IPreferencesDataStore {

    /** Сохраняет опцию сортировки списка треков */
    override suspend fun saveSortOption(key: String, value: String) {
        val dataStoreKey = stringPreferencesKey(key)
        dataStore.edit {
            it[dataStoreKey] = value
        }
    }
    /** Возвращает опцию сортировки, выбранную пользователем */
    override suspend fun readSortOption(key: String): Flow<SortTypeDataStore> {
        val dataStoreKey = stringPreferencesKey(key)
        return dataStore.data.map {
            SortTypeDataStore.valueOf(it[dataStoreKey] ?: StorageConstants.DEFAULT_SORT_TYPE)
        }
    }
    /** Сохраняет режим повтора трека, выбранный пользователем */
    override suspend fun saveRepeatMode(key: String, value: String) {
        val dataStoreKey = stringPreferencesKey(key)
        dataStore.edit {
            it[dataStoreKey] = value
        }
    }
    /** Возвращает режим повтора трека, выбранный пользователем */
    override suspend fun readRepeatMode(key: String): Flow<RepeatMode> {
        val dataStoreKey = stringPreferencesKey(key)
        return dataStore.data.map {
            RepeatMode.valueOf(it[dataStoreKey] ?: StorageConstants.DEFAULT_REPEAT_MODE)
        }
    }
}