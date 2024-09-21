package com.kire.audio.presentation.util.search

import com.kire.audio.presentation.model.Track

/**
 * Фильтрует список треков на основе введенного поискового запроса
 *
 * @param tracks Фильтруемый список треков
 * @param searchString Поисковая строка для поиска соответствий в списке треков
 *
 * @return Отфильтрованный список треков, соответствующий поисковому запросу
 *
 * @author Михаил Гонтарев (KiREHwYE)
 */
fun onSearchRequestChange(
    tracks: List<Track>,
    searchString: String
): List<Track> = tracks.filter { track ->
            searchString.isNotEmpty() && (
                    levenshteinRatio(track.title.lowercase(), searchString.lowercase()) > 0.6 || track.title.contains(searchString, ignoreCase = true)
                            || levenshteinRatio(track.artist.lowercase(), searchString.lowercase()) > 0.6 || track.artist.contains(searchString, ignoreCase = true)
                            || levenshteinRatio("${track.album}".lowercase(), searchString.lowercase()) > 0.6 || track.album?.contains(searchString, ignoreCase = true) == true
                    )
}