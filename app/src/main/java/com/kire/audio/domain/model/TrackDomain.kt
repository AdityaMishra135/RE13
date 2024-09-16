package com.kire.audio.domain.model

data class TrackDomain(
    val title: String = "",
    val album: String? = null,
    val artist: String = "",
    val duration: Long = 0L,
    val lyrics: String = "",
    val path: String = "",
    val albumId: Long? = 0L,
    val imageUri: String? = null,
    val dateAdded: String? = "",
    val isFavourite: Boolean = false,
    val defaultImageUri: String? = null,
    val id: String = ""
)
