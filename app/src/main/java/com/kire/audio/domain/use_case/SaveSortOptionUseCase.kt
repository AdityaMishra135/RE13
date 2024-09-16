package com.kire.audio.domain.use_case

import com.kire.audio.domain.repository.IPreferencesRepository
import com.kire.audio.domain.constants.PreferencesDataStoreConstants
import com.kire.audio.domain.constants.SortTypeDomain
import javax.inject.Inject

class SaveSortOptionUseCase @Inject constructor(
    private val preferencesDataStoreRepository: IPreferencesRepository
) {

    suspend operator fun invoke(value: SortTypeDomain) =
        preferencesDataStoreRepository
            .saveSortOption(
                PreferencesDataStoreConstants.SORT_OPTION_KEY,
                value.toString()
            )
}