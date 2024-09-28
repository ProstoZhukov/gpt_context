package ru.tensor.sbis.message_panel.integration

import ru.tensor.sbis.communicator.generated.MessageResult
import ru.tensor.sbis.communicator.generated.SendMessageResult
import ru.tensor.sbis.message_panel.contract.MessagePanelController
import ru.tensor.sbis.message_panel.viewModel.MessagePanelViewModel

/**
 * Псевдоним для работы [MessagePanelController] с микросервисом сообщений
 *
 * @author vv.chekurda
 */
typealias CommunicatorMessagePanelController = MessagePanelController<MessageResult, SendMessageResult, CommunicatorDraftMessage>
typealias CommunicatorMessagePanelViewModel = MessagePanelViewModel<MessageResult, SendMessageResult, CommunicatorDraftMessage>