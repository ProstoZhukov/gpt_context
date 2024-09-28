package ru.tensor.sbis.design_selection.ui.main.vm.contract.live_data

import io.reactivex.Observable
import ru.tensor.sbis.design_selection.contract.data.SelectionItem
import ru.tensor.sbis.design_selection.contract.customization.selected.panel.SelectedData

/**
 * Интерфейс состояния кнопки подтверждения выбора.
 *
 * @author vv.chekurda
 */
internal interface DoneButtonLiveData {

    /**
     * Подписка на отображение кнопки завершения
     */
    val doneButtonVisible: Observable<Boolean>
}

/**
 * Делегат для управления кнопкой подтверждения выбора.
 */
internal interface DoneButtonDelegate<ITEM : SelectionItem> : DoneButtonLiveData {

    /**
     * Установка информации о том, какие данные были изначально выбраны (при инициализации).
     */
    fun setInitialData(data: SelectedData<ITEM>)

    /**
     * Установка информации о выбранных сейчас данных.
     */
    fun setSelectedData(data: SelectedData<ITEM>)
}