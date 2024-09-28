package ru.tensor.sbis.message_panel.integration

import ru.tensor.sbis.communicator.generated.DraftMessage
import ru.tensor.sbis.communicator.generated.Message

/**
 * Модель черновика сообщения
 *
 * @property data  данные черновика сообщения
 * @property quote цитата черновика сообщения
 *
 * @author vv.chekurda
 */
data class CommunicatorDraftMessage(
    val data: DraftMessage,
    val quote: Message?
)
