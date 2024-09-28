package ru.tensor.sbis.communicator.communicator_crm_chat_list.filter.channels.store

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.tensor.sbis.common.data.DependencyProvider
import ru.tensor.sbis.communication_decl.crm.CrmChannelListCase
import ru.tensor.sbis.communicator.common.analytics.CRMChatWorkEvent
import ru.tensor.sbis.communicator.common.analytics.CRMChatWorkEvent.ReassignConsultation
import ru.tensor.sbis.communicator.common.analytics.ReassignConsultationTarget
import ru.tensor.sbis.communicator.communicator_crm_chat_list.CRMChatListPlugin.analyticsUtilProvider
import ru.tensor.sbis.consultations.generated.ConsultationService
import java.util.UUID

/**
 * Интерактор переназначения консультации.
 *
 * @author da.zhukov
 */
internal interface CRMChannelsInteractor {

    suspend fun reassignConsultation(
        channelId: UUID?,
        operatorGroupId: UUID?
    )
}

/**
 * Реализация интерактора переназначения консультации.
 *
 * @author da.zhukov
 */
internal class CRMChannelsInteractorImpl(
    consultationServiceProvider: DependencyProvider<ConsultationService>,
    private val case: CrmChannelListCase
) : CRMChannelsInteractor {

    private val consultationService by lazy { consultationServiceProvider.get() }

    override suspend fun reassignConsultation(
        channelId: UUID?,
        operatorGroupId: UUID?
    ) {
        withContext(Dispatchers.IO) {
            (case as? CrmChannelListCase.CrmChannelReassignCase)?.consultationId?.let {
                analyticsUtilProvider?.get()?.getAnalyticsUtil()?.sendAnalytics(
                    getAnalyticsEvent(channelId, operatorGroupId),
                )
                consultationService.changeOperator(
                    consultationId = it,
                    operatorId = null,
                    channelId = channelId,
                    operatorGroupId = operatorGroupId,
                    messageToOperator = null
                )
            }
        }
    }

    private fun getAnalyticsEvent(channelId: UUID?, operatorGroupId: UUID?): CRMChatWorkEvent {
        val target = when {
            channelId != null -> ReassignConsultationTarget.CHANNEL
            operatorGroupId != null -> ReassignConsultationTarget.LINE
            else -> ReassignConsultationTarget.QUEUE
        }
        return ReassignConsultation(target)
    }
}