package ru.tensor.sbis.message_panel.interactor.message

import io.reactivex.Completable
import io.reactivex.Single
import ru.tensor.sbis.attachments.generated.FileInfo
import ru.tensor.sbis.common.generated.CommandStatus
import ru.tensor.sbis.communicator.generated.MessageTextWithMentions
import ru.tensor.sbis.communicator.generated.SignActions
import java.util.UUID

/**
 * @author vv.chekurda
 * Создан 10/3/2019
 */
interface MessagePanelMessageInteractor<out MESSAGE_RESULT, out MESSAGE_SENT_RESULT> {

    fun sendMessage(
        text: String?,
        attachments: List<FileInfo>,
        recipientUuids: List<UUID>?,
        documentUuid: UUID?,
        conversationUuid: UUID?,
        folderUuid: UUID?,
        signActions: SignActions?,
        quotedMessageUuid: UUID?,
        answeredMessageUuid: UUID?,
        metaData: String?
    ): Single<out MESSAGE_SENT_RESULT>

    fun sendLink(conversationUuid: UUID, url: String): Completable

    fun editMessage(editingMessage: UUID, newMessageText: String): Single<out MESSAGE_RESULT>

    fun getMessageByUuid(messageUuid: UUID, conversationUuid: UUID, documentUuid: UUID?): Single<out MESSAGE_RESULT>

    fun getMessageText(messageUuid: UUID, conversationUuid: UUID): Single<MessageTextWithMentions>

    fun notifyUserTyping(conversationUuid: UUID): Completable

    /**
     * Начать редактирование сообщения.
     */
    fun beginEditMessage(editingMessage: UUID): Single<CommandStatus>

    /**
     * Применить изменения к сообщению (текст передается в метод, вложения крепятся через addAttachments.
     */
    fun commitEditMessage(
        editingMessage: UUID,
        newMessageText: String,
        serviceObject: String?
    ): Single<out MESSAGE_RESULT>

    /**
     * Отменить редактирование сообщения.
     */
    fun cancelEditMessage(editingMessage: UUID): Single<CommandStatus>

    /**
     * Изменить эмоцию отправленного медиа сообщения.
     */
    fun editMediaMessageEmotion(messageUuid: UUID, emotionCode: Int): Completable

    /**
     * Установить название экрана для аналитики.
     */
    fun setAnalyticsUsageName(name: String?)
}