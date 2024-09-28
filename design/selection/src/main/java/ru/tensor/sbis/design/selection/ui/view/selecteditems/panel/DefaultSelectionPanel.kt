package ru.tensor.sbis.design.selection.ui.view.selecteditems.panel

import ru.tensor.sbis.design.selection.bl.vm.selection.multi.MultiSelectionViewModel
import ru.tensor.sbis.design.selection.ui.model.SelectorItemModel
import ru.tensor.sbis.design.selection.ui.view.selecteditems.model.SelectedTextItem
import ru.tensor.sbis.design.selection.ui.view.selecteditems.viewholder.view.SelectedItemsContainerView

/**
 * Реализация [AbstractSelectionPanel] по умолчанию, которая отображает элементы строками [SelectedTextItem]
 *
 * @author ma.kolpakov
 */
internal class DefaultSelectionPanel(
    private val containerView: SelectedItemsContainerView,
    private val selectionVm: MultiSelectionViewModel<SelectorItemModel>
) : AbstractSelectionPanel<SelectorItemModel> {

    override fun setSelectedItems(list: List<SelectorItemModel>) {
        containerView.setItems(
            list.map {
                SelectedTextItem(it.id, it.title).apply {
                    onClick = { selectionVm.removeSelection(it) }
                }
            }
        )
    }
}