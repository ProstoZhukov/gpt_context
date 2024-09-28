package ru.tensor.sbis.message_panel.viewModel.stateMachine

import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import ru.tensor.sbis.common.generated.ErrorCode
import ru.tensor.sbis.common.rx.plusAssign
import ru.tensor.sbis.common.util.statemachine.SessionStateEvent
import ru.tensor.sbis.communicator.generated.MessageTextWithMentions
import ru.tensor.sbis.message_panel.model.EditContent
import ru.tensor.sbis.message_panel.viewModel.MessagePanelViewModel
import ru.tensor.sbis.message_panel.viewModel.livedata.keyboard.OpenedByRequest
import timber.log.Timber
import java.util.*

/**
 * @author Subbotenko Dmitry
 */
@Deprecated("https://online.sbis.ru/opendoc.html?guid=bb1754f3-4936-4641-bdc2-beec53070c4b")
internal class EditingState<MESSAGE_RESULT, MESSAGE_SENT_RESULT, DRAFT_RESULT>(
    viewModel: MessagePanelViewModel<MESSAGE_RESULT, MESSAGE_SENT_RESULT, DRAFT_RESULT>,
    private val eventEdit: EventEdit
) : BaseState<MESSAGE_RESULT, MESSAGE_SENT_RESULT, DRAFT_RESULT>(viewModel) {

    private var isSent: Boolean = false
    private val sendRestrictionsDisposable = CompositeDisposable()

    init {
        disposer.add(sendRestrictionsDisposable)
        addOnSetAction { editMessage(eventEdit.content) }

        event(EventSend::class) { onEventSend() }
        event(EventCancel::class) { onEventCancelled() }
        event(EventCancelEdit::class) { (cancelUuid) -> onEventCancelled(cancelUuid) }
        event(EventReplay::class) { changeState(ReplayingStateEvent(it)) }
        event(EventDisable::class) { changeState(DisabledStateEvent()) }
        event(EventEdit::class) { changeState(EditingStateEvent(it)) }
    }

    override fun stop() {
        if (isSent) {
            super.stop()
        } else {
            cancelEditMessage {
                super.stop()
            }
        }
    }

    private fun changeState(state: SessionStateEvent) {
        clearEditState()
        fire(state)
    }

    private fun onEventSend() {
        isSent = true
        viewModel.liveData.setAttachmentsInEditTransaction(isEditTransaction = false)
        changeState(SendingEditMessageEvent(eventEdit.content.uuid, eventEdit.content.isAttachmentsEditable))
    }

    private fun onEventCancelled(cancelUuid: UUID? = null) {
        fun cancel(withCallback: Boolean) {
            if (withCallback) viewModel.onMessageEditCanceled()
            clearEditState()
            loadDraftForNewMessage(viewModel.conversationInfo, true)
        }

        if (cancelUuid == null) {
            cancel(withCallback = false)
        } else if (cancelUuid == eventEdit.content.uuid) {
            cancel(withCallback = true)
        }
    }

    private fun editMessage(content: EditContent) {
        sendRestrictionsDisposable += Observable.combineLatest<String, String, Boolean, Boolean, Boolean>(
            liveData.originalMessageText.map(Any::toString),
            liveData.messageText.map { it.value!! },
            liveData.isAttachmentsEdited,
            liveData.hasAttachments
        ) { original, current, isAttachmentsEdited, hasAttachments ->
            val isTextChanged = original != current
            (!isTextChanged && !isAttachmentsEdited) || (current.isBlank() && !hasAttachments)
        }.subscribe(liveData::setSendCoreRestrictions)

        disposer += viewModel.saveDraft()
            .andThen(messageInteractor.beginEditMessage(content.uuid).ignoreElement())
            .andThen(
                liveData.conversationUuid.firstOrError().map { container ->
                    liveData.setEditMessageUuid(content.uuid)
                    val info = viewModel.conversationInfo
                    /*
                    Использование незадокумментированной особонности метода getMessageText(), при которой возможно загрузить
                    информацию о собщении при использовании идентификатора документа вместо иденификатора переписки
                     */
                    checkNotNull(container.value ?: info.document) {
                        "Unable to get uuid neither from conversationUuid nor from documentUuid. Info: $info"
                    }
                }
            ).flatMap { conversationUuid ->
                if (content.text != null && content.subtitle != null) {
                    Single.just(Triple(content.subtitle, content.text, MessageTextWithMentions()))
                } else {
                    messageInteractor.getMessageText(content.uuid, conversationUuid)
                        .map { textWithMentions ->
                            val subtitle = content.subtitle ?: textWithMentions.messageText
                            val text = content.text ?: textWithMentions.messageText
                            Triple(subtitle, text, textWithMentions)
                        }
                }
            }.subscribe { (subtitle, text, textWithMentions) ->
                liveData.apply {
                    forceChangeAttachmentsButtonVisibility(isVisible = eventEdit.content.isAttachmentsEditable)
                    if (eventEdit.content.isAttachmentsEditable) {
                        viewModel.liveData.setAttachmentsInEditTransaction(isEditTransaction = true)
                        viewModel.attachmentPresenter.loadAttachments(content.uuid)
                    } else {
                        viewModel.attachmentPresenter.clearAttachments()
                    }
                    setMessageText(text)
                    setDraftMentions(textWithMentions)
                    setQuoteText(
                        title = eventEdit.title,
                        subtitle = subtitle,
                        text = text
                    )
                    forceHideRecipientsPanel(true)
                    postKeyboardEvent(OpenedByRequest)
                }
        }
    }

    private fun cancelEditMessage(
        messageUuid: UUID = eventEdit.content.uuid,
        onFinish: (() -> Unit)? = null
    ) {
        disposer += messageInteractor.cancelEditMessage(messageUuid)
            .doOnError { onFinish?.invoke() }
            .subscribe { status ->
                if (status.errorCode != ErrorCode.SUCCESS) {
                    Timber.e(status.errorMessage)
                }
                onFinish?.invoke()
            }
    }

    private fun clearEditState() {
        sendRestrictionsDisposable.dispose()
        liveData.setSendCoreRestrictions(false)
        liveData.setAttachmentsInEditTransaction(isEditTransaction = false)
        viewModel.attachmentPresenter.clearAttachments()
        liveData.apply {
            resetEditMessageUuid()
            forceChangeAttachmentsButtonVisibility(isVisible = true)
        }
    }
}