package ru.tensor.sbis.communicator.common.message_panel

import ru.tensor.sbis.common.generated.CommandStatus
import ru.tensor.sbis.common.generated.ErrorCode
import ru.tensor.sbis.common.generated.QueryDirection
import ru.tensor.sbis.common.util.asArrayList
import ru.tensor.sbis.communicator.common.CommunicatorCommonPlugin
import ru.tensor.sbis.communicator.generated.DataRefreshedMessageControllerCallback
import ru.tensor.sbis.communicator.generated.MessageFilter
import ru.tensor.sbis.communicator.generated.MessageResult
import ru.tensor.sbis.communicator.generated.SendMessageResult
import ru.tensor.sbis.design.message_panel.decl.MessagePanelUseCase
import ru.tensor.sbis.design.message_panel.decl.message.MessageService
import ru.tensor.sbis.design.message_panel.domain.*
import java.util.*
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * TODO: 27.06.2022 Добавить документацию
 *
 * @author vv.chekurda
 */
internal class MessagePanelMessageService : MessageService<MessageResult, SendMessageResult> {

    val controller by lazy { CommunicatorCommonPlugin.messageControllerProvider.getMessageController().get() }

    override suspend fun send(
        useCase: MessagePanelUseCase,
        text: String,
        recipientsUuid: List<UUID>
    ): SendMessageResult = with(useCase as AbstractMessagePanelUseCase) {
        when (this) {
            is SendMessageUseCase -> controller.enqueueMessage2(conversationUuid, folderUuid, text, documentUuid, recipientsUuid.asArrayList(), null, null, null, null, null)
            is EditMessageUseCase -> {
                val editResult = controller.editMessage(editingMessageUuid, text)
                SendMessageResult(conversationUuid, editingMessageUuid, editResult.status)
            }
            is QuoteMessageUseCase -> {
                controller.enqueueMessage2(conversationUuid, folderUuid, text, documentUuid, recipientsUuid.asArrayList(), null, null, quotingMessageUuid, null, null)
            }
            is ShareMessageUseCase -> TODO()
            EmptyMessageUseCase -> error("Unexpected method call")
        }
    }

    override suspend fun getMessage(
        useCase: MessagePanelUseCase,
        messageUuid: UUID
    ): MessageResult = with(useCase) {

        fun readMessage(): MessageResult {
            val message = controller.read(messageUuid)?.let { controller.deserializeFromBinaryToMessage(it).data }
            return MessageResult(message, CommandStatus())
        }

        suspend fun readMessageWithSync(): MessageResult = suspendCoroutine { continuation ->
            val filter = MessageFilter(conversationUuid).apply {
                direction = QueryDirection.TO_NEWER
                fromUuid = messageUuid
                count = 1
                includeAnchor = true
                requestId = "read($messageUuid)"
            }
            val refreshCallback = object : DataRefreshedMessageControllerCallback() {
                val subscription = controller.dataRefreshed().subscribe(this)
                override fun onEvent(param: HashMap<String, String>) {
                    // дожидаемся обработки собственного запроса и перечитываем данные из кэша
                    if (param[REQUEST_ID] == filter.requestId) {
                        val messageResult = readMessage()
                        if (messageResult.data == null) {
                            messageResult.status.apply {
                                errorMessage = "Unable to read message with id $messageUuid" +
                                        " in conversation $conversationUuid" +
                                        " by document $documentUuid"
                                errorCode = ErrorCode.MESSAGE_ID_NOT_FOUND
                            }
                        }
                        subscription.disable()
                        continuation.resume(messageResult)
                    }
                }
            }
            controller.dataRefreshed().subscribe(refreshCallback)
            controller.list(filter)
        }

        readMessage().takeIf { it.data != null }
            ?: readMessageWithSync()
    }

    override suspend fun getMessageText(
        messageUuid: UUID,
        conversationUuid: UUID
    ): String = controller.getMessageText(messageUuid, conversationUuid).messageText
}

private const val REQUEST_ID = "request_id"