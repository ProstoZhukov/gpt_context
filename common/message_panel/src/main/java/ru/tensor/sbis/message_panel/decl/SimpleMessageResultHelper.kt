package ru.tensor.sbis.message_panel.decl

import ru.tensor.sbis.persons.IPersonModel
import java.util.UUID

/**
 * Примитивная реализация для [MessageResultHelper], которую можно использовать, например, если механика отправки не
 * требуется.
 * Известные сценарии: ДЗЗ, Отзывы в SabyGet
 *
 * @see SimpleMessageServiceWrapper
 *
 * @author vv.chekurda
 */
open class SimpleMessageResultHelper<in MESSAGE_RESULT, in MESSAGE_SENT_RESULT> :
    MessageResultHelper<MESSAGE_RESULT, MESSAGE_SENT_RESULT> {

    override fun isResultError(message: MESSAGE_RESULT): Boolean =
        false

    override fun isSentResultError(message: MESSAGE_SENT_RESULT): Boolean =
        false

    override fun getSentMessageUuid(message: MESSAGE_SENT_RESULT): UUID? =
        null

    override fun getResultError(message: MESSAGE_RESULT): String =
        "Something went wrong while loading $message. Override MessageResultHelper.getResultError() to return specific message"

    override fun getSentResultError(message: MESSAGE_SENT_RESULT): String =
        "Something went wrong while sending $message. Override MessageResultHelper.getSentResultError() to return specific message"

    override fun getSender(message: MESSAGE_RESULT): IPersonModel =
        error("Unable to get sender from $message. Override MessageResultHelper.getSender() to return sender from specific data")
}