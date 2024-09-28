package ru.tensor.sbis.communicator.declaration.crm

import android.content.Intent
import androidx.fragment.app.Fragment
import ru.tensor.sbis.communicator.declaration.crm.model.CRMChatFilterModel
import ru.tensor.sbis.communication_decl.crm.CRMConsultationParams
import ru.tensor.sbis.communicator.declaration.crm.providers.CRMAnotherOperatorParams
import ru.tensor.sbis.plugin_struct.feature.Feature

/**
 * Интерфейс роутера для реестра чатов тех. поддержки.
 *
 * @author dv.baranov
 */
interface CRMHostRouter {

    /**
     * Открыть чат тех. поддержки.
     */
    fun openCRMConversation(params: CRMConsultationParams)

    /**
     * Открыть экран фильтров.
     */
    fun openFilters(filterModel: CRMChatFilterModel)

    /**
     * Открыть следующий чат тех. поддержки.
     */
    fun openNextConsultation(chatParams: CRMConsultationParams)

    /**
     * Инициализировать роутер.
     */
    fun initRouter(fragment: Fragment, isHistoryMode: Boolean = false)

    /**
     * Открыть контент.
     */
    fun openContentScreen(fragment: Fragment, tag: String)

    /**
     * Открыть контент в новой активности.
     */
    fun openContentScreenInActivity(intent: Intent)

    /**
     * Поставщик роутера [CRMHostRouter]
     */
    interface Provider : Feature {

        /** @SelfDocumented */
        fun getCRMHostRouter(): CRMHostRouter
    }
}
