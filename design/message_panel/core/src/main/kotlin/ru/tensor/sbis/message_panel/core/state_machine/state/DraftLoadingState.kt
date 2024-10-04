package ru.tensor.sbis.message_panel.core.state_machine.state

import ru.tensor.sbis.message_panel.core.state_machine.state.config.DraftLoadingStateConfig
import ru.tensor.sbis.message_panel.declaration.vm.MessagePanelViewModel
import java.util.*

/**
 * TODO: 11/12/2020 Добавить документацию https://online.sbis.ru/opendoc.html?guid=27078b6d-5ded-4c38-a504-ef29e4c6c902
 *
 * @author ma.kolpakov
 */
class DraftLoadingState(
    viewModel: MessagePanelViewModel,
    documentUuid: UUID?,
    conversationUuid: UUID?,
    needToClean: Boolean = true
) : AbstractMessagePanelState<MessagePanelViewModel>(viewModel) {

    init {
        disposer.add(DraftLoadingStateConfig(documentUuid, conversationUuid, needToClean).apply(this))
    }
}