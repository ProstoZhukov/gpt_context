package ru.tensor.sbis.message_panel.integration

import io.reactivex.Completable
import io.reactivex.Single
import ru.tensor.sbis.common.data.DependencyProvider
import ru.tensor.sbis.common.generated.CommandStatus
import ru.tensor.sbis.common.generated.ErrorCode
import ru.tensor.sbis.common.generated.QueryDirection
import ru.tensor.sbis.common.util.UUIDUtils
import ru.tensor.sbis.communicator.generated.DataRefreshedMessageControllerCallback
import ru.tensor.sbis.communicator.generated.DialogController
import ru.tensor.sbis.communicator.generated.DialogDocument
import ru.tensor.sbis.communicator.generated.DraftMessage
import ru.tensor.sbis.communicator.generated.MessageController
import ru.tensor.sbis.communicator.generated.MessageFilter
import ru.tensor.sbis.communicator.generated.MessageResult
import ru.tensor.sbis.communicator.generated.MessageTextWithMentions
import ru.tensor.sbis.communicator.generated.SendMessageResult
import ru.tensor.sbis.message_panel.decl.DraftArguments
import ru.tensor.sbis.message_panel.decl.EditArguments
import ru.tensor.sbis.message_panel.decl.MessageServiceWrapper
import ru.tensor.sbis.message_panel.decl.SendArguments
import ru.tensor.sbis.message_panel.helper.MessagePanelMetricsDispatcher
import ru.tensor.sbis.message_panel.helper.asArrayList
import ru.tensor.sbis.message_panel.interactor.util.SubscriptionHolder
import java.util.UUID

internal const val REQUEST_ID = "request_id"

/**
 * Реализация [MessageServiceWrapper] для работы с микросервисом сообщений
 *
 * @author vv.chekurda
 */
internal class CommunicatorMessageServiceWrapper(
    private val controllerProvider: DependencyProvider<MessageController>,
    private val dialogControllerProvider: DependencyProvider<DialogController>
) : MessageServiceWrapper<MessageResult, SendMessageResult, CommunicatorDraftMessage> {

    private var analyticsUsageName: String? = null

    override fun send(arguments: SendArguments): Single<SendMessageResult> = Single.fromCallable {
        with(arguments) {
            val eventName = getAnalyticSendEventName(containsAttachments = attachments.isNotEmpty())
            eventName?.also(MessagePanelMetricsDispatcher::startTrace)
            val result = controllerProvider.get().enqueueMessage2(
                conversationUuid,
                folderUuid,
                text,
                documentUuid,
                recipientUuidList.asArrayList(),
                attachments.asArrayList(),
                signActions,
                quotedMessageUuid,
                answeredMessageUuid,
                metaData
            )
            eventName?.also(MessagePanelMetricsDispatcher::stopTrace)
            result
        }
    }

    override fun sendLink(conversationUuid: UUID, url: String): Completable =
        Completable.fromAction {
            val uuidFirstIndex = url.indexOf("=") + 1
            val docUuidString = url.removeRange(0, uuidFirstIndex).take(36)
            val docUuid = UUIDUtils.fromString(docUuidString)
            val result = dialogControllerProvider.get().notifyDialogAboutDocument(conversationUuid, DialogDocument(docUuid))
            if (result.errorCode != ErrorCode.SUCCESS) {
                throw IllegalStateException("On sendLink failed: ${result.errorMessage}")
            }
        }

    override fun edit(arguments: EditArguments): Single<MessageResult> = Single.fromCallable {
        with(arguments) {
            controllerProvider.get().editMessage(messageUuid, text)
        }
    }

    override fun read(messageUuid: UUID, conversationUuid: UUID, documentUuid: UUID?): Single<MessageResult> = Single.create { emitter ->
        with(controllerProvider.get()){
            val cachedMessage = read(messageUuid)?.let { deserializeFromBinaryToMessage(it).data }
            if (cachedMessage == null) {
                val filter = MessageFilter(conversationUuid).apply {
                    direction = QueryDirection.TO_NEWER
                    fromUuid = messageUuid
                    count = 1
                    includeAnchor = true
                    requestId = "read($messageUuid)"
                }
                list(filter)
                val refreshCallback = object : DataRefreshedMessageControllerCallback() {
                    override fun onEvent(param: HashMap<String, String>) {
                        // дожидаемся обработки собственного запроса и перечитываем данные из кэша
                        if (param[REQUEST_ID] == filter.requestId) {
                            val message = read(messageUuid)?.let { deserializeFromBinaryToMessage(it).data }
                            val commandStatus = CommandStatus()
                            if (message == null) {
                                commandStatus.errorMessage = "Unable to read message with id $messageUuid" +
                                        " in conversation $conversationUuid" +
                                        " by document $documentUuid"
                                commandStatus.errorCode = ErrorCode.MESSAGE_ID_NOT_FOUND
                            }
                            emitter.onSuccess(MessageResult(message, commandStatus))
                        }
                    }
                }
                val subscription = SubscriptionHolder(dataRefreshed().subscribe(refreshCallback))
                emitter.setCancellable(subscription)
            } else {
                emitter.onSuccess(MessageResult(cachedMessage, CommandStatus()))
            }
        }
    }

    override fun readText(messageUuid: UUID, conversation: UUID): Single<MessageTextWithMentions> = Single.fromCallable {
        controllerProvider.get().getMessageText(messageUuid, conversation)
    }

    override fun saveDraft(draft: DraftArguments): Completable = Completable.fromAction {
        with(draft) {
            val draftMessage = DraftMessage(
                draftUuid,
                System.currentTimeMillis(),
                recipientsUuidList.asArrayList(),
                text.orEmpty(),
                attachmentUuidList.asArrayList(),
                answerUuid,
                quoteUuid,
                serviceObject
            )
            controllerProvider.get().saveDraft(themeUuid!!, draftMessage)
        }
    }

    override fun loadDraft(themeUuid: UUID, documentUuid: UUID?, clearDraft: Boolean): Single<CommunicatorDraftMessage> =
        Single.fromCallable {
            if (clearDraft) {
                controllerProvider.get().clearDraft(themeUuid)
            } else {
                controllerProvider.get().getDraft(themeUuid)
            }
        }
            .flatMap { draftContent ->
                val quoteUuid = draftContent.quotedMessageId
                if (quoteUuid != null) {
                    read(quoteUuid, themeUuid, documentUuid)
                        .map { CommunicatorDraftMessage(draftContent, it.data) }
                } else {
                    Single.just(CommunicatorDraftMessage(draftContent, null))
                }
            }

    override fun notifyUserTyping(conversationUuid: UUID): Completable =
        Completable.fromAction { controllerProvider.get().onTypingMessage(conversationUuid) }

    override fun beginEditMessage(editingMessage: UUID): Single<CommandStatus> =
        Single.fromCallable { controllerProvider.get().beginEditMessage(editingMessage) }

    override fun commitEditMessage(arguments: EditArguments): Single<MessageResult> =
        Single.fromCallable {
            val status = controllerProvider.get().commitEditMessage(
                arguments.messageUuid,
                arguments.text,
                arguments.serviceObject
            )
            MessageResult(null, status)
        }

    override fun cancelEditMessage(editingMessage: UUID): Single<CommandStatus> =
        Single.fromCallable { controllerProvider.get().cancelEditMessage(editingMessage) }

    override fun editMediaMessageEmotion(messageUuid: UUID, emotionCode: Int): Completable =
        Completable.fromAction { controllerProvider.get().editMessageEmotion(messageUuid, emotionCode) }

    override fun setAnalyticsUsageName(name: String?) {
        analyticsUsageName = name
    }

    private fun getAnalyticSendEventName(containsAttachments: Boolean): String? {
        val screenName = analyticsUsageName ?: return null
        return StringBuilder(ANALYTICS_SEND_EVENT_NAME)
            .append(SNAKE_SPACE)
            .append(screenName)
            .append(SNAKE_SPACE)
            .append(MESSAGE_POSTFIX)
            .apply {
                if (containsAttachments) {
                    append(SNAKE_SPACE)
                    append(ATTACHMENTS_POSTFIX)
                }
            }
            .toString()
            .take(40)
    }
}

private const val ANALYTICS_SEND_EVENT_NAME = "send"
private const val SNAKE_SPACE = "_"
private const val MESSAGE_POSTFIX = "message"
private const val ATTACHMENTS_POSTFIX = "with_files"