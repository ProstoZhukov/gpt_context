package ru.tensor.sbis.message_panel.view.mentions

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import ru.tensor.sbis.common.util.UUIDUtils
import timber.log.Timber

/**
 * Парсер упоминаний. Позволяет:
 * 1) Получить спаны упоминаний из сервисного JSON объекта.
 * 2) Список спанов уведомлений преобразовать в сервисный объект.
 *
 * @author dv.baranov
 */
internal class MentionParser(private val context: Context) {

    /**
     * Извлечь список [MentionSpan] из сервисного объекта.
     */
    fun getMentionsFromServiceObject(jsonServiceObject: JSONObject?): List<MentionSpan> {
        if (jsonServiceObject == null) return emptyList()
        val mentionsIsEmpty = jsonServiceObject.optString(MENTIONS_KEY).isEmpty()
        return if (!mentionsIsEmpty) {
            tryGetMentionsFromJsonArray(jsonServiceObject)
        } else {
            emptyList()
        }
    }

    private fun tryGetMentionsFromJsonArray(jsonServiceObject: JSONObject): List<MentionSpan> {
        val mentionsResultList: MutableList<MentionSpan> = mutableListOf()
        return try {
            val mentions = jsonServiceObject.getJSONArray(MENTIONS_KEY)
            (0 until mentions.length()).forEach {
                val mentionJsonObject = mentions.getJSONObject(it)
                mentionsResultList.add(getMention(mentionJsonObject))
            }
            mentionsResultList
        } catch (ex: Exception) {
            Timber.e(ex)
            mentionsResultList
        }
    }

    private fun getMention(jsonObject: JSONObject): MentionSpan {
        val uuid = jsonObject.optString(MENTION_UUID_KEY)
        val start = jsonObject.getInt(MENTION_START_KEY)
        val end = jsonObject.getInt(MENTION_END_KEY)
        return MentionSpan(context).apply {
            setPersonUuid(UUIDUtils.fromString(uuid))
            setBounds(start, end)
        }
    }

    companion object {

        /**
         * Cписок из [MentionSpan] привести к виду сервисного объекта.
         */
        fun buildServiceObjectWithMentions(mentions: List<MentionData>): JSONObject {
            val jsonArray = JSONArray()
            mentions.forEach {
                jsonArray.put(it.convertToJsonObject())
            }
            return JSONObject().apply { put(MENTIONS_KEY, jsonArray) }
        }

        private fun MentionData.convertToJsonObject(): JSONObject = JSONObject().apply {
            put(MENTION_UUID_KEY, UUIDUtils.toString(personUuid))
            put(MENTION_START_KEY, start)
            put(MENTION_END_KEY, end)
        }
    }
}

private const val MENTIONS_KEY = "mentions"
private const val MENTION_UUID_KEY = "uuid"
private const val MENTION_START_KEY = "start"
private const val MENTION_END_KEY = "end"