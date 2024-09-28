package ru.tensor.sbis.pushnotification.util

import org.json.JSONObject
import ru.tensor.sbis.common.util.UUIDUtils
import ru.tensor.sbis.common.util.date.DateParseTemplate
import ru.tensor.sbis.common.util.date.DateParseUtils
import java.util.*

/**
 * Утилита для парсинга отдельных стандартных полей пуш уведомления
 *
 * @author am.boldinov
 */
object PushParserUtil {

    /**
     * Парсинг даты из пуш уведомления с учетом временной зоны
     *
     * @param dateString дата уведомления из пуша строкой
     * @return дата уведомления или null
     */
    @JvmStatic
    fun parseDate(dateString: String?): Date? {
        if (dateString == null) {
            return null
        }
        var date =
            DateParseUtils.parseDate(DateParseUtils.adaptTimezone(dateString), DateParseTemplate.WITH_LONG_MILLISECONDS)
        if (date != null) {
            return date
        }
        date = DateParseUtils.parseDate(dateString, DateParseTemplate.WITH_LONG_MILLISECONDS_NO_TIMEZONE)
        return date ?: DateParseUtils.parseDate(dateString as String?) //overload костыль из-за перегрузки методов
    }

    /**
     * Парсинг json структуры данных из пуша
     *
     * @param source строка для преобразования
     * @return сформированный json
     */
    @JvmStatic
    fun parseJson(source: String?): JSONObject {
        if (source.isNullOrEmpty()) {
            return JSONObject()
        }
        return try {
            JSONObject(source)
        } catch (e: Exception) {
            PushLogger.error(e)
            JSONObject()
        }
    }

    /**
     * Метод-расширение для парсинга непустой строки из json
     *
     * @param key ключ для парсинга строки
     * @return непустая строка по ключу, либо null
     */
    fun JSONObject.optStringNonEmpty(key: String): String? {
        if (isNull(key)) {
            return null
        }
        val result = optString(key)
        return if (!result.isNullOrEmpty()) {
            result
        } else {
            null
        }
    }

    /**
     * Метод-расширение для парсинга UUID из json
     *
     * Идентификатор формируется по строке, если она пустая, метод возвращает null
     *
     * @param key ключ для парсинга UUID
     * @return UUID, либо null
     */
    fun JSONObject.optUUID(key: String): UUID? {
        return UUIDUtils.fromString(optStringNonEmpty(key))
    }
}