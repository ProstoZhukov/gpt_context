@file:Suppress("MemberVisibilityCanBePrivate")

package ru.tensor.sbis.communicator.base.conversation.presentation.presenter.message_panel

import androidx.annotation.CallSuper
import io.reactivex.disposables.CompositeDisposable
import ru.tensor.sbis.attachments.generated.FileInfo
import ru.tensor.sbis.common.event.UnreadCountEvent
import ru.tensor.sbis.common.generated.ErrorCode
import ru.tensor.sbis.common.util.ResourceProvider
import ru.tensor.sbis.common.util.storeIn
import ru.tensor.sbis.communicator.base.conversation.presentation.ui.contracts.BaseConversationMessagePanelView
import ru.tensor.sbis.communicator.base.conversation.data.BaseConversationData
import ru.tensor.sbis.communicator.base.conversation.data.BaseCoreConversationInfo
import ru.tensor.sbis.communicator.base.conversation.data.model.BaseConversationMessage
import ru.tensor.sbis.communicator.base.conversation.data.model.ConversationAccess
import ru.tensor.sbis.communicator.base.conversation.interactor.BaseConversationInteractor
import ru.tensor.sbis.communicator.base.conversation.presentation.presenter.contracts.BaseConversationMessagePanelPresenterContract
import ru.tensor.sbis.communicator.base.conversation.presentation.presenter.dispatcher.BaseConversationDataDispatcher
import ru.tensor.sbis.communicator.base.conversation.presentation.presenter.dispatcher.BaseConversationState
import ru.tensor.sbis.communicator.base.conversation.presentation.presenter.dispatcher.ConversationEvent
import ru.tensor.sbis.communicator.base.conversation.presentation.presenter.dispatcher.NewMessageState
import ru.tensor.sbis.communicator.base.conversation.utils.AttachmentErrorDialogHelper
import ru.tensor.sbis.communicator.common.conversation.ConversationRouter
import ru.tensor.sbis.communicator.common.conversation.data.Message
import ru.tensor.sbis.communicator.generated.MessageResult
import ru.tensor.sbis.communicator.generated.SendMessageResult
import ru.tensor.sbis.communicator.core.utils.MessageUtils
import ru.tensor.sbis.communicator.design.R
import ru.tensor.sbis.design.buttons.base.models.style.PrimaryButtonStyle
import ru.tensor.sbis.design.confirmation_dialog.ButtonModel
import ru.tensor.sbis.design.confirmation_dialog.ConfirmationButtonId
import ru.tensor.sbis.message_panel.MessagePanelPlugin
import ru.tensor.sbis.message_panel.contract.MessagePanelSignDelegate
import ru.tensor.sbis.message_panel.contract.attachments.ViewerSliderArgsFactory
import ru.tensor.sbis.message_panel.integration.CommunicatorMessagePanelController
import ru.tensor.sbis.message_panel.integration.CommunicatorMessagePanelViewModel
import ru.tensor.sbis.message_panel.model.CoreConversationInfo
import ru.tensor.sbis.message_panel.model.EditContent
import ru.tensor.sbis.message_panel.model.QuoteContent
import ru.tensor.sbis.message_panel.viewModel.MessageAttachError
import ru.tensor.sbis.mvp.presenter.AbstractBasePresenter
import timber.log.Timber
import kotlin.collections.HashMap

/**
 * Базовая реализация делегата презентера по панели сообщений переписки.
 *
 * @author vv.chekurda
 */
abstract class BaseConversationMessagePanelPresenter<
    VIEW : BaseConversationMessagePanelView<MESSAGE>,
    INTERACTOR : BaseConversationInteractor<*>,
    MESSAGE : BaseConversationMessage,
    STATE : BaseConversationState<MESSAGE>,
    DATA : BaseConversationData,
    INFO : BaseCoreConversationInfo,
    DISPATCHER : BaseConversationDataDispatcher<MESSAGE, STATE, DATA>>(
        protected val interactor: INTERACTOR,
        protected val coreConversationInfo: INFO,
        protected val dataDispatcher: DISPATCHER,
        protected val resourceProvider: ResourceProvider? = null
) : AbstractBasePresenter<VIEW, Pair<UnreadCountEvent.EventType, HashMap<String, String>>>(null),
    BaseConversationMessagePanelPresenterContract<VIEW> {

    protected val conversationState: STATE
        get() = dataDispatcher.getConversationState()

    protected val conversationData: DATA?
        get() = dataDispatcher.getConversationData()

    protected var controller: CommunicatorMessagePanelController? = null
    protected var messagePanelViewModel: CommunicatorMessagePanelViewModel? = null

    protected var conversationAccess: ConversationAccess = ConversationAccess()
    protected open val isConversationDisabled: Boolean
        get() = (coreConversationInfo.isChat && conversationAccess.chatPermissions?.canSendMessage == false)
            || !conversationAccess.isAvailable

    override val signDelegate: MessagePanelSignDelegate? = null
    protected abstract var messagePanelCoreConversationInfo: CoreConversationInfo?
    protected abstract val viewerSliderArgsFactory: ViewerSliderArgsFactory

    protected open val isEditAttachmentsEnabled: Boolean = false

    protected var selectedAttachment: FileInfo? = null

    protected val compositeDisposable = CompositeDisposable()

    protected var isChatClosed = false

    protected var isInArchive = false

    protected var conversationRouter: ConversationRouter? = null

    private var isSubscribed = false

    @CallSuper
    override fun attachView(view: VIEW) {
        // важно вызвать перед получением событий от родительского презентера
        initMessagePanelController(view)
        super.attachView(view)
        if (!isSubscribed) {
            subscribeOnDataUpdate()
            isSubscribed = true
        }
    }

    override fun viewIsStarted() = Unit
    override fun viewIsStopped() = Unit
    override fun viewIsResumed() = Unit
    override fun viewIsPaused() = Unit

    @CallSuper
    protected open fun initMessagePanelController(view: VIEW) {
        controller = view.initMessagePanelController(
            messagePanelCoreConversationInfo,
            viewerSliderArgsFactory
        ).apply {
            onMessageSending = ::onMessageSending
            onMessageSent = ::onMessageSent
            onMessageEdit = ::onMessageEdited
            onMessageEditCanceled = ::onMessageEditCanceled
            onMessageAttachmentErrorClicked = ::onMessagePanelAttachmentErrorClicked
            onKeyboardForcedHidden = ::onKeyboardForcedHidden
            messagePanelViewModel = viewModel
        }
    }

    @CallSuper
    protected open fun subscribeOnDataUpdate() {
        dataDispatcher.run {
            compositeDisposable.addAll(
                conversationDataObservable.subscribe(::handleDataWithUpdate),
                conversationEventObservable.subscribe(::handleConversationEvent),
                conversationStateObservable.subscribe {
                    handleConversationStateChanges(currentState = it.first, newState = it.second)
                }
            )
        }
    }

    private fun handleDataWithUpdate(conversationData: DATA) {
        handleConversationDataChanges(conversationData)
        mView?.let { displayViewState(it) }
    }

    /**
     * Обработка состояния недоступности переписки
     */
    protected abstract fun handleConversationAvailabilityChanges(isDisable: Boolean)

    @CallSuper
    protected open fun handleConversationDataChanges(conversationData: DATA) {
        conversationAccess = conversationData.conversationAccess
    }

    protected open fun handleConversationStateChanges(currentState: STATE?, newState: STATE) = Unit

    @CallSuper
    protected open fun handleConversationEvent(event: ConversationEvent) {
        when (event) {
            ConversationEvent.EDIT_MESSAGE  -> editMessage(conversationState.selectedMessage!!.message!!)
            ConversationEvent.QUOTE_MESSAGE -> quoteMessage(conversationState.selectedMessage!!.message!!)
            ConversationEvent.STOP_EDITING  -> stopEditing()
            ConversationEvent.UPDATE_VIEW   -> mView?.run { displayViewState(this) }
            else                            -> Unit
        }
    }

    @CallSuper
    protected open fun editMessage(editingMessage: Message) {
        dataDispatcher.updateConversationState(
            conversationState.apply {
                newMessageState = NewMessageState.EDITING
                editedMessage = conversationState.selectedMessage
            }
        )
        controller!!.editMessage(
            EditContent(
                uuid = editingMessage.uuid,
                subtitle = MessageUtils.getEditMessageSubtitle(editingMessage, resourceProvider),
                text = MessageUtils.getEditMessageText(editingMessage),
                isAttachmentsEditable = !editingMessage.isMediaMessage && isEditAttachmentsEnabled
            )
        )
        mView?.showKeyboard()
    }

    protected open fun quoteMessage(quotedMessage: Message) {
        dataDispatcher.updateConversationState(conversationState.apply { newMessageState = NewMessageState.QUOTING })
        @Suppress("DEPRECATION")
        controller!!.quoteMessage(
            QuoteContent(
                quotedMessage.uuid,
                MessageUtils.getSenderNameForQuote(quotedMessage),
                MessageUtils.getMessageTextForQuote(quotedMessage, resourceProvider)
            )
        )
    }

    @CallSuper
    protected open fun stopEditing() {
        dataDispatcher.updateConversationState(
            conversationState.apply {
                selectedMessage = null
                editedMessage = null
                newMessageState = NewMessageState.DEFAULT
            }
        )
        controller!!.cancelEdit()
    }

    @CallSuper
    protected open fun onMessageSending() {
        dataDispatcher.updateConversationState(
            conversationState.apply {
                newMessageState = NewMessageState.SENDING
            }
        )
        if (conversationState.isNewConversation) {
            dataDispatcher.updateConversationState(conversationState.apply { isNewConversation = false })
            dataDispatcher.sendConversationEvent(ConversationEvent.UPDATE_VIEW)
        }
    }

    @CallSuper
    protected open fun onMessageSent(sendResult: SendMessageResult) {
        dataDispatcher.updateConversationState(conversationState.apply { newMessageState = NewMessageState.DEFAULT })
        val status = sendResult.status
        if (status.errorCode == ErrorCode.SUCCESS) {
            if (conversationState.isNewConversation) {
                dataDispatcher.updateConversationState(conversationState.apply { isNewConversation = false })
                dataDispatcher.sendConversationEvent(ConversationEvent.UPDATE_VIEW)
            }
        } else {
            mView?.showError(status.errorMessage)
        }
    }

    @CallSuper
    protected open fun onMessageEdited(editResult: MessageResult) {
        if (editResult.status.errorCode == ErrorCode.SUCCESS) {
            dataDispatcher.sendConversationEvent(ConversationEvent.STOP_EDITING)
        }
    }

    @CallSuper
    protected open fun onMessageEditCanceled() {
        dataDispatcher.sendConversationEvent(ConversationEvent.STOP_EDITING)
    }

    @CallSuper
    protected open fun onMessagePanelAttachmentErrorClicked(error: MessageAttachError) {
        if (error.errorMessage.isEmpty()) return
        selectedAttachment = error.fileInfo
        mView?.showConfirmationDialog(
            text = AttachmentErrorDialogHelper.getConfirmationDialogTitle(
                MessagePanelPlugin.resourceProvider.get(),
                error.errorMessage
            ),
            buttons = getPanelAttachmentErrorConfirmationDialogButtons(),
            tag = PANEL_ATTACHMENT_ERROR_CONFIRMATION_DIALOG_TAG
        )
    }

    private fun getPanelAttachmentErrorConfirmationDialogButtons(): List<ButtonModel<ConfirmationButtonId>> =
        listOf(
            ButtonModel(
                ConfirmationButtonId.NO,
                R.string.communicator_confirmation_dialog_attachment_error_no
            ),
            ButtonModel(
                ConfirmationButtonId.YES,
                R.string.communicator_confirmation_dialog_attachment_error_yes,
                PrimaryButtonStyle,
                true
            )
        )

    override fun onConfirmationDialogButtonClicked(tag: String?, id: String) {
        when (tag) {
            PANEL_ATTACHMENT_ERROR_CONFIRMATION_DIALOG_TAG -> {
                if (id == ConfirmationButtonId.YES.toString()) {
                    selectedAttachment?.id?.let { attachmentId ->
                        controller?.restartUploadAttachment(attachmentId)
                    } ?: Timber.e("${javaClass.simpleName}: Null id for restartUploadAttachment")
                }
            }
        }
    }

    override fun onDialogDeletingConfirmed() {
        if (conversationState.isNewConversation) {
            Timber.d("Attempt to delete new conversation")
            return
        }

        val deleteDialogSingle = if (isInArchive) {
            interactor.deleteDialogFromArchive(coreConversationInfo.conversationUuid!!, true)
        } else {
            interactor.deleteDialog(coreConversationInfo.conversationUuid!!)
        }
        deleteDialogSingle
            .subscribe(
                { commandStatus ->
                    if (commandStatus.errorCode == ErrorCode.SUCCESS) {
                        conversationRouter.safeExit()
                    } else {
                        mView?.showError(commandStatus.errorMessage)
                    }
                },
                { Timber.e(it) }
            )
            .storeIn(compositeDisposable)
    }

    override fun onDialogDeletingClicked() {
        interactor.deleteDialogFromArchive(coreConversationInfo.conversationUuid!!, false)
            .subscribe(
                { commandStatus ->
                    if (commandStatus.errorCode == ErrorCode.SUCCESS) {
                        conversationRouter.safeExit()
                    } else {
                        mView?.showError(commandStatus.errorMessage)
                    }
                },
                { Timber.e(it) }
            )
            .storeIn(compositeDisposable)
    }

    private fun onKeyboardForcedHidden() {
        mView?.forceHideKeyboard()
    }

    override fun detachView() {
        super.detachView()
        controller = null
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.dispose()
    }

    override fun onMessagePanelEnabled() {
        controller!!.setConversationInfo(messagePanelCoreConversationInfo)
    }

    override fun onMessagePanelDisabled() {
        controller!!.setConversationInfo(null)
    }

    @CallSuper
    override fun displayViewState(view: VIEW) {
        super.displayViewState(view)
        handleConversationAvailabilityChanges(isConversationDisabled)
    }

    override fun isNeedToDisplayViewState(): Boolean = true

    private fun ConversationRouter?.safeExit() = this?.exit()
        ?: Timber.w(IllegalStateException("Call exit() on null router. Presenter was detached"))
}

private const val PANEL_ATTACHMENT_ERROR_CONFIRMATION_DIALOG_TAG = "PANEL_ATTACHMENT_ERROR_CONFIRMATION_DIALOG_TAG"