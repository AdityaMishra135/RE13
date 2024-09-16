package com.kire.audio.presentation.mapper

import com.kire.audio.domain.constants.SortTypeDomain
import com.kire.audio.presentation.constants.SortType

fun SortTypeDomain.toPresentation() =
    SortType.valueOf(this.toString())