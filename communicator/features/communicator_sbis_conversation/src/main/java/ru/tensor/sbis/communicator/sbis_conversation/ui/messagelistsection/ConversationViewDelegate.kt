package ru.tensor.sbis.communicator.sbis_conversation.ui.messagelistsection

import android.content.Context
import android.os.Handler
import android.widget.Toast
import androidx.annotation.Px
import androidx.annotation.StringRes
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import ru.tensor.sbis.base_components.BaseFragment
import ru.tensor.sbis.base_components.BaseProgressDialogFragment
import ru.tensor.sbis.base_components.adapter.sectioned.content.ListSectionHolder
import ru.tensor.sbis.communication_decl.complain.data.ComplainUseCase
import ru.tensor.sbis.communicator.base.conversation.data.model.MessageAction
import ru.tensor.sbis.communicator.sbis_conversation.adapters.MessageViewHolder
import ru.tensor.sbis.communicator.sbis_conversation.adapters.MessagesListAdapter
import ru.tensor.sbis.communicator.sbis_conversation.conversation.ConversationRouterImpl
import ru.tensor.sbis.communicator.sbis_conversation.ui.message.ConversationMessagesContract
import ru.tensor.sbis.communicator.sbis_conversation.ui.message.ConversationMessagesPresenter
import ru.tensor.sbis.communicator.sbis_conversation.data.model.ConversationMessage
import ru.tensor.sbis.communicator.sbis_conversation.ui.messagepanel.ConversationMessagePanelContract
import ru.tensor.sbis.communicator.sbis_conversation.ui.toolbar.ConversationToolbarPresenterImpl
import ru.tensor.sbis.design.stubview.StubViewContent
import ru.tensor.sbis.design.text_span.SimpleInformationView
import ru.tensor.sbis.design_dialogs.fragment.AlertDialogFragment
import ru.tensor.sbis.design_notification.snackbar.SnackbarBuilder
import ru.tensor.sbis.design_notification.SbisPopupNotification
import ru.tensor.sbis.modalwindows.dialogalert.PopupConfirmation
import timber.log.Timber
import java.util.*
import ru.tensor.sbis.communicator.design.R as RCommunicatorDesign
import ru.tensor.sbis.common.R as RCommon

/**
 * Делегат экрана переписки
 *
 * @param context - контекст
 * @param sectionHolder - холдер секции
 * @param presenter - презентер экрана переписки
 * @param toolbarPresenter - презентер тулбара экрана переписки
 * @param messageActionsDelegate - делегат действий с сообщениями
 * @param childFragmentManager - менеджер фрагментов
 * @param adapter - адаптер списка сообщений
 * @param onEditMessage - действие при редактировании сообщения
 * @param onReplyMessage - действие при ответе на сообщение
 */
internal class ConversationViewDelegate(private val context: Context,
                                        private val sectionHolder: ListSectionHolder,
                                        private val presenter: ConversationMessagesPresenter,
                                        private val toolbarPresenter: ConversationToolbarPresenterImpl,
                                        private val messageActionsDelegate: ConversationMessagePanelContract.Presenter<*>,
                                        private val childFragmentManager: FragmentManager,
                                        private val adapter: MessagesListAdapter,
                                        private val onEditMessage: ((messageUuid: UUID) -> Unit)?,
                                        private val onReplyMessage: ((themeUuid: UUID, messageUuid: UUID, countersUuid: UUID, showKeyboard: Boolean) -> Unit)?,
                                        private val containerId: Int = 0) :
    ConversationMessagesContract.View,
    AlertDialogFragment.YesNoListener {

    companion object {
        private const val DIALOG_CODE_CONFIRM_DELETE_DIALOG = 1
        private const val DIALOG_CODE_CONFIRM_DELETE_DIALOG_FOR_ALL = 4
        private const val DIALOG_CODE_CONFIRM_DELETE_MESSAGE_FOR_ALL = 5
        private const val DIALOG_CODE_CONFIRM_DELETE_MESSAGE = 6
        private const val DIALOG_CODE_SELECTED_MESSAGE_ACTIONS = 8
        private const val DIALOG_CODE_DELETED_DIALOG = 101
        private val NOTIFY_DIALOG_REMOVED_TAG = PopupConfirmation::class.simpleName + ":notifyDialogRemoved"
    }

    private var sbisListView: RecyclerView = sectionHolder.getListView()!!

    private val listHandler = Handler()

    private var progressDialogFragment: BaseProgressDialogFragment? = null

    private var lastToast: Toast? = null

    @Px
    var defaultRecyclerBottomPadding: Int = 0

    private var performDelayedScrollToPosition: Boolean = false

    private var delayedScrollPosition: Int = 0

    init {
        defaultRecyclerBottomPadding = context.resources.getDimensionPixelOffset(RCommunicatorDesign.dimen.communicator_messages_list_bottom_padding)

        sbisListView.apply {
            setPadding(
                sbisListView.paddingLeft,
                sbisListView.paddingTop,
                sbisListView.paddingRight,
                defaultRecyclerBottomPadding
            )
            isVerticalScrollBarEnabled = true
            stopScroll()
        }

        sbisListView.viewTreeObserver.addOnGlobalLayoutListener { markAsReadMessages() }
        listHandler.post { sbisListView.adapter = adapter }
    }

    /** @SelfDocumented */
    fun onAttachView() {
        val baseFragment = sectionHolder as BaseFragment
        val router = ConversationRouterImpl(baseFragment, containerId)
        presenter.setRouter(router)
        toolbarPresenter.setRouter(router)
    }

    /** @SelfDocumented */
    fun onDestroyRoot() {
        progressDialogFragment = null
        presenter.setRouter(null)
        toolbarPresenter.setRouter(null)
        listHandler.removeCallbacksAndMessages(null)
        lastToast?.cancel()
        lastToast = null
    }

    /** @SelfDocumented */
    override fun showConversationMembers() {
        toolbarPresenter.onToolbarClick()
    }

    /** @SelfDocumented */
    override fun showAttachmentsSigning(attachmentsUuids: List<UUID>) {
        //ignore
    }

    /** @SelfDocumented */
    override fun showProgressInRejectButton(show: Boolean, messagePosition: Int) {
        val holder = sbisListView.findViewHolderForAdapterPosition(messagePosition) as? MessageViewHolder
        holder?.changeRejectProgress(show)
    }

    override fun showPopupDeleteMessageForMe() {
        showPopupDeleteMessage(
            DIALOG_CODE_CONFIRM_DELETE_MESSAGE,
            RCommunicatorDesign.string.communicator_delete_message_forever
        )
    }

    override fun showPopupDeleteMessageForAll() {
        showPopupDeleteMessage(
            DIALOG_CODE_CONFIRM_DELETE_MESSAGE_FOR_ALL,
            RCommunicatorDesign.string.communicator_delete_message_forever
        )
    }

    override fun notifyDialogRemoved() {
        if (childFragmentManager.findFragmentByTag(NOTIFY_DIALOG_REMOVED_TAG) != null) return

        val popup = PopupConfirmation.newSimpleInstance(
            DIALOG_CODE_DELETED_DIALOG,
        ).also {
            it.requestTitle(context.resources.getString(RCommunicatorDesign.string.communicator_alert_dialog_title_dialog_removed))
            it.requestPositiveButton(context.resources.getString(RCommunicatorDesign.string.communicator_alert_info_continue))
            it.setEventProcessingRequired(true)
            it.isCancelable = false
        }
        try {
            popup.show(childFragmentManager, NOTIFY_DIALOG_REMOVED_TAG)
        } catch (ex: IllegalStateException) {
            Timber.w(ex, "ConversationViewDelegate.notifyDialogRemoved error: violation was already closed.")
        }
    }

    override fun updateSendingState(position: Int) {
        adapter.updateSendingState(position)
    }

    override fun getAdapterPosition(message: ConversationMessage): Int =
        adapter.getPositionForMessage(message)

    private fun showPopupDeleteMessage(requestCode: Int, title: Int) {
        PopupConfirmation.newSimpleInstance(
            requestCode,
        ).also {
            it.requestTitle(context.resources.getString(title))
            it.requestPositiveButton(context.resources.getString(RCommon.string.common_delete_dialog_positive), true)
            it.requestNegativeButton(context.resources.getString(RCommon.string.common_delete_dialog_negative))
            it.setEventProcessingRequired(true)
        }.show(childFragmentManager, PopupConfirmation::class.simpleName)
    }

    //region PopupErrorView
    /** @SelfDocumented */
    override fun showSnackbarError(errorTextId: Int) {
        SnackbarBuilder(sbisListView).shortDuration().message(errorTextId).actionText(null).show()
    }

    /** @SelfDocumented */
    override fun showToast(toastTextId: Int) {
        lastToast?.cancel()
        lastToast = SbisPopupNotification.pushToast(sbisListView.context, toastTextId)
    }

    /** @SelfDocumented */
    override fun showToast(message: String) {
        lastToast?.cancel()
        lastToast = SbisPopupNotification.pushToast(sbisListView.context, message)
    }

    /** @SelfDocumented */
    override fun showError(errorMessage: String) {
        Timber.e(Exception(errorMessage))
    }
    //endregion

    //region ConversationMessagesContract.View
    override fun setRelevantMessagePosition(position: Int) {
        sbisListView.post {
            if (position <= 0) {
                scrollToBottom(skipScrollToPosition = true, withHide = true)
            } else {
                scrollToPosition(position)
            }
        }
    }

    /** @SelfDocumented */
    override fun setFastScrollDownUnreadCounterValue(unreadCounter: Int) {
        //ignore
    }

    /** @SelfDocumented */
    override fun showFastScrollDownButton() {
        //ignore
    }

    override fun hideKeyboard() {
        //ignore
    }

    override fun finishConversationActivityWithCommonError() = Unit

    /** @SelfDocumented */
    override fun showMessageActionsList(message: ConversationMessage, actions: List<MessageAction>) {
        val dialogItems = ArrayList<String>()
        for (action in actions) {
            dialogItems.add(sbisListView.resources.getString(action.textRes))
        }

        val dialogFragment = AlertDialogFragment.newInstance(
            DIALOG_CODE_SELECTED_MESSAGE_ACTIONS,
            sbisListView.resources.getString(RCommunicatorDesign.string.communicator_choose_action_contextual_menu_title), dialogItems)

        dialogFragment.showAllowingStateLoss(childFragmentManager,
            AlertDialogFragment::class.java.simpleName)
    }

    override fun showPhoneNumberActionsList(messageUuid: UUID?, actions: List<Int>) = Unit

    /** @SelfDocumented */
    override fun setListViewBottomPadding(bottomPadding: Int) {
        //ignore
    }

    /** @SelfDocumented */
    override fun scrollListView(scrollBy: Int, post: Boolean) {
        if (post) {
            listHandler.post { sbisListView.scrollBy(0, scrollBy) }
        } else {
            sbisListView.scrollBy(0, scrollBy)
        }
    }

    /** @SelfDocumented */
    override fun setHighlightedMessageUuid(messageUuid: UUID?) {
        adapter.setHighlightedMessageUuid(messageUuid)
    }

    /** @SelfDocumented */
    override fun scrollToBottom(skipScrollToPosition: Boolean, withHide: Boolean) {
        sbisListView.scrollBy(0, Integer.MAX_VALUE)
        listHandler.post {
            sbisListView.scrollBy(0, Integer.MAX_VALUE)
        } //Изначально это был костыль, деталей не помню, но проблема была в скроллинге списка после изменения высоты панели
        // т.е. когда появляется строка со списком получателей, а появляется она не сразу, последний элемент был частично закрыт
        // панелью ввода сообщения. Я более не уверен, что этот подход костыльный, но у меня не хватило времени сделать
        // код не похожим на костыль. onMessagePanelChangeListener - вторая часть костыля
    }

    /** @SelfDocumented */
    override fun setMessagesListStyle(newConversation: Boolean) = Unit

    /** @SelfDocumented */
    override fun showControllerErrorMessage(errorMessage: String?) {
        errorMessage?.let {
            //sbisListView.showInformationViewData(createEmptyViewContent(it))
        }
    }

    @Suppress("unused")
    private fun createEmptyViewContent(@StringRes messageTextId: Int): SimpleInformationView.Content {
        return SimpleInformationView.Content(
            sbisListView.context, 0, messageTextId, 0)
    }

    @Suppress("unused")
    private fun createEmptyViewContent(messageText: String): SimpleInformationView.Content {
        return SimpleInformationView.Content(null, messageText, null)
    }

    @Suppress("unused")
    private fun createEmptyViewContent(@StringRes messageTextId: Int, @StringRes detailTextId: Int): SimpleInformationView.Content {
        return SimpleInformationView.Content(
            sbisListView.context, messageTextId, 0, detailTextId)
    }

    /** @SelfDocumented */
    override fun showUnattachedPhoneError(errorText: String?) = presenter.showVerificationPhoneDialog()

    override fun getStringRes(stringId: Int) = context.getString(stringId)
    //endregion

    //region BaseTwoWayPaginationView
    /** @SelfDocumented */
    override fun showNewerLoadingProgress(show: Boolean) {
        adapter.showNewerLoadingProgress(show)
    }

    /** @SelfDocumented */
    override fun showLoading() {
        //ignore
    }

    override fun showCancelRecordingConfirmationDialog() = Unit

    override fun showComplainDialogFragment(complainUseCase: ComplainUseCase) = Unit

    private fun markAsReadMessages() {
        val layoutManager = sbisListView.layoutManager as androidx.recyclerview.widget.LinearLayoutManager
        presenter.onItemsLaidOut(
            layoutManager.findLastVisibleItemPosition(),
            layoutManager.findFirstVisibleItemPosition()
        )
    }

    /** @SelfDocumented */
    override fun notifyItemsChanged(position: Int, count: Int) {
        notifyItemsChanged(position, count, null)
    }

    /** @SelfDocumented */
    override fun notifyItemsChanged(position: Int, count: Int, payload: Any?) {
        if (count > 1) {
            adapter.notifyItemRangeChanged(position, count)
        } else {
            if (payload != null) {
                adapter.notifyItemChanged(position, payload)
            } else {
                adapter.notifyItemChanged(position)
            }
        }
    }

    /** @SelfDocumented */
    override fun ignoreProgress(ignore: Boolean) {
        //sbisListView.ignoreProgress(ignore)
    }

    /** @SelfDocumented */
    override fun scrollToPosition(position: Int) {
        sbisListView.scrollToPosition(position)
        performDelayedScrollToPosition = true
        delayedScrollPosition = position
    }


    /** @SelfDocumented */
    override fun hideLoading() {
        //sbisListView.setRefreshing(false)
    }

    /** @SelfDocumented */
    override fun forceHideKeyboard() {
        //ignore
    }

    /** @SelfDocumented */
    override fun showOlderLoadingProgress(show: Boolean) {
        adapter.showOlderLoadingProgress(show)
    }

    /** @SelfDocumented */
    override fun showNewerLoadingError() {
        adapter.showNewerLoadingError()
    }

    /** @SelfDocumented */
    override fun showOlderLoadingError() {
        adapter.showOlderLoadingError()
    }

    /** @SelfDocumented */
    override fun resetPagingLoadingError() {
        adapter.resetPagingLoadingError()
    }

    //region YesNoNeutralListener
    /** @SelfDocumented */
    override fun onYes(dialogCode: Int) {
        when (dialogCode) {
            DIALOG_CODE_CONFIRM_DELETE_DIALOG_FOR_ALL  -> messageActionsDelegate.onDialogDeletingClicked()
            DIALOG_CODE_CONFIRM_DELETE_DIALOG          -> messageActionsDelegate.onDialogDeletingConfirmed()
            DIALOG_CODE_DELETED_DIALOG                 -> presenter.close()
            DIALOG_CODE_CONFIRM_DELETE_MESSAGE_FOR_ALL -> presenter.deleteMessageForAll()
            DIALOG_CODE_CONFIRM_DELETE_MESSAGE         -> presenter.deleteMessageOnlyForMe()
        }
    }

    /** @SelfDocumented */
    override fun onNo(dialogCode: Int) {
        //ignore
    }

    override fun hideFastScrollDownButton(force: Boolean) = Unit

    override fun updateDataList(dataList: List<ConversationMessage>?, offset: Int) {
        adapter.setData(dataList, offset)
    }

    override fun showStubView(messageTextId: Int) = Unit

    override fun showStubView(stubContent: StubViewContent) = Unit

    override fun hideStubView() = Unit

    override fun updateDataListWithoutNotification(dataList: List<ConversationMessage>?, offset: Int) {
        adapter.setDataWithoutNotify(dataList, offset)
    }

    /** @SelfDocumented */
    override fun onItem(dialogCode: Int, option: Int) {
        when (dialogCode) {
            DIALOG_CODE_SELECTED_MESSAGE_ACTIONS -> {
                if (presenter.currentActionIsEdit(option)) {
                    presenter.invokeForSelectedMessage(onEditMessage)
                } else if (presenter.currentActionIsQuote(option)) {
                    presenter.invokeForSelectedMessage(onReplyMessage)
                }
                presenter.onMessageActionClick(option)
            }
        }
    }
//endregion

}