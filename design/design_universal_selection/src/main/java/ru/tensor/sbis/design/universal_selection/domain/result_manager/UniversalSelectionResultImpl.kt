package ru.tensor.sbis.design.universal_selection.domain.result_manager

import ru.tensor.sbis.communication_decl.selection.result_manager.EMPTY_REQUEST_KEY
import ru.tensor.sbis.communication_decl.selection.result_manager.SelectionResultStatus
import ru.tensor.sbis.communication_decl.selection.universal.data.UniversalSelectionData
import ru.tensor.sbis.communication_decl.selection.universal.manager.UniversalSelectionResult

/**
 * Модель результата компонента универсального выбора.
 *
 * @see UniversalSelectionResult
 *
 * @author vv.chekurda
 */
internal data class UniversalSelectionResultImpl(
    override val data: UniversalSelectionData = UniversalSelectionData(),
    override val status: SelectionResultStatus = SelectionResultStatus.CLEARED,
    override val requestKey: String = EMPTY_REQUEST_KEY
) : UniversalSelectionResult