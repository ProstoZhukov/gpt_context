package ru.tensor.sbis.design.recipient_selection.ui.items

import ru.tensor.sbis.communication_decl.selection.recipient.RecipientSelectionItemsMode
import ru.tensor.sbis.design.profile_decl.person.PersonClickListener
import ru.tensor.sbis.design.recipient_selection.domain.factory.RecipientItem
import ru.tensor.sbis.design.recipient_selection.ui.items.single_line.SingleLineSelectableItemsCustomization
import ru.tensor.sbis.design_selection.contract.customization.SelectionCustomization
import ru.tensor.sbis.design_selection.contract.customization.selected.DefaultSelectedItemsCustomization
import ru.tensor.sbis.design_selection.contract.customization.selected.SelectedItemsCustomization
import ru.tensor.sbis.design_selection.contract.customization.selection.DefaultSelectableItemsCustomization
import ru.tensor.sbis.design_selection.contract.customization.selection.SelectableItemsCustomization

/**
 * Кастомизация компонента выбора получателей.
 *
 * @author vv.chekurda
 */
@Suppress("UNCHECKED_CAST")
internal class RecipientSelectionCustomization(
    private val itemsMode: RecipientSelectionItemsMode,
    private val personClickListener: PersonClickListener? = null
) : SelectionCustomization<RecipientItem> {

    override fun getListItemsCustomization(): SelectableItemsCustomization<RecipientItem> =
        when (itemsMode) {
            RecipientSelectionItemsMode.DEFAULT -> {
                DefaultSelectableItemsCustomization(personClickListener) as SelectableItemsCustomization<RecipientItem>
            }
            RecipientSelectionItemsMode.SINGLE_LINE -> {
                SingleLineSelectableItemsCustomization(personClickListener)
            }
        }

    override fun getSelectedItemsCustomization(): SelectedItemsCustomization<RecipientItem> =
        DefaultSelectedItemsCustomization(personClickListener) as SelectedItemsCustomization<RecipientItem>
}