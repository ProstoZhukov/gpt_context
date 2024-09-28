package ru.tensor.sbis.message_panel.decl

import java.util.UUID

/**
 * Параметры для редактирования
 *
 * @author vv.chekurda
 */
data class EditArguments(
    /**
     * Идентификатор редактируемого сообщения
     */
    val messageUuid: UUID,
    /**
     * Отредактированный текст
     */
    val text: String,
    /**
     * Сервисный объект (например, со список упоминаний), прилагаемый к редактируемому сообщению.
     */
    val serviceObject: String? = null
)