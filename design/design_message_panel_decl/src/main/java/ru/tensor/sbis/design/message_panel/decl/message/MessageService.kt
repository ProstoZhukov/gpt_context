package ru.tensor.sbis.design.message_panel.decl.message

import ru.tensor.sbis.design.message_panel.decl.MessagePanelUseCase
import java.util.*

/**
 * Интерфейс взаимодействия с сервисом для отправки сообщений
 *
 * @see MessageServiceHelper
 *
 * @author ma.kolpakov
 */
interface MessageService<out MESSAGE, out RESULT> {

    suspend fun send(useCase: MessagePanelUseCase, text: String, recipientsUuid: List<UUID>): RESULT

    suspend fun getMessage(useCase: MessagePanelUseCase, messageUuid: UUID): MESSAGE

    suspend fun getMessageText(messageUuid: UUID, conversationUuid: UUID): String
}
