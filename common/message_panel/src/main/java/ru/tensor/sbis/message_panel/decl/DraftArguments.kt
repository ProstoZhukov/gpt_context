package ru.tensor.sbis.message_panel.decl

import java.util.UUID

/**
 * Параметры для черновика
 *
 * @author vv.chekurda
 */
data class DraftArguments(
    /**
     * Идентификатор обсуждения или документа
     */
    val themeUuid: UUID?,
    /**
     * Идентификатор загруженного черновика
     */
    val draftUuid: UUID,
    /**
     * Список идентификаторов получателей, кому подготовлен черновик
     */
    val recipientsUuidList: List<UUID>,
    /**
     * Текст черновика (пользовательский ввод)
     */
    val text: String?,
    /**
     * Список идентификаторов вложений, которые добавлены в черновик
     */
    val attachmentUuidList: List<UUID>,
    /**
     * Идентификатор цитируемого сообщения, на которое добавлен черновик
     */
    val quoteUuid: UUID?,
    /**
     * Идентификатор сообщения, для ответа на которое добавлен черновик
     */
    val answerUuid: UUID?,
    /**
     * Сервисный объект (например, с списком упоминаний) сообщения.
     */
    val serviceObject: String?
)