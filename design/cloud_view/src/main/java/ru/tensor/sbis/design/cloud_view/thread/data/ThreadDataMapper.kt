package ru.tensor.sbis.design.cloud_view.thread.data

import org.json.JSONObject
import ru.tensor.sbis.common.util.UUIDUtils
import ru.tensor.sbis.design.profile_decl.person.InitialsStubData
import ru.tensor.sbis.persons.PersonName
import ru.tensor.sbis.person_decl.profile.model.Person
import ru.tensor.sbis.person_decl.profile.model.PersonModel
import java.util.Date

/**
 * Маппер данных треда [ThreadData].
 *
 * @author vv.chekurda
 */
object ThreadDataMapper {

    /**
     * Получить модель треда из сервисного объекта.
     */
    fun getThreadData(
        serviceObject: JSONObject?,
        timestamp: Long = 0L,
        isOutgoing: Boolean = true,
        isGroupConversation: Boolean = true
    ): ThreadData? =
        serviceObject?.takeIf(::isThreadData)?.run {
            ThreadData(
                dialogUuid = getString(DIALOG_UUID_KEY)
                    .let(UUIDUtils::fromString),
                relevantMessageUuid = optString(MESSAGE_UUID_KEY)
                    .takeIf { it.isNotBlank() }
                    .let(UUIDUtils::fromString),
                title = optString(TITLE_KEY).takeIf { it.isNotBlank() },
                showDocumentIcon = optBoolean(IS_DOCUMENT),
                date = Date(timestamp),
                recipients = getRecipients(),
                recipientCount = optInt(RECIPIENT_COUNT),
                relevantMessageText = optString(MESSAGE_TEXT_KEY),
                isServiceText = optBoolean(IS_SERVICE_TEXT),
                messageCount = optInt(MESSAGE_COUNT),
                unreadCount = optInt(UNREAD_COUNT),
                isOutgoing = isOutgoing,
                isGroupConversation = isGroupConversation
            )
        }

    private fun isThreadData(serviceObject: JSONObject): Boolean =
        serviceObject.optString(TYPE_KEY) == THREAD_TYPE_KEY

    private fun JSONObject.getRecipients(): List<Person> {
        val recipientArray = getJSONArray(RECIPIENT_ARRAY_KEY)
        val recipients = mutableListOf<Person>()
        repeat(recipientArray.length()) { index ->
            val recipientObject = recipientArray.getJSONObject(index)
            val model = recipientObject.let {
                PersonModel(
                    uuid = it.getString(RECIPIENT_UUID_KEY).let(UUIDUtils::fromString),
                    name = PersonName("", it.getString(RECIPIENT_NAME_KEY), ""),
                    photoUrl = it.optString(RECIPIENT_PHOTO_URL_KEY),
                    initialsStubData = InitialsStubData(
                        it.optString(RECIPIENT_INITIALS_KEY),
                        it.optString(RECIPIENT_BACKGROUND_COLOR_HEX_KEY)
                    )
                )
            }
            recipients.add(model)
        }
        return recipients
    }
}

private const val TYPE_KEY = "type"
private const val THREAD_TYPE_KEY = "thread"
private const val DIALOG_UUID_KEY = "dialog_uuid"
private const val MESSAGE_UUID_KEY = "relevant_message_id"
private const val TITLE_KEY = "title"
private const val IS_DOCUMENT = "is_document"
private const val RECIPIENT_COUNT = "recipients_count"
private const val MESSAGE_TEXT_KEY = "relevant_message_text"
private const val MESSAGE_COUNT = "message_count"
private const val UNREAD_COUNT = "unread_count"
private const val IS_SERVICE_TEXT = "is_service"

private const val RECIPIENT_ARRAY_KEY = "recipients"
private const val RECIPIENT_UUID_KEY = "uuid"
private const val RECIPIENT_NAME_KEY = "name"
private const val RECIPIENT_PHOTO_URL_KEY = "photo_url"
private const val RECIPIENT_BACKGROUND_COLOR_HEX_KEY = "background_color_hex"
private const val RECIPIENT_INITIALS_KEY = "initials"