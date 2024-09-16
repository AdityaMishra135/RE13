package com.kire.audio.presentation.ui.theme.localization

import android.content.Context
import android.content.res.Configuration
import java.util.Locale

object LocalizationProvider {
    private var currentLocalization: Localization = detectLocalization()

    val strings: IStrings
        get() = when (currentLocalization) {
            Localization.English -> EnglishStrings
            Localization.Russian -> RussianStrings
            Localization.Ukrainian -> UkrainianStrings
        }

    private fun detectLocalization(): Localization {
        val locale = Locale.getDefault()
        return when (locale.language) {
            "en" -> Localization.English
            "ru" -> Localization.Russian
            "uk" -> Localization.Ukrainian
            else -> Localization.English
        }
    }

    fun updateLocalization(context: Context) {
        val configuration: Configuration = context.resources.configuration
        val locale = configuration.locales[0]
        currentLocalization = when (locale.language) {
            "en" -> Localization.English
            "ru" -> Localization.Russian
            "uk" -> Localization.Ukrainian
            else -> Localization.English
        }
    }
}