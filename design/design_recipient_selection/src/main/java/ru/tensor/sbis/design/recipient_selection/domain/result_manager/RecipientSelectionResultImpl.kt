package ru.tensor.sbis.design.recipient_selection.domain.result_manager

import ru.tensor.sbis.communication_decl.selection.recipient.data.RecipientSelectionData
import ru.tensor.sbis.communication_decl.selection.recipient.manager.RecipientSelectionResult
import ru.tensor.sbis.communication_decl.selection.result_manager.EMPTY_REQUEST_KEY
import ru.tensor.sbis.communication_decl.selection.result_manager.SelectionResultStatus

/**
 * Модель результата компонента выбора получателей.
 *
 * @see RecipientSelectionResult
 *
 * @author vv.chekurda
 */
internal data class RecipientSelectionResultImpl(
    override val data: RecipientSelectionData = RecipientSelectionData(),
    override val status: SelectionResultStatus = SelectionResultStatus.CLEARED,
    override val requestKey: String = EMPTY_REQUEST_KEY
) : RecipientSelectionResult