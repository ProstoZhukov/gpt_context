package ru.tensor.sbis.message_panel.recorder.viewmodel

import ru.tensor.sbis.recorder.decl.RecordRecipientMediator
/**
 * @author ma.kolpakov
 */
class RecordRecipientMediatorMock : RecordRecipientMediator {

    var allow = true

    override fun withRecipient(block: () -> Unit) {
        if (allow) block()
    }
}