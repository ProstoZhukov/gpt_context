package ru.tensor.sbis.design_selection.contract.customization

import ru.tensor.sbis.design_selection.contract.data.SelectionItem
import ru.tensor.sbis.design_selection.contract.customization.selection.SelectableItemsCustomization
import ru.tensor.sbis.design_selection.contract.customization.selected.SelectedItemsCustomization

/**
 * Кастомизация компонента выбора.
 *
 * @author vv.chekurda
 */
interface SelectionCustomization<ITEM : SelectionItem> {

    /**
     * Получить кастомизацию списка элементов доступных для выбора.
     */
    fun getListItemsCustomization(): SelectableItemsCustomization<ITEM>

    /**
     * Получить кастомизацию для списка выбранных элементов.
     */
    fun getSelectedItemsCustomization(): SelectedItemsCustomization<ITEM>
}