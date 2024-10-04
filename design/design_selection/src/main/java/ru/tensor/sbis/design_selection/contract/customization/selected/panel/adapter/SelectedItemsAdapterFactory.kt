package ru.tensor.sbis.design_selection.contract.customization.selected.panel.adapter

import ru.tensor.sbis.design_selection.contract.customization.selected.DefaultSelectedItemsCustomization
import ru.tensor.sbis.design_selection.contract.customization.selected.SelectedItemClickDelegate
import ru.tensor.sbis.design_selection.contract.customization.selected.SelectedItemsCustomization
import ru.tensor.sbis.design_selection.contract.data.SelectionItem

/**
 * Фабрика для создания адаптера списка выбранных получателей.
 *
 * @author vv.chekurda
 */
class SelectedItemsAdapterFactory(
    private val clickDelegate: SelectedItemClickDelegate,
    private val selectedItemsCustomization: SelectedItemsCustomization<SelectionItem> =
        DefaultSelectedItemsCustomization()
) {

    fun create(): SelectedItemsAdapter {
        val viewHoldersHelper = selectedItemsCustomization.createViewHolderHelpers(clickDelegate)
        return SelectedItemsAdapter(selectedItemsCustomization, viewHoldersHelper)
    }
}