package ru.tensor.sbis.communicator.core.views.contact_view

import ru.tensor.sbis.design.custom_view_tools.utils.HighlightSpan

/**
 * Преобразовать позиции выделения текста из списка Int значений в список [HighlightSpan]-ов.
 *
 * @author rv.krohalev
 */
fun highlightSpansFrom(highlights: List<Int>?): List<HighlightSpan>? {
    if (highlights.isNullOrEmpty()) return null
    var highlightStart = 0
    val highlightSpans = mutableListOf<HighlightSpan>()
    highlights.forEachIndexed { index, item ->
        if (index % 2 == 0) {
            highlightStart = item
        } else {
            highlightSpans.add(HighlightSpan(start = highlightStart, end = item + 1))
        }
    }
    return highlightSpans
}
