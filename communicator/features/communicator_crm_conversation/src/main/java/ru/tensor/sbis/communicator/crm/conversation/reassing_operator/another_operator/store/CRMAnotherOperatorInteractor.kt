package ru.tensor.sbis.communicator.crm.conversation.reassing_operator.another_operator.store

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.tensor.sbis.common.data.DependencyProvider
import ru.tensor.sbis.communicator.common.analytics.CRMChatWorkEvent.ReassignConsultation
import ru.tensor.sbis.communicator.common.analytics.ReassignConsultationTarget
import ru.tensor.sbis.communicator.crm.conversation.CRMConversationPlugin.crmConversationDependency
import ru.tensor.sbis.communicator.declaration.crm.providers.CRMAnotherOperatorParams
import ru.tensor.sbis.consultations.generated.ConsultationService
import java.util.UUID

/**
 * Интерактор переназначения оператору.
 *
 * @author da.zhukov
 */
internal interface CRMAnotherOperatorInteractor {

    suspend fun reassignConsultationToOperator(operatorId: UUID)
}

/**
 * Реализация интерактора переназначения оператору.
 *
 * @author da.zhukov
 */
internal class CRMAnotherOperatorInteractorImpl(
    consultationServiceProvider: DependencyProvider<ConsultationService>,
    private val params: CRMAnotherOperatorParams
) : CRMAnotherOperatorInteractor {

    private val consultationService by lazy { consultationServiceProvider.get() }

    override suspend fun reassignConsultationToOperator(operatorId: UUID) {
        withContext(Dispatchers.IO) {
            crmConversationDependency?.analyticsUtilProvider?.getAnalyticsUtil()?.sendAnalytics(
                ReassignConsultation(
                    ReassignConsultationTarget.OPERATOR,
                ),
            )
            consultationService.changeOperator(
                consultationId = params.consultationId,
                operatorId = operatorId,
                channelId = null,
                operatorGroupId = null,
                messageToOperator = params.message
            )
        }
    }
}