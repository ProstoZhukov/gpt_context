package ru.tensor.sbis.message_panel.integration

import ru.tensor.sbis.communicator.generated.MessageResult
import ru.tensor.sbis.communicator.generated.SendMessageResult
import ru.tensor.sbis.message_panel.delegate.MessagePanelInitializerDelegate

/**
 * Псевдоним для работы [MessagePanelInitializerDelegate] с микросервисом сообщений
 *
 * @author vv.chekurda
 */
typealias CommunicatorMessagePanelInitializerDelegate =
        MessagePanelInitializerDelegate<MessageResult, SendMessageResult, CommunicatorDraftMessage>