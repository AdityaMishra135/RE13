package com.kire.audio.presentation.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.kire.audio.R
import com.kire.audio.presentation.ui.theme.color.AudioExtendedColors
import com.kire.audio.presentation.ui.theme.font.AudioExtendedFonts
import com.kire.audio.presentation.ui.theme.typo.AudioExtendedType

val LocalAudioExtendedColors = staticCompositionLocalOf { AudioExtendedColors() }

val extendedLightColors = AudioExtendedColors(
    background = Color.White,
    controlElementsBackground  = Color(0xFFEBEBEB),
    specificBackground = Color(0xFFFFF1F1),
    divider = Color(0xFFEBEBEB),
    primaryText = Color.Black,
    secondaryText = Color.Gray,
    button = Color.Black,
    playerScreenButton = Color.White,
    scrollToTopButton = Color.White,
    lyricsText = Color.DarkGray,
    shadow = Color.DarkGray,
    textDayNightTheSame = Color.White,
    buttonDayNightTheSame = Color.White,
    inactiveTrack = Color.White,
    activeTrack = Color.Black,
    thumb = Color.White,
    thumbBorder = Color.Black,
    sliderDurationAndDivider = Color.Black,
    albumSuggestionItemText = Color.White,
    heartPressed = Color.Red
)

val extendedDarkColors = AudioExtendedColors(
    background = Color.Black,
    controlElementsBackground  = Color(0xFF1A1A1A),
    specificBackground = Color.Black,
    divider = Color.DarkGray,
    primaryText = Color.White,
    secondaryText = Color(0xFFEBEBEB),
    button = Color.White,
    playerScreenButton = Color.White,
    scrollToTopButton = Color.Black,
    lyricsText = Color.White,
    textDayNightTheSame = Color.White,
    buttonDayNightTheSame = Color.White,
    inactiveTrack = Color(0xFF5c5c5c),
    activeTrack = Color.White,
    thumb = Color(0xFFEBEBEB),
    thumbBorder = Color.White,
    sliderDurationAndDivider = Color.White,
    albumSuggestionItemText = Color.White,
    heartPressed = Color.Red
)

val LocalAudioExtendedType = staticCompositionLocalOf { AudioExtendedType() }

val extendedType = AudioExtendedType(
    screenTitle = TextStyle(
        fontSize = 52.sp,
        lineHeight = 52.sp,
        fontWeight = FontWeight.W700,
        fontFamily = FontFamily.SansSerif,
    ),
    mediumTitle = TextStyle(
        color = Color.White,
        fontSize = 24.sp,
        lineHeight = 24.sp,
        fontWeight = FontWeight.Bold,
        fontFamily = FontFamily.SansSerif
    ),
    mediumTitleSatellite = TextStyle(
        color = Color.LightGray,
        fontSize = 16.sp,
        lineHeight = 16.sp,
        fontWeight = FontWeight.W300,
        fontFamily = FontFamily.SansSerif,
    ),
    smallTitle = TextStyle(
        fontSize = 12.sp,
        lineHeight = 12.sp,
        fontWeight = FontWeight.Bold,
        fontFamily = FontFamily.SansSerif
    )
)

val LocalAudioExtendedFonts = staticCompositionLocalOf { AudioExtendedFonts() }

val extendedFonts = AudioExtendedFonts(
    rubikFontFamily = FontFamily(
        Font(R.font.rubik_light, FontWeight.Light),
        Font(R.font.rubik_regular, FontWeight.W400),
        Font(R.font.rubik_medium, FontWeight.Medium),
        Font(R.font.rubik_semi_bold, FontWeight.SemiBold),
        Font(R.font.rubik_bold, FontWeight.Bold),
        Font(R.font.rubik_black, FontWeight.Black)
    )
)

@Composable
fun AudioExtendedTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val extendedColors = if (darkTheme) extendedDarkColors else extendedLightColors

    CompositionLocalProvider(LocalAudioExtendedType provides extendedType) {
        CompositionLocalProvider(LocalAudioExtendedColors provides extendedColors) {
            CompositionLocalProvider(LocalAudioExtendedFonts provides extendedFonts) {
                AudioTheme(content = content)
            }
        }
    }
}

object AudioExtendedTheme {
    val extendedColors: AudioExtendedColors
        @Composable
        @ReadOnlyComposable
        get() = LocalAudioExtendedColors.current
    val extendedType: AudioExtendedType
        @Composable
        @ReadOnlyComposable
        get() = LocalAudioExtendedType.current
    val extendedFonts: AudioExtendedFonts
        @Composable
        @ReadOnlyComposable
        get() = LocalAudioExtendedFonts.current
}