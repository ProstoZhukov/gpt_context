package ru.tensor.sbis.message_panel.model

import java.util.*

/**
 * Набор моделей содержимого панели, которые можно устанавливать извне
 *
 * @author vv.chekurda
 * Создан 8/10/2019
 */
@Deprecated("https://online.sbis.ru/opendoc.html?guid=bb1754f3-4936-4641-bdc2-beec53070c4b")
sealed class MessagePanelContent

/**
 * Модель текстового содержимого
 *
 * @property text текст сообщения
 */
@Deprecated("https://online.sbis.ru/opendoc.html?guid=bb1754f3-4936-4641-bdc2-beec53070c4b")
open class TextContent(
    val text: String
) : MessagePanelContent()

/**
 * Модель цитируемого содержимого
 *
 * @property uuid идентификатор цитируемого сообщения
 * @property sender строка с информацией об отправителе (ФИО)
 */
@Deprecated("https://online.sbis.ru/opendoc.html?guid=bb1754f3-4936-4641-bdc2-beec53070c4b")
class QuoteContent(
    val uuid: UUID,
    val sender: String,
    text: String
) : TextContent(text)

/**
 * Модель редактируемого содержимого.
 *
 * @property uuid идентификатор редактируемого сообщения
 * @property subtitle подзаголовок редакции сообщения, null - текст сообщения.
 * @property text текст для начала редакции сообщения, null - текст сообщения.
 * @property isAttachmentsEditable признак возможности редактировать вложения.
 */
data class EditContent(
    val uuid: UUID,
    val subtitle: String? = null,
    val text: String? = null,
    val isAttachmentsEditable: Boolean = false
) : MessagePanelContent()

/**
 * Модель содержимого, которым нужно поделиться
 *
 * @property fileUriList список *uri* путей файлов
 */
@Deprecated("https://online.sbis.ru/opendoc.html?guid=bb1754f3-4936-4641-bdc2-beec53070c4b")
class ShareContent(
    text: String,
    val fileUriList: List<String>
) : TextContent(text)