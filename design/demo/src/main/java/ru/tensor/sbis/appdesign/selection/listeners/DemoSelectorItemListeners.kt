package ru.tensor.sbis.appdesign.selection.listeners

import android.widget.Toast
import ru.tensor.sbis.appdesign.selection.RecipientMultiSelectorActivity
import ru.tensor.sbis.design.selection.ui.contract.listeners.ItemClickListener
import ru.tensor.sbis.design.selection.ui.contract.listeners.SelectorItemListeners
import ru.tensor.sbis.design.selection.ui.model.recipient.DefaultRecipientSelectorItemModel

/**
 * Слушатели для элемента выбора.
 *
 * @author us.bessonov
 */
internal fun getDemoSelectorItemListeners(): SelectorItemListeners<DefaultRecipientSelectorItemModel, RecipientMultiSelectorActivity> =
    SelectorItemListeners(
        itemClickListener = object : ItemClickListener<DefaultRecipientSelectorItemModel, RecipientMultiSelectorActivity> {
            override fun onClicked(activity: RecipientMultiSelectorActivity, item: DefaultRecipientSelectorItemModel) {
                Toast.makeText(activity.applicationContext, "CLICKED item", Toast.LENGTH_SHORT).show()
            }
        },

        iconClickListener = object : ItemClickListener<DefaultRecipientSelectorItemModel, RecipientMultiSelectorActivity> {
            override fun onClicked(activity: RecipientMultiSelectorActivity, item: DefaultRecipientSelectorItemModel) {
                Toast.makeText(activity.applicationContext, "CLICKED item`s icon", Toast.LENGTH_SHORT).show()
            }
        },

        rightActionListener = object : ItemClickListener<DefaultRecipientSelectorItemModel, RecipientMultiSelectorActivity> {
            override fun onClicked(activity: RecipientMultiSelectorActivity, item: DefaultRecipientSelectorItemModel) {
                Toast.makeText(activity.applicationContext, "CLICKED right icon", Toast.LENGTH_SHORT).show()
            }
        },
        itemLongClickListener = object : ItemClickListener<DefaultRecipientSelectorItemModel, RecipientMultiSelectorActivity> {
            override fun onClicked(activity: RecipientMultiSelectorActivity, item: DefaultRecipientSelectorItemModel) {
                Toast.makeText(activity.applicationContext, "CLICKED long click", Toast.LENGTH_SHORT).show()
            }
        },
    )
