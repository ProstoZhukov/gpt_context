package ru.tensor.sbis.design.message_panel.domain

import android.net.Uri
import ru.tensor.sbis.design.message_panel.decl.env.MessagePanelEnvironmentModel
import ru.tensor.sbis.design.message_panel.vm.MessagePanelViewModel

/**
 * @author ma.kolpakov
 */
class ShareMessageUseCase internal constructor(
    private val text: String,
    private val attachments: List<Uri>,
    environment: MessagePanelEnvironmentModel,
): AbstractMessagePanelUseCase(environment) {

    override suspend fun setup(vm: MessagePanelViewModel) {
        TODO("Not yet implemented")
    }

    override suspend fun send() {
        TODO("Not yet implemented")
    }
}