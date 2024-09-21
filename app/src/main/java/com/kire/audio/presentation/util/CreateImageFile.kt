package com.kire.audio.presentation.util

import android.content.Context
import android.os.Environment

import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Создает файл для хранения фотографии
 *
 * @return файл для записи фотографии
 */
fun Context.createImageFile(): File {

    /** Временная метка */
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    /** Название файла */
    val imageFileName = "JPEG_" + timeStamp + "_"

    val image = File.createTempFile(
        imageFileName,  /* Префикс */
        ".jpg", /* Суффикс */
        getExternalFilesDir(Environment.DIRECTORY_PICTURES)    /* Директория */
    )

    return image
}