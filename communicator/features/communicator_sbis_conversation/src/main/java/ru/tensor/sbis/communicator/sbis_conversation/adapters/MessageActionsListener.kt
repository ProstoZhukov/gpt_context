package ru.tensor.sbis.communicator.sbis_conversation.adapters

import android.view.View
import ru.tensor.sbis.attachments.models.AttachmentModel
import ru.tensor.sbis.communicator.base.conversation.presentation.adapter.holders.BaseMessageActionsListener
import ru.tensor.sbis.communicator.common.conversation.data.Message
import ru.tensor.sbis.communicator.sbis_conversation.data.model.ConversationMessage
import ru.tensor.sbis.communicator.base.conversation.presentation.ui.ServiceMessageActionListener
import ru.tensor.sbis.communicator.generated.AttachmentViewModel
import ru.tensor.sbis.design.cloud_view.content.phone_number.PhoneNumberClickListener
import java.util.*

/**
 * Слушатель действий над сообщениями.
 * @see BaseMessageActionsListener
 * @see ServiceMessageActionListener
 *
 * @author vv.chekurda
 */
internal interface MessageActionsListener
    : BaseMessageActionsListener<ConversationMessage>,
    ServiceMessageActionListener,
    PhoneNumberClickListener,
    MessageSignButtonListener,
    MessageAccessButtonListener,
    MediaMessageActionListener,
    MessageThreadActionListener,
    SenderActionClickListener

/**
 * Слушатель действий над кнопками подписать/отклонить.
 */
internal interface MessageSignButtonListener {

    /**
     * Клик по подтверждению подписания
     *
     * @param data модель сообщения
     */
    fun onAcceptSigningButtonClicked(data: ConversationMessage)

    /**
     * Клик по отклонению подписания
     *
     * @param data модель сообщения
     */
    fun onRejectSigningButtonClicked(data: ConversationMessage)
}

/**
 * Слушатель действий над сообщениями с разрешениями.
 */
internal interface MessageAccessButtonListener {

    /**
     * Клик по кнопке разрешающей доступ к файлу.
     * @param data модель сообщения
     */
    fun onGrantAccessButtonClicked(data: ConversationMessage, sender: View)

    /**
     * Клик по кнопке отказа в доступе к файлу.
     * @param data модель сообщения
     */
    fun onDenyAccessButtonClicked(data: ConversationMessage)
}

/**
 * Слушатель действий над медиасообщениями.
 */
internal interface MediaMessageActionListener {
    /**
     * Обработчик нажатия на кнопку расширения распознанного текста в медиа сообщении
     * @return необходимость подскрола во время анимации.
     */
    fun onMediaMessageExpandClicked(data: ConversationMessage, expanded: Boolean): Boolean

    /**
     * Ошибка воспроизведения аудио или видео сообщения
     */
    fun onMediaPlaybackError(error: Throwable)
}

/**
 * Слушатель действий над тредами.
 */
internal interface MessageThreadActionListener {
    /**
    * Обработать клик по треду.
    */
    fun onThreadMessageClicked(data: ConversationMessage)

    /**
     * Обработать клик по сервисному сообщению о создании треда.
     */
    fun onThreadCreationServiceClicked(data: ConversationMessage)
}

internal interface SenderActionClickListener {
    /**
     * Клик по аватарке отправителя сообщения
     *
     * @param senderUuid идентификатор отправителя
     */
    fun onPhotoClicked(senderUuid: UUID)

    /**
     * Клик по имени отправителя сообщения
     *
     * @param senderUuid идентификатор отправителя
     */
    fun onSenderNameClicked(senderUuid: UUID)
}

/**
 * Обертка над слушателем [MessageActionsListener]
 * для возможности подмены имплементирующей реализации [listener].
 * Механика необходима для возможности повторного переиспользования адаптера и холдеров сообщений.
 */
internal class MessageActionsListenerWrapper(
    private var listener: MessageActionsListener? = null
) : MessageActionsListener {

    fun init(listener: MessageActionsListener) {
        this.listener = listener
    }

    fun clear() {
        listener = null
    }

    override fun onAcceptSigningButtonClicked(data: ConversationMessage) {
        listener?.onAcceptSigningButtonClicked(data)
    }

    override fun onRejectSigningButtonClicked(data: ConversationMessage) {
        listener?.onRejectSigningButtonClicked(data)
    }

    override fun onGrantAccessButtonClicked(data: ConversationMessage, sender: View) {
        listener?.onGrantAccessButtonClicked(data, sender)
    }

    override fun onDenyAccessButtonClicked(data: ConversationMessage) {
        listener?.onDenyAccessButtonClicked(data)
    }

    override fun onMediaMessageExpandClicked(data: ConversationMessage, expanded: Boolean): Boolean =
        listener?.onMediaMessageExpandClicked(data, expanded) ?: false

    override fun onMediaPlaybackError(error: Throwable) {
        listener?.onMediaPlaybackError(error)
    }

    override fun onPhotoClicked(senderUuid: UUID) {
        listener?.onPhotoClicked(senderUuid)
    }

    override fun onSenderNameClicked(senderUuid: UUID) {
        listener?.onSenderNameClicked(senderUuid)
    }

    override fun onMessageAttachmentClicked(message: Message, attachment: AttachmentViewModel) {
        listener?.onMessageAttachmentClicked(message, attachment)
    }

    override fun onMessageSelected(conversationMessage: ConversationMessage) {
        listener?.onMessageSelected(conversationMessage)
    }

    override fun onMessageClicked(conversationMessage: ConversationMessage) {
        listener?.onMessageClicked(conversationMessage)
    }

    override fun onMessageErrorStatusClicked(conversationMessage: ConversationMessage) {
        listener?.onMessageErrorStatusClicked(conversationMessage)
    }

    override fun onLinkClicked() {
        listener?.onLinkClicked()
    }

    override fun onQuoteClicked(quotedMessageUuid: UUID) {
        listener?.onQuoteClicked(quotedMessageUuid)
    }

    override fun onQuoteLongClicked(enclosingMessageUuid: UUID) {
        listener?.onQuoteLongClicked(enclosingMessageUuid)
    }

    override fun onMessageQuotedBySwipe(message: ConversationMessage) {
        listener?.onMessageQuotedBySwipe(message)
    }

    override fun onServiceMessageClicked(position: Int) {
        listener?.onServiceMessageClicked(position)
    }

    override fun onPhoneNumberClicked(phoneNumber: String) {
        listener?.onPhoneNumberClicked(phoneNumber)
    }

    override fun onPhoneNumberLongClicked(phoneNumber: String, messageUUID: UUID?) {
        listener?.onPhoneNumberLongClicked(phoneNumber, messageUUID)
    }

    override fun onDeleteUploadClicked(message: ConversationMessage, attachmentModel: AttachmentModel) {
        listener?.onDeleteUploadClicked(message, attachmentModel)
    }

    override fun onRetryUploadClicked(message: ConversationMessage, attachmentModel: AttachmentModel) {
        listener?.onRetryUploadClicked(message, attachmentModel)
    }

    override fun onErrorUploadClicked(
        message: ConversationMessage,
        attachmentModel: AttachmentModel,
        errorMessage: String
    ) {
        listener?.onErrorUploadClicked(message, attachmentModel, errorMessage)
    }

    override fun onThreadMessageClicked(data: ConversationMessage) {
        listener?.onThreadMessageClicked(data)
    }

    override fun onThreadCreationServiceClicked(data: ConversationMessage) {
        listener?.onThreadCreationServiceClicked(data)
    }
}