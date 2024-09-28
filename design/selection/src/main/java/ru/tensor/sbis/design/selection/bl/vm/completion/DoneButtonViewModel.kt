package ru.tensor.sbis.design.selection.bl.vm.completion

import ru.tensor.sbis.design.selection.bl.contract.model.SelectorItem

/**
 * Вию модель для управления кнопкой "Применить"
 *
 * @author ma.kolpakov
 */
internal interface DoneButtonViewModel : DoneButtonState {

    /**
     * Установка информации о том, какие данные были _изначально выбраны_ (при инициализации)
     */
    fun setInitialData(data: List<SelectorItem>)

    /**
     * Установка информации о выбранных _сейчас_ данных
     */
    fun setSelectedData(data: List<SelectorItem>)
}