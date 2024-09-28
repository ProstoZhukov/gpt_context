package ru.tensor.sbis.design.selection.bl.vm.selection.multi

import io.reactivex.Observable
import ru.tensor.sbis.design.selection.bl.contract.listener.ClickHandleStrategy
import ru.tensor.sbis.design.selection.bl.contract.model.SelectorItem
import ru.tensor.sbis.design.selection.bl.vm.completion.DoneButtonState
import ru.tensor.sbis.design.selection.bl.vm.selection.SelectionViewModel

/**
 * View model области выбранных элементов в компоненте выбора
 *
 * @author ma.kolpakov
 */
internal interface MultiSelectionViewModel<DATA : SelectorItem> :
    SelectionViewModel<List<DATA>>,
    DoneButtonState {

    /**
     * Подписка на достижение максимального количества элементов
     */
    val limitExceed: Observable<Int>

    /**
     * Добавление элемента в список выбранных либо завершение выбора, если:
     * - ещё не было изменений в списке выбранных
     * - элемент должен быть обработан по правилу [ClickHandleStrategy.COMPLETE_SELECTION]
     */
    fun setSelected(data: DATA)

    /**
     * Удаление элемента из списка выбранных
     */
    fun removeSelection(data: DATA)

    /**
     * Добавление или удаление элемента из списка выбранных
     */
    fun toggleSelection(data: DATA)

    /**
     * Завершение работы и получение результата выбора
     *
     * @see doneButtonVisible
     */
    fun complete()
}