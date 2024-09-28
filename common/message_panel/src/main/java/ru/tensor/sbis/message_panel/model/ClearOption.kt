package ru.tensor.sbis.message_panel.model

/**
 * Опции очистки панели ввода после отправки сообщения
 *
 * @author us.bessonov
 */
enum class ClearOption {
    /**
     * Сброс получателей
     */
    CLEAR_RECIPIENTS,
    /**
     * Скрытие клавиатуры
     */
    HIDE_KEYBOARD
}
