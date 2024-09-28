package ru.tensor.sbis.message_panel.core.state_machine.state

import ru.tensor.sbis.message_panel.core.state_machine.event.transition.SimpleSendStateEvent
import ru.tensor.sbis.message_panel.core.state_machine.state.config.CleanStateConfig
import ru.tensor.sbis.message_panel.core.state_machine.state.config.SendStateConfig
import ru.tensor.sbis.message_panel.declaration.vm.MessagePanelViewModel

/**
 * Состояние перед отправкой сообщения, при котором пользователь ещё не произвёл каких либо действий с панелью ввода
 *
 * @author ma.kolpakov
 */
class CleanSendState(
    viewModel: MessagePanelViewModel,
    needToClean: Boolean
) : AbstractMessagePanelState<MessagePanelViewModel>(viewModel) {

    init {
        disposer.addAll(
            SendStateConfig.apply(this),
            CleanStateConfig(needToClean, SimpleSendStateEvent).apply(this)
        )
    }
}