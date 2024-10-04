package ru.tensor.sbis.design.recipient_selection.domain.result_manager

import ru.tensor.sbis.communication_decl.selection.recipient.data.RecipientId
import ru.tensor.sbis.communication_decl.selection.recipient.data.RecipientPersonId
import ru.tensor.sbis.communication_decl.selection.recipient.data.RecipientPreselectedData
import ru.tensor.sbis.communication_decl.selection.recipient.data.RecipientSelectionData
import ru.tensor.sbis.communication_decl.selection.recipient.manager.RecipientSelectionResultManager
import ru.tensor.sbis.communication_decl.selection.recipient.manager.RecipientSelectionResult
import ru.tensor.sbis.communication_decl.selection.result_manager.AbstractSelectionResultManager
import ru.tensor.sbis.communication_decl.selection.result_manager.SelectionResultStatus
import ru.tensor.sbis.communication_decl.selection.recipient.manager.RecipientSelectionResultDelegate
import java.util.UUID

/**
 * Реализация менеджера для работы с результатами компонента выбора получателей.
 *
 * @see RecipientSelectionResultManager
 *
 * @author vv.chekurda
 */
internal class RecipientSelectionResultManagerImpl
    : AbstractSelectionResultManager<RecipientPreselectedData, RecipientSelectionData, RecipientSelectionResult>(),
    RecipientSelectionResultManager,
    RecipientSelectionResultDelegate {

    override val clearedResult: RecipientSelectionResult
        get() = RecipientSelectionResultImpl()

    override fun preselect(personsUuids: List<UUID>?) {
        val data = RecipientPreselectedData(ids = personsUuids?.map(::RecipientPersonId).orEmpty())
        preselect(data)
    }

    override fun preselectIds(ids: List<RecipientId>?) {
        val data = RecipientPreselectedData(ids = ids.orEmpty())
        preselect(data)
    }

    override fun createResult(
        data: RecipientSelectionData?,
        status: SelectionResultStatus,
        requestKey: String,
    ): RecipientSelectionResult =
        RecipientSelectionResultImpl(
            data = data ?: RecipientSelectionData(),
            status = status,
            requestKey = requestKey
        )
}