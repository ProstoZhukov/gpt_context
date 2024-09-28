package ru.tensor.sbis.communication_decl.crm

import androidx.fragment.app.Fragment
import ru.tensor.sbis.communication_decl.crm.CRMConsultationParams
import ru.tensor.sbis.plugin_struct.feature.Feature

/**
 * Фабрика фрагмента консультации CRM.
 *
 * @author da.zhukov
 */
interface CRMConversationFragmentFactory : Feature {

    /**
     * Создать фрагмент консультации.
     * @param params параметры для создания фрагмента консультации.
     */
    fun createCRMConversationFragment(params: CRMConsultationParams): Fragment
}