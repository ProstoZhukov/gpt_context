package ru.tensor.sbis.design_selection.contract.customization.selected

import ru.tensor.sbis.design_selection.contract.data.SelectionItem

/**
 * Делегат обработки кликов по ячейке выбранного элемента.
 *
 * @author vv.chekurda
 */
interface SelectedItemClickDelegate {

    /**
     * Обработать клик для отмены выбранного элемента [item].
     */
    fun onUnselectClicked(item: SelectionItem)
}