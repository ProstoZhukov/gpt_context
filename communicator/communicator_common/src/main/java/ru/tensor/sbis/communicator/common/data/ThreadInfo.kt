package ru.tensor.sbis.communicator.common.data

import org.json.JSONObject
import ru.tensor.sbis.common.util.UUIDUtils
import java.io.Serializable
import java.util.UUID

/**
 * Информация о треде.
 *
 * @property parentConversationUuid идентификатор родительской переписки, от которой создан (или будет) тред.
 * @property parentConversationMessageUuid идентификатор сообщения, от которого создан (или будет) тред.
 * @property isChat признак является ли родительская переписка чатом.
 *
 * @author vv.chekurda
 */
data class ThreadInfo(
    val parentConversationUuid: UUID,
    val parentConversationMessageUuid: UUID,
    val isChat: Boolean
) : Serializable {

    companion object {

        /**
         * Создать информацию о треде по сервисному объекту сообщения.
         */
        fun fromServiceObject(
            serviceObject: JSONObject?,
            isChat: Boolean = false
        ): ThreadInfo? =
            serviceObject?.let {
                val parentConversationUuid = it.optString(THREAD_SERVICE_OBJECT_PARENT_ID_KEY)
                if (parentConversationUuid.isBlank()) null
                else {
                    ThreadInfo(
                        parentConversationUuid = UUIDUtils.fromString(parentConversationUuid),
                        parentConversationMessageUuid = it.getString(THREAD_SERVICE_OBJECT_SOURCE_MESSAGE_ID_KEY)
                            .let(UUIDUtils::fromString),
                        isChat = isChat
                    )
                }
            }
    }

    /**
     * Получить сервисный объект для отправки сообщения и создания нового треда для текущей модели информации.
     */
    fun getSendServiceObject(
        threadStartMessageCopyId: UUID
    ): JSONObject =
        JSONObject()
            .put(SEND_THREAD_SERVICE_OBJECT_DIALOG_KEY, parentConversationUuid)
            .put(SEND_THREAD_SERVICE_OBJECT_MESSAGE_KEY, parentConversationMessageUuid)
            .put(SEND_THREAD_SERVICE_OBJECT_MESSAGE_COPY_KEY, threadStartMessageCopyId)
}

private const val SEND_THREAD_SERVICE_OBJECT_DIALOG_KEY = "parent_dialog_uuid"
private const val SEND_THREAD_SERVICE_OBJECT_MESSAGE_KEY = "parent_dialog_message_uuid"
private const val SEND_THREAD_SERVICE_OBJECT_MESSAGE_COPY_KEY = "thread_start_message_uuid"

private const val THREAD_SERVICE_OBJECT_PARENT_ID_KEY = "parent_id"
private const val THREAD_SERVICE_OBJECT_SOURCE_MESSAGE_ID_KEY = "source_message_id"