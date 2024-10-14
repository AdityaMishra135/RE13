package com.kire.audio.data.preferencesDataStore

import com.kire.audio.data.constants.SortTypeDataStore
import com.kire.audio.device.audio.util.RepeatMode
import kotlinx.coroutines.flow.Flow

/**
 * Интерфейс для базы данных, хранящей пользовательские настройки
 *
 * @author Михаил Гонтарев (KiREHwYE)
 */
interface IPreferencesDataStore {
    /** Сохраняет опцию сортировки списка треков */
    suspend fun saveSortOption(key: String, value: String)
    /** Возвращает опцию сортировки, выбранную пользователем */
    suspend fun readSortOption(key: String): Flow<SortTypeDataStore>
    /** Сохраняет режим повтора трека, выбранный пользователем */
    suspend fun saveRepeatMode(key: String, value: String)
    /** Возвращает режим повтора трека, выбранный пользователем */
    suspend fun readRepeatMode(key: String): Flow<RepeatMode>
}