package ru.tensor.sbis.message_panel.viewModel.stateMachine

import ru.tensor.sbis.message_panel.viewModel.MessagePanelViewModel
import ru.tensor.sbis.message_panel.viewModel.livedata.keyboard.OpenedByRequest

/**
 * @author Subbotenko Dmitry
 */
@Deprecated("https://online.sbis.ru/opendoc.html?guid=bb1754f3-4936-4641-bdc2-beec53070c4b")
internal class QuotingState<MESSAGE_RESULT, MESSAGE_SENT_RESULT, DRAFT_RESULT>(
    viewModel: MessagePanelViewModel<MESSAGE_RESULT, MESSAGE_SENT_RESULT, DRAFT_RESULT>,
    eventQuote: EventQuote
) : BaseState<MESSAGE_RESULT, MESSAGE_SENT_RESULT, DRAFT_RESULT>(viewModel) {

    init {
        addOnSetAction { quote(eventQuote) }

        event(EventSend::class) { fire(SendingQuoteMessageEvent()) }
        event(EventQuote::class) { fire(QuotingStateEvent(it)) }
        event(EventEdit::class) { fire(EditingStateEvent(it)) }
        event(EventRecipients::class) { viewModel.loadRecipients(it.recipients, it.isUserSelected, it.add) }
        //в SimpleSendState осуществляются проверки на наличие других вводов от пользователя,
        //если все поля чисты - после перейдем в CleanSendState
        event(EventCancel::class) { clearQuote() }
        event(EventDisable::class) { fire(DisabledStateEvent()) }
        event(EventSendMediaMessage::class) { fire(SendingMediaMessageEvent(it.attachment, it.metaData)) }
    }

    private fun quote(eventQuote: EventQuote) {
        with(eventQuote.content) {
            liveData.setQuoteText(sender, text)
            liveData.setQuotedMessageUuid(uuid)
            if (eventQuote.showKeyboard) {
                liveData.postKeyboardEvent(OpenedByRequest)
            }
        }
    }

    private fun clearQuote() {
        liveData.setQuoteVisibility(false)
        liveData.setQuotedMessageUuid(null)
        fire(SimpleSendStateEvent())
    }
}