package com.kire.audio.screen.functional

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Конвертирует Long в строку формата "dd|MM|yyyy"
 *
 * @return Строка формата "dd|MM|yyyy"
 *
 * @author Михаил Гонтарев (KiREHwYE)
 */
fun convertLongToTime(time: Long): String {

    val date = Date(time)
    val format = SimpleDateFormat("dd|MM|yyyy", Locale.getDefault())

    return format.format(date)
}