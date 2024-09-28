package ru.tensor.sbis.design.message_view.utils.rich_text_converter

import android.text.Spannable
import org.json.JSONObject
import ru.tensor.sbis.communicator.generated.Message

/**
 * Конвертер обычной строки в декорированную.
 *
 * @author da.zhukov
 */
interface MessageRichTextConverter {

    /**
     * Конвертировать текст с набором тегов в spannable строку.
     */
    fun convert(source: String): Spannable

    /**
     * Получить декорированный текст сообщения.
     */
    fun getRichTextMessage(
        message: Message,
        serviceObject: JSONObject?
    ): Spannable

    /**
     * Очистить ссылки.
     */
    fun clearReferences()
}