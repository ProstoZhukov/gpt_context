package ru.tensor.sbis.communicator.declaration.crm.providers

import android.content.Intent
import ru.tensor.sbis.communication_decl.crm.CRMConsultationParams
import ru.tensor.sbis.plugin_struct.feature.Feature

/**
 * Интерфейс предоставляющий CRMConversationActivityIntent.
 *
 * @author da.zhukov
 */
interface CRMConversationProvider : Feature {

    /**
     * Создать интент для открытия экрана переписки CRM.
     * @param params параметры для создания интента.
     */
    fun getCRMConversationActivityIntent(params: CRMConsultationParams): Intent
}