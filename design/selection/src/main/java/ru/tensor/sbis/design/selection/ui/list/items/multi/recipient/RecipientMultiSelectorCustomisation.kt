package ru.tensor.sbis.design.selection.ui.list.items.multi.recipient

import androidx.fragment.app.FragmentActivity
import ru.tensor.sbis.design.selection.bl.vm.selection.multi.MultiSelectionViewModel
import ru.tensor.sbis.design.selection.ui.contract.listeners.ItemClickListener
import ru.tensor.sbis.design.selection.ui.list.items.MultiSelectorCustomisation
import ru.tensor.sbis.design.selection.ui.list.items.multi.share.TitleMultiSelectorViewHolderHelper
import ru.tensor.sbis.design.selection.ui.list.items.multi.share.dialog.DialogMultiSelectorViewHolderHelper
import ru.tensor.sbis.design.selection.ui.list.listener.SelectionClickDelegate
import ru.tensor.sbis.design.selection.ui.model.SelectorItemModel
import ru.tensor.sbis.design.selection.ui.model.recipient.ContractorSelectorItemModel
import ru.tensor.sbis.design.selection.ui.model.recipient.DepartmentSelectorItemModel
import ru.tensor.sbis.design.selection.ui.model.recipient.GroupSelectorItemModel
import ru.tensor.sbis.design.selection.ui.model.recipient.PersonSelectorItemModel
import ru.tensor.sbis.design.selection.ui.model.share.TitleItemModel
import ru.tensor.sbis.design.selection.ui.model.share.dialog.DialogSelectorItemModel
import ru.tensor.sbis.design.selection.ui.view.selecteditems.panel.AbstractSelectionPanel
import ru.tensor.sbis.design.selection.ui.view.selecteditems.panel.RecipientSelectionPanel
import ru.tensor.sbis.design.selection.ui.view.selecteditems.viewholder.view.SelectedItemsContainerView
import ru.tensor.sbis.list.view.item.ViewHolderHelper
import javax.inject.Provider

/**
 * Переопределение внешнего вида по стандарту множественного выбора получателей
 *
 * TODO: 11/2/2020 https://online.sbis.ru/opendoc.html?guid=1b992aae-cd3c-44b1-ab83-6c1cf20247f3
 *
 * @author ma.kolpakov
 */
@Suppress("UNCHECKED_CAST" /* безопасность типов обеспечена в момент создания фрагмента */)
internal class RecipientMultiSelectorCustomisation : MultiSelectorCustomisation {

    override fun createViewHolderHelpers(
        clickDelegate: SelectionClickDelegate<SelectorItemModel>,
        iconClickListener: ItemClickListener<SelectorItemModel, FragmentActivity>?,
        activityProvider: Provider<FragmentActivity>?,
    ): Map<Any, ViewHolderHelper<SelectorItemModel, *>> {

        return mapOf(
            PersonSelectorItemModel::class.java to PersonMultiSelectorViewHolderHelper(
                clickDelegate,
                iconClickListener,
                activityProvider
            ) as ViewHolderHelper<SelectorItemModel, *>,

            GroupSelectorItemModel::class.java to GroupMultiSelectorViewHolderHelper(
                clickDelegate,
                iconClickListener,
                activityProvider
            ) as ViewHolderHelper<SelectorItemModel, *>,

            DepartmentSelectorItemModel::class.java to DepartmentMultiSelectorViewHolderHelper(
                clickDelegate,
                iconClickListener,
                activityProvider
            ) as ViewHolderHelper<SelectorItemModel, *>,

            ContractorSelectorItemModel::class.java to ContractorMultiSelectorViewHolderHelper(
                clickDelegate
            ) as ViewHolderHelper<SelectorItemModel, *>,

            TitleItemModel::class.java to TitleMultiSelectorViewHolderHelper()
                as ViewHolderHelper<SelectorItemModel, *>,

            DialogSelectorItemModel::class.java to DialogMultiSelectorViewHolderHelper()
                as ViewHolderHelper<SelectorItemModel, *>,
        )
    }

    override fun getViewHolderType(model: SelectorItemModel): Any = when (model) {
        is PersonSelectorItemModel -> PersonSelectorItemModel::class.java
        is GroupSelectorItemModel -> GroupSelectorItemModel::class.java
        is DepartmentSelectorItemModel -> DepartmentSelectorItemModel::class.java
        is ContractorSelectorItemModel -> ContractorSelectorItemModel::class.java
        is TitleItemModel -> TitleItemModel::class.java
        is DialogSelectorItemModel -> DialogSelectorItemModel::class.java
        else -> error(
            "Unexpected model ${model::class.java} for RecipientMultiSelectorCustomisation"
        )
    }

    override fun createSelectionPanel(
        selectionView: SelectedItemsContainerView,
        selectionVm: MultiSelectionViewModel<SelectorItemModel>
    ): AbstractSelectionPanel<SelectorItemModel> =
        // Затиранние типа безопасно т.к. другие типы не попадают в список выбранных по механике работы
        RecipientSelectionPanel(selectionView, selectionVm) as AbstractSelectionPanel<SelectorItemModel>
}