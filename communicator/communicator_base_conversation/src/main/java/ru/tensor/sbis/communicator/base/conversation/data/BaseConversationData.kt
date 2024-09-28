package ru.tensor.sbis.communicator.base.conversation.data

import ru.tensor.sbis.communicator.base.conversation.data.model.ConversationAccess
import ru.tensor.sbis.communicator.base.conversation.data.model.ToolbarData

/**
 * Базовый интерфейс загружаемых данных по переписке
 * для передачи между делегатами презентера.
 *
 * @property toolbarData        данные для отображения в тулбаре.
 * @property conversationAccess модель разрешений и признаков доступности переписки.
 *
 * @author vv.chekurda
 */
interface BaseConversationData {
    var toolbarData: ToolbarData?
    var conversationAccess: ConversationAccess
}