package ru.tensor.sbis.message_panel.core.state_machine.state

import ru.tensor.sbis.message_panel.core.state_machine.event.action.*
import ru.tensor.sbis.message_panel.core.state_machine.event.transition.*
import ru.tensor.sbis.message_panel.declaration.vm.MessagePanelViewModel

/**
 * @author ma.kolpakov
 */
internal class QuotingState(
    viewModel: MessagePanelViewModel,
    eventQuote: EventQuote
) : AbstractMessagePanelState<MessagePanelViewModel>(viewModel) {

    init {
        addOnSetAction { quote(eventQuote) }

        event(EventSend::class) { fire(SendingQuoteMessageEvent(eventQuote.content.uuid)) }
        event(EventQuote::class) { fire(QuotingStateEvent(it)) }
        event(EventEdit::class) { fire(EditingStateEvent(it)) }
        event(EventRecipients::class) { viewModel.loadRecipients(it.recipients, it.isUserSelected) }
        //в SimpleSendState осуществляются проверки на наличие других вводов от пользователя,
        //если все поля чисты - после перейдем в CleanSendState
        event(EventCancel::class) { clearQuote() }
        event(EventDisable::class) { fire(DisabledStateEvent) }
    }

    private fun quote(eventQuote: EventQuote) {
        with(eventQuote.content) {
            liveData.setQuoteText(sender, text)
            liveData.postKeyboardEvent(OpenedByRequest)
        }
    }

    private fun clearQuote() {
        liveData.setQuoteVisibility(false)
        fire(SimpleSendStateEvent)
    }
}