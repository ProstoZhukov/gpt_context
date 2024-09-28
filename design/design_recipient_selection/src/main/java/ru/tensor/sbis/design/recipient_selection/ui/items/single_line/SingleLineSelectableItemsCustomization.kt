package ru.tensor.sbis.design.recipient_selection.ui.items.single_line

import ru.tensor.sbis.design_selection.contract.customization.selection.SelectableItemsCustomization
import ru.tensor.sbis.design_selection.contract.customization.selection.SelectionClickDelegate

import androidx.fragment.app.FragmentActivity
import org.apache.commons.lang3.StringUtils
import ru.tensor.sbis.design.profile_decl.person.PersonClickListener
import ru.tensor.sbis.design.recipient_selection.domain.factory.RecipientItem
import ru.tensor.sbis.design.recipient_selection.ui.items.single_line.folder.SingleLineSelectionFolderViewHolderHelper
import ru.tensor.sbis.design.recipient_selection.ui.items.single_line.person.SingleLineSelectionPersonViewHolderHelper
import ru.tensor.sbis.design_selection.contract.data.SelectionFolderItem
import ru.tensor.sbis.design_selection.contract.data.SelectionPersonItem
import ru.tensor.sbis.list.view.item.ViewHolderHelper
import javax.inject.Provider

/**
 * Дефолтная реализация кастомизации ячеек доступных для выбора.
 * Отвечает за стандартное отображение персон и папок, доступных для выбора, в одиночном и множественном режиме.
 *
 * @see SelectableItemsCustomization
 *
 * @author vv.chekurda
 */
internal class SingleLineSelectableItemsCustomization(
    private val personClickListener: PersonClickListener? = null
) : SelectableItemsCustomization<RecipientItem> {

    @Suppress("UNCHECKED_CAST")
    override fun createViewHolderHelpers(
        clickDelegate: SelectionClickDelegate,
        activityProvider: Provider<FragmentActivity>
    ): Map<Any, ViewHolderHelper<RecipientItem, *>> = mapOf(
        SelectionPersonItem::class.java.simpleName.withSuffix(multi = true) to
                SingleLineSelectionPersonViewHolderHelper(
                    clickDelegate = clickDelegate,
                    viewHolderType = PERSON_VIEW_HOLDER_TYPE,
                    isMultiSelection = true,
                    personClickListener = personClickListener
                ) as ViewHolderHelper<RecipientItem, *>,

        SelectionPersonItem::class.java.simpleName.withSuffix(multi = false) to
                SingleLineSelectionPersonViewHolderHelper(
                    clickDelegate = clickDelegate,
                    viewHolderType = PERSON_VIEW_HOLDER_TYPE,
                    isMultiSelection = false,
                    personClickListener = personClickListener
                ) as ViewHolderHelper<RecipientItem, *>,

        SelectionFolderItem::class.java.simpleName.withSuffix(multi = true) to
                SingleLineSelectionFolderViewHolderHelper<SelectionFolderItem>(
                    clickDelegate = clickDelegate,
                    viewHolderType = FOLDER_VIEW_HOLDER_TYPE,
                    isMultiSelection = true
                ) as ViewHolderHelper<RecipientItem, *>,

        SelectionFolderItem::class.java.simpleName.withSuffix(multi = false) to
                SingleLineSelectionFolderViewHolderHelper<SelectionFolderItem>(
                    clickDelegate = clickDelegate,
                    viewHolderType = FOLDER_VIEW_HOLDER_TYPE,
                    isMultiSelection = false
                ) as ViewHolderHelper<RecipientItem, *>
    )

    override fun getViewHolderType(item: RecipientItem, isMultiSelection: Boolean): Any =
        when (item) {
            is SelectionPersonItem -> SelectionPersonItem::class.java.simpleName.withSuffix(isMultiSelection)
            is SelectionFolderItem -> SelectionFolderItem::class.java.simpleName.withSuffix(isMultiSelection)
            else -> error("Unexpected model ${item::class.java} for DefaultListItemsCustomization")
        }

    private fun String?.withSuffix(multi: Boolean): String =
        checkNotNull(this) + if (multi) StringUtils.EMPTY else SINGLE_TYPE
}

private const val SINGLE_TYPE = "Single"
private const val PERSON_VIEW_HOLDER_TYPE = 0
private const val FOLDER_VIEW_HOLDER_TYPE = 1