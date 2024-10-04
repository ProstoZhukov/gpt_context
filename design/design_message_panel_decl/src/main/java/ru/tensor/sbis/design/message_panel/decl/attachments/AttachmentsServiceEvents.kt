package ru.tensor.sbis.design.message_panel.decl.attachments

import kotlinx.coroutines.flow.Flow

/**
 * Набор подписок на события загрузки вложений.
 * Оформлено отдельным интерфейсом для возможной реализации делегатом
 *
 * @author ma.kolpakov
 */
interface AttachmentsServiceEvents {

    /**
     * Подписка на обновление прогресса по загрузке вложений
     */
    val uploadingProgress: Flow<AttachmentUploadingProgress>

    /**
     * Подписка на возникающие при загрузке вложений ошибки
     */
    val uploadingError: Flow<AttachmentUploadingError>
}