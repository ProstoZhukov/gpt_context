package ru.tensor.sbis.design.selection.ui.list.items.single.recipient

import androidx.fragment.app.FragmentActivity
import ru.tensor.sbis.design.selection.ui.contract.listeners.ItemClickListener
import ru.tensor.sbis.design.selection.ui.list.items.SelectorCustomisation
import ru.tensor.sbis.design.selection.ui.list.listener.SelectionClickDelegate
import ru.tensor.sbis.design.selection.ui.model.SelectorItemModel
import ru.tensor.sbis.design.selection.ui.model.recipient.ContractorSelectorItemModel
import ru.tensor.sbis.design.selection.ui.model.recipient.DepartmentSelectorItemModel
import ru.tensor.sbis.design.selection.ui.model.recipient.GroupSelectorItemModel
import ru.tensor.sbis.design.selection.ui.model.recipient.PersonSelectorItemModel
import ru.tensor.sbis.list.view.item.ViewHolderHelper
import javax.inject.Provider

/**
 * Переопределение внешнего вида по стандарту одиночного выбора получателей
 *
 * @author ma.kolpakov
 */
@Suppress("UNCHECKED_CAST" /* безопасность типов обеспечена в момент создания фрагмента */)
internal class RecipientSingleSelectorCustomisation : SelectorCustomisation {

    override fun createViewHolderHelpers(
        clickDelegate: SelectionClickDelegate<SelectorItemModel>,
        iconClickListener: ItemClickListener<SelectorItemModel, FragmentActivity>?,
        activityProvider: Provider<FragmentActivity>?,
    ): Map<Any, ViewHolderHelper<SelectorItemModel, *>> {
        return mapOf(
            PersonSelectorItemModel::class.java to PersonSingleSelectorViewHolderHelper(
                iconClickListener,
                activityProvider
            ) as ViewHolderHelper<SelectorItemModel, *>,

            GroupSelectorItemModel::class.java to GroupSingleSelectorViewHolderHelper(
                iconClickListener,
                activityProvider
            ) as ViewHolderHelper<SelectorItemModel, *>,

            DepartmentSelectorItemModel::class.java to DepartmentSingleSelectorViewHolderHelper(
                iconClickListener,
                activityProvider
            ) as ViewHolderHelper<SelectorItemModel, *>,

            ContractorSelectorItemModel::class.java to ContractorSingleSelectorViewHolderHelper()
                as ViewHolderHelper<SelectorItemModel, *>
        )
    }

    override fun getViewHolderType(model: SelectorItemModel): Any = when (model) {
        is PersonSelectorItemModel -> PersonSelectorItemModel::class.java
        is GroupSelectorItemModel -> GroupSelectorItemModel::class.java
        is DepartmentSelectorItemModel -> DepartmentSelectorItemModel::class.java
        is ContractorSelectorItemModel -> ContractorSelectorItemModel::class.java
        else -> error(
            "Unexpected model ${model::class.java} for RecipientMultiSelectorCustomisation"
        )
    }
}