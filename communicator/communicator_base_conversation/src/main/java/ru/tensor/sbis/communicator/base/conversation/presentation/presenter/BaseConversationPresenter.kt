package ru.tensor.sbis.communicator.base.conversation.presentation.presenter

import ru.tensor.sbis.communicator.base.conversation.presentation.presenter.contracts.BaseConversationMessagesPresenterContract
import ru.tensor.sbis.communicator.base.conversation.presentation.presenter.contracts.BaseConversationPresenterContract
import ru.tensor.sbis.communicator.base.conversation.presentation.ui.contracts.BaseConversationViewContract
import ru.tensor.sbis.communicator.base.conversation.presentation.presenter.contracts.BaseConversationMessagePanelPresenterContract
import ru.tensor.sbis.communicator.base.conversation.presentation.presenter.contracts.BaseConversationToolbarPresenterContract
import ru.tensor.sbis.message_panel.contract.MessagePanelSignDelegate
import java.util.UUID

/**
 * Базовый презентер реестра сообщений, реализующий базовый функционал экрана.
 *
 * @author vv.chekurda
 */
abstract class BaseConversationPresenter<VIEW : BaseConversationViewContract<*>>(
    private val messagePresenter: BaseConversationMessagesPresenterContract<VIEW>,
    private val panelPresenter: BaseConversationMessagePanelPresenterContract<VIEW>,
    private val toolbarPresenter: BaseConversationToolbarPresenterContract<VIEW>
) : BaseConversationPresenterContract<VIEW> {

    override val signDelegate: MessagePanelSignDelegate?
        get() = panelPresenter.signDelegate

    override var actionsMenuShown: Boolean
        get() = messagePresenter.actionsMenuShown
        set(value) { messagePresenter.actionsMenuShown = value }

    override fun onKeyboardAppears(keyboardHeight: Int) {
        messagePresenter.onKeyboardAppears(keyboardHeight)
    }

    override fun onKeyboardDisappears(keyboardHeight: Int) {
        messagePresenter.onKeyboardDisappears(keyboardHeight)
    }

    override fun onFastScrollDownPressed() {
        messagePresenter.onFastScrollDownPressed()
    }

    override fun onMessageActionClick(actionOrder: Int) {
        messagePresenter.onMessageActionClick(actionOrder)
    }

    override fun onQuoteClicked(quotedMessageUuid: UUID) {
        messagePresenter.onQuoteClicked(quotedMessageUuid)
    }

    override fun onQuoteLongClicked(enclosingMessageUuid: UUID) {
        messagePresenter.onQuoteLongClicked(enclosingMessageUuid)
    }

    override fun onPinnedMessageClicked(messageUuid: UUID) {
        messagePresenter.onPinnedMessageClicked(messageUuid)
    }

    override fun onScroll(dy: Int, firstVisibleItemPosition: Int, lastVisibleItemPosition: Int) {
        messagePresenter.onScroll(dy, firstVisibleItemPosition, lastVisibleItemPosition)
    }

    override fun onRefresh() {
        messagePresenter.onRefresh()
    }

    override fun onFirstItemShownStateChanged(shown: Boolean, atBottomOfItem: Boolean) {
        messagePresenter.onFirstItemShownStateChanged(shown, atBottomOfItem)
    }

    override fun onItemsLaidOut(topmostItemPosition: Int, bottommostItemPosition: Int) {
        messagePresenter.onItemsLaidOut(topmostItemPosition, bottommostItemPosition)
    }

    override fun onScrollStateChanged(state: Int) {
        messagePresenter.onScrollStateChanged(state)
    }

    override fun knownHead(): Boolean =
        messagePresenter.knownHead()

    override fun knownTail(): Boolean =
        messagePresenter.knownTail()

    override fun onVisibleRangeChanged(firstVisible: Int, lastVisible: Int, direction: Int) {
        messagePresenter.onVisibleRangeChanged(firstVisible, lastVisible, direction)
    }

    override fun onMessagePanelHeightChanged(difference: Int, isFirstLayout: Boolean) {
        messagePresenter.onMessagePanelHeightChanged(difference, isFirstLayout)
    }

    override fun onMessagePanelEnabled() {
        panelPresenter.onMessagePanelEnabled()
    }

    override fun onMessagePanelDisabled() {
        panelPresenter.onMessagePanelDisabled()
    }

    override fun onToolbarClick() {
        toolbarPresenter.onToolbarClick()
    }

    override fun onConfirmationDialogButtonClicked(tag: String?, id: String) {
        messagePresenter.onConfirmationDialogButtonClicked(tag, id)
        panelPresenter.onConfirmationDialogButtonClicked(tag, id)
        toolbarPresenter.onConfirmationDialogButtonClicked(tag, id)
    }

    override fun attachView(view: VIEW) {
        messagePresenter.attachView(view)
        panelPresenter.attachView(view)
        toolbarPresenter.attachView(view)
    }

    override fun detachView() {
        messagePresenter.detachView()
        panelPresenter.detachView()
        toolbarPresenter.detachView()
    }

    override fun onDestroy() {
        messagePresenter.onDestroy()
        panelPresenter.onDestroy()
        toolbarPresenter.onDestroy()
    }

    override fun viewIsStarted() {
        messagePresenter.viewIsStarted()
        panelPresenter.viewIsStarted()
        toolbarPresenter.viewIsStarted()
    }

    override fun viewIsStopped() {
        messagePresenter.viewIsStopped()
        panelPresenter.viewIsStopped()
        toolbarPresenter.viewIsStopped()
    }

    override fun viewIsResumed() {
        messagePresenter.viewIsResumed()
        panelPresenter.viewIsResumed()
        toolbarPresenter.viewIsResumed()
    }

    override fun viewIsPaused() {
        messagePresenter.viewIsPaused()
        panelPresenter.viewIsPaused()
        toolbarPresenter.viewIsPaused()
    }
}