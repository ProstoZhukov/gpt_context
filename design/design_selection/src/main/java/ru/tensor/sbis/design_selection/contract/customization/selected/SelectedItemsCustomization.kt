package ru.tensor.sbis.design_selection.contract.customization.selected

import androidx.recyclerview.widget.RecyclerView
import ru.tensor.sbis.design_selection.contract.data.SelectionItem
import ru.tensor.sbis.list.view.item.ViewHolderHelper

/**
 * Кастомизатор выбранных ячеек компонента выбора.
 *
 * @author vv.chekurda
 */
interface SelectedItemsCustomization<ITEM : SelectionItem> {

    /**
     * Метод для создания набора [ViewHolderHelper], для работы с типами view от предметной области
     *
     * @see getViewHolderType
     */
    fun createViewHolderHelpers(
        clickDelegate: SelectedItemClickDelegate
    ): Map<Any, ViewHolderHelper<ITEM, RecyclerView.ViewHolder>>

    /**
     * Метод для определения типа [ViewHolderHelper] по модели данных [item].
     *
     * @see createViewHolderHelpers
     */
    fun getViewHolderType(item: ITEM): Any
}