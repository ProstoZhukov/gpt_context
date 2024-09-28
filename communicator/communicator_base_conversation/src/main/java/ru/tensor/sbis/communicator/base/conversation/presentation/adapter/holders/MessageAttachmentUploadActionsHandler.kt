package ru.tensor.sbis.communicator.base.conversation.presentation.adapter.holders

import ru.tensor.sbis.attachments.models.AttachmentModel
import ru.tensor.sbis.communicator.base.conversation.data.model.BaseConversationMessage

/**
 * Обработчик действий загрузки исходящих вложений в сообщении.
 *
 * @author vv.chekurda
 */
interface MessageAttachmentUploadActionsHandler<MESSAGE : BaseConversationMessage> {

    /**
     * Обработать клик по кнопке удаления загружаемого вложения.
     */
    fun onDeleteUploadClicked(message: MESSAGE, attachmentModel: AttachmentModel) = Unit

    /**
     * Обработать клик по кнопке повтора загрузки вложения с ошибкой.
     */
    fun onRetryUploadClicked(message: MESSAGE, attachmentModel: AttachmentModel) = Unit

    /**
     * Обработать клик по иконке ошибки загрузки вложения.
     */
    fun onErrorUploadClicked(message: MESSAGE, attachmentModel: AttachmentModel, errorMessage: String) = Unit
}