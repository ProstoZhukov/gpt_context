package ru.tensor.sbis.communicator.declaration.crm.contract

import ru.tensor.sbis.communication_decl.crm.CRMConsultationParams
import java.util.UUID

/**
 * Контракт для обработки событий открытия новых экранов переписки.
 *
 * @author da.zhukov
 */
interface CRMConversationContract {

    /**
     * Обратный вызов о необходимости отобразить новую переписку по консультации.
     *
     * @param params параметры для отображения новой переписки.
     */
    fun showNewConversation(params: CRMConsultationParams)

    /**
     * Обратный вызов о необходимости отобразить карточку компании в sabyget/brand.
     *
     * @param companyId uuid компании (sourceId).
     */
    fun openSalePointDetailCard(companyId: UUID)
}