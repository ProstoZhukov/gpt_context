package ru.tensor.sbis.communicator.sbis_conversation.interactor

import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import ru.tensor.sbis.attachments.generated.FileInfoViewModel
import ru.tensor.sbis.common.generated.CommandStatus
import ru.tensor.sbis.communicator.base.conversation.interactor.BaseConversationInteractor
import ru.tensor.sbis.communicator.common.conversation.data.Message
import ru.tensor.sbis.communicator.common.data.ThreadInfo
import ru.tensor.sbis.communicator.generated.ChatResult
import ru.tensor.sbis.communicator.generated.CreateDraftDialogResult
import ru.tensor.sbis.communicator.generated.DataRefreshedMessageControllerCallback
import ru.tensor.sbis.communicator.generated.DocumentAccessType
import ru.tensor.sbis.communicator.generated.MessageFilter
import ru.tensor.sbis.communicator.sbis_conversation.data.model.ConversationMessage
import ru.tensor.sbis.communicator.sbis_conversation.interactor.data.ConversationDataInteractor
import ru.tensor.sbis.design.profile_decl.person.PersonData
import ru.tensor.sbis.edo_decl.document.DocumentType
import ru.tensor.sbis.person_decl.profile.model.ProfileActivityStatus
import java.util.UUID
import ru.tensor.sbis.communicator.generated.Message as ControllerMessage

/**
 * Интерактор экрана сообщений сбис
 *
 * @author vv.chekurda
 */
internal interface ConversationInteractor
    : BaseConversationInteractor<ConversationMessage>, ConversationDataInteractor {

    /**
     * Очистка ссылок
     */
    fun clearReferences()

    /**
     * Загрузить содержимое группы сервисных сообщений
     *
     * @param serviceGroupUuid uuid сервисной группы
     * @return Observable со списком сервисных сообщений группы
     */
    fun loadServiceMessages(serviceGroupUuid: UUID): Observable<List<ConversationMessage>>

    /**
     * Создать драфт диалога или вернуть UUID существующего диалога если он есть (для обсуждения по документу).
     *
     * @param dialogUuid       идентификатор диалога
     * @param documentUuid     идентификатор документа
     * @param documentType     тип диалога
     * @param folderUuid       идентификатор папки
     * @param newMessageUuid   идентификатор нового сообщения
     * @param participantUuids список идентификаторов участников диалога
     * @param threadInfo       информация для создания треда
     * @return результат создания драфтового диалога
     */
    fun createDraftDialogIfNotExists(
        dialogUuid: UUID?,
        documentUuid: UUID?,
        documentType: DocumentType?,
        folderUuid: UUID?,
        newMessageUuid: UUID?,
        participantUuids: List<UUID>,
        threadInfo: ThreadInfo? = null
    ): Single<Pair<CreateDraftDialogResult, List<UUID>>>

    /**
     * Удалить драфтовый диалог.
     */
    fun deleteDraftDialog(dialogUuid: UUID): Completable

    /**
     * Обновить участников диалога
     *
     * @param conversationUuid идентификатор переписки
     * @param participantUuids список всех участников
     * @return статус операции
     */
    fun updateDialog(conversationUuid: UUID, participantUuids: Collection<UUID>): Single<CommandStatus>

    /**
     * Отметить прочитанными все сообщения переписки
     *
     * @param conversationUuid идентификатор переписки
     * @return статус операции
     */
    fun markAllMessages(conversationUuid: UUID): Single<CommandStatus>

    /**
     * Отклонить подписание
     *
     * @param message модель сообщения, по которому запрашивали подписание
     * @return статус операции
     */
    fun rejectSignature(message: Message): Single<CommandStatus>

    /**
     * Подписаться на статус активности пользователя в шапке.
     *
     * @param personUuid идентификатор персоны.
     * @return hot Observable с актуальным статусом активности
     */
    fun subscribeActivityObserver(personUuid: UUID): Observable<ProfileActivityStatus>

    /**
     * Запросить обновление текущего статуса активности персоны.
     */
    fun forceUpdateActivityStatus(personUuid: UUID): Completable

    /**
     * Выйти из чата и скрыть его
     *
     * @param chatUuid идентификатор чата
     * @return статус операции
     */
    fun quitAndHideChat(chatUuid: UUID): Single<CommandStatus>

    /**
     * Выйти из чата
     *
     * @param chatUuid идентификатор чата
     * @return статус операции
     */
    fun quitChat(chatUuid: UUID): Single<CommandStatus>

    /**
     * Скрыть чат
     *
     * @param chatUuid идентификатор чата
     * @return статус операции
     */
    fun hideChat(chatUuid: UUID): Single<CommandStatus>

    /**
     * Восстановить чат
     *
     * @param chatUuid идентификатор чата
     * @return статус операции
     */
    fun restoreChat(chatUuid: UUID): Single<CommandStatus>

    /**
     * Загрузить список имен добавленных участников чата из сервисного сообщения
     * и добавить их в это сообщение
     *
     * @param serviceMessageUuid идентификатор сервисного сообщения
     * @param newPersonListCount количество запрашиваемых имен
     * @return обновленное сервисное сообщение
     */
    fun loadServiceMessageNames(serviceMessageUuid: UUID, newPersonListCount: Int): Single<ConversationMessage>

    /**
     * Изменить тип уведомлений о новых сообщениях в чате
     *
     * @param chatUuid     идентификатор чата
     * @param onlyPersonal true, если уведомлять только о персональный
     * @return [ChatResult], в случае успешного переключения
     */
    fun changeNotificationType(chatUuid: UUID, onlyPersonal: Boolean): Single<ChatResult>

    /**
     * Добавить новых участников в чат
     *
     * @param chatUuid         идентификатор чата
     * @param participantUuids список идентификаторов участников, которых нужно добавить
     */
    fun addChatParticipants(chatUuid: UUID, participantUuids: List<UUID>): Completable

    /**
     * Получить идентификатор приватного чата по идентификатору участника
     *
     * @param participantUUID идентификатор участника
     * @return идентификатор личного чата
     */
    fun getPrivateChatUUID(participantUUID: UUID): Single<UUID>

    /**
     * Получение ссылки на переписку через uuid
     * @param themeUUID идентификатор переписки
     */
    fun getUrlById(themeUUID: UUID): Observable<String>

    /**
     * Сообщение успешно подписано
     *
     * @param message модель сообщения
     * @return статус операции
     */
    fun onMessageSigningSuccess(message: Message): Single<CommandStatus>

    /**
     * Добавить скрытый чат обратно в реестр
     *
     * @param chatUuid идентификатор чата
     */
    fun unhideChat(chatUuid: UUID): Completable

    /**
     * Добавить удаленный диалог обратно в реестр
     *
     * @param dialogUuid идентификатор диалога
     */
    fun restoreDialog(dialogUuid: UUID): Completable

    /**
     * Закрепить сообщение в чате
     *
     * @param chatUuid    идентификатор чата
     * @param messageUuid идентификатор сообщения
     */
    fun pinMessage(chatUuid: UUID, messageUuid: UUID): Completable

    /**
     * Открепить закрепленное сообщение в чате
     *
     * @param chatUuid идентификатор сообщения
     */
    fun unpinMessage(chatUuid: UUID): Completable

    /**
     * Десериализовать сообщение, которое пришло в виде бинарных данных
     */
    fun deserializeMessage(data: ByteArray): ConversationMessage

    /**
     * Предоставить доступ к файлу
     */
    fun acceptAccessRequest(message: Message, accessType: DocumentAccessType): Single<CommandStatus>

    /**
     * Отклонить запрос доступа к файлу
     */
    fun declineAccessRequest(message: Message): Single<CommandStatus>

    /**
     * Установить тему диалога.
     */
    fun setDialogTitle(dialogUuid: UUID, newTitle: String): Single<CommandStatus>

    /**
     * Получить список UUID-ов вложений для подписания документов.
     *
     * @param fileInfoViewModels список моделей вложения.
     */
    fun getAttachmentsUuidsToSign(fileInfoViewModels: ArrayList<FileInfoViewModel>): Single<List<UUID>>

    /**
     * Установить состояние активации локальной фичи выброса ошибок загрузки вложений.
     */
    fun setAttachmentsUploadErrors(activated: Boolean): Completable

    /**
     * Получить список моделей получателей сообщения.
     */
    fun getMessageRecipients(message: Message): Single<List<PersonData>>
}
