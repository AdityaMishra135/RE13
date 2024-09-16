package com.kire.audio.domain.use_case

import com.kire.audio.domain.repository.IPreferencesRepository
import com.kire.audio.domain.constants.PreferencesDataStoreConstants
import javax.inject.Inject

class ReadSortOptionUseCase @Inject constructor(
    private val preferencesDataRepository: IPreferencesRepository
) {

    suspend operator fun invoke() =
        preferencesDataRepository.readSortOption(PreferencesDataStoreConstants.SORT_OPTION_KEY)
}