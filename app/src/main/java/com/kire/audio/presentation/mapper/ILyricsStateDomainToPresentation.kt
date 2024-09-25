package com.kire.audio.presentation.mapper

import com.kire.audio.domain.model.ILyricsRequestStateDomain
import com.kire.audio.presentation.model.state.ILyricsRequestState

fun ILyricsRequestStateDomain.toPresentation() =
    when(this) {
        is ILyricsRequestStateDomain.Successful -> ILyricsRequestState.Success(this.lyrics)
        is ILyricsRequestStateDomain.Unsuccessful -> ILyricsRequestState.Unsuccessful(this.message)
        is ILyricsRequestStateDomain.OnRequest -> ILyricsRequestState.OnRequest
    }