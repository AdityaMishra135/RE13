package com.kire.audio.presentation.model

import android.net.Uri
import com.kire.audio.presentation.model.state.ILyricsRequestState

data class Track(
    val title: String = "",
    val album: String? = null,
    val artist: String = "",
    val duration: Long = 0L,
    val lyrics: ILyricsRequestState = ILyricsRequestState.OnRequest,
    val path: String = "",
    val albumId: Long? = null,
    val imageUri: Uri? = null,
    val dateAdded: String? = null,
    val isFavourite: Boolean = false,
    val defaultImageUri: Uri? = null,
    val id: String = ""
)
