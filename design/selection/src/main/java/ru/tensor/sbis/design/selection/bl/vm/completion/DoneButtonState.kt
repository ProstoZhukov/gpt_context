package ru.tensor.sbis.design.selection.bl.vm.completion

import io.reactivex.Observable

/**
 * Интерфейс бизнес логики управления кнопкой "Применить"
 *
 * @author ma.kolpakov
 */
internal interface DoneButtonState {

    /**
     * Подписка на отображение кнопки завершения
     */
    val doneButtonVisible: Observable<Boolean>

    /**
     * Подписка на активацию кнопки завершения
     */
    val doneButtonEnabled: Observable<Boolean>
}