package com.kire.audio.presentation.ui.details.common

import android.net.Uri
import androidx.annotation.DrawableRes
import androidx.compose.material3.CircularProgressIndicator

import androidx.compose.runtime.Composable

import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage

import coil.compose.SubcomposeAsyncImage
import com.kire.audio.R

/**
 * SubcomposeAsyncImage из Coil с индикатором загрузки
 *
 * @param imageUri uri изображения для отрисовки
 * @param modifier модификатор
 * @param contentDescription описание к изображению
 * @param colorFilter цветовой фильтр
 * @param defaultImage drawable ресурс картинки,
 * которая должна быть отрисована, если основной нет
 *
 * @author Михаил Гонтарев (KiREHwYE)
 */
@Composable
fun AsyncImageWithLoading(
    imageUri: Uri?,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    colorFilter: ColorFilter = ColorFilter.colorMatrix(ColorMatrix()),
    @DrawableRes defaultImage: Int = R.drawable.logo
) {
    SubcomposeAsyncImage(
        model = imageUri,
        contentDescription = contentDescription,
        loading = {
            /** Показываем индикатор загрузки, пока model не отрисован */
            CircularProgressIndicator()
        },
        error = {
            /** Показываем дефолтную картинку в случае ошибки */
            AsyncImage(
                model = defaultImage,
                contentDescription = null,
                modifier = modifier,
                colorFilter = colorFilter,
                contentScale = ContentScale.Crop
            )
        },
        colorFilter = colorFilter,
        contentScale = ContentScale.Crop,
        modifier = modifier
    )
}