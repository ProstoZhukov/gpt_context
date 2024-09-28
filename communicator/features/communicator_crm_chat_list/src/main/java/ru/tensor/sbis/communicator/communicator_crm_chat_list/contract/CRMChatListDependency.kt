package ru.tensor.sbis.communicator.communicator_crm_chat_list.contract

import ru.tensor.sbis.clients_feature.di.ClientsFeature
import ru.tensor.sbis.common.di.CommonSingletonComponent
import ru.tensor.sbis.communication_decl.selection.recipient.RecipientSelectionProvider
import ru.tensor.sbis.communicator.common.push.MessagesPushManagerProvider
import ru.tensor.sbis.communication_decl.crm.CRMConversationFragmentFactory

/**
 * Внешние зависимости модуля.
 * @see CommonSingletonComponent
 * @see CRMConversationFragmentFactory
 * @see RecipientSelectionProvider
 * @see ClientsFeature
 *
 * @author da.zhukov
 */
interface CRMChatListDependency : MessagesPushManagerProvider,
    CommonSingletonComponent,
    CRMConversationFragmentFactory,
    RecipientSelectionProvider,
    ClientsFeature