package com.kire.audio.device.audio.media_controller

import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.session.MediaController

import com.kire.audio.device.audio.util.MediaCommands
import com.kire.audio.presentation.model.Track

private fun getMetaDataFromMediaClass(media: Track): MediaMetadata {
    return MediaMetadata.Builder()
        .setTitle(media.title)
        .setAlbumTitle(media.title)
        .setDisplayTitle(media.title)
        .setArtist(media.artist)
        .setAlbumArtist(media.artist)
        .setArtworkUri(media.imageUri)
        .build()
}

fun MediaController.performPlayMedia(media: Track) {

    val metadata = getMetaDataFromMediaClass(media)
    val mediaItem = MediaItem.Builder()
        .setUri(media.path)
        .setMediaId(media.id)
        .setMediaMetadata(metadata)
        .build()

    this.apply {
        if (isPlaying)
            stop()

        if (MediaCommands.isRepeatRequired)
            seekTo(0L)
        else
            setMediaItem(mediaItem)

        prepare()
        play()

        MediaCommands.isPlayRequired = true
    }
}