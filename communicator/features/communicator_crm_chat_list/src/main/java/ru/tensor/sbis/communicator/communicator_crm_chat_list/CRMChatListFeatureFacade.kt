package ru.tensor.sbis.communicator.communicator_crm_chat_list

import ru.tensor.sbis.communicator.communicator_crm_chat_list.ui.CRMChatListFragment
import ru.tensor.sbis.communicator.communicator_crm_chat_list.ui.hostfragment.CRMChatListHostFragment
import ru.tensor.sbis.communicator.communicator_crm_chat_list.ui.router.CRMHostRouterImpl
import ru.tensor.sbis.communicator.declaration.crm.CRMHostRouter
import ru.tensor.sbis.communication_decl.crm.CRMChatListFragmentFactory
import ru.tensor.sbis.communication_decl.crm.CRMChatListHostFragmentFactory
import ru.tensor.sbis.communication_decl.crm.CrmChannelListFragmentFactory
import ru.tensor.sbis.communicator.communicator_crm_chat_list.filter.channels.ui.CRMChannelsFragment

/**
 * Фасад чатов CRM.
 *
 * @author dv.baranov
 */
internal object CRMChatListFeatureFacade : CRMChatListFragmentFactory by CRMChatListFragment.Companion,
    CRMChatListHostFragmentFactory by CRMChatListHostFragment.Companion,
    CRMHostRouter.Provider by CRMHostRouterImpl.Companion,
    CrmChannelListFragmentFactory by CRMChannelsFragment.Companion
