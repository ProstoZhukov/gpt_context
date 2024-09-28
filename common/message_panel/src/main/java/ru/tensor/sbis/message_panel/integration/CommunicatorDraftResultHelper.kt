package ru.tensor.sbis.message_panel.integration

import org.apache.commons.lang3.StringUtils.EMPTY
import ru.tensor.sbis.communicator.generated.Message
import ru.tensor.sbis.communicator.generated.MessageContentItem
import ru.tensor.sbis.communicator.generated.MessageContentItemType
import ru.tensor.sbis.message_panel.decl.DraftResultHelper
import ru.tensor.sbis.message_panel.model.QuoteContent
import java.util.UUID

/**
 * Реализация [DraftResultHelper] для работы с черновиком микросервиса сообщений
 *
 * @author vv.chekurda
 */
internal class CommunicatorDraftResultHelper : DraftResultHelper<CommunicatorDraftMessage> {

    override fun getId(draft: CommunicatorDraftMessage): UUID =
        draft.data.id

    override fun isEmpty(draft: CommunicatorDraftMessage): Boolean =
        draft.data.text.isEmpty() && draft.quote == null

    override fun getText(draft: CommunicatorDraftMessage): String =
        draft.data.text

    override fun getServiceObject(draft: CommunicatorDraftMessage): String? =
        draft.data.serviceObject

    override fun getRecipients(draft: CommunicatorDraftMessage): List<UUID> =
        draft.data.recipients

    override fun getQuoteContent(draft: CommunicatorDraftMessage): QuoteContent? =
        draft.quote?.let {
            QuoteContent(
                it.uuid,
                "${it.sender.name.last} ${it.sender.name.first}".trim(),
                getMessageTextForQuote(it)
            )
        }

    private fun getMessageTextForQuote(message: Message): String =
        // поиск текста или ссылок по контенту сообщения
        findTextInChilds(message.content, message.rootElements)
            // если пусто -> проверка наличия цититы в контенте
            ?: findFirstQuoteItem(message)?.let {
                // поиск текста или ссылок по контенту цитаты
                findTextInChilds(message.content, it.children)
            } ?: EMPTY

    private fun findFirstQuoteItem(message: Message): MessageContentItem? =
        message.rootElements.find { message.content[it].itemType == MessageContentItemType.QUOTE }
            ?.let { message.content[it] }

    private fun findTextInChilds(itemList: List<MessageContentItem>, childIndexes: List<Int>): String? {
        val result = StringBuilder()
        childIndexes.map(itemList::get).forEach {
            val text = when {
                it.itemType == MessageContentItemType.TEXT && it.text.isNotEmpty() -> "${it.text.trim()} "
                it.itemType == MessageContentItemType.LINK && !it.linkUrl.isNullOrEmpty() -> "${it.linkUrl} "
                else -> EMPTY
            }
            result.append(text)
        }
        return result.toString().trim().takeIf { it.isNotEmpty() }
    }

    override fun getAnsweredMessageId(draft: CommunicatorDraftMessage): UUID? = draft.data.answer
}