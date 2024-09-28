package ru.tensor.sbis.communicator.sbis_conversation.interactor.data

import io.reactivex.Observable
import io.reactivex.Single
import ru.tensor.sbis.communicator.base.conversation.interactor.BaseConversationInteractor
import ru.tensor.sbis.communicator.sbis_conversation.data.model.ConversationData
import ru.tensor.sbis.communicator.sbis_conversation.data.model.ConversationMessage
import ru.tensor.sbis.person_decl.profile.model.ProfileActivityStatus
import java.util.UUID

/**
 * Интерактор для загрузки данных по экрану реестра сообщений.
 *
 * @author vv.chekurda
 */
internal interface ConversationDataInteractor {

    /**
     * Loads [ConversationData] (view model for conversation screen) by input parameters.
     * Depending on input parameters the ConversationData may be filled fully or partially.
     * For example, in a new conversation for specified recipients only field mRecipients in [ConversationData]
     * will be filled. In this case input params contains only recipientsUuids.
     * If conversationUuid is set it fills the ConversationData with all information of the dialog
     * if the dialog exists, or return empty [ConversationData] otherwise.
     */
    fun loadConversationData(
        conversationUuid: UUID?,
        documentUuid: UUID?,
        startMessage: ConversationMessage?,
        messagesCount: Int,
        isChat: Boolean,
        isConsultation: Boolean
    ): Observable<ConversationDataResult>

    /**
     * Загрузка данных [ConversationDataResult] по переписке.
     * Аналогичен [loadConversationData], но не переключается на main thread
     */
    fun backgroundLoadConversationData(
        conversationUuid: UUID?,
        documentUuid: UUID?,
        startMessage: ConversationMessage?,
        messagesCount: Int,
        isChat: Boolean,
        isConsultation: Boolean
    ): Observable<ConversationDataResult>

    /**
     * Создать подзаголовок для шапки
     *
     * @param conversationData модель информации о переписке
     * @param status модель статуса активности получателя
     * @return строковый литерал подзаголовка
     */
    fun createToolbarSubtitle(conversationData: ConversationData, status: ProfileActivityStatus): Single<String>

    interface ConversationDataResult : BaseConversationInteractor.BaseConversationDataResult<ConversationData>
}