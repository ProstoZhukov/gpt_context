package ru.tensor.sbis.communication_decl.selection.recipient.data

import kotlinx.parcelize.Parcelize
import ru.tensor.sbis.communication_decl.selection.SelectionItemId
import java.util.UUID

/**
 * ID получателя.
 *
 * @author vv.chekurda
 */
sealed interface RecipientId : SelectionItemId {
    val uuid: UUID
}

/**
 * ID получателя персоны.
 */
@Parcelize
data class RecipientPersonId(override val uuid: UUID) : RecipientId

/**
 * ID получателя подразделения.
 */
@Parcelize
data class RecipientDepartmentId(override val uuid: UUID): RecipientId