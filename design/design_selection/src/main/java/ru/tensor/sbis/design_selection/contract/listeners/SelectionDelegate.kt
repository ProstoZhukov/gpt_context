package ru.tensor.sbis.design_selection.contract.listeners

import io.reactivex.Observable
import kotlinx.coroutines.flow.MutableStateFlow
import ru.tensor.sbis.design_selection.contract.data.SelectionItem
import ru.tensor.sbis.design_selection.contract.customization.selected.panel.SelectedData

/**
 * Делегат для управления выбранными элементами в компоненте выбора.
 *
 * @author vv.chekurda
 */
interface SelectionDelegate {

    /**
     * Поставщик делегата [SelectionDelegate].
     */
    interface Provider {

        fun getSelectionDelegate(): SelectionDelegate
    }

    /**
     * Для подписки и установки поискового запроса.
     */
    val searchQuery: MutableStateFlow<String>

    /**
     * Для подписки на получение данных о текущих выбранных элементах.
     */
    val selectedItemsWatcher: Observable<SelectedData<SelectionItem>>

    /**
     * Для подписки на признак наличия элементов доступных для выбора.
     * Если false - в списке отображается заглушка.
     */
    val hasSelectableItems: MutableStateFlow<Boolean>

    /**
     * Отменить выбор элемента.
     */
    fun unselectItem(item: SelectionItem)

    /**
     * Закрыть все папки.
     */
    fun closeAllFolders()

    fun resetScroll()
}