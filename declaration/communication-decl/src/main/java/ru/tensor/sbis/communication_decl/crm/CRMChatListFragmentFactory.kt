package ru.tensor.sbis.communication_decl.crm

import androidx.fragment.app.Fragment
import ru.tensor.sbis.plugin_struct.feature.Feature

/**
 * Фабрика фрагмента списка чатов CRM.
 *
 * @author da.zhukov
 */
interface CRMChatListFragmentFactory : Feature {

    /**
     * Создать фрагмент списка чатов CRM.
     */
    fun createCRMChatListFragment(crmChatListParams: CRMChatListParams): Fragment
}