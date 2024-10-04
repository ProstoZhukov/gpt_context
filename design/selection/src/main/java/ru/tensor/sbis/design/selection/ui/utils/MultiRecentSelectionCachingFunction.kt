package ru.tensor.sbis.design.selection.ui.utils

import ru.tensor.sbis.design.selection.ui.contract.list.RecentSelectionCachingFunction
import ru.tensor.sbis.design.selection.ui.model.SelectorItemModel

/**
 * Реализация [RecentSelectionCachingFunction], которая сохраняет ранее выбранные элементы и дополняет ими исходный
 * список, если он не является результатом поискового запроса
 *
 * @author us.bessonov
 */
internal class MultiRecentSelectionCachingFunction : RecentSelectionCachingFunction {

    private val recentlySelectedItems = LinkedHashMap<String, SelectorItemModel>()

    override fun apply(
        selection: List<SelectorItemModel>,
        items: List<SelectorItemModel>,
        hasSearchQuery: Boolean
    ): List<SelectorItemModel> {
        if (hasSearchQuery) {
            return items
        }
        val itemsIncludingRecentlySelected = recentlySelectedItems.values
            .filter { !it.meta.isSelected && items.none { item -> it.id == item.id } }
            .plus(items)
        selection.forEach { recentlySelectedItems[it.id] = it }
        return itemsIncludingRecentlySelected
    }

    override fun hasRecentlySelectedItems() = recentlySelectedItems.isNotEmpty()
}