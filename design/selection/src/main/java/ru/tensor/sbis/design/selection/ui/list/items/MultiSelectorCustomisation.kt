package ru.tensor.sbis.design.selection.ui.list.items

import ru.tensor.sbis.design.selection.bl.vm.selection.multi.MultiSelectionViewModel
import ru.tensor.sbis.design.selection.ui.model.SelectorItemModel
import ru.tensor.sbis.design.selection.ui.view.selecteditems.panel.AbstractSelectionPanel
import ru.tensor.sbis.design.selection.ui.view.selecteditems.viewholder.view.SelectedItemsContainerView

/**
 * Расширенный интерфейс [SelectorCustomisation] для работы со множественным выбором
 *
 * @author ma.kolpakov
 */
internal interface MultiSelectorCustomisation : SelectorCustomisation {

    /**
     * Метод для создания реализации [AbstractSelectionPanel], которая адаптирует [SelectedItemsContainerView] для
     * работы с моделями предметной области
     */
    fun createSelectionPanel(
        selectionView: SelectedItemsContainerView,
        selectionVm: MultiSelectionViewModel<SelectorItemModel>
    ): AbstractSelectionPanel<SelectorItemModel>
}