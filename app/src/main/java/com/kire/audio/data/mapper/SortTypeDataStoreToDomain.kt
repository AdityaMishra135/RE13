package com.kire.audio.data.mapper

import com.kire.audio.data.constants.SortTypeDataStore
import com.kire.audio.domain.constants.SortTypeDomain

fun SortTypeDataStore.toDomain() =
    SortTypeDomain.valueOf(this.toString())