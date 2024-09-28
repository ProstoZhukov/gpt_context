package ru.tensor.sbis.design.universal_selection.domain.result_manager

import ru.tensor.sbis.communication_decl.selection.result_manager.AbstractSelectionResultManager
import ru.tensor.sbis.communication_decl.selection.result_manager.SelectionResultStatus
import ru.tensor.sbis.communication_decl.selection.universal.data.UniversalPreselectedData
import ru.tensor.sbis.communication_decl.selection.universal.data.UniversalSelectionData
import ru.tensor.sbis.communication_decl.selection.universal.manager.UniversalSelectionResult
import ru.tensor.sbis.communication_decl.selection.universal.manager.UniversalSelectionResultDelegate
import ru.tensor.sbis.communication_decl.selection.universal.manager.UniversalSelectionResultManager

/**
 * Реализация менеджера для работы с результатами компонента универсального выбора.
 *
 * @see UniversalSelectionResultManager
 *
 * @author vv.chekurda
 */
internal class UniversalSelectionResultManagerImpl :
    AbstractSelectionResultManager<UniversalPreselectedData, UniversalSelectionData, UniversalSelectionResult>(),
    UniversalSelectionResultManager,
    UniversalSelectionResultDelegate {

    override val clearedResult: UniversalSelectionResult
        get() = UniversalSelectionResultImpl()

    override fun createResult(
        data: UniversalSelectionData?,
        status: SelectionResultStatus,
        requestKey: String,
    ): UniversalSelectionResult =
        UniversalSelectionResultImpl(
            data = data ?: UniversalSelectionData(),
            status = status,
            requestKey = requestKey
        )
}