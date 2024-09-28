package ru.tensor.sbis.message_panel.viewModel.stateMachine

import io.reactivex.Flowable
import io.reactivex.Scheduler
import ru.tensor.sbis.common.rx.scheduler.TensorSchedulers
import ru.tensor.sbis.common.util.statemachine.SessionEvent
import ru.tensor.sbis.common.util.statemachine.SessionStateEvent
import ru.tensor.sbis.common.util.statemachine.StateMachineImpl
import ru.tensor.sbis.common.util.statemachine.StateMachineInner
import ru.tensor.sbis.common_attachments.Attachment
import ru.tensor.sbis.communicator.generated.SignActions
import ru.tensor.sbis.message_panel.model.EditContent
import ru.tensor.sbis.message_panel.model.QuoteContent
import ru.tensor.sbis.message_panel.model.ShareContent
import ru.tensor.sbis.message_panel.viewModel.MessagePanelViewModel
import java.util.*

/**
 * @author Subbotenko Dmitry
 */
internal data class EventReplay(val conversationUuid: UUID, val messageUuid: UUID, val documentUuid: UUID, val showKeyboard: Boolean) : SessionEvent
internal data class EventQuote(val content: QuoteContent, val showKeyboard: Boolean = true) : SessionEvent
internal data class EventShare(val content: ShareContent) : SessionEvent
// прикладное действие
internal data class EventSign(val action: SignActions) : SessionEvent
internal data class EventEdit(val title: String, val content: EditContent) : SessionEvent
internal data class EventSendMediaMessage(val attachment: Attachment, val metaData: String) : SessionEvent
internal data class EventRecipients(
    val recipients: List<UUID>,
    val isUserSelected: Boolean = false,
    val add: Boolean = false
) : SessionEvent
internal class EventUserInput : SessionEvent
internal class EventUserInputClear: SessionEvent
class EventSend : SessionEvent
internal class EventCancel : SessionEvent
/**
 * Событие условной отмены редактирования
 *
 * @param editingMessage [UUID] сообщения, редактрирование которого нужно прервать
 */
internal data class EventCancelEdit(val editingMessage: UUID) : SessionEvent
internal class EventDisable : SessionEvent
internal class EventEnable : SessionEvent

internal class DisabledStateEvent : SessionStateEvent
internal data class EditingStateEvent(val eventEdit: EventEdit) : SessionStateEvent
internal data class ReplayingStateEvent(val eventReplay: EventReplay) : SessionStateEvent
internal data class QuotingStateEvent(val eventQuote: EventQuote) : SessionStateEvent
internal data class SharingStateEvent(val content: ShareContent) : SessionStateEvent
data class CleanStateEvent(val needToClean: Boolean = true) : SessionStateEvent
internal class SimpleSendStateEvent : SessionStateEvent
internal data class SendingEditMessageEvent(val messageUuid: UUID, val isAttachmentsEditable: Boolean = false) : SessionStateEvent
internal data class SendingMediaMessageEvent(val attachment: Attachment, val metaData: String) : SessionStateEvent
internal class SendingQuoteMessageEvent : SessionStateEvent
// прикладное действие
internal data class SendingSignMessageEvent(val action: SignActions) : SessionStateEvent
class SendingSimpleMessageEvent : SessionStateEvent

internal data class EventEnableWithDraft(
    val documentUuid: UUID?,
    val conversationUuid: UUID?,
    val isNewConversation: Boolean,
    val recipients: List<UUID>
) : SessionEvent

data class DraftLoadingStateEvent(
    val documentUuid: UUID?,
    val conversationUuid: UUID?,
    val isNewConversation: Boolean,
    val recipients: List<UUID>,
    val needToClean: Boolean,
    val clearDraft: Boolean = false
) : SessionStateEvent

interface MessagePanelStateMachine<MESSAGE_RESULT, MESSAGE_SENT_RESULT, DRAFT_RESULT> : StateMachineInner {
    val isEnabled: Flowable<Boolean>
    val isSending: Flowable<Boolean>
    val isEditing: Flowable<Boolean>
    val isQuoting: Flowable<Boolean>

    /**
     * Запуск машины, установка начального состояния
     */
    fun start()
}

open class MessagePanelStateMachineImpl<MESSAGE_RESULT, MESSAGE_SENT_RESULT, DRAFT_RESULT>(
    private val viewModel: MessagePanelViewModel<MESSAGE_RESULT, MESSAGE_SENT_RESULT, DRAFT_RESULT>,
    scheduler: Scheduler = TensorSchedulers.androidUiScheduler
) : MessagePanelStateMachine<MESSAGE_RESULT, MESSAGE_SENT_RESULT, DRAFT_RESULT>, StateMachineInner by StateMachineImpl(scheduler, scheduler) {
    override val isEnabled = currentStateObservable.map { it !is DisabledState }.distinctUntilChanged()
    override val isSending = currentStateObservable.map { it is SendingState<*, *, *> }.distinctUntilChanged()
    override val isEditing = currentStateObservable.map { it is EditingState<*, *, *> }.distinctUntilChanged()
    override val isQuoting = currentStateObservable.map { it is QuotingState<*, *, *> }.distinctUntilChanged()

    init {
        state(DisabledStateEvent::class) { setState(DisabledState(viewModel)) }
        state(EditingStateEvent::class) { setState(EditingState(viewModel, it.eventEdit)) }
        state(QuotingStateEvent::class) { setState(QuotingState(viewModel, it.eventQuote)) }
        state(SharingStateEvent::class) { setState(SimpleSendState(viewModel, it.content)) }
        state(ReplayingStateEvent::class) { setState(ReplayingState(viewModel, it.eventReplay)) }
        state(CleanStateEvent::class) { setState(CleanSendState(viewModel, it.needToClean)) }
        state(SimpleSendStateEvent::class) { setState(SimpleSendState(viewModel)) }
        state(SendingEditMessageEvent::class) { setState(SendingEditMessageState(viewModel, it.messageUuid, it.isAttachmentsEditable)) }
        state(SendingMediaMessageEvent::class) { setState(SendingMediaMessageState(viewModel, it.attachment, it.metaData)) }
        state(SendingQuoteMessageEvent::class) { setState(SendingQuoteMessageState(viewModel)) }
        state(SendingSignMessageEvent::class) { setState(SendingSignMessageState(viewModel, it.action)) }
        state(SendingSimpleMessageEvent::class) { setState(SendingSimpleMessageState(viewModel)) }
        state(DraftLoadingStateEvent::class) {
            setState(
                DraftLoadingState(
                    viewModel,
                    it.documentUuid,
                    it.conversationUuid,
                    it.isNewConversation,
                    it.recipients,
                    it.needToClean,
                    it.clearDraft
                )
            )
        }
    }

    override fun start() {
        setState(DisabledState(viewModel))
    }
}