package com.kire.audio.data.repository

import com.kire.audio.data.mapper.toDomain
import com.kire.audio.data.preferencesDataStore.PreferencesDataStore
import com.kire.audio.domain.repository.IPreferencesRepository
import com.kire.audio.device.audio.util.RepeatMode
import com.kire.audio.di.IoDispatcher
import com.kire.audio.domain.constants.SortTypeDomain

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

import javax.inject.Inject

/**
 * Репозиторий для доступа к базе данных настроек, выбранных пользователем
 *
 * @param preferencesDataStore Сама база данных настроек
 * @param coroutineDispatcher Пул потоков, в котором будет исполнена корутина
 *
 * @author Михаил Гонтарев (KiREHwYE)
 * */
class PreferencesRepository @Inject constructor(
    private val preferencesDataStore: PreferencesDataStore,
    @IoDispatcher private val coroutineDispatcher: CoroutineDispatcher
) : IPreferencesRepository {

    /** Сохраняет опцию сортировки списка треков */
    override suspend fun saveSortOption(key: String, value: String) =
        withContext(coroutineDispatcher) {
            preferencesDataStore.saveSortOption(key, value)
        }

    /** Возвращает опцию сортировки, выбранную пользователем */
    override suspend fun readSortOption(key: String): Flow<SortTypeDomain> =
        withContext(coroutineDispatcher) {
            preferencesDataStore.readSortOption(key).toDomain()
        }

    /** Сохраняет режим повтора трека, выбранный пользователем */
    override suspend fun saveRepeatMode(key: String, value: String) =
        withContext(coroutineDispatcher) {
            preferencesDataStore.saveRepeatMode(key, value)
        }

    /** Возвращает режим повтора трека, выбранный пользователем */
    override suspend fun readRepeatMode(key: String): Flow<RepeatMode> =
        withContext(coroutineDispatcher) {
            preferencesDataStore.readRepeatMode(key)
        }
}