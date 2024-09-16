package com.kire.audio.domain.use_case

import com.kire.audio.domain.repository.IPreferencesRepository
import com.kire.audio.domain.constants.PreferencesDataStoreConstants
import javax.inject.Inject

class ReadRepeatModeUseCase @Inject constructor(
    private val preferencesDataStoreRepository: IPreferencesRepository
) {

    suspend operator fun invoke() =
        preferencesDataStoreRepository.readRepeatMode(PreferencesDataStoreConstants.REPEAT_MODE_KEY.toString())
}