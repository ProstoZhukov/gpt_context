package ru.tensor.sbis.message_panel.decl

import androidx.annotation.WorkerThread
import ru.tensor.sbis.message_panel.model.QuoteContent
import java.util.UUID

/**
 * Интерфейс объекта, для получения информации о прикладной модели черновика [DRAFT_RESULT]
 *
 * @author vv.chekurda
 */
@WorkerThread
interface DraftResultHelper<in DRAFT_RESULT> {

    /**
     * Возвращает идентификатор черновика
     */
    fun getId(draft: DRAFT_RESULT): UUID

    /**
     * Возвращает `true`, если черновик считается пустым. В состоянии пустого черновика панель ввода может применять
     * внешние изменения
     */
    fun isEmpty(draft: DRAFT_RESULT): Boolean

    /**
     * Возвращает текст черновика
     */
    fun getText(draft: DRAFT_RESULT): String

    /**
     * Возвращает сервисный объект сообщения.
     */
    fun getServiceObject(draft: DRAFT_RESULT): String? = null

    /**
     * Возвращает список получателей, для кого готовился черновик
     */
    fun getRecipients(draft: DRAFT_RESULT): List<UUID>

    /**
     * Возвращает контент цитаты, сохраненной в черновик
     */
    fun getQuoteContent(draft: DRAFT_RESULT): QuoteContent?

    /**
     * Возвращает идентификатор сообщения, ответ на которое содержит черновик
     */
    fun getAnsweredMessageId(draft: DRAFT_RESULT): UUID? = null
}