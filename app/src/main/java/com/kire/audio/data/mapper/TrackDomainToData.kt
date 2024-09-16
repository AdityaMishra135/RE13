package com.kire.audio.data.mapper

import android.net.Uri
import com.kire.audio.data.model.TrackEntity
import com.kire.audio.domain.model.TrackDomain

fun TrackDomain.toEntity() = TrackEntity(
    title = this.title,
    album = this.album,
    artist = this.artist,
    duration = this.duration,
    lyrics = this.lyrics,
    path = this.path,
    albumId = this.albumId,
    imageUri = Uri.parse(this.imageUri),
    dateAdded = this.dateAdded,
    isFavourite = this.isFavourite,
    defaultImageUri = Uri.parse(this.defaultImageUri),
    id = this.id
)