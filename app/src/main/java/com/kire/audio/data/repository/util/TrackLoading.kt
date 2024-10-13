package com.kire.audio.data.repository.util

import android.annotation.SuppressLint
import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore

import com.kire.audio.data.mapper.toDomain
import com.kire.audio.data.model.TrackEntity
import com.kire.audio.domain.model.TrackDomain

import java.io.File
import javax.inject.Inject

/**
 * Реализует загрузку в базу данных треков,
 * сохраненных на устройстве пользователя
 *
 * @param context Контекст
 *
 * @author Михаил Гонтарев (KiREHwYE)
 * */
class TracksLoading @Inject constructor(
    private val context: Context
) {
    @SuppressLint("Range")
    suspend operator fun invoke(
        getTrack: suspend (String) -> TrackEntity,
        upsertTrack: suspend (TrackDomain) -> Unit
    ) {

        /** Дает доступ к хранилищу */
        val cursor: Cursor? = context.contentResolver?.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            null,
            null,
            null,
            null
        )

        /** Базовый путь для обложки трека */
        val basePath = Uri.parse("content://media/external/audio/albumart")

        cursor?.apply {

            if (moveToFirst()) {
                do {
                    /** Название */
                    val trackTitle = getString(getColumnIndex(MediaStore.Audio.Media.TITLE))
                    /** Имя исполнителя */
                    val trackArtist = getString(getColumnIndex(MediaStore.Audio.Media.ARTIST))
                    /** Путь */
                    val trackPath = getString(getColumnIndex(MediaStore.Audio.Media.DATA))
                    /** ID альбома */
                    val trackAlbumId = getLong(getColumnIndex(MediaStore.Audio.Media.ALBUM_ID))
                    /** Uri обложки трека */
                    val trackImageUri = ContentUris.withAppendedId(basePath, trackAlbumId)
                    /** Готовая сущность трека */
                    val track = TrackEntity(
                        id = getString(getColumnIndex(MediaStore.Audio.Media._ID)),
                        title = when (trackTitle) {
                            null -> "No title"
                            else -> trackTitle
                        },
                        album = getString(getColumnIndex(MediaStore.Audio.Media.ALBUM)),
                        artist = when (trackArtist) {
                            null, "<unknown>" -> "Unknown artist"
                            else -> trackArtist
                        },
                        path = trackPath,
                        duration = getLong(getColumnIndex(MediaStore.Audio.Media.DURATION)),
                        albumId = trackAlbumId,
                        imageUri = trackImageUri,
                        dateAdded = File(trackPath).lastModified().toString(),
                        isFavourite = false,
                        defaultImageUri = trackImageUri
                    )

                    /** Проверяем, сохранен ли уже данный трек в базе */
                    if (File(trackPath).exists()) {
                        /** Получаем трек из базы данных по id */
                        val existingTrack: TrackEntity? = getTrack(track.id)

                        /** Если путь не совпадает, добавляем в базу */
                        if (existingTrack?.path != track.path)
                            upsertTrack(track.toDomain())
                    }

                } while (moveToNext())
            }
            /** Прекращает работу Cursor */
            close()
        }
    }
}