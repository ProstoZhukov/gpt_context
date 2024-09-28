package ru.tensor.sbis.communicator.crm.conversation.interactor.contract

import io.reactivex.Completable
import io.reactivex.Observable
import ru.tensor.sbis.communicator.base.conversation.interactor.BaseConversationInteractor
import ru.tensor.sbis.communicator.crm.conversation.data.CRMConversationData
import ru.tensor.sbis.communicator.crm.conversation.data.model.CRMConversationMessage
import ru.tensor.sbis.communication_decl.crm.CRMConsultationCase
import ru.tensor.sbis.communicator.generated.ConsultationChatType
import ru.tensor.sbis.communicator.generated.DataRefreshedMessageControllerCallback
import ru.tensor.sbis.communicator.generated.Message
import ru.tensor.sbis.communicator.generated.MessageFilter
import java.util.UUID

/**
 * Интерактор чата CRM.
 *
 * @author da.zhukov
 */
internal interface CRMConversationInteractor : BaseConversationInteractor<CRMConversationMessage> {

    /**
     * Метод для подписки на события ConsultationService для актуализации информации о счетчике непрочитанных сообщений в истории.
     */
    fun subscribeOnCounterUpdates(): Observable<Pair<UUID, Int>>

    /**
     * Получить данные переписки.
     * @param viewId   Идентификатор активного реестра, если он задан, для навигации будет использован фильтр активного реестра.
     * @param consultation Идентификатор чата-консультации.
     * @param chatType Из какого реестра отображается консультация.
     */
    fun loadConversationData(
        viewId: UUID?,
        consultation: UUID,
        refresh: Boolean,
        chatType: ConsultationChatType
    ): Observable<CRMConversationDataResult>

    /**
     * Метод для создания новой консультации.
     *
     * @param case cценарий создания переписки.
     */
    fun createConsultation(
        case: CRMConsultationCase
    ): Observable<CRMConversationDataResult>

    /**
     * Получить ссылку на чат тех.поддержки.
     *
     * @param consultationUUID идентификатор чата.
     */
    fun getUrlByUuid(consultationUUID: UUID): Observable<String>

    /**
     * Вернуть чат тех. поддержки в очередь.
     *
     * @param consultationUUID идентификатор чата.
     */
    fun reassignToQueue(consultationUUID: UUID): Completable

    /**
     * Взять в обработку чат тех. поддержки.
     *
     * @param consultationUUID идентификатор чата.
     */
    fun takeConsultation(consultationUUID: UUID): Completable

    /**
     * Возобновить чат тех. поддержки.
     *
     * @param consultationUUID идентификатор чата.
     */
    fun reopenConsultation(consultationUUID: UUID): Completable

    /**
     * Завершить чат тех. поддержки.
     *
     * @param consultationUUID идентификатор чата.
     * @param documentUUID идентификатор документа.
     */
    fun closeConsultation(consultationUUID: UUID, documentUUID: UUID? = null): Completable

    /**
     * Удалить чат тех. поддержки.
     *
     * @param consultationUUID идентификатор чата.
     */
    fun deleteConsultation(consultationUUID: UUID): Completable

    /**
     * Подписаться на события изменения консультации.
     *
     * @param callback обрабочик события.
     */
    fun subscribeConsultationChangedCallback(): Observable<UUID>

    /**
     * Получить список приветствий для консультации.
     */
    suspend fun getGreetings(consultationUUID: UUID): List<String>

    /**
     * Отправить приветственное сообщение.
     */
    suspend fun sendGreetingMessage(consultationUUID: UUID?, text: String)

    /**
     * Отправить клиенту сервисное сообщение о запросе контактных данных.
     */
    fun requestContacts(consultationUUID: UUID): Completable

    interface CRMConversationDataResult: BaseConversationInteractor.BaseConversationDataResult<CRMConversationData>
}