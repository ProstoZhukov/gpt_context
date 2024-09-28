package ru.tensor.sbis.design.selection.bl.vm.selection

import ru.tensor.sbis.design.selection.bl.contract.model.SelectorItem

/**
 * Набор состояний завершения работы с компонентом выбора
 *
 * @author ma.kolpakov
 */
internal sealed class CompleteEvent<out DATA : SelectorItem>

/**
 * Отмена выбора
 */
internal object CancelSelection : CompleteEvent<Nothing>()

/**
 * Публикация выбранных элементов
 */
internal object ApplySelection : CompleteEvent<Nothing>()

/**
 * Выбор определённого элемента [data] с переопределением существующего выбора
 */
internal data class SetSelection<DATA : SelectorItem>(val data: DATA) : CompleteEvent<DATA>()