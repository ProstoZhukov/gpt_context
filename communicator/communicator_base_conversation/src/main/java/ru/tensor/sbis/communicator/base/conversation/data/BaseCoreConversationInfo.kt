package ru.tensor.sbis.communicator.base.conversation.data

import java.util.*

/**
 * Базовый интерфейс информации по переписке.
 *
 * @property conversationUuid uuid переписки.
 * @property messageUuid uuid сообщения.
 * @property isChat является ли чатом.
 * @property isInitAsGroupDialog инициализировать диалог, как групповой.
 *
 * @author vv.chekurda
 */
interface BaseCoreConversationInfo {
    var conversationUuid: UUID?
    val messageUuid: UUID?
    var isChat: Boolean
    val isInitAsGroupDialog: Boolean
        get() = false
    val isFullViewMode: Boolean
}
