package ru.tensor.sbis.design_selection.domain.completion

import ru.tensor.sbis.design_selection.contract.data.SelectionItem
import ru.tensor.sbis.design_selection.contract.listeners.SelectionResultListener.SelectionComponentResult

/**
 * Набор состояний завершения работы с компонентом выбора.
 *
 * @author vv.chekurda
 */
internal sealed class CompleteEvent<out ITEM : SelectionItem>

/**
 * Отмена выбора.
 */
internal object CancelSelection : CompleteEvent<Nothing>()

/**
 * Публикация выбранных элементов.
 */
internal data class ApplySelection<ITEM : SelectionItem>(
    val result: SelectionComponentResult<ITEM>
) : CompleteEvent<ITEM>()