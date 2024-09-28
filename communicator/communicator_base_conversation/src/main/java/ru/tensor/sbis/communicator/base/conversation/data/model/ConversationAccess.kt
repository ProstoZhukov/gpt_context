package ru.tensor.sbis.communicator.base.conversation.data.model

import ru.tensor.sbis.communicator.generated.Permissions

/**
 * Модель разрешений и признаков доступности переписки.
 *
 * @property chatPermissions список разрешений для чата.
 * @property isAvailable true, если переписка доступна.
 * @property canRemoveMessages true, если можно удалять сообщения в переписке.
 * @property canChooseRecipients true, если можно выбирать получателей в переписке.
 *
 * @author vv.chekurda
 */
data class ConversationAccess @JvmOverloads constructor(
    val chatPermissions: Permissions? = null,
    var isAvailable: Boolean = true,
    val canRemoveMessages: Boolean = true,
    val canChooseRecipients: Boolean = true
)
