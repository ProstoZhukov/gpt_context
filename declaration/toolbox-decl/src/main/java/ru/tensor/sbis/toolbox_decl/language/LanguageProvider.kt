package ru.tensor.sbis.toolbox_decl.language

import android.content.Context

/**
 * Поставщик [LanguageFeature].
 *
 * @author kv.martyshenko
 */
object LanguageProvider {

    fun interface LanguageFinder {
        fun findLanguageFeature(context: Context): LanguageFeature?
    }

    private var languageFinder = LanguageFinder {
        (it.applicationContext as? HasLanguageFeature)?.getLanguageFeature()
    }

    fun setFinder(finder: LanguageFinder) {
        languageFinder = finder
    }

    fun get(context: Context): LanguageFeature? {
        return languageFinder.findLanguageFeature(context)
    }
}