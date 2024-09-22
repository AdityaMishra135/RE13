package com.kire.audio.presentation.ui.details.player_screen_ui.panel

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Environment

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.PhotoCamera
import androidx.compose.material.icons.rounded.PhotoLibrary
import androidx.compose.material.icons.rounded.Refresh

import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon

import androidx.compose.runtime.Composable

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext

import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider

import com.kire.audio.BuildConfig
import com.kire.audio.presentation.util.createImageFile
import com.kire.audio.presentation.util.modifier.bounceClick
import com.kire.audio.presentation.ui.theme.AudioExtendedTheme
import com.kire.audio.presentation.ui.theme.dimen.Dimens

import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.Objects

/** Диалог для смены обложки трека,
 * либо путем выбора фото из галереи, либо созданием нового снимка
 *
 * @param openPhotoChangingDialog функция открытия/закрытия диалога
 * @param updateUri функция обновления Uri обложки трека
 * @param imageUri Uri обложки трека
 * @param defaultImageUri Uri обложки по умолчанию
 *
 * @author Михаил Гонтарев (KiREHwYE)
 * */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DialogGalleryOrPhoto(
    openPhotoChangingDialog: (Boolean) -> Unit,
    updateUri: (Uri) -> Unit,
    imageUri: Uri?,
    defaultImageUri: Uri?
){
    /** Текущий контекст */
    val context = LocalContext.current

    /** Создаем файл для записи в него фотографии */
    val file = context.createImageFile()
    /** Получаем путь для нашего файла */
    val uri = FileProvider.getUriForFile(
        Objects.requireNonNull(context),
        BuildConfig.APPLICATION_ID + ".provider", file
    )

    /** Создаем лаунчер для выбора фото из галереи */
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { newUri ->
            /** Если ничего не выбрали, сворачиваемся */
            if (newUri == null) return@rememberLauncherForActivityResult

            /** Получаем текущую дату и настоящее время*/
            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            /** Создаем на основе timeStamp имя файлу */
            val imageFileName = "JPEG_$timeStamp.jpg"

            /** Открываем поток для чтения выбранного фото */
            val input = context.contentResolver.openInputStream(newUri) ?: return@rememberLauncherForActivityResult
            /** Открываем файл для записи нового фото */
            val outputFile = File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), imageFileName)

            /** Копируем фото в созданный файл */
            outputFile.outputStream().use { output ->
                input.copyTo(output)
            }

            /** Закрываем поток для чтения */
            input.close()

            /** Получаем готовое URI к фото */
            val localUri = FileProvider.getUriForFile(
                context,
                "${BuildConfig.APPLICATION_ID}.provider",
                outputFile
            )

            /** Сохраняем URI, обновляя соответствующее поле трека в базе данных */
            updateUri(localUri)
        }
    )

    /** Создаем лаунчер для создания нового фото */
    val cameraLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.TakePicture(),
            onResult = { isTaken ->
                /** Если фото сделано, сохраняем URI, обновляя соответствующее поле трека в базе данных */
                if (isTaken)
                    updateUri(uri)
            }
        )


    /** Создаем лаунчер для запроса разрешений на доступ к галерее и камере */
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        /** Если разрешение дано, запускаем лаунчер для создания нового фото */
        if (isGranted) {
            cameraLauncher.launch(uri)
        } else {  }
    }

    /** Диалог, позволяющий выбрать способ изменения обложки трека:
     * фото из галереи или создание новой фотографии
     * */
    BasicAlertDialog(
        onDismissRequest = {
            openPhotoChangingDialog(false)
        }
    ) {
        Row(
            modifier = Modifier
                .wrapContentSize()
                .background(
                    color = AudioExtendedTheme.extendedColors.controlElementsBackground,
                    shape = RoundedCornerShape(Dimens.universalRoundedCorner)
                )
                .padding(Dimens.universalPad),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Dimens.columnAndRowUniversalSpacedBy)
        ) {

            /** Создание фото с нуля */
            Icon(
                imageVector = Icons.Rounded.PhotoCamera,
                contentDescription = null,
                tint = AudioExtendedTheme.extendedColors.button,
                modifier = Modifier
                    .size(Dimens.photoPickerIconSize)
                    .bounceClick {
                        val permissionCheckResult =
                            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                        if (permissionCheckResult == PackageManager.PERMISSION_GRANTED) {
                            cameraLauncher.launch(uri)
                        } else {
                            permissionLauncher.launch(Manifest.permission.CAMERA)
                        }
                    }
            )
            /** Выбор фото из галереи */
            Icon(
                imageVector = Icons.Rounded.PhotoLibrary,
                contentDescription = null,
                tint = AudioExtendedTheme.extendedColors.button,
                modifier = Modifier
                    .size(Dimens.photoPickerIconSize)
                    .bounceClick {
                        galleryLauncher.launch("image/*")
                    }
            )

            /** Сброс обложки трека к дефолтному значению */
            if (imageUri != defaultImageUri)
                Icon(
                    imageVector = Icons.Rounded.Refresh,
                    contentDescription = null,
                    tint = AudioExtendedTheme.extendedColors.button,
                    modifier = Modifier
                        .size(Dimens.photoPickerIconSize)
                        .bounceClick {
                            defaultImageUri?.let {
                                updateUri(it)
                            }
                        }
                )
        }
    }
}