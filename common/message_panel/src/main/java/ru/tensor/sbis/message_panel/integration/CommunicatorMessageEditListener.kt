package ru.tensor.sbis.message_panel.integration

import ru.tensor.sbis.communicator.generated.MessageResult
import ru.tensor.sbis.message_panel.contract.MessageEditListener

/**
 * Псевдоним для работы [MessageEditListener] с микросервисом сообщений
 *
 * @author vv.chekurda
 */
typealias CommunicatorMessageEditListener = MessageEditListener<MessageResult>