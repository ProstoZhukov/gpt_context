package ru.tensor.sbis.messages_main_screen_addon

import ru.tensor.sbis.messages_main_screen_addon.MessagesMainScreenAddon.Companion.MESSAGES_ITEM_IDENTIFIER

/**
 * Тип пункта навигации модуля сообщений, определяющий используемый navx идентификатор.
 *
 * @author us.bessonov
 */
enum class MessagesItemNavxType(internal val navxId: String) {
    /** Раздел диалогами и каналами. */
    MESSAGES(MESSAGES_ITEM_IDENTIFIER),
    /** Раздел только с диалогами. */
    DIALOGS("dialogs")
}