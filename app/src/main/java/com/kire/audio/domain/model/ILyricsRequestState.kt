package com.kire.audio.domain.model

sealed interface ILyricsRequestStateDomain {
    data class Successful(val lyrics: String): ILyricsRequestStateDomain
    data class Unsuccessful(val message: String): ILyricsRequestStateDomain
    data object OnRequest: ILyricsRequestStateDomain
}