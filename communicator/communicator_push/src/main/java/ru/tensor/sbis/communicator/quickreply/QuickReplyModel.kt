package ru.tensor.sbis.communicator.quickreply

import ru.tensor.sbis.communicator.push.model.MessagePushModel
import java.util.*

/**
 * Модель для быстрого ответа по пушу
 *
 * @param dialogUuid    uuid диалога
 * @param messageUuid   uuid сообщения
 * @param recipient     получатель сообщения
 * @param targetMessage текст сообщения
 * @param isComment     является ли комментарием
 *
 * @author da.zhukov
 */
data class QuickReplyModel(
    val dialogUuid: UUID?,
    val messageUuid: UUID?,
    val recipient: MessagePushModel.Sender?,
    var targetMessage: String?,
    val isComment: Boolean
)