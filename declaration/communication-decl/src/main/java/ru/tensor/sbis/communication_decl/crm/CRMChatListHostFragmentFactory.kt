@file:Suppress("unused")

package ru.tensor.sbis.communication_decl.crm

import androidx.fragment.app.Fragment
import ru.tensor.sbis.plugin_struct.feature.Feature

/**
 * Фабрика хост фрагмента для реестра чатов CRM.
 *
 * @author da.zhukov
 */
interface CRMChatListHostFragmentFactory : Feature {

    /**
     * Создать хост фрагмент для реестра чатов CRM.
     */
    fun createCRMChatListHostFragment(crmChatListParams: CRMChatListParams = CRMChatListDefaultParams()): Fragment
}
