package ru.tensor.sbis.message_panel.interactor.draft

import io.reactivex.Completable
import io.reactivex.Single
import ru.tensor.sbis.message_panel.decl.DraftArguments
import ru.tensor.sbis.message_panel.decl.MessageServiceWrapper
import ru.tensor.sbis.mvp.interactor.BaseInteractor
import java.util.UUID

class MessagePanelDraftInteractorImpl<DRAFT_RESULT>(
    private val serviceWrapper: MessageServiceWrapper<*, *, DRAFT_RESULT>
) : BaseInteractor(), MessagePanelDraftInteractor<DRAFT_RESULT> {

    override fun saveDraft(
        draftUuid: UUID,
        conversationUuid: UUID?,
        documentUuid: UUID?,
        recipientsUuidList: List<UUID>,
        message: String?,
        attachmentUuidList: List<UUID>,
        quoteUuid: UUID?,
        answerUuid: UUID?,
        serviceObject: String?
    ): Completable =
        serviceWrapper.saveDraft(
            DraftArguments(
                getResolvedUuidForDraft(conversationUuid, documentUuid),
                draftUuid,
                recipientsUuidList,
                message,
                attachmentUuidList,
                quoteUuid,
                answerUuid,
                serviceObject
            )
        ).compose(completableBackgroundSchedulers)

    override fun loadDraft(conversationUuid: UUID?, documentUuid: UUID?, clearDraft: Boolean): Single<DRAFT_RESULT> {
        return serviceWrapper.loadDraft(
            getResolvedUuidForDraft(conversationUuid, documentUuid),
            documentUuid,
            clearDraft
        ).compose(getSingleBackgroundSchedulers())
    }

    /**
     * Сохранение черновика для пользователя. Сохранение ответа не поддерживается, т.к. данный метод используется только
     * при создании нового диалога
     */
    override fun saveDraftByRecipient(
        draftUuid: UUID,
        recipientsUuid: UUID,
        message: String?,
        attachmentUuidList: List<UUID>,
        serviceObject: String?
    ): Completable {
        return serviceWrapper.saveDraft(
            DraftArguments(
                null,
                draftUuid,
                listOf(recipientsUuid),
                message,
                attachmentUuidList,
                null,
                null,
                serviceObject
            )
        ).compose(completableBackgroundSchedulers)
    }

    /**
     * Получение uuid для сохранения черновика. Сохранение осуществляется по идентификаторам 2 типов - в случае если
     * имеется uuid диалога, то используется именно он, поскольку в рамках одного документа может быть
     * несколько диалогов, для каждого из которых будет отдельный черновик. В противном случае используется uuid
     * документа
     */
    private fun getResolvedUuidForDraft(conversationUuid: UUID?, documentUuid: UUID?): UUID {
        return checkNotNull(conversationUuid ?: documentUuid) {
            "Conversation and document uuid are null. At least one argument should be provided"
        }
    }
}