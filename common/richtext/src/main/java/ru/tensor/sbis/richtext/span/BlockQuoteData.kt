package ru.tensor.sbis.richtext.span

import java.util.UUID

/**
 * Данные по цитате.
 *
 * @property messageUuid идентификатор сообщения-цитаты
 * @property messageTheme идентификатор темы, к которой относится сообщение
 * @property senderUuid идентификатор отправителя сообщения, которое было процитировано
 *
 * @author am.boldinov
 */
data class BlockQuoteData(
    val messageUuid: UUID,
    val messageTheme: UUID,
    val senderUuid: UUID,
)