package com.kire.audio.data.trackDatabase.util

import android.net.Uri
import androidx.room.TypeConverter

/**
 * Конвертеры для Room
 *
 * @author Михаил Гонтарев (KiREHwYE)
 * */
class Converters {

    /** Преобразует Uri к String */
    @TypeConverter
    fun toStringFromUri(uri: Uri?): String? {
        return uri?.toString()
    }

    /** Преобразует Uri в String */
    @TypeConverter
    fun toUriFromString(string: String?): Uri? {
        return if (string == null) null else Uri.parse(string)
    }
}