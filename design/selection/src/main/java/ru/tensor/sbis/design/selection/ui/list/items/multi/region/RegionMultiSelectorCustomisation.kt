package ru.tensor.sbis.design.selection.ui.list.items.multi.region

import androidx.fragment.app.FragmentActivity
import ru.tensor.sbis.design.selection.bl.contract.listener.ClickHandleStrategy
import ru.tensor.sbis.design.selection.bl.vm.selection.multi.MultiSelectionViewModel
import ru.tensor.sbis.design.selection.ui.contract.listeners.ItemClickListener
import ru.tensor.sbis.design.selection.ui.list.MultiSelectorChooseAllViewHolderHelper
import ru.tensor.sbis.design.selection.ui.list.items.MultiSelectorCustomisation
import ru.tensor.sbis.design.selection.ui.list.listener.SelectionClickDelegate
import ru.tensor.sbis.design.selection.ui.model.SelectorItemModel
import ru.tensor.sbis.design.selection.ui.view.selecteditems.panel.AbstractSelectionPanel
import ru.tensor.sbis.design.selection.ui.view.selecteditems.panel.DefaultSelectionPanel
import ru.tensor.sbis.design.selection.ui.view.selecteditems.viewholder.view.SelectedItemsContainerView
import ru.tensor.sbis.list.view.item.ViewHolderHelper
import javax.inject.Provider

/**
 * Переопределение внешнего вида по стандарту множественного выбора регионов
 *
 * @author ma.kolpakov
 */
@Suppress("UNCHECKED_CAST" /* безопасность типов обеспечена в момент создания фрагмента */)
internal class RegionMultiSelectorCustomisation : MultiSelectorCustomisation {

    override fun createViewHolderHelpers(
        clickDelegate: SelectionClickDelegate<SelectorItemModel>,
        iconClickListener: ItemClickListener<SelectorItemModel, FragmentActivity>?,
        activityProvider: Provider<FragmentActivity>?,
    ): Map<Any, ViewHolderHelper<SelectorItemModel, *>> {
        return mapOf(
            ClickHandleStrategy.DEFAULT to
                RegionMultiSelectorViewHolderHelper(clickDelegate) as ViewHolderHelper<SelectorItemModel, *>,
            ClickHandleStrategy.COMPLETE_SELECTION to
                MultiSelectorChooseAllViewHolderHelper()
        )
    }

    override fun getViewHolderType(model: SelectorItemModel): Any =
        model.meta.handleStrategy

    override fun createSelectionPanel(
        selectionView: SelectedItemsContainerView,
        selectionVm: MultiSelectionViewModel<SelectorItemModel>
    ): AbstractSelectionPanel<SelectorItemModel> =
        DefaultSelectionPanel(selectionView, selectionVm)
}