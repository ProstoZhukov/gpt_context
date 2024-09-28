package ru.tensor.sbis.our_organisations.presentation.highlight_helper

/**
 * Класс реализует поиск по словам, которые выделяются из поискового текста.
 *
 * @author mv.ilin
 */
internal class WordsSearchRangeBuilder : SearchRangeBuilder() {
    override fun getRegex(words: List<String>, ignoreCase: Boolean): Regex {
        val wordsString = words.joinToString(separator = WORD_OR_SEPARATOR)
        val flags = if (ignoreCase) setOf(RegexOption.IGNORE_CASE) else setOf()
        return Regex("\\b$wordsString\\b", flags)
    }
}
