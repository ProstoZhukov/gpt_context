package ru.tensor.sbis.design_selection.ui.main.vm.contract.live_data

import io.reactivex.Observable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import ru.tensor.sbis.design_selection.contract.data.SelectionItem
import ru.tensor.sbis.design_selection.domain.completion.CompleteEvent
import ru.tensor.sbis.design_selection.contract.customization.selected.panel.SelectedData

/**
 * Интерфейс состояния компонента выбора.
 *
 * @author vv.chekuda
 */
internal interface SelectionLiveData<ITEM : SelectionItem> {

    /**
     * Для подписки на результат компонента выбора.
     */
    val result: Observable<CompleteEvent<ITEM>>

    /**
     * Для подписки на получение данных о текущих выбранных элементах.
     */
    val selectedDataObservable: Observable<SelectedData<ITEM>>

    /**
     * Данные о текущих выбранных элементах.
     */
    val selectedData: SelectedData<ITEM>

    /**
     * Для подписки на сообщение об ошибке.
     */
    val errorMessage: Observable<String>

    /**
     * Для подписки и установки поискового запроса.
     */
    val searchQuery: MutableStateFlow<String>

    /**
     * Для подписки на признак наличия элементов доступных для выбора.
     * Если false - в списке отображается заглушка.
     */
    val hasSelectableItems: MutableStateFlow<Boolean>

    val resetScroll: Flow<Unit>
}