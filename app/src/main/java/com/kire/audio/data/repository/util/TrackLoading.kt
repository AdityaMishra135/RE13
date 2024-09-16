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

class TracksLoading @Inject constructor(
    private val context: Context
) {

    @SuppressLint("Range")
    suspend fun getTracksFromLocalStorage(
        getTrack: suspend (String) -> TrackEntity,
        upsertTrack: suspend (TrackDomain) -> Unit
    ) {

        val cursor: Cursor? = context.contentResolver?.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            null,
            null,
            null,
            null
        )
        val sArtworkUri = Uri
            .parse("content://media/external/audio/albumart")

        cursor?.apply {

            if (moveToFirst()) {
                do {
                    val trackTitle =
                        getString(getColumnIndex(MediaStore.Audio.Media.TITLE))

                    val trackArtist =
                        getString(getColumnIndex(MediaStore.Audio.Media.ARTIST))

                    val trackPath = getString(getColumnIndex(MediaStore.Audio.Media.DATA))

                    val trackAlbumId =
                        getLong(getColumnIndex(MediaStore.Audio.Media.ALBUM_ID))

                    val trackImageUri = ContentUris.withAppendedId(sArtworkUri, trackAlbumId)

//                    val trackImageUri: Uri? = getAlbumArt(trackAlbumId, context)

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

                    if (File(trackPath).exists()) {
                        val existingTrack: TrackEntity? = getTrack(track.id)

                        if (existingTrack?.path != track.path)
                            upsertTrack(track.toDomain())
                    }

                } while (moveToNext())
            }

            close()
        }
    }
}