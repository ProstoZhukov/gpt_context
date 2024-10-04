package ru.tensor.sbis.design.selection.ui.utils

import ru.tensor.sbis.design.selection.bl.vm.selection.single.SingleSelectionViewModel
import ru.tensor.sbis.design.selection.ui.contract.list.SelectorMergeFunction
import ru.tensor.sbis.design.selection.ui.model.SelectorItemModel

/**
 * Функция для вставки выбранного элемента в общий список
 *
 * @author ma.kolpakov
 */
internal class SingleSelectionMergeFunction(
    private val selectionViewModel: SingleSelectionViewModel<SelectorItemModel>
) : SelectorMergeFunction {

    override fun apply(
        selection: List<SelectorItemModel>,
        items: List<SelectorItemModel>
    ): List<SelectorItemModel> {
        require(selection.size < 2) { "Unexpected multiple items for single selection" }

        if (selection.isEmpty()) {
            return items
        }

        val (selectedItem) = selection
        items.firstOrNull { it.id == selectedItem.id }?.let {
            selectionViewModel.updateSelection(it)
        }
        return items
    }
}