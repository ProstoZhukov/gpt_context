package ru.tensor.sbis.design_selection.ui.main.vm.contract

import io.reactivex.Observable
import ru.tensor.sbis.design_selection.contract.data.SelectionItem
import ru.tensor.sbis.design_selection.contract.customization.selected.panel.SelectedData
import ru.tensor.sbis.design_selection.contract.customization.selected.panel.listener.SelectedItemClickListener
import ru.tensor.sbis.design_selection.contract.listeners.SelectionResultListener.SelectionComponentResult
import ru.tensor.sbis.design_selection.ui.main.vm.contract.live_data.SelectionLiveData

/**
 * Делегат области контента компонента выбора.
 *
 * @author vv.chekuda
 */
internal interface SelectionContentDelegate<ITEM : SelectionItem> :
    SelectionLiveData<ITEM> {

    /**
     * Слушатель кликов по выбранным элементам.
     */
    val selectedItemsClickListener: SelectedItemClickListener<ITEM>

    /**
     * Для подписки на нажатие на кнопку подтверждения выбора.
     */
    val onDoneButtonClickedObservable: Observable<Unit>

    /**
     * Для подписка на очистку всех выбранных.
     */
    val clearSelectedObservable: Observable<Unit>

    /**
     * Установить данные о выбранных элементах.
     */
    fun setSelectedData(selectedData: SelectedData<ITEM>)

    /**
     * Подтвердить выбор элементов.
     *
     * @param result результат выбора.
     */
    fun complete(result: SelectionComponentResult<ITEM>)

    /**
     * Обработать ошибку.
     *
     * @param errorMessage текст ошибки.
     */
    fun onError(errorMessage: String)
}