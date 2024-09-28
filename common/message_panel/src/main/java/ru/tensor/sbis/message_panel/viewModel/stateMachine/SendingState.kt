package ru.tensor.sbis.message_panel.viewModel.stateMachine

import io.reactivex.Completable
import io.reactivex.Single
import ru.tensor.sbis.attachments.generated.AttachmentEvents
import ru.tensor.sbis.attachments.generated.DataRefreshedAttachmentCallback
import ru.tensor.sbis.attachments.generated.FileInfo
import ru.tensor.sbis.common.rx.livedata.value
import ru.tensor.sbis.common.rx.plusAssign
import ru.tensor.sbis.common.util.UUIDUtils
import ru.tensor.sbis.common_attachments.Attachment
import ru.tensor.sbis.communicator.generated.SignActions
import ru.tensor.sbis.message_panel.R
import ru.tensor.sbis.message_panel.attachments.MessagePanelAttachmentPresenterImpl
import ru.tensor.sbis.message_panel.view.AlertDialogData
import ru.tensor.sbis.message_panel.viewModel.MessagePanelViewModel
import ru.tensor.sbis.message_panel.viewModel.livedata.keyboard.ClosedByRequest
import ru.tensor.sbis.platform.generated.Subscription
import timber.log.Timber
import java.util.*

/**
 * @author Subbotenko Dmitry
 */
@Deprecated("https://online.sbis.ru/opendoc.html?guid=bb1754f3-4936-4641-bdc2-beec53070c4b")
internal abstract class SendingState<MESSAGE_RESULT, MESSAGE_SENT_RESULT, DRAFT_RESULT>(
    viewModel: MessagePanelViewModel<MESSAGE_RESULT, MESSAGE_SENT_RESULT, DRAFT_RESULT>
) : BaseState<MESSAGE_RESULT, MESSAGE_SENT_RESULT, DRAFT_RESULT>(viewModel)

/**
 * @author Subbotenko Dmitry
 */
@Deprecated("https://online.sbis.ru/opendoc.html?guid=bb1754f3-4936-4641-bdc2-beec53070c4b")
internal class SendingEditMessageState<MESSAGE_RESULT, MESSAGE_SENT_RESULT, DRAFT_RESULT>(
    viewModel: MessagePanelViewModel<MESSAGE_RESULT, MESSAGE_SENT_RESULT, DRAFT_RESULT>,
    private val messageUuid: UUID,
    private val isAttachmentsEditable: Boolean = false
) : SendingState<MESSAGE_RESULT, MESSAGE_SENT_RESULT, DRAFT_RESULT>(viewModel) {
    init {
        addOnSetAction { sendEditedMessage() }
    }

    private fun sendEditedMessage() {
        viewModel.onMessageSending()

        viewModel.liveData.resetDraftUuid()
        val text = liveData.messageText.value ?: ""
        val editAction = if (isAttachmentsEditable) {
            messageInteractor.commitEditMessage(
                messageUuid,
                text,
                liveData.getModifiedMessageMetaData(liveData.getMentionsObject())
            )
        } else {
            messageInteractor.editMessage(messageUuid, text)
        }

        disposer += editAction.subscribe({ result ->
            attachmentPresenter.clearAttachments()
            if (messageResultHelper.isResultError(result)) {
                Timber.e("Editing failure %s", messageResultHelper.getResultError(result))
                liveData.showToast(resourceProvider.getString(R.string.message_panel_comment_edit_message))
            } else {
                if (viewModel.shouldHideKeyboardOnClear()) {
                    liveData.postKeyboardEvent(ClosedByRequest)
                }
                viewModel.onMessageEdit(result)
            }
            loadDraftForNewMessage(viewModel.conversationInfo, viewModel.shouldClearRecipients())
        }, {
            attachmentPresenter.clearAttachments()
            liveData.showToast(resourceProvider.getString(R.string.message_panel_comment_edit_message))
            loadDraftForNewMessage(viewModel.conversationInfo, viewModel.shouldClearRecipients())
        })
    }
}

@Deprecated("https://online.sbis.ru/opendoc.html?guid=bb1754f3-4936-4641-bdc2-beec53070c4b")
internal open class SendingSimpleMessageState<MESSAGE_RESULT, MESSAGE_SENT_RESULT, DRAFT_RESULT>(
    viewModel: MessagePanelViewModel<MESSAGE_RESULT, MESSAGE_SENT_RESULT, DRAFT_RESULT>
) : SendingState<MESSAGE_RESULT, MESSAGE_SENT_RESULT, DRAFT_RESULT>(viewModel) {

    protected open val signActions: SignActions? = null

    init {
        addOnSetAction { sendNewMessage() }
    }

    private fun sendNewMessage() {
        viewModel.onMessageSending()

        val sendMessageSingle = messageInteractor.sendMessage(
            text = liveData.messageText.value,
            attachments = attachmentPresenter.attachments,
            recipientUuids = liveData.recipientsUuidList,
            documentUuid = liveData.document.value,
            conversationUuid = liveData.conversationUuid.value,
            folderUuid = liveData.folderUuid.value,
            signActions = signActions,
            quotedMessageUuid = liveData.quotedMessageUuid.value,
            answeredMessageUuid = liveData.answeredMessageUuid.value,
            metaData = liveData.getModifiedMessageMetaData(liveData.getMentionsObject())
        ).cache()

        val draftUuid = viewModel.liveData.getDraftUuid()
        viewModel.liveData.resetDraftUuid()
        disposer += sendMessageSingle.subscribe { result ->
            if (messageResultHelper.isSentResultError(result)) {
                showAlertWithErrorMessage(messageResultHelper.getSentResultError(result))
                // Сообщение не отправлено, черновик остался прежним
                draftUuid?.let { viewModel.liveData.setDraftUuid(draftUuid) }
                fire(CleanStateEvent(false))
                return@subscribe
            }

            liveData.setMessageText("")
            viewModel.onMessageSent(result)
            if (viewModel.shouldHideKeyboardOnClear()) {
                liveData.postKeyboardEvent(ClosedByRequest)
            }
            attachmentPresenter.clearAttachments()
            loadDraftForNewMessage(viewModel.conversationInfo, viewModel.shouldClearRecipients())
        }
    }

    private fun showAlertWithErrorMessage(errorMessage: String?) {
        val errorText =
            if (!errorMessage.isNullOrBlank()) {
                errorMessage
            } else {
                resourceProvider.getString(R.string.message_panel_error_send_message)
            }
        liveData.showAlertDialog(AlertDialogData(errorText))
        Timber.e("Send message failure %s", errorText)
    }
}

@Deprecated("https://online.sbis.ru/opendoc.html?guid=bb1754f3-4936-4641-bdc2-beec53070c4b")
internal class SendingQuoteMessageState<MESSAGE_RESULT, MESSAGE_SENT_RESULT, DRAFT_RESULT>(
    viewModel: MessagePanelViewModel<MESSAGE_RESULT, MESSAGE_SENT_RESULT, DRAFT_RESULT>
) : SendingSimpleMessageState<MESSAGE_RESULT, MESSAGE_SENT_RESULT, DRAFT_RESULT>(viewModel)

@Deprecated("https://online.sbis.ru/opendoc.html?guid=bb1754f3-4936-4641-bdc2-beec53070c4b")
internal class SendingSignMessageState<MESSAGE_RESULT, MESSAGE_SENT_RESULT, DRAFT_RESULT>(
    viewModel: MessagePanelViewModel<MESSAGE_RESULT, MESSAGE_SENT_RESULT, DRAFT_RESULT>,
    override val signActions: SignActions
) : SendingSimpleMessageState<MESSAGE_RESULT, MESSAGE_SENT_RESULT, DRAFT_RESULT>(viewModel)

internal class SendingMediaMessageState<MESSAGE_RESULT, MESSAGE_SENT_RESULT, DRAFT_RESULT>(
    viewModel: MessagePanelViewModel<MESSAGE_RESULT, MESSAGE_SENT_RESULT, DRAFT_RESULT>,
    private val attachment: Attachment,
    private val metaData: String
) : SendingState<MESSAGE_RESULT, MESSAGE_SENT_RESULT, DRAFT_RESULT>(viewModel) {

    private val messagePanelAttachmentPresenter = viewModel.attachmentPresenter as MessagePanelAttachmentPresenterImpl
    private var subscription: Subscription? = null

    init {
        addOnSetAction { sendNewMessage() }
    }

    private fun sendNewMessage() {
        viewModel.onMessageSending()
        disposer += viewModel.liveData.draftUuidUpdater.firstOrError().flatMapCompletable { draftUuid ->
            // отключаем обновление UI панели при добавлении вложения аудио/видео сообщения
            messagePanelAttachmentPresenter.pauseAttachmentsUpdate()
            // Т.к. выше отключили стандартную обработку вложений, то для аудио/видео нужно зарегать свой коллбек
            // на добавление вложения, чтобы при отправке сообщения передать актуальный FileInfo аудио/видео.
            disposer += attachmentsInteractor.setAttachmentListRefreshCallback(attachmentAddedCallback(draftUuid))
                .subscribe { subscription = it }
            attachmentsInteractor.addAttachments(draftUuid, listOf(attachment.uri))
                .andThen(viewModel.saveDraft(draftUuid))
        }.subscribe()
    }

    private fun attachmentAddedCallback(messageUuid: UUID): DataRefreshedAttachmentCallback =
        object : DataRefreshedAttachmentCallback() {
            override fun onEvent(param: HashMap<String, String>) {
                val catalogId: String? = param[AttachmentEvents.ATTACHMENT_REFRESH_CATALOG_ID]
                if (UUIDUtils.equals(catalogId, messageUuid)) {
                    disposer += attachmentsInteractor.loadAttachments(messageUuid).subscribe { attachments ->
                        if (attachments.isNotEmpty()) {
                            subscription?.disable()
                            subscription = null
                            disposer += sendMessage(attachments).subscribe { result ->
                                handleSendMessageResult(result)
                            }
                        }
                    }
                }
            }
        }

    private fun sendMessage(attachments: List<FileInfo>): Single<out MESSAGE_SENT_RESULT> =
        Completable.fromCallable { viewModel.liveData.resetDraftUuid() }
            .andThen(
                messageInteractor.sendMessage(
                    text = null,
                    attachments = attachments,
                    recipientUuids = liveData.recipientsUuidList,
                    documentUuid = liveData.document.value,
                    conversationUuid = liveData.conversationUuid.value,
                    folderUuid = null,
                    signActions = null,
                    quotedMessageUuid = liveData.quotedMessageUuid.value,
                    answeredMessageUuid = null,
                    metaData = metaData
                )
            )

    private fun handleSendMessageResult(result: MESSAGE_SENT_RESULT) {
        // возвращаем обновление UI панели при добавлении вложения
        messagePanelAttachmentPresenter.resumeAttachmentsUpdate()

        if (messageResultHelper.isSentResultError(result)) {
            Timber.e("Send message failure %s", messageResultHelper.getSentResultError(result))
            liveData.showToast(resourceProvider.getString(R.string.message_panel_error_send_message))
            fire(CleanStateEvent())
            return
        }

        viewModel.onMessageSent(result)
        if (viewModel.shouldHideKeyboardOnClear()) {
            liveData.postKeyboardEvent(ClosedByRequest)
        }
        viewModel.liveData.resetDraftUuid()
        attachmentPresenter.clearAttachments()
        loadDraftForNewMessage(viewModel.conversationInfo, viewModel.shouldClearRecipients())
    }
}