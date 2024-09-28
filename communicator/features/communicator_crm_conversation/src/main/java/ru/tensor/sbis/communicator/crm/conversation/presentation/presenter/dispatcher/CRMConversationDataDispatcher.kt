package ru.tensor.sbis.communicator.crm.conversation.presentation.presenter.dispatcher

import io.reactivex.subjects.BehaviorSubject
import ru.tensor.sbis.communicator.base.conversation.presentation.presenter.dispatcher.BaseConversationDataDispatcher
import ru.tensor.sbis.communicator.crm.conversation.data.CRMConversationData
import ru.tensor.sbis.communicator.crm.conversation.data.model.CRMConversationMessage

/**
 * Реализация шины событий между делегатами презентера чата CRM.
 *
 * @author da.zhukov
 */
internal class CRMConversationDataDispatcher
    : BaseConversationDataDispatcher<CRMConversationMessage, CRMConversationState, CRMConversationData>() {

    override val conversationStateSubject: BehaviorSubject<CRMConversationState> =
        BehaviorSubject.createDefault(CRMConversationState())
}