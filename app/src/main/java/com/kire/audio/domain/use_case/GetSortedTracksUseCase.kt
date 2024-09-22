package com.kire.audio.domain.use_case

import com.kire.audio.domain.constants.PreferencesDataStoreConstants
import com.kire.audio.domain.model.TrackDomain
import com.kire.audio.domain.repository.IPreferencesRepository
import com.kire.audio.domain.repository.ITrackRepository
import com.kire.audio.domain.constants.SortTypeDomain
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

class GetSortedTracksUseCase @Inject constructor(
    private val preferencesDataStoreRepository: IPreferencesRepository,
    private val trackRepository: ITrackRepository
) {

    @OptIn(ExperimentalCoroutinesApi::class)
    suspend operator fun invoke(): Flow<List<TrackDomain>> =
        preferencesDataStoreRepository.readSortOption(PreferencesDataStoreConstants.SORT_OPTION_KEY)
            .flatMapLatest { sortType ->
                when(sortType) {
                    SortTypeDomain.DATE_ASC_ORDER -> trackRepository.getTracksOrderedByDateAddedASC()
                    SortTypeDomain.DATE_DESC_ORDER -> trackRepository.getTracksOrderedByDateAddedDESC()
                    SortTypeDomain.TITLE_ASC_ORDER -> trackRepository.getTracksOrderedByTitleASC()
                    SortTypeDomain.TITLE_DESC_ORDER -> trackRepository.getTracksOrderedByTitleDESC()
                    SortTypeDomain.ARTIST_ASC_ORDER -> trackRepository.getTracksOrderedByArtistASC()
                    SortTypeDomain.ARTIST_DESC_ORDER -> trackRepository.getTracksOrderedByArtistDESC()
                    SortTypeDomain.DURATION_ASC_ORDER -> trackRepository.getTracksOrderedByDurationASC()
                    SortTypeDomain.DURATION_DESC_ORDER -> trackRepository.getTracksOrderedByDurationDESC()
                }
            }
}