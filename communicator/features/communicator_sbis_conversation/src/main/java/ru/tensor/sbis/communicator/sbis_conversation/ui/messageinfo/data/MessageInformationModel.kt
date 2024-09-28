package ru.tensor.sbis.communicator.sbis_conversation.ui.messageinfo.data

import java.util.*

/**
 * Модель экрана информации о сообщении.
 *
 * @param dialogUuid    идентификатор диалога.
 * @param messageUuid   идентификатор сообщения.
 * @param isGroupDialog true, если сообщение принадлежит групповой переписке.
 * @param isChannel     true, если сообщение принадлежит каналу.
 */
internal data class MessageInformationModel(
    val dialogUuid: UUID,
    val messageUuid: UUID,
    val isGroupDialog: Boolean,
    val isChannel: Boolean
)