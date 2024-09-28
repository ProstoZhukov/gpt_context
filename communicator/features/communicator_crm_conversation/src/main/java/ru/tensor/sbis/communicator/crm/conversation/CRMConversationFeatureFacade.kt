package ru.tensor.sbis.communicator.crm.conversation

import android.content.Intent
import android.os.Parcelable
import androidx.fragment.app.Fragment
import ru.tensor.sbis.communicator.crm.conversation.CRMConversationPlugin.singletonComponent
import ru.tensor.sbis.communicator.crm.conversation.contract.CRMConversationFeature
import ru.tensor.sbis.communicator.crm.conversation.data.mapper.CRMMessageMapperHelper
import ru.tensor.sbis.communicator.crm.conversation.reassing_operator.another_operator.ui.CRMAnotherOperatorFragment
import ru.tensor.sbis.communication_decl.crm.CRMConsultationParams
import ru.tensor.sbis.communicator.declaration.crm.providers.CRMAnotherOperatorFragmentFactory
import ru.tensor.sbis.communication_decl.crm.CRMConversationFragmentFactory
import ru.tensor.sbis.communicator.crm.conversation.presentation.ui.CRMConversationFragment as CrudCRMConversationFragment

/**
 * Фасад модуля реестра переписки по чатам техподдержки.
 * Предоставляет фичи [CRMConversationFeature].
 *
 * @author da.zhukov
 */
internal object CRMConversationFeatureFacade : CRMConversationFeature,
    CRMConversationFragmentFactory,
    CRMAnotherOperatorFragmentFactory by CRMAnotherOperatorFragment.Companion {

    override fun createCRMConversationFragment(params: CRMConsultationParams): Fragment {
        return CrudCRMConversationFragment.createCRMConversationFragment(params)
    }

    val crmMessageMapperHelper = CRMMessageMapperHelper()

    override fun getCRMConversationActivityIntent(params: CRMConsultationParams): Intent =
        Intent(singletonComponent.context, CRMConversationActivity::class.java)
            .putExtra(CRM_CONVERSATION_CHAT_PARAMS_KEY, params as Parcelable)

    const val CRM_CONVERSATION_CHAT_PARAMS_KEY = "CRM_CONVERSATION_CHAT_PARAMS_KEY"
}

