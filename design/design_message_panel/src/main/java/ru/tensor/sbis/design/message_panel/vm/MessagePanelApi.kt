package ru.tensor.sbis.design.message_panel.vm

import ru.tensor.sbis.design.message_panel.vm.attachments.MessagePanelAttachmentsApi
import ru.tensor.sbis.design.message_panel.vm.keyboard.MessagePanelKeyboardApi
import ru.tensor.sbis.design.message_panel.vm.quote.MessagePanelQuoteApi
import ru.tensor.sbis.design.message_panel.vm.recipients.MessagePanelRecipientsApi
import ru.tensor.sbis.design.message_panel.vm.state.MessagePanelStateApi
import ru.tensor.sbis.design.message_panel.vm.text.MessagePanelTextApi
import ru.tensor.sbis.design.message_panel.vm.usecase.MessagePanelUseCaseApi

/**
 * Публичный API панели ввода
 *
 * @author ma.kolpakov
 */
interface MessagePanelApi :
    MessagePanelUseCaseApi,
    MessagePanelTextApi,
    MessagePanelAttachmentsApi,
    MessagePanelRecipientsApi,
    MessagePanelQuoteApi,
    MessagePanelStateApi,
    MessagePanelKeyboardApi {

    fun onSendClicked()
}
