package ru.tensor.sbis.design.universal_selection.domain.factory.customization.selectable

import androidx.fragment.app.FragmentActivity
import org.apache.commons.lang3.StringUtils
import ru.tensor.sbis.design.universal_selection.domain.factory.UniversalItem
import ru.tensor.sbis.design.universal_selection.domain.factory.UniversalSelectionFolderItem
import ru.tensor.sbis.design.universal_selection.domain.factory.UniversalSelectionItem
import ru.tensor.sbis.design.universal_selection.domain.factory.customization.selectable.view_holders.simple.UniversalSelectionViewHolderHelper
import ru.tensor.sbis.design.universal_selection.domain.factory.customization.selectable.view_holders.folders.UniversalSelectionFolderViewHolderHelper
import ru.tensor.sbis.design_selection.contract.customization.selection.SelectableItemsCustomization
import ru.tensor.sbis.design_selection.contract.customization.selection.SelectionClickDelegate
import ru.tensor.sbis.list.view.item.ViewHolderHelper
import javax.inject.Provider

/**
 * Реализация кастомизации ячеек доступных для выбора в универсальном справочнике.
 *
 * @author vv.chekurda
 */
internal class UniversalSelectableItemsCustomization : SelectableItemsCustomization<UniversalItem> {

    @Suppress("UNCHECKED_CAST")
    override fun createViewHolderHelpers(
        clickDelegate: SelectionClickDelegate,
        activityProvider: Provider<FragmentActivity>,
    ): Map<Any, ViewHolderHelper<UniversalItem, *>> = mapOf(
        UniversalSelectionItem::class.java.simpleName.withSuffix(multi = true) to
            UniversalSelectionViewHolderHelper(
                clickDelegate = clickDelegate,
                viewHolderType = ITEM_VIEW_HOLDER_TYPE,
                isMultiSelection = true
            ) as ViewHolderHelper<UniversalItem, *>,

        UniversalSelectionItem::class.java.simpleName.withSuffix(multi = false) to
            UniversalSelectionViewHolderHelper(
                clickDelegate = clickDelegate,
                viewHolderType = ITEM_VIEW_HOLDER_TYPE,
                isMultiSelection = false
            ) as ViewHolderHelper<UniversalItem, *>,

        UniversalSelectionFolderItem::class.java.simpleName.withSuffix(multi = true) to
            UniversalSelectionFolderViewHolderHelper(
                clickDelegate = clickDelegate,
                viewHolderType = FOLDER_ITEM_VIEW_HOLDER_TYPE,
                isMultiSelection = true
            ) as ViewHolderHelper<UniversalItem, *>,

        UniversalSelectionFolderItem::class.java.simpleName.withSuffix(multi = false) to
            UniversalSelectionFolderViewHolderHelper(
                clickDelegate = clickDelegate,
                viewHolderType = FOLDER_ITEM_VIEW_HOLDER_TYPE,
                isMultiSelection = false
            ) as ViewHolderHelper<UniversalItem, *>
    )

    override fun getViewHolderType(item: UniversalItem, isMultiSelection: Boolean): Any =
        when (item) {
            is UniversalSelectionItem -> {
                UniversalSelectionItem::class.java.simpleName.withSuffix(isMultiSelection)
            }
            is UniversalSelectionFolderItem -> {
                UniversalSelectionFolderItem::class.java.simpleName.withSuffix(isMultiSelection)
            }
        }

    private fun String?.withSuffix(multi: Boolean): String =
        checkNotNull(this) + if (multi) StringUtils.EMPTY else SINGLE_TYPE
}

private const val SINGLE_TYPE = "Single"
private const val ITEM_VIEW_HOLDER_TYPE = 0
private const val FOLDER_ITEM_VIEW_HOLDER_TYPE = 1