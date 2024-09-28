package ru.tensor.sbis.message_panel.interactor.draft

import io.reactivex.Completable
import io.reactivex.Single
import java.util.UUID

/**
 * Контракт интерактора, отвечающего за сохранение черновиков
 */
interface MessagePanelDraftInteractor<out DRAFT_RESULT> {

    /**
     * Сохранение черновика по uuid диалога или документа
     * @param draftUuid uuid черновика
     * @param conversationUuid uuid диалога
     * @param documentUuid uuid документа
     * @param recipientsUuidList список получателей сообщения
     * @param message текст сообщения
     * @param attachmentUuidList список uuid'ов вложений
     * @param quoteUuid uuid сообщения, которое пользователь цитирует
     * @param answerUuid uuid сообщения, на которое отвечал пользователь
     * @param serviceObject сервисный объект сообщения (например, содержит в себе упоминания)
     */
    fun saveDraft(
        draftUuid: UUID,
        conversationUuid: UUID?,
        documentUuid: UUID?,
        recipientsUuidList: List<UUID>,
        message: String?,
        attachmentUuidList: List<UUID>,
        quoteUuid: UUID?,
        answerUuid: UUID?,
        serviceObject: String?
    ): Completable

    /**
     * Чтение черновика по одному из доступных uuid. В случае отсутствия uuid возвращается пустой черновик
     * @param conversationUuid - uuid диалога
     * @param documentUuid - uuid документа
     * @param clearDraft - если true метод очистит текущий черновик и вернет новый
     */
    fun loadDraft(conversationUuid: UUID?, documentUuid: UUID?, clearDraft: Boolean = false): Single<out DRAFT_RESULT>

    /**
     * Сохранение черновика для получателя. Одновременно может быть не более одного такого черновика, при попытке
     * сохранить черновик для другого пользователя, предыдущий черновик будет потерян
     * @param draftUuid uuid черновика
     * @param recipientsUuid получатель черновика
     * @param message текст сообщения
     * @param attachmentUuidList список uuid'ов вложений
     * @param serviceObject сервисный объект сообщения (например, содержит в себе упоминания)
     */
    fun saveDraftByRecipient(
        draftUuid: UUID,
        recipientsUuid: UUID,
        message: String?,
        attachmentUuidList: List<UUID>,
        serviceObject: String?
    ): Completable
}