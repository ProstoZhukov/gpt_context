package ru.tensor.sbis.message_panel.core.state_machine.state


import ru.tensor.sbis.message_panel.core.state_machine.state.config.SendStateConfig
import ru.tensor.sbis.message_panel.core.state_machine.state.config.SimpleSendStateConfig
import ru.tensor.sbis.message_panel.declaration.data.ShareContent
import ru.tensor.sbis.message_panel.declaration.vm.MessagePanelViewModel

/**
 * @author ma.kolpakov
 */
class SimpleSendState(
    viewModel: MessagePanelViewModel,
    sharedContent: ShareContent? = null
) : AbstractMessagePanelState<MessagePanelViewModel>(viewModel) {

    init {
        disposer.addAll(
            SendStateConfig.apply(this),
            SimpleSendStateConfig(sharedContent).apply(this)
        )
    }
}