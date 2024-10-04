package ru.tensor.sbis.message_panel.declaration.data

import java.util.*

/**
 * Набор моделей содержимого панели, которые можно устанавливать извне
 *
 * @author ma.kolpakov
 */
sealed class MessagePanelContent

/**
 * Модель текстового содержимого
 *
 * @property text текст сообщения
 */
open class TextContent(
    val text: String
) : MessagePanelContent()

/**
 * Модель цитируемого содержимого
 *
 * @property uuid идентификатор цитируемого сообщения
 * @property sender строка с информацией об отправителе (ФИО)
 */
class QuoteContent(
    val uuid: UUID,
    val sender: String,
    text: String
) : TextContent(text)

/**
 * Модель содержимого, которым нужно поделиться
 *
 * @property fileUriList список *uri* путей файлов
 */
class ShareContent(
    text: String,
    val fileUriList: List<String>
) : TextContent(text)