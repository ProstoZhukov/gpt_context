package ru.tensor.sbis.communicator.base.conversation.interactor

import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import org.apache.commons.lang3.NotImplementedException
import ru.tensor.sbis.common.generated.CommandStatus
import ru.tensor.sbis.communicator.base.conversation.data.BaseConversationData
import ru.tensor.sbis.communicator.base.conversation.data.model.BaseConversationMessage
import ru.tensor.sbis.communicator.generated.DataRefreshedMessageControllerCallback
import ru.tensor.sbis.communicator.generated.MarkMessagesResult
import ru.tensor.sbis.communicator.generated.MessageErrorResult
import ru.tensor.sbis.communicator.generated.MessageFilter
import ru.tensor.sbis.communicator.generated.MessageListResult
import ru.tensor.sbis.communicator.generated.MessageTextWithMentions
import ru.tensor.sbis.communicator.generated.ServiceMessageGroup
import ru.tensor.sbis.platform.generated.Subscription
import java.util.*

/**
 * Базовый интерфейс интерактора сообщений.
 *
 * @author vv.chekurda
 */
interface BaseConversationInteractor<MESSAGE : BaseConversationMessage> {

    /**
     * Метод для подписки на события ThemeController для актуализации информации о переписке (ConversationData).
     */
    fun observeThemeControllerUpdates(): Observable<HashMap<String, String>>

    /**
     * Метод для подписки на события ThemeController для актуализации информации о переписке (ConversationData).
     */
    fun observeMessageControllerCallbackSubscription(callback: DataRefreshedMessageControllerCallback): Observable<Subscription>

    /**
     * Метод для подписки на события ThemeController о печатающих пользователях.
     */
    fun observeTypingUsers(): Observable<List<String>> = Observable.empty()

    /**
     * Повторить отправку сообщения из любого состояния, кроме "доставлено".
     *
     * @param messageUuid идентификатор сообщения.
     */
    fun forceResendMessage(messageUuid: UUID): Single<CommandStatus>

    /**
     * Удалить сообщение только у меня.
     *
     * @param conversationUuid uuid переписки.
     * @param messageUuid      uuid сообщения.
     * @return модель результата
     */
    fun deleteMessageForMe(conversationUuid: UUID, messageUuid: UUID): Single<MessageListResult>

    /**
     * Переписка сейчас откроется.
     * Метод необходимо вызывать, чтобы контроллер сбросил timestampSentLocal для доставленных на облако сообщений.
     * Иначе сообщения в переписке могут отображаться в неправильном порядке.
     */
    fun onThemeBeforeOpened(themeUUID: UUID)

    /**
     * Переписка открылась.
     * Метод необходимо вызывать, когда переписка отображается на переднем плане,
     * чтобы контроллер знал о приоритете синхронизации.
     *
     * @param themeUUID идентификатор переписки.
     */
    fun onThemeAfterOpened(themeUUID: UUID, filter: MessageFilter?, isChat: Boolean): Completable

    /**
     * Переписка закрылась.
     * Метод необходимо вызывать, когда переписка закрывается или уходит на второй план,
     * чтобы контроллер изменил приоритеты синхронизации.
     *
     * @param themeUUID идентификатор переписки.
     */
    fun onThemeClosed(themeUUID: UUID): Completable

    /**
     * Получить текст из сообщения.
     *
     * @param conversationUuid uuid переписки.
     * @param messageUuid      uuid сообщения.
     * @return текст из сообщения.
     */
    fun getMessageText(conversationUuid: UUID, messageUuid: UUID): Single<MessageTextWithMentions>

    /**
     * Получить ошибку сообщения.
     */
    fun getMessageError(messageUuid: UUID): Maybe<MessageErrorResult>

    /**
     * Получить модель сообщения из кэша по идентификатору [uuid].
     */
    fun getMessageByUuid(uuid: UUID): Maybe<MESSAGE>

    /**
     * Пометить список сообщений [messagesUuid] как прочитанные.
     */
    fun markMessagesAsRead(messagesUuid: ArrayList<UUID>): Single<MarkMessagesResult>

    /**
     * Пометить список сервисных сообщений [serviceMessageGroup] как прочитанные в обсуждении [conversationUuid].
     */
    fun markGroupServiceMessageAsRead(
        conversationUuid: UUID,
        serviceMessageGroup: List<ServiceMessageGroup>
    ): Observable<Pair<ServiceMessageGroup, CommandStatus>>

    /**
     * Отменить загрузку вложения в исходящем сообщении.
     */
    fun cancelUploadAttachment(messageUuid: UUID, attachmentLocalId: Long): Single<CommandStatus> =
        Single.error(NotImplementedError())

    /**
     * Удалить диалог в архив.
     *
     * @param conversationUuid идентификатор переписки.
     * @return статус операции.
     */
    fun deleteDialog(conversationUuid: UUID): Single<CommandStatus> =
        Single.error(NotImplementedException("Not yet implemented"))

    /**
     * Удалить/восстановить диалог из архива.
     *
     * @param conversationUuid идентификатор переписки.
     * @param deleteFromArchive true, если удалить навсегда из архива, и false, если восстановить из архива.
     * @return статус операции.
     */
    fun deleteDialogFromArchive(conversationUuid: UUID, deleteFromArchive: Boolean): Single<CommandStatus> =
        Single.error(NotImplementedException("Not yet implemented"))

    /**
     * Удалить сообщение у всех.
     *
     * @param conversationUuid идентификатор переписки.
     * @param messageUuid      идентификатор сообщения.
     * @return модель удаленного сообщения.
     */
    fun deleteMessageForEveryone(conversationUuid: UUID, messageUuid: UUID): Completable

    suspend fun checkMessagesArea(filter: MessageFilter): CommandStatus

    /**
     * Обработать нажатие на кнопку, присланную чат-ботом.
     *
     * @param conversationUuid идентификатор переписки.
     * @param serviceMessageUUID идентификатор сообщения, в котором были кнопки.
     * @param title заголовок кнопки.
     */
    suspend fun onChatBotButtonClick(conversationUuid: UUID, serviceMessageUUID: UUID, title: String)

    interface BaseConversationDataResult<DATA : BaseConversationData> {
        val conversationData: DATA
        val commandStatus: CommandStatus
    }
}

