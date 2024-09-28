package ru.tensor.sbis.design.selection.ui.utils

import ru.tensor.sbis.design.selection.bl.vm.selection.multi.MultiSelectionViewModel
import ru.tensor.sbis.design.selection.ui.contract.list.SelectorMergeFunction
import ru.tensor.sbis.design.selection.ui.model.SelectorItemModel

/**
 * Функция для получения элементов для отображения в зависимости от выбора и состояния его раскрытия
 *
 * @author ma.kolpakov
 */
internal class MultiSelectionMergeFunction(
    private val selectionViewModel: MultiSelectionViewModel<SelectorItemModel>
) : SelectorMergeFunction {

    override fun apply(
        selection: List<SelectorItemModel>,
        items: List<SelectorItemModel>
    ): List<SelectorItemModel> {
        // ускорить с помощью treeMap при необходимости
        val selectionUpdate = items.filter { item -> selection.any { selected -> selected.id == item.id } }
        selectionViewModel.updateSelection(selectionUpdate)
        return when {
            selection.isEmpty() -> items
            else -> items.minusItems(selection)
        }
    }
}