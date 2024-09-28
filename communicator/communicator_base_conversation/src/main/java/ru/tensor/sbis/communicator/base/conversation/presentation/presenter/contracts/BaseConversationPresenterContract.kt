package ru.tensor.sbis.communicator.base.conversation.presentation.presenter.contracts

import androidx.recyclerview.widget.ConversationLayoutManager
import ru.tensor.sbis.base_components.adapter.sectioned.content.ListController
import ru.tensor.sbis.communicator.base.conversation.presentation.ui.contracts.BaseConversationMessagePanelView
import ru.tensor.sbis.communicator.base.conversation.presentation.ui.contracts.BaseConversationMessagesView
import ru.tensor.sbis.communicator.base.conversation.presentation.ui.contracts.BaseConversationToolbarView
import ru.tensor.sbis.communicator.base.conversation.presentation.ui.contracts.BaseConversationViewContract
import ru.tensor.sbis.communicator.base.conversation.presentation.adapter.holders.MessageSelectionItemListener
import ru.tensor.sbis.communicator.base.conversation.presentation.ui.contracts.PopupErrorHandler
import ru.tensor.sbis.design.cloud_view.content.quote.QuoteClickListener
import ru.tensor.sbis.message_panel.contract.MessagePanelSignDelegate
import ru.tensor.sbis.mvp.presenter.BasePresenter
import java.util.UUID

/**
 * Общий контракт презентера сообщений.
 *
 * @author vv.chekurda
 */
interface BaseConversationPresenterContract<VIEW : BaseConversationViewContract<*>>
    : BaseConversationMessagesPresenterContract<VIEW>,
    BaseConversationMessagePanelPresenterContract<VIEW>,
    BaseConversationToolbarPresenterContract<VIEW>

/**
 * Общий контракт делегата презентера по секции сообщений.
 */
interface BaseConversationMessagesPresenterContract<VIEW : BaseConversationMessagesView<*>>
    : BasePresenter<VIEW>,
    BaseLifecycleObserver,
    MessageSelectionItemListener,
    QuoteClickListener,
    ConversationLayoutManager.LaidOutItemsListener,
    ListController,
    BaseConversationKeyboardEvents,
    PopupErrorHandler {

    /** @SelfDocumented */
    var actionsMenuShown: Boolean

    /** @SelfDocumented */
    fun onPinnedMessageClicked(messageUuid: UUID)

    /** @SelfDocumented */
    fun onFastScrollDownPressed()

    fun onMessagePanelHeightChanged(difference: Int, isFirstLayout: Boolean)

    /**
     * Удаление сообщения у всех.
     */
    fun deleteMessageForAll()

    /**
     * Удаление сообщения у меня
     */
    fun deleteMessageOnlyForMe()

    /** @SelfDocumented */
    fun onRefresh()

    fun onScroll(
        dy: Int,
        firstVisibleItemPosition: Int,
        lastVisibleItemPosition: Int
    )
}

/**
 * Общий контракт делегата презентера по панели сообщений.
 */
interface BaseConversationMessagePanelPresenterContract<VIEW : BaseConversationMessagePanelView<*>>
    : BasePresenter<VIEW>,
    BaseLifecycleObserver,
    PopupErrorHandler {

    /** @SelfDocumented */
    val signDelegate: MessagePanelSignDelegate?

    /** @SelfDocumented */
    fun onMessagePanelEnabled()

    /** @SelfDocumented */
    fun onMessagePanelDisabled()

    /**
     * Удаление диалога из архива только у меня.
     */
    fun onDialogDeletingClicked()

    /** @SelfDocumented */
    fun onDialogDeletingConfirmed()
}

/**
 * Общий контракт делегата презентера по тулбару переписки.
 */
interface BaseConversationToolbarPresenterContract<VIEW : BaseConversationToolbarView<*>>
    : BasePresenter<VIEW>,
    BaseLifecycleObserver,
    PopupErrorHandler {

    /** @SelfDocumented */
    fun onToolbarClick()
}