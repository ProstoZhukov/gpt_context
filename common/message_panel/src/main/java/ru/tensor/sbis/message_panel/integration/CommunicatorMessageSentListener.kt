package ru.tensor.sbis.message_panel.integration

import ru.tensor.sbis.communicator.generated.SendMessageResult
import ru.tensor.sbis.message_panel.contract.MessageSentListener

/**
 * Псевдоним для работы [MessageSentListener] с микросервисом сообщений
 *
 * @author vv.chekurda
 */
typealias CommunicatorMessageSentListener = MessageSentListener<SendMessageResult>