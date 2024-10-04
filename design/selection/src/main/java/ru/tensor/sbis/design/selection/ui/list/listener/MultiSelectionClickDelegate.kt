package ru.tensor.sbis.design.selection.ui.list.listener

import androidx.fragment.app.FragmentActivity
import dagger.Lazy
import ru.tensor.sbis.design.selection.bl.contract.listener.OpenHierarchyListener
import ru.tensor.sbis.design.selection.bl.vm.selection.multi.MultiSelectionViewModel
import ru.tensor.sbis.design.selection.ui.contract.listeners.SelectorItemListeners
import ru.tensor.sbis.design.selection.ui.model.HierarchySelectorItemModel
import ru.tensor.sbis.design.selection.ui.model.SelectorItemModel
import ru.tensor.sbis.design.selection.ui.model.share.TitleItemModel
import ru.tensor.sbis.design.selection.ui.utils.vm.SearchViewModel
import ru.tensor.sbis.toolbox_decl.selection_statistic.SelectionStatisticAction.CLICK_ON_ADD_BUTTON
import ru.tensor.sbis.toolbox_decl.selection_statistic.SelectionStatisticAction.CLICK_ON_ITEM
import ru.tensor.sbis.toolbox_decl.selection_statistic.SelectionStatisticEvent
import ru.tensor.sbis.design_selection.ui.content.utils.SelectionStatisticUtil
import javax.inject.Provider

/**
 * @author ma.kolpakov
 */
internal class MultiSelectionClickDelegate<DATA : SelectorItemModel>(
    /**
     * Вьюмодель выбора
     */
    private val selectionViewModel: MultiSelectionViewModel<DATA>,

    /**
     * Вьюмодель поиска
     */
    private val searchViewModel: SearchViewModel,

    /**
     * Обработчик переходов по иерархии. Задавать только при работе с иерархичными данными
     */
    private val lazyHierarchyListener: Lazy<OpenHierarchyListener<DATA>?>,

    /**
     * Набор кастомных слушателей кликов
     */
    private val customListeners: SelectorItemListeners<SelectorItemModel, FragmentActivity>,

    /**
     * Провайдер активити для передачи в кастомные листенеры
     */
    private val activityProvider: Provider<FragmentActivity>,

    /**
     * Название usecase текущего выбора.
     */
    private val useCaseValue: String
) : SelectionClickDelegate<DATA> {

    override fun onAddButtonClicked(data: DATA) {
        SelectionStatisticUtil.sendStatistic(
            SelectionStatisticEvent(useCaseValue, CLICK_ON_ADD_BUTTON.value)
        )
        searchViewModel.clearSearch()
        selectionViewModel.toggleSelection(data)
        customListeners.rightActionListener?.onClicked(activityProvider.get(), data)
    }

    override fun onItemClicked(data: DATA) {
        SelectionStatisticUtil.sendStatistic(
            SelectionStatisticEvent(useCaseValue, CLICK_ON_ITEM.value)
        )
        val hierarchyListener = lazyHierarchyListener.get()
        when {
            data.meta.selected -> selectionViewModel.toggleSelection(data)
            hierarchyListener != null -> {
                require(data is HierarchySelectorItemModel) {
                    "Items should be instance of ${HierarchySelectorItemModel::class} " +
                        "to work with OpenHierarchyListener"
                }
                if (data.hasNestedItems)
                    hierarchyListener.invoke(data)
                else
                    selectionViewModel.setSelected(data)
            }
            data is TitleItemModel -> return
            else -> selectionViewModel.setSelected(data)
        }
        searchViewModel.clearSearch()
        customListeners.itemClickListener?.onClicked(activityProvider.get(), data)
    }

    override fun onItemLongClicked(data: DATA) {
        customListeners.itemLongClickListener?.onClicked(activityProvider.get(), data)
    }
}
