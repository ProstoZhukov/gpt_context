package ru.tensor.sbis.design.selection.ui.utils

import ru.tensor.sbis.design.selection.ui.contract.list.RecentSelectionCachingFunction
import ru.tensor.sbis.design.selection.ui.model.SelectorItemModel

/**
 * Реализация [RecentSelectionCachingFunction], которая возвращает исходный список элементов, поскольку для одиночного
 * выбора кэширование выбранных элементов не имеет смысла
 *
 * @author us.bessonov
 */
internal class IdleRecentSelectionCachingFunction : RecentSelectionCachingFunction {

    override fun apply(
        selection: List<SelectorItemModel>,
        items: List<SelectorItemModel>,
        hasSearchQuery: Boolean
    ): List<SelectorItemModel> = items

    override fun hasRecentlySelectedItems() = false
}