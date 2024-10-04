package ru.tensor.sbis.design_selection.contract.customization.selection

import androidx.fragment.app.FragmentActivity
import org.apache.commons.lang3.StringUtils
import ru.tensor.sbis.design.profile_decl.person.PersonClickListener
import ru.tensor.sbis.design_selection.contract.customization.selection.folder.SelectionFolderViewHolderHelper
import ru.tensor.sbis.design_selection.contract.customization.selection.person.SelectionPersonViewHolderHelper
import ru.tensor.sbis.design_selection.contract.data.SelectionFolderItem
import ru.tensor.sbis.design_selection.contract.data.SelectionItem
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
class DefaultSelectableItemsCustomization(
    private val personClickListener: PersonClickListener? = null
) : SelectableItemsCustomization<SelectionItem> {

    @Suppress("UNCHECKED_CAST")
    override fun createViewHolderHelpers(
        clickDelegate: SelectionClickDelegate,
        activityProvider: Provider<FragmentActivity>
    ): Map<Any, ViewHolderHelper<SelectionItem, *>> = mapOf(
        SelectionPersonItem::class.java.simpleName.withSuffix(multi = true) to
            SelectionPersonViewHolderHelper(
                clickDelegate = clickDelegate,
                viewHolderType = PERSON_VIEW_HOLDER_TYPE,
                isMultiSelection = true,
                personClickListener = personClickListener
            ) as ViewHolderHelper<SelectionItem, *>,

        SelectionPersonItem::class.java.simpleName.withSuffix(multi = false) to
            SelectionPersonViewHolderHelper(
                clickDelegate = clickDelegate,
                viewHolderType = PERSON_VIEW_HOLDER_TYPE,
                isMultiSelection = false,
                personClickListener = personClickListener
            ) as ViewHolderHelper<SelectionItem, *>,

        SelectionFolderItem::class.java.simpleName.withSuffix(multi = true) to
            SelectionFolderViewHolderHelper<SelectionFolderItem>(
                clickDelegate = clickDelegate,
                viewHolderType = FOLDER_VIEW_HOLDER_TYPE,
                isMultiSelection = true
            ) as ViewHolderHelper<SelectionItem, *>,

        SelectionFolderItem::class.java.simpleName.withSuffix(multi = false) to
            SelectionFolderViewHolderHelper<SelectionFolderItem>(
                clickDelegate = clickDelegate,
                viewHolderType = FOLDER_VIEW_HOLDER_TYPE,
                isMultiSelection = false
            ) as ViewHolderHelper<SelectionItem, *>
    )

    override fun getViewHolderType(item: SelectionItem, isMultiSelection: Boolean): Any =
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