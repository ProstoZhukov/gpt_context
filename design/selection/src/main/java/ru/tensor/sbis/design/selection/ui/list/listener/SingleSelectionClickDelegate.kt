package ru.tensor.sbis.design.selection.ui.list.listener

import androidx.fragment.app.FragmentActivity
import dagger.Lazy
import ru.tensor.sbis.design.selection.bl.contract.listener.OpenHierarchyListener
import ru.tensor.sbis.design.selection.bl.vm.selection.single.SingleSelectionViewModel
import ru.tensor.sbis.design.selection.ui.contract.listeners.SelectorItemListeners
import ru.tensor.sbis.design.selection.ui.model.HierarchySelectorItemModel
import ru.tensor.sbis.design.selection.ui.model.SelectorItemModel
import ru.tensor.sbis.design.selection.ui.utils.vm.SearchViewModel
import ru.tensor.sbis.toolbox_decl.selection_statistic.SelectionStatisticAction.CLICK_ON_ITEM
import ru.tensor.sbis.design_selection.ui.content.utils.SelectionStatisticUtil
import ru.tensor.sbis.toolbox_decl.selection_statistic.SelectionStatisticEvent
import javax.inject.Provider

/**
 * @author ma.kolpakov
 */
internal class SingleSelectionClickDelegate<DATA : SelectorItemModel>(
    /**
     * Вьюмодель выбора
     */
    private val selectionViewModel: SingleSelectionViewModel<DATA>,

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

    override fun onAddButtonClicked(data: DATA) =
        throw UnsupportedOperationException("Single selection shouldn't contain add button")

    override fun onItemClicked(data: DATA) {
        SelectionStatisticUtil.sendStatistic(
            SelectionStatisticEvent(useCaseValue, CLICK_ON_ITEM.value)
        )
        searchViewModel.finishEditingSearchQuery()
        val hierarchyListener = lazyHierarchyListener.get()
        when {
            hierarchyListener != null -> {
                require(data is HierarchySelectorItemModel) {
                    "Items should be instance of ${HierarchySelectorItemModel::class} " +
                        "to work with OpenHierarchyListener"
                }
                if (data.hasNestedItems)
                    hierarchyListener.invoke(data)
                else
                    selectionViewModel.complete(data)
            }
            else -> selectionViewModel.complete(data)
        }
        searchViewModel.cancelSearch()
        customListeners.itemClickListener?.onClicked(activityProvider.get(), data)
    }

    override fun onItemLongClicked(data: DATA) {
        customListeners.itemLongClickListener?.onClicked(activityProvider.get(), data)
    }
}
