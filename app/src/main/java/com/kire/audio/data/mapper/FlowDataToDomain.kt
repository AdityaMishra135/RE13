package com.kire.audio.data.mapper

import com.kire.audio.data.constants.ErrorMessages
import com.kire.audio.data.model.TrackEntity
import com.kire.audio.data.constants.SortTypeDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

//fun Flow<List<TrackEntity>>.toDomain() = map { list ->
//    list.map { it.toDomain() }
//}

//fun <T, V> Flow<List<T>>.toDomain(transform: (T) -> V) = map { list ->
//    list.map(transform)
//}

fun <T> Flow<Any>.toDomain(): Flow<T> = map { value ->
    when(value) {
        is TrackEntity -> value.toDomain()
        is SortTypeDataStore -> value.toDomain()
        is List<*> -> {
            value.map {
                when (it) {
                    is TrackEntity -> it.toDomain()
                    is SortTypeDataStore -> it.toDomain()
                    else -> throw NotImplementedError(ErrorMessages.NO_CAST_FOUND)
                }
            }
        }
        else -> throw NotImplementedError(ErrorMessages.NO_CAST_FOUND)
    } as T
}