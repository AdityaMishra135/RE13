package com.kire.audio.presentation.ui.details.common

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
 * @param model изображение для отрисовки
 * @param modifier модификатор
 * @param contentDescription описание изображения
 * @param colorFilter цветовой фильтр
 *
 * @author Михаил Гонтарев (KiREHwYE)
 */
@Composable
fun AsyncImageWithLoading(
    model: Any?,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    colorFilter: ColorFilter = ColorFilter.colorMatrix(ColorMatrix())
) {
    SubcomposeAsyncImage(
        model = model,
        contentDescription = contentDescription,
        loading = {
            CircularProgressIndicator()
        },
        error = {
            AsyncImage(
                model = R.drawable.logo,
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