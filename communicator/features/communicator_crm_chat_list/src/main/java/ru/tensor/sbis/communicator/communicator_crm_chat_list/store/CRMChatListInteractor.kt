package ru.tensor.sbis.communicator.communicator_crm_chat_list.store

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.withContext
import ru.tensor.sbis.common.data.DependencyProvider
import ru.tensor.sbis.communicator.common.analytics.CRMChatWorkEvent
import ru.tensor.sbis.communicator.communicator_crm_chat_list.CRMChatListPlugin.analyticsUtilProvider
import ru.tensor.sbis.consultations.generated.ConsultationService
import ru.tensor.sbis.consultations.generated.OnTakeOldestButtonChangedCallback
import ru.tensor.sbis.consultations.generated.OnTakeOldestButtonChangedEvent
import ru.tensor.sbis.consultations.generated.ResultDocument
import java.util.UUID

/**
 * Интерактор чатов CRM.
 *
 * @author da.zhukov
 */
internal interface CRMChatListInteractor {

    /** Удалить консультацию. */
    suspend fun deleteConsultation(consultationId: UUID)

    /** Завершить консультацию. */
    suspend fun completeConsultation(consultationId: UUID): ResultDocument

    /** Взять консультацию в работу. */
    suspend fun takeConsultation(consultationId: UUID)

    /** Проверить является ли текущий пользователь оператором. */
    suspend fun getIsCurrentUserOperator(): Boolean

    /** Проверить есть ли консультация для взятия в работу. */
    suspend fun getIsTakeOldest(): Boolean

    /** Взять последнюю доступную консультацию в работу. */
    suspend fun takeOldest(): UUID?

    /** Получить flow для подписки на событие скрытия и показа кнопки. */
    suspend fun onTakeOldestButtonVisibilityFlow(): Flow<Boolean>
}

/**
 * Реализация интерактора чатов CRM.
 *
 * @author da.zhukov
 */
internal class CRMChatListInteractorImpl(
    consultationServiceProvider: DependencyProvider<ConsultationService>
) : CRMChatListInteractor {

    private val consultationService by lazy { consultationServiceProvider.get() }
    private val analyticsUtil = analyticsUtilProvider?.get()?.getAnalyticsUtil()

    override suspend fun deleteConsultation(consultationId: UUID) =
        withContext(Dispatchers.IO) {
            consultationService.delete(consultationId)
        }

    override suspend fun completeConsultation(consultationId: UUID): ResultDocument =
        withContext(Dispatchers.IO) {
            analyticsUtil?.sendAnalytics(
                CRMChatWorkEvent.CompleteConsultation,
            )
            consultationService.close(consultationId, null)
        }

    override suspend fun takeConsultation(consultationId: UUID) =
        withContext(Dispatchers.IO) {
            analyticsUtil?.sendAnalytics(
                CRMChatWorkEvent.TakeConsultation,
            )
            consultationService.take(consultationId)
        }

    override suspend fun getIsCurrentUserOperator(): Boolean =
        withContext(Dispatchers.IO) {
            consultationService.getIsCurrentUserOperator()
        }

    override suspend fun getIsTakeOldest(): Boolean =
        withContext(Dispatchers.IO) {
            consultationService.getIsTakeOldest()
        }

    override suspend fun takeOldest(): UUID? =
        withContext(Dispatchers.IO) {
            consultationService.takeOldest()
        }

    override suspend fun onTakeOldestButtonVisibilityFlow(): Flow<Boolean> =
        withContext(Dispatchers.IO) {
            consultationService.onTakeOldestButtonChanged().asFlow()
        }

    private fun OnTakeOldestButtonChangedEvent.asFlow(): Flow<Boolean> = callbackFlow {
        val callback = object : OnTakeOldestButtonChangedCallback() {
            override fun onEvent(visible: Boolean) {
                trySend(visible)
            }
        }
        val subscription = subscribe(callback)
        awaitClose {
            subscription.disable()
        }
    }
}