package ru.tensor.sbis.design.message_panel.decl.draft

import ru.tensor.sbis.design.message_panel.decl.quote.MessagePanelQuote
import java.util.*

/**
 * Вспомогательный класс для предоставления информации о моделях из сервиса [MessageDraftService]
 *
 * @author ma.kolpakov
 */
interface MessageDraftServiceHelper<in DRAFT> {

    /**
     * Возвращает идентификатор черновика
     */
    suspend fun getId(draft: DRAFT): UUID

    /**
     * Возвращает `true`, если черновик считается пустым. В состоянии пустого черновика панель
     * ввода может применять внешние изменения
     */
    suspend fun isEmpty(draft: DRAFT): Boolean

    /**
     * Возвращает текст черновика
     */
    suspend fun getText(draft: DRAFT): String

    /**
     * Возвращает список получателей, для кого готовился черновик
     */
    suspend fun getRecipients(draft: DRAFT): List<UUID>

    /**
     * Возвращает контент цитаты, сохраненной в черновик
     */
    suspend fun getQuoteContent(draft: DRAFT): MessagePanelQuote?

    /**
     * Возвращает идентификатор сообщения, ответ на которое содержит черновик
     */
    suspend fun getAnsweredMessageId(draft: DRAFT): UUID?
}
