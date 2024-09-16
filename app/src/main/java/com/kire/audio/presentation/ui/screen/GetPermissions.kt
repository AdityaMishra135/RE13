package com.kire.audio.screen.functional

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner

import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState

/**
 * Запрашивает необходимые для работы приложения разрешения при запуске
 *
 * @param lifecycleOwner владелец жизненного цикла
 * @param updateTrackDataBase обновление базы данных
 *
 * @author Michael Gontarev (KiREHwYE)
 */
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun GetPermissions(
    lifecycleOwner: LifecycleOwner,
    updateTrackDataBase: () -> Unit
){

    /** Необходимые разрешения */
    val permissionsState = rememberMultiplePermissionsState(
        permissions = listOf(
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.READ_MEDIA_AUDIO,
            android.Manifest.permission.POST_NOTIFICATIONS,
            android.Manifest.permission.INTERNET
        )
    )

    // Запрос разрешений при запуске
    DisposableEffect(
        key1 = lifecycleOwner,
        effect = {
            val observer = LifecycleEventObserver { _, event ->
                if (event == Lifecycle.Event.ON_START) {
                    permissionsState.launchMultiplePermissionRequest()
                }
            }
            lifecycleOwner.lifecycle.addObserver(observer)

            onDispose {
                lifecycleOwner.lifecycle.removeObserver(observer)
            }
        }
    )

    // Обновление базы данных при получении разрешения на доступ к внутреннему хранилищу
    if (permissionsState.permissions[0].hasPermission ||
        permissionsState.permissions[1].hasPermission)
        LaunchedEffect(Unit) {
            updateTrackDataBase()
        }
}
