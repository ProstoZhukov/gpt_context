package ru.tensor.sbis.message_panel.core.state_machine.state

import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import ru.tensor.sbis.common.util.statemachine.SessionStateEvent
import ru.tensor.sbis.message_panel.core.state_machine.event.action.*
import ru.tensor.sbis.message_panel.core.state_machine.event.transition.*
import ru.tensor.sbis.message_panel.declaration.vm.MessagePanelViewModel
import java.util.*

/**
 * @author ma.kolpakov
 */
internal class EditingState(
    viewModel: MessagePanelViewModel,
    eventEdit: EventEdit
) : AbstractMessagePanelState<MessagePanelViewModel>(viewModel) {

    init {
        addOnSetAction { editMessage(eventEdit.editingMessage) }

        event(EventSend::class) { changeState(SendingEditMessageEvent(eventEdit.editingMessage)) }
        event(EventCancel::class) { changeState(CleanStateEvent()) }
        event(EventCancelEdit::class) { (cancelUuid) ->
            if (cancelUuid == eventEdit.editingMessage) {
                changeState(CleanStateEvent())
                viewModel.onMessageEditCanceled()
            }
        }
        event(EventReplay::class) { changeState(ReplayingStateEvent(it)) }
        event(EventDisable::class) { changeState(DisabledStateEvent) }
        event(EventEdit::class) { changeState(EditingStateEvent(it)) }

    }

    private fun changeState(state: SessionStateEvent) {
        liveData.resetRecipientButtonForceState()
        fire(state)
    }

    private fun editMessage(editingMessage: UUID) {
        // подписка на изменение текста нужна только в состоянии редактирования
        disposer.add(Observable.combineLatest<String, String, Boolean>(
            liveData.originalMessageText.map(Any::toString),
            liveData.messageText.map { it.value!! },
            BiFunction { original, current -> original == current })
            .subscribe(liveData::setSendCoreRestrictions)
        )

        disposer.add(liveData.conversationUuid.map { container ->
            val info = viewModel.conversationInfo
            /*
            Использование незадокумментированной особонности метода getMessageText(), при которой возможно загрузить
            информацию о собщении при использовании идентификатора документа вместо иденификатора переписки
             */
            checkNotNull(container.value ?: info.document) {
                "Unable to get uuid neither from conversationUuid nor from documentUuid. Info: $info"
            }
        }.flatMapSingle { uuid ->
            messageInteractor.getMessageText(uuid, editingMessage)
        }.subscribe { text ->
            liveData.apply {
                setMessageText(text)
                setQuoteText(eventEdit.title, text)
                setAttachments(emptyList())
                forceHideRecipientsButton(true)
                forceHideRecipientsPanel(true)
                postKeyboardEvent(OpenedByRequest)
            }
        })
    }
}