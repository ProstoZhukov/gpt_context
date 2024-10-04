package ru.tensor.sbis.message_panel.core.state_machine.state

import ru.tensor.sbis.common.util.statemachine.SessionStateEvent
import ru.tensor.sbis.message_panel.R
import ru.tensor.sbis.message_panel.core.state_machine.event.action.*
import ru.tensor.sbis.message_panel.core.state_machine.event.transition.*
import ru.tensor.sbis.message_panel.declaration.vm.MessagePanelViewModel
import ru.tensor.sbis.message_panel.viewModel.MessagePanelViewModel
import ru.tensor.sbis.message_panel.viewModel.livedata.keyboard.OpenedByRequest
import timber.log.Timber

/**
 * @author ma.kolpakov
 */
internal class ReplayingState(
    viewModel: MessagePanelViewModel,
    eventReplay: EventReplay
) : AbstractMessagePanelState<MessagePanelViewModel>(viewModel) {

    private var startSpawn: String? = null

    init {
        addOnSetAction { replyComment(eventReplay) }

        event(EventSend::class) { fire(SendingSimpleMessageEvent) }
        event(EventReplay::class) { changeState(ReplayingStateEvent(it)) }
        event(EventDisable::class) { changeState(DisabledStateEvent) }
        event(EventEdit::class) { changeState(EditingStateEvent(it)) }
        event(EventCancel::class) { changeState(CleanStateEvent(false)) }
    }

    private fun changeState(state: SessionStateEvent) {
        startSpawn?.let {
            val value = liveData.messageText.value
            liveData.setMessageText(value?.removePrefix(it))
            startSpawn = null
        }

        fire(state)
    }

    private fun replyComment(eventReplay: EventReplay) {
        disposer.add(messageInteractor.getMessageByUuid(eventReplay.messageUuid).subscribe { message ->
            if (messageResultHelper.isResultError(message)) {
                Timber.e("Conversation data failure %s", messageResultHelper.getResultError(message))
                liveData.showToast(viewModel.resourceProvider.getString(R.string.message_panel_error_data_receive))
                return@subscribe
            }

            if (eventReplay.showKeyboard) {
                liveData.postKeyboardEvent(OpenedByRequest)
            }

            val sender = messageResultHelper.getSender(message)

            startSpawn = "${sender.name.firstName}, "
            liveData.setMessageText(startSpawn)

            liveData.setConversationUuid(eventReplay.conversationUuid)
            liveData.setDocumentUuid(eventReplay.documentUuid)
            liveData.setAnsweredMessageUuid(eventReplay.messageUuid)

            // комментарий, а соответственно получателя, выбирает пользователь - обеспечиваем подстановку получателя
            sender.uuid.let { viewModel.loadRecipients(listOf(it), isUserSelected = true) }
        })
    }
}