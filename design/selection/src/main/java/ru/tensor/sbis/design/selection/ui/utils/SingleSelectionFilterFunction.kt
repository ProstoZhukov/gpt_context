package ru.tensor.sbis.design.selection.ui.utils

import ru.tensor.sbis.design.selection.ui.di.common.FilterFunction
import ru.tensor.sbis.design.selection.ui.model.SelectorItemModel

/**
 * Фильтрация выбранного элемента по вхождению поисковой строки
 *
 * @author ma.kolpakov
 */
internal class SingleSelectionFilterFunction : FilterFunction {

    override fun apply(selection: List<SelectorItemModel>, searchQuery: String): List<SelectorItemModel> {
        require(selection.size < 2) { "Unexpected multiple items for single selection" }

        if (selection.isEmpty()) {
            return selection
        }

        val (selectedItem) = selection
        if (searchQuery.isBlank()) {
            selectedItem.meta.queryRanges = emptyList()
            return selection
        }

        val queryRange = selectedItem.getQueryRangeList(searchQuery)
        selectedItem.meta.queryRanges = queryRange
        return if (queryRange.isEmpty()) emptyList() else selection
    }
}