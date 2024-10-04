package ru.tensor.sbis.design_selection.contract.customization

import ru.tensor.sbis.design.profile_decl.person.PersonClickListener
import ru.tensor.sbis.design_selection.contract.customization.selection.SelectableItemsCustomization
import ru.tensor.sbis.design_selection.contract.customization.selected.SelectedItemsCustomization
import ru.tensor.sbis.design_selection.contract.customization.selected.DefaultSelectedItemsCustomization
import ru.tensor.sbis.design_selection.contract.customization.selection.DefaultSelectableItemsCustomization
import ru.tensor.sbis.design_selection.contract.data.SelectionItem

/**
 * Дефолтная кастомизация компонента выбора.
 *
 * @author vv.chekurda
 */
@Suppress("UNCHECKED_CAST")
class DefaultSelectionCustomization<ITEM : SelectionItem>(
    private val personClickListener: PersonClickListener? = null
) : SelectionCustomization<ITEM> {

    override fun getListItemsCustomization(): SelectableItemsCustomization<ITEM> =
        DefaultSelectableItemsCustomization(personClickListener) as SelectableItemsCustomization<ITEM>

    override fun getSelectedItemsCustomization(): SelectedItemsCustomization<ITEM> =
        DefaultSelectedItemsCustomization(personClickListener) as SelectedItemsCustomization<ITEM>
}