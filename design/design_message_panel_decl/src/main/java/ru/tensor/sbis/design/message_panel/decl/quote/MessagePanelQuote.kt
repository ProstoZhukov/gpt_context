package ru.tensor.sbis.design.message_panel.decl.quote

/**
 * Модель цитируемого содержимого
 *
 * @property title заголовок цитаты (например, строка с информацией об отправителе (ФИО))
 * @property text текст цитируемого сообщения
 *
 * @author ma.kolpakov
 */
data class MessagePanelQuote(
    val title: String,
    val text: String
)
