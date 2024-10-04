package ru.tensor.sbis.design_selection.ui.main.vm.contract

import ru.tensor.sbis.design_selection.contract.data.SelectionItem
import ru.tensor.sbis.design_selection.contract.header_button.HeaderButtonStrategy
import ru.tensor.sbis.design_selection.contract.listeners.SelectionDelegate
import ru.tensor.sbis.design_selection.ui.main.router.SelectionRouter
import ru.tensor.sbis.design_selection.ui.main.vm.contract.live_data.DoneButtonLiveData
import ru.tensor.sbis.design_selection.ui.main.vm.contract.live_data.SelectionLiveData
import ru.tensor.sbis.design_selection.ui.main.vm.contract.live_data.SelectionHeaderButtonLiveData

/**
 * Вью-модель хост-фрагмента компонента выбора.
 *
 * @author vv.chekurda
 */
internal interface SelectionHostViewModel<ITEM : SelectionItem> :
    SelectionLiveData<ITEM>,
    SelectionHeaderButtonLiveData,
    DoneButtonLiveData,
    SelectionDelegate {

    /**
     * Обработать нажатие на кнопку подтвреждения выбора.
     */
    fun onDoneButtonClicked()

    /**
     * Обработать нажатие на головную кнопку.
     *
     * @param strategy стратегия кнопки.
     */
    fun onHeaderButtonClicked(strategy: HeaderButtonStrategy)

    /**
     * Закрыть компонент выбора.
     */
    fun cancel()

    /**
     * Установить роутер для навигации.
     */
    fun setRouter(router: SelectionRouter?)

    /**
     * Обработать нажатие системной кнопки назад.
     *
     * @return true, если обработка была произведена.
     */
    fun onBackPressed(): Boolean
}