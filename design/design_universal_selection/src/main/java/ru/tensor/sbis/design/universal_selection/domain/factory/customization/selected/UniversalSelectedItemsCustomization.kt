package ru.tensor.sbis.design.universal_selection.domain.factory.customization.selected

import androidx.recyclerview.widget.RecyclerView
import ru.tensor.sbis.design.universal_selection.domain.factory.UniversalItem
import ru.tensor.sbis.design.universal_selection.domain.factory.UniversalSelectionFolderItem
import ru.tensor.sbis.design.universal_selection.domain.factory.UniversalSelectionItem
import ru.tensor.sbis.design.universal_selection.domain.factory.customization.selected.view_holders.simple.UniversalSelectedViewHolderHelper
import ru.tensor.sbis.design_selection.contract.customization.selected.SelectedItemClickDelegate
import ru.tensor.sbis.design_selection.contract.customization.selected.SelectedItemsCustomization
import ru.tensor.sbis.design_selection.contract.customization.selected.folder.SelectedFolderItemViewHolderHelper
import ru.tensor.sbis.list.view.item.ViewHolderHelper

/**
 * Реализация кастомизации выбранных ячеек в универсальном справочнике.
 *
 * @author vv.chekurda
 */
internal class UniversalSelectedItemsCustomization : SelectedItemsCustomization<UniversalItem> {

    @Suppress("UNCHECKED_CAST")
    override fun createViewHolderHelpers(
        clickDelegate: SelectedItemClickDelegate
    ): Map<Any, ViewHolderHelper<UniversalItem, RecyclerView.ViewHolder>> = mapOf(
        UniversalSelectionItem::class.java.simpleName to UniversalSelectedViewHolderHelper(
            clickDelegate = clickDelegate
        ) as ViewHolderHelper<UniversalItem, RecyclerView.ViewHolder>,

        UniversalSelectionFolderItem::class.java.simpleName to SelectedFolderItemViewHolderHelper(
            clickDelegate = clickDelegate
        ) as ViewHolderHelper<UniversalItem, RecyclerView.ViewHolder>
    )

    override fun getViewHolderType(item: UniversalItem): Any =
        when (item) {
            is UniversalSelectionItem -> UniversalSelectionItem::class.java.simpleName
            is UniversalSelectionFolderItem -> UniversalSelectionFolderItem::class.java.simpleName
        }
}