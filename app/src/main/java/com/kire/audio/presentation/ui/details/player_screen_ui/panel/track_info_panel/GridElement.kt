package com.kire.audio.presentation.ui.details.player_screen_ui.panel.track_info_panel

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.BasicTextField

import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

import com.kire.audio.presentation.ui.details.common.RubikFontText
import com.kire.audio.presentation.ui.theme.AudioExtendedTheme

@Composable
fun GridElementTitle(
    title: String
){
    RubikFontText(
        text = title,
        style = TextStyle(
            color = Color.White,
            fontSize = 19.sp,
            lineHeight = 19.sp,
            fontWeight = FontWeight.Medium
        )
    )
}

@Composable
fun GridElementInfo(
    text: String,
    isEnabled: Boolean = false,
    isEditable: Boolean = false,
    isImageURI: Boolean = false,
    updateText: ((String) -> Unit)? = null,
    changeOpenDialog: ((Boolean) -> Unit)? = null
){
    var newText by rememberSaveable { mutableStateOf(text) }

    BasicTextField(
        modifier = Modifier
            .background(
                color = Color.Transparent,
                shape = MaterialTheme.shapes.small,
            )
            .fillMaxWidth(0.5f)
            .pointerInput(isEnabled && isEditable && isImageURI) {
                detectTapGestures {
                    if (isEnabled && isEditable && isImageURI && changeOpenDialog != null)
                        changeOpenDialog(true)
                }
            },
        value = newText,
        onValueChange = {
            newText = it.also {
                if (updateText != null && newText != text)
                    updateText(it)
            }
        },
        enabled = isEnabled && isEditable && !isImageURI,
        cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
        textStyle = LocalTextStyle.current.copy(
            color = if (isEnabled && isEditable) AudioExtendedTheme.extendedColors.roseAccent else Color.White,
            fontSize = 15.sp,
            fontWeight = FontWeight.Light,
            lineHeight = 15.sp,
            fontFamily = AudioExtendedTheme.extendedFonts.rubikFontFamily
        ),
        decorationBox = { innerTextField ->
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.5f),
                contentAlignment = Alignment.CenterStart
            ) {
                Box(Modifier.fillMaxWidth()) {
                    innerTextField()
                }
            }
        }
    )
}