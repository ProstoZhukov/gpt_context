package ru.tensor.sbis.our_organisations.presentation.highlight_helper

/**
 * Класс реализует поиск всех вхождений подстроки в строке.
 *
 * @author mv.ilin
 */
internal class SubstringSearchRangeBuilder : SearchRangeBuilder() {
    override fun provideRanges(text: CharSequence, searchText: CharSequence, ignoreCase: Boolean): Set<IntRange> {
        if (text.isBlank() || searchText.isBlank()) return emptySet()

        return super.provideRanges(text, searchText, ignoreCase)
    }

    override fun getRegex(words: List<String>, ignoreCase: Boolean): Regex {
        val wordsString = words.joinToString(separator = WORD_OR_SEPARATOR)
        val flags = if (ignoreCase) setOf(RegexOption.IGNORE_CASE) else setOf()
        return Regex(wordsString, flags)
    }
}
