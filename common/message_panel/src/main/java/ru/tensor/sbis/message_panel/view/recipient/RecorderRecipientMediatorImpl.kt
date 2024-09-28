package ru.tensor.sbis.message_panel.view.recipient

import ru.tensor.sbis.message_panel.viewModel.livedata.recipients.MessagePanelRecipientsData
import ru.tensor.sbis.recorder.decl.RecordRecipientMediator

/**
 * @author ma.kolpakov
 */
internal class RecorderRecipientMediatorImpl(private val liveData: MessagePanelRecipientsData) : RecordRecipientMediator {

    override fun withRecipient(block: () -> Unit) {
        if(liveData.requireRecipients){
            liveData.onRecipientButtonClick()
        }else{
            block()
        }
    }
}