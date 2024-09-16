package com.kire.audio.domain.use_case

import com.kire.audio.di.IoDispatcher
import com.kire.audio.domain.constants.GetTrackLyricsFromGeniusStrings
import com.kire.audio.domain.model.ILyricsRequestStateDomain
import com.kire.audio.domain.constants.LyricsRequestModeDomain
import kotlinx.coroutines.CoroutineDispatcher

import kotlinx.coroutines.withContext

import org.jsoup.Jsoup

import javax.inject.Inject

class GetTrackLyricsFromGeniusUseCase @Inject constructor(
    @IoDispatcher private val coroutineDispatcher: CoroutineDispatcher
) {

    suspend operator fun invoke(
        mode: LyricsRequestModeDomain,
        title: String?,
        artist: String?,
        userInput: String?
    ): ILyricsRequestStateDomain {

        GetTrackLyricsFromGeniusStrings.apply {

            return withContext(coroutineDispatcher){

                try {
                    val titleFormatted = title?.toAllowedForm()
                    val artistFormatted = artist?.toAllowedForm()?.replaceFirstChar(Char::titlecase)

                    val url =
                        when(mode) {
                            LyricsRequestModeDomain.BY_LINK -> userInput
                            LyricsRequestModeDomain.BY_TITLE_AND_ARTIST -> {
                                val urlPart = userInput?.toAllowedForm()?.replaceFirstChar(Char::titlecase)
                                (URL_BEGINNING + urlPart + URL_ENDING).replace(MINUS_MINUS_PLUS.toRegex(), "-")
                            }
                            else ->
                                (URL_BEGINNING + artistFormatted + ARTIST_TITLE_SPACER + titleFormatted + URL_ENDING)
                                    .replace(MINUS_MINUS_PLUS.toRegex(), HYPHEN)

                        }

                    var doc: org.jsoup.nodes.Document =
                        Jsoup.connect(url).userAgent(USER_AGENT).get()
                    val temp = doc.html().replace(BREAK_LINE_TAG, DOLLARS)
                    doc = Jsoup.parse(temp)

                    val elements = doc.select(CSS_QUERY)

                    var text = EMPTY_STRING

                    for (i in 0 until elements.size)
                        text += elements.eq(i).text().replace(DOLLARS, "\n")

                    ILyricsRequestStateDomain.Successful(text)

                } catch (e: Exception) {
                    ILyricsRequestStateDomain.Unsuccessful(e.message ?: ERROR_MESSAGE)
                }
            }
        }
    }


    private fun String.toAllowedForm(): String {
        GetTrackLyricsFromGeniusStrings.apply {
            val notAllowedCharacters = NOT_ALLOWED_CHARACTERS_TEMPLATE.toRegex()
            val hyphen = HYPHEN_TEMPLATE.toRegex()

            return this@toAllowedForm.trim().lowercase().replace(AND_SYMBOL, AND_WORD).replace(notAllowedCharacters, EMPTY_STRING)
                .replace(hyphen, HYPHEN).run {
                    if (this.contains(FEAT)) this.removeRange(
                        this.indexOf(FEAT) - 1,
                        this.length
                    ) else this
                }
        }
    }
}