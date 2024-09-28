package ru.tensor.sbis.communicator.crm.conversation.contract

import ru.tensor.sbis.communication_decl.crm.CRMConversationFragmentFactory
import ru.tensor.sbis.communicator.declaration.crm.providers.CRMConversationProvider

/**
 * Api модуля переписки CRM.
 * @see CRMConversationFragmentFactory
 * @see CRMConversationProvider
 *
 * @author da.zhukov
 */
interface CRMConversationFeature : CRMConversationFragmentFactory,
    CRMConversationProvider