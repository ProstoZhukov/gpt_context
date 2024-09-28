package ru.tensor.sbis.design.message_view.content.crm_views.chat_bot_buttons

import org.apache.commons.lang3.builder.EqualsBuilder
import org.apache.commons.lang3.builder.HashCodeBuilder
import org.json.JSONObject

/**
 * Дата класс параметров для группы кнопок в чате с ботом.
 *
 * @param titles список заголовков для кнопок.
 * @param needDisable true для блокировки кнопок, иначе для разблокировки.
 * @param needAlignRightSide нужно ли выравнить группу по правому краю экрана.
 * @param needShow нужно ли отображать кнопки.
 *
 * @author dv.baranov
 */
internal data class ChatBotButtonsParams(
    val titles: MutableList<String>,
    val needDisable: Boolean = false,
    val needAlignRightSide: Boolean = false,
    val needShow: Boolean = true
) {
    override fun equals(other: Any?): Boolean =
        other is ChatBotButtonsParams && titles.containsAll(other.titles) &&
            EqualsBuilder()
                .append(titles.size, other.titles.size)
                .append(needDisable, other.needDisable)
                .append(needAlignRightSide, other.needAlignRightSide)
                .append(needShow, other.needShow)
                .isEquals

    override fun hashCode(): Int {
        return HashCodeBuilder()
            .append(titles)
            .append(needDisable)
            .append(needAlignRightSide)
            .append(needShow)
            .toHashCode()
    }
}

/**
 * Получить параметры группы кнопок в чате с ботом из сервисного объекта [serviceObject].
 */
internal fun getChatBotButtonsParamsFromServiceObject(
    jsonObject: JSONObject?
): ChatBotButtonsParams? {
    val jsonServiceObject = jsonObject ?: return null
    val buttonsIsEmpty = jsonServiceObject.optString(CHAT_BOT_BUTTONS_KEY).isEmpty()
    val isActive = jsonServiceObject.optBoolean(CHAT_BOT_BUTTON_ACTIVE_KEY)
    val needShow = !jsonServiceObject.optBoolean(CHAT_BOT_BUTTON_BUTTON_PRESSED_KEY)
    val buttonsTitles = ArrayList<String>()
    if (!buttonsIsEmpty) {
        val buttons = jsonServiceObject.getJSONArray(CHAT_BOT_BUTTONS_KEY)
        (0 until buttons.length()).forEach {
            val buttonServiceObject = buttons.getJSONObject(it)
            val title = buttonServiceObject.optString(CHAT_BOT_BUTTON_CAPTION_KEY)
            if (title.isNotEmpty()) buttonsTitles.add(title)
        }
    }
    return ChatBotButtonsParams(
        titles = buttonsTitles.toMutableList(),
        needDisable = !isActive,
        needAlignRightSide = false,
        needShow = needShow
    )
}

private const val CHAT_BOT_BUTTONS_KEY = "chatbot_buttons"
private const val CHAT_BOT_BUTTON_CAPTION_KEY = "name"
private const val CHAT_BOT_BUTTON_ACTIVE_KEY = "isActive"
private const val CHAT_BOT_BUTTON_BUTTON_PRESSED_KEY = "is_active"
