package ru.tensor.sbis.communication_decl.recipient_selection

import io.reactivex.Observable
import java.util.UUID

/**
 * Контракт результата выбора сотрудников
 */
interface RecipientSelectionResultManagerContract {
    /** @SelfDocumented */
    fun clearSelectionResult()

    /** @SelfDocumented */
    fun isIncompleteRecipients(): Boolean

    /** @SelfDocumented */
    fun addRecipient(recipientUuid: UUID)

    /** @SelfDocumented */
    fun putNewDataAsUuidList(recipientUuids: List<UUID>?)

    /** @SelfDocumented */
    fun putResultCanceled()

    /** @SelfDocumented */
    fun selectionResult(): RecipientSelectionResultDataContract

    /** @SelfDocumented */
    fun selectionDoneObservable(): Observable<RecipientSelectionResultDataContract>
}