package com.kire.audio.data.repository.util

import android.content.ContentUris
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.ParcelFileDescriptor

fun getAlbumArt(
    trackAlbumId: Long?,
    context: Context
): Uri? {

    var bitmap: Bitmap? = null
    var uri: Uri? = null
    var parcelFileDescriptor: ParcelFileDescriptor? = null

    try {
        val sArtworkUri = Uri
            .parse("content://media/external/audio/albumart")
        trackAlbumId?.let { id ->
            uri = ContentUris.withAppendedId(sArtworkUri, id)
        }
        uri?.let {
            parcelFileDescriptor = context.getContentResolver().openFileDescriptor(it, "r")
        }
        parcelFileDescriptor?.let { descriptor ->
            val fileDescriptor = descriptor.fileDescriptor
            bitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor)
        }
        parcelFileDescriptor?.close()
    } catch (exception: Exception) { }

    return if (bitmap == null) Uri.parse("android.resource://com.kire.audio/drawable/music_icon") else uri
}