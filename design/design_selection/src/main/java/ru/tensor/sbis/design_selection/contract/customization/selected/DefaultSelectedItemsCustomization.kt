package ru.tensor.sbis.design_selection.contract.customization.selected

import androidx.recyclerview.widget.RecyclerView
import ru.tensor.sbis.design.profile_decl.person.PersonClickListener
import ru.tensor.sbis.design_selection.contract.data.SelectionItem
import ru.tensor.sbis.design_selection.contract.customization.selected.folder.SelectedFolderItemViewHolderHelper
import ru.tensor.sbis.design_selection.contract.customization.selected.person.SelectedPersonItemViewHolderHelper
import ru.tensor.sbis.design_selection.contract.data.SelectionFolderItem
import ru.tensor.sbis.design_selection.contract.data.SelectionPersonItem
import ru.tensor.sbis.list.view.item.ViewHolderHelper

/**
 * Дефолтная реализация кастомизации выбранных ячеек компонента выбора.
 * Отвечает за стандартное отображение выбранных персон и папок.
 *
 * @see SelectedItemsCustomization
 *
 * @author vv.chekurda
 */
class DefaultSelectedItemsCustomization(
    private val personClickListener: PersonClickListener? = null
) : SelectedItemsCustomization<SelectionItem> {

    @Suppress("UNCHECKED_CAST")
    override fun createViewHolderHelpers(
        clickDelegate: SelectedItemClickDelegate
    ): Map<Any, ViewHolderHelper<SelectionItem, RecyclerView.ViewHolder>> = mapOf(
        SelectionPersonItem::class.java.simpleName to SelectedPersonItemViewHolderHelper(
            clickDelegate = clickDelegate,
            personClickListener = personClickListener
        ) as ViewHolderHelper<SelectionItem, RecyclerView.ViewHolder>,

        SelectionFolderItem::class.java.simpleName to SelectedFolderItemViewHolderHelper(
            clickDelegate = clickDelegate
        ) as ViewHolderHelper<SelectionItem, RecyclerView.ViewHolder>
    )

    override fun getViewHolderType(item: SelectionItem): Any =
        when (item) {
            is SelectionPersonItem -> SelectionPersonItem::class.java.simpleName
            is SelectionFolderItem -> SelectionFolderItem::class.java.simpleName
            else -> error("Unexpected model ${item::class.java} for DefaultSelectedItemsCustomization")
        }
}