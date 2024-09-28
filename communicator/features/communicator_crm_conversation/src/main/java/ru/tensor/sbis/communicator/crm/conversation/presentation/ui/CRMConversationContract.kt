package ru.tensor.sbis.communicator.crm.conversation.presentation.ui

import ru.tensor.sbis.communicator.base.conversation.presentation.presenter.contracts.BaseConversationPresenterContract
import ru.tensor.sbis.communicator.base.conversation.presentation.ui.contracts.BaseConversationViewContract
import ru.tensor.sbis.communicator.crm.conversation.presentation.presenter.message.CRMConversationMessagesPresenterContract
import ru.tensor.sbis.communicator.crm.conversation.presentation.presenter.message.CRMConversationMessagesView
import ru.tensor.sbis.communicator.crm.conversation.data.model.CRMConversationMessage
import ru.tensor.sbis.communicator.crm.conversation.presentation.presenter.contracts.CRMConversationMessagePanelPresenterContract
import ru.tensor.sbis.communicator.crm.conversation.presentation.presenter.contracts.CRMConversationToolbarPresenterContract
import ru.tensor.sbis.communicator.crm.conversation.presentation.ui.contracts.CRMConversationMessagePanelView
import ru.tensor.sbis.communicator.crm.conversation.presentation.ui.contracts.CRMConversationToolbarView

internal interface CRMConversationContract {

    interface CRMConversationViewContract
        : BaseConversationViewContract<CRMConversationMessage>,
        CRMConversationMessagePanelView,
        CRMConversationMessagesView,
        CRMConversationToolbarView

    interface CRMConversationPresenterContract
        : BaseConversationPresenterContract<CRMConversationViewContract>,
        CRMConversationMessagesPresenterContract<CRMConversationViewContract>,
        CRMConversationMessagePanelPresenterContract<CRMConversationViewContract>,
        CRMConversationToolbarPresenterContract<CRMConversationViewContract>
}