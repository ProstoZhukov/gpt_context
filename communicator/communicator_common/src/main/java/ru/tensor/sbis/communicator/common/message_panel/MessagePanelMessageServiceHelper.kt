package ru.tensor.sbis.communicator.common.message_panel

import ru.tensor.sbis.common.generated.ErrorCode
import ru.tensor.sbis.communicator.generated.MessageResult
import ru.tensor.sbis.communicator.generated.SendMessageResult
import ru.tensor.sbis.design.message_panel.decl.message.MessageServiceHelper

/**
 * TODO: 27.06.2022 Добавить документацию
 *
 * @author vv.chekurda
 */
internal class MessagePanelMessageServiceHelper : MessageServiceHelper<MessageResult, SendMessageResult> {

    override suspend fun isMessageError(message: MessageResult) =
        message.status.errorCode != ErrorCode.SUCCESS

    override suspend fun isResultError(message: SendMessageResult) =
        message.status.errorCode != ErrorCode.SUCCESS

    override suspend fun getMessageError(message: MessageResult) =
        message.status.errorMessage

    override suspend fun getResultError(message: SendMessageResult) =
        message.status.errorMessage
}