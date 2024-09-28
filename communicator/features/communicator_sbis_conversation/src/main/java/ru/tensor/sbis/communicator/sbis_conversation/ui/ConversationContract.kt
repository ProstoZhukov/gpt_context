package ru.tensor.sbis.communicator.sbis_conversation.ui

import ru.tensor.sbis.communicator.base.conversation.presentation.presenter.contracts.BaseConversationPresenterContract
import ru.tensor.sbis.communicator.base.conversation.presentation.ui.contracts.BaseConversationViewContract
import ru.tensor.sbis.communicator.sbis_conversation.ui.message.ConversationMessagesContract
import ru.tensor.sbis.communicator.sbis_conversation.data.model.ConversationMessage
import ru.tensor.sbis.communicator.sbis_conversation.ui.messagepanel.ConversationMessagePanelContract
import ru.tensor.sbis.communicator.sbis_conversation.ui.toolbar.ConversationToolbarContract

/**
 * Общий контракт для View и Presenter экрана переписки.
 * Совмещает в себе контракты для тулбара, реестра сообщений и панели сообщений.
 */
internal interface ConversationContract {

    /** @SelfDocumented */
    interface ConversationView :
        BaseConversationViewContract<ConversationMessage>,
        ConversationToolbarContract.View,
        ConversationMessagesContract.View,
        ConversationMessagePanelContract.View

    /** @SelfDocumented */
    interface ConversationPresenter :
        BaseConversationPresenterContract<ConversationView>,
        ConversationMessagesContract.Presenter<ConversationView>,
        ConversationMessagePanelContract.Presenter<ConversationView>,
        ConversationToolbarContract.Presenter<ConversationView>
}