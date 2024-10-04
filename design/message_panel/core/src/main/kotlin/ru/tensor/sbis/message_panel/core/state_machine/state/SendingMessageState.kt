package ru.tensor.sbis.message_panel.core.state_machine.state

import ru.tensor.sbis.message_panel.core.state_machine.state.config.SimpleSendingStateConfig
import ru.tensor.sbis.message_panel.declaration.vm.MessagePanelViewModel

/**
 * Состояние отправки сообщения (простого, запроса подписи, ответа)
 *
 * @see SendingAudioMessageState
 * @see SendingEditMessageState
 *
 * @author ma.kolpakov
 */
class SendingMessageState(
    viewModel: MessagePanelViewModel,
    stateConfig: SimpleSendingStateConfig = SimpleSendingStateConfig()
) : AbstractMessagePanelState<MessagePanelViewModel>(viewModel) {

    init {
        disposer.add(stateConfig.apply(this))
    }
}