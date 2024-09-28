package ru.tensor.sbis.our_organisations.presentation.highlight_helper

import java.util.regex.Pattern

/**
 * Функция поиска отрезков поискового текста в предоставленном тексте [CharSequence]
 *
 * @param searchText - поисковой текст
 * @param ignoreCase - игнорировать регистр
 * @param searchRangeBuilders - список поставщиков [SearchRangeBuilder]
 *
 * @author mv.ilin
 */
internal fun CharSequence.findRanges(
    searchText: String,
    ignoreCase: Boolean,
    searchRangeBuilders: Array<SearchRangeBuilder>
): Set<IntRange> {
    val findRanges = mutableSetOf<IntRange>()

    val substring = this
        .substring(searchText)
        ?.also(findRanges::add)

    if (substring?.length == this.length) {
        return findRanges
    }

    for (index in searchRangeBuilders.indices) {
        val builder = searchRangeBuilders[index]

        val ranges = builder.provideRanges(this, searchText, ignoreCase)

        if (ranges.isNotEmpty()) {
            findRanges.addAll(ranges)
            break
        }
    }

    return findRanges
}

private val IntRange.length
    get() = if (isEmpty()) {
        0
    } else {
        last - first
    }

private fun CharSequence.substring(searchText: String): IntRange? = searchText
    .run { Pattern.quote(this) }
    .toRegex()
    .find(this)
    ?.range
