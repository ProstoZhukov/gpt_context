package ru.tensor.sbis.communicator.base.conversation.data.model

import ru.tensor.sbis.communicator.generated.ServiceMessageGroup

/**
 * Базовая информация о сервисном сообщении.
 *
 * @property read                true, если прочитано.
 * @property serviceMessageGroup группа сервисных сообщений.
 *
 * @author vv.chekurda
 */
interface BaseServiceMessage {
    var read: Boolean
    val serviceMessageGroup: ServiceMessageGroup?
    val expandServiceGroupAction: ()-> Unit
}