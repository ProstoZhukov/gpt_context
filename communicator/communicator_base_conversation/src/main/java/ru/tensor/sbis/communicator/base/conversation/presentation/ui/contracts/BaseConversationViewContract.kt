package ru.tensor.sbis.communicator.base.conversation.presentation.ui.contracts

import androidx.annotation.StringRes
import ru.tensor.sbis.communicator.base.conversation.data.model.BaseConversationMessage
import ru.tensor.sbis.communicator.base.conversation.data.model.MessageAction
import ru.tensor.sbis.communicator.base.conversation.data.model.ToolbarData
import ru.tensor.sbis.communicator.base.conversation.presentation.adapter.PagingLoadingErrorActions
import ru.tensor.sbis.communicator.base.conversation.ui.user_typing.UsersTypingView
import ru.tensor.sbis.communicator.common.conversation.data.Message
import ru.tensor.sbis.design.stubview.StubViewContent
import ru.tensor.sbis.message_panel.contract.attachments.ViewerSliderArgsFactory
import ru.tensor.sbis.message_panel.integration.CommunicatorMessagePanelController
import ru.tensor.sbis.message_panel.model.CoreConversationInfo
import ru.tensor.sbis.mvp.presenter.BaseLoadingView
import java.util.UUID

/**
 * Общий контракт вью переписки.
 *
 * @author vv.chekurda
 */
interface BaseConversationViewContract<MESSAGE : BaseConversationMessage>
    : BaseConversationMessagesView<MESSAGE>,
    BaseConversationMessagePanelView<MESSAGE>,
    BaseConversationToolbarView<MESSAGE>

/** @SelfDocumented */
interface BaseConversationMessagesView<MESSAGE : BaseConversationMessage> :
    BaseLoadingView,
    PagingLoadingErrorActions,
    PopupErrorView {

    /**
     * Выставить позицию релевантного сообщения, на которой начнет отображаться список.
     *
     * @param position позиция релевантного сообщения
     */
    fun setRelevantMessagePosition(position: Int)

    /**
     * Показать меню с действиями над сообщением.
     *
     * @param message модель сообщения
     * @param actions список действий с сообщением
     */
    fun showMessageActionsList(message: MESSAGE, actions: List<MessageAction>)

    /** @SelfDocumented */
    fun setMessagesListStyle(newConversation: Boolean)

    /** @SelfDocumented */
    fun forceHideKeyboard()

    /** @SelfDocumented */
    fun hideKeyboard()

    /** @SelfDocumented */
    fun scrollListView(scrollBy: Int, post: Boolean)

    /** @SelfDocumented */
    fun scrollToBottom(skipScrollToPosition: Boolean, withHide: Boolean)

    /**
     * Установка нижнего отступа на список сообщений.
     *
     * @param bottomPadding размер нижнего отступа.
     */
    fun setListViewBottomPadding(bottomPadding: Int)

    /**
     * Изменить размер нижнего отступа у списка сообщений
     *.
     * @param difference разница, в сравнении с предыдущим отступом.
     * @param withScroll true, если необходимо изменить отступ и подскролить список.
     * @param addWithKeyboard true, если необходимо изменить отступ и подскролить список вместе с подъемом клавиатуры,
     * чтобы сохранить текущее положение.
     */
    fun changeListViewBottomPadding(difference: Int, withScroll: Boolean, addWithKeyboard: Boolean = false) = Unit

    /** @SelfDocumented */
    fun setFastScrollDownUnreadCounterValue(unreadCounter: Int)

    /** @SelfDocumented */
    fun hideFastScrollDownButton(force: Boolean = false)

    /** @SelfDocumented */
    fun showFastScrollDownButton()

    /** @SelfDocumented */
    fun showUnattachedPhoneError(errorText: String? = null)

    /** @SelfDocumented */
    fun setHighlightedMessageUuid(messageUuid: UUID?)

    /** @SelfDocumented */
    fun showPinnedChatMessage(message: Message?, canUnpin: Boolean) = Unit

    /**
     * Обновить состояние статуса отправки сообщения.
     */
    fun updateSendingState(position: Int)

    fun getAdapterPosition(message: MESSAGE): Int

    /**
     * @SelfDocumented
     */
    fun updateDataList(dataList: List<MESSAGE>?, offset: Int)

    /**
     * Отобразить заглушку.
     *
     * @param errorMessage сообщение в заглушке.
     */
    fun showControllerErrorMessage(errorMessage: String?)


    /**
     * @SelfDocumented
     */
    fun showStubView(@StringRes messageTextId: Int)


    /**
     * @SelfDocumented
     */
    fun showStubView(stubContent: StubViewContent)

    /**
     * Показать диалог удаления сообщения у всех.
     */
    fun showPopupDeleteMessageForAll()

    /**
     * @SelfDocumented
     */
    fun showOlderLoadingProgress(show: Boolean)

    /**
     * @SelfDocumented
     */
    fun showNewerLoadingProgress(show: Boolean)

    /**
     * @SelfDocumented
     */
    fun notifyItemsChanged(position: Int, count: Int)

    /**
     * @SelfDocumented
     */
    fun notifyItemsChanged(position: Int, count: Int, payload: Any?)

    /**
     * @SelfDocumented
     */
    fun scrollToPosition(position: Int)

    /**
     * @SelfDocumented
     */
    fun ignoreProgress(ignore: Boolean)

    /**
     * @SelfDocumented
     */
    fun hideStubView()

    /**
     * @SelfDocumented
     */
    fun updateDataListWithoutNotification(dataList: List<MESSAGE>?, offset: Int)

    /**
     * Сообщение только что процитировано пользователем.
     */
    fun onMessageQuoted() = Unit
}


/** @SelfDocumented */
interface BaseConversationMessagePanelView<MESSAGE : BaseConversationMessage>
    : PopupErrorView {

    /** @SelfDocumented */
    fun initMessagePanelController(
        coreConversationInfo: CoreConversationInfo?,
        viewerSliderArgsFactory: ViewerSliderArgsFactory
    ): CommunicatorMessagePanelController

    /** @SelfDocumented */
    fun showKeyboard()

    /** @SelfDocumented */
    fun hideKeyboard()

    /** @SelfDocumented */
    fun forceHideKeyboard()

    /** @SelfDocumented */
    fun scrollToBottom(skipScrollToPosition: Boolean, withHide: Boolean)
}

/** @SelfDocumented */
interface BaseConversationToolbarView<MESSAGE : BaseConversationMessage>
    : PopupErrorView {

    /** @SelfDocumented */
    fun forceHideKeyboard()

    /** @SelfDocumented */
    fun setToolbarData(toolbarData: ToolbarData)

    /** @SelfDocumented */
    fun setHasActivityStatus(hasStatus: Boolean) = Unit

    /** @SelfDocumented */
    fun setTypingUsers(data: UsersTypingView.UsersTypingData)
}
