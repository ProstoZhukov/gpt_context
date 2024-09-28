package ru.tensor.sbis.our_organisations.presentation.highlight_helper

import java.util.regex.Pattern

/**
 * Контракт поставщика отрезков поискового текста в предоставленном тексте.
 *
 * @author mv.ilin
 */
internal abstract class SearchRangeBuilder {

    companion object {
        internal const val MINIMUM_WORD_LENGTH = 2
        internal const val WORD_OR_SEPARATOR = "|"
        internal val WORD_SEPARATOR_PATTERN = Regex("\\s+")
    }

    /**
     * Предоставить отрезки поискового текста в предоставленном тексте.
     *
     * @param text текст в котором пытаемся найти поисковый текст.
     * @param searchText поисковый текст
     * @param ignoreCase игнорировать регистр
     */
    open fun provideRanges(text: CharSequence, searchText: CharSequence, ignoreCase: Boolean): Set<IntRange> {
        val words = searchText
            .split(WORD_SEPARATOR_PATTERN)
            .asSequence()
            .filter { it.length >= MINIMUM_WORD_LENGTH }
            .map { Pattern.quote(it) }
            .toList()

        val searchRegex = getRegex(words, ignoreCase)

        val matches = searchRegex.findAll(text)

        return matches.map { it.range }.toSet()
    }

    /**
     * Предоставить регулярное выражение для поиска.
     *
     * @param words лист слов.
     * @param ignoreCase игнорировать регистр
     */
    protected abstract fun getRegex(words: List<String>, ignoreCase: Boolean): Regex
}
