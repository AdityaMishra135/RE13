package com.kire.audio.data.model

import android.net.Uri

import androidx.room.Entity
import androidx.room.PrimaryKey

import com.kire.audio.domain.model.TrackDomain

@Entity(tableName = "track")
data class TrackEntity(
    val title: String = "",
    val album: String? = null,
    val artist: String = "",
    val duration: Long = 0,
    val lyrics: String = "",
    val path: String = "",
    val albumId: Long? = 0L,
    val imageUri: Uri? = Uri.EMPTY,
    val dateAdded: String? = null,
    val isFavourite: Boolean = false,
    val defaultImageUri: Uri? = null,
    @PrimaryKey
    val id: String = ""
)