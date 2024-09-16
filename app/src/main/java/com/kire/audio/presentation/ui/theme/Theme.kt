package com.kire.audio.presentation.ui.theme

import android.app.Activity

import androidx.compose.foundation.isSystemInDarkTheme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect

import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView

import androidx.core.view.WindowCompat

import com.kire.audio.presentation.ui.theme.color.Pink40
import com.kire.audio.presentation.ui.theme.color.Pink80
import com.kire.audio.presentation.ui.theme.color.Purple40
import com.kire.audio.presentation.ui.theme.color.Purple80
import com.kire.audio.presentation.ui.theme.color.PurpleGrey40
import com.kire.audio.presentation.ui.theme.color.PurpleGrey80
import com.kire.audio.presentation.ui.theme.typo.Typography

private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40
)

@Composable
fun AudioTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme =
        when {
            darkTheme -> DarkColorScheme
            else -> LightColorScheme
        }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}