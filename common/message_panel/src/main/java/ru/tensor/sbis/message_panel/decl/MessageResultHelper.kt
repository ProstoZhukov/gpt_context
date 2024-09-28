package ru.tensor.sbis.message_panel.decl

import androidx.annotation.WorkerThread
import ru.tensor.sbis.persons.IPersonModel
import java.util.UUID

/**
 * Интерфейс объекта, для получения информации о прикладном контейнере сообщения [MESSAGE_RESULT] и результате отправки
 * [MESSAGE_SENT_RESULT]
 *
 * @author vv.chekurda
 */
@WorkerThread
interface MessageResultHelper<in MESSAGE_RESULT, in MESSAGE_SENT_RESULT> {

    /**
     * Возвращает `true`, если [message] является контейнером с ошибкой
     */
    fun isResultError(message: MESSAGE_RESULT): Boolean

    /**
     * Возвращает `true`, если результат отправки [message] - ошибка
     */
    fun isSentResultError(message: MESSAGE_SENT_RESULT): Boolean

    /**
     * Возвращает идентификатор отправленного сообщения.
     */
    fun getSentMessageUuid(message: MESSAGE_SENT_RESULT): UUID?

    /**
     * Возвращает сообщение об ошибке из контейнера [message]
     */
    fun getResultError(message: MESSAGE_RESULT): String

    /**
     * Возвращает сообщение об ошибке отправки из [message]
     */
    fun getSentResultError(message: MESSAGE_SENT_RESULT): String

    /**
     * Возвращает отправителя сообщения из контейнера [message]
     */
    fun getSender(message: MESSAGE_RESULT): IPersonModel
}