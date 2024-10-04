package ru.tensor.sbis.design.message_view.content.threads

import org.json.JSONObject
import ru.tensor.sbis.common.util.UUIDUtils
import ru.tensor.sbis.communicator.generated.ThreadInfo

/**
 * Создать информацию о треде по сервисному объекту сообщения.
 *
 * @author vv.chekurda
 */
fun getThreadInfoFromServiceObject(
    serviceObject: JSONObject?,
    isChat: Boolean = false
): ThreadInfo? = serviceObject?.let {
    val parentConversationUuid = it.optString(THREAD_SERVICE_OBJECT_PARENT_ID_KEY)
    if (parentConversationUuid.isBlank()) {
        null
    } else {
        ThreadInfo(
            UUIDUtils.fromString(parentConversationUuid),
            isChat,
            it.getString(THREAD_SERVICE_OBJECT_SOURCE_MESSAGE_ID_KEY).let(UUIDUtils::fromString)
        )
    }
}

private const val THREAD_SERVICE_OBJECT_PARENT_ID_KEY = "parent_id"
private const val THREAD_SERVICE_OBJECT_SOURCE_MESSAGE_ID_KEY = "source_message_id"