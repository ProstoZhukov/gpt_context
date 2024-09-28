package ru.tensor.sbis.communicator.crm.conversation.presentation.presenter.message_panel

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.tensor.sbis.common.generated.ErrorCode
import ru.tensor.sbis.common.rx.livedata.value
import ru.tensor.sbis.common.util.UUIDUtils.NIL_UUID
import ru.tensor.sbis.communication_decl.crm.CRMConsultationCase
import ru.tensor.sbis.communication_decl.selection.recipient.RecipientSelectionUseCase
import ru.tensor.sbis.communicator.base.conversation.presentation.presenter.dispatcher.ConversationEvent
import ru.tensor.sbis.communicator.base.conversation.presentation.presenter.message_panel.BaseConversationMessagePanelPresenter
import ru.tensor.sbis.communicator.common.util.doIf
import ru.tensor.sbis.communicator.crm.conversation.data.CRMConversationData
import ru.tensor.sbis.communicator.crm.conversation.data.CRMCoreConversationInfo
import ru.tensor.sbis.communicator.crm.conversation.data.model.CRMConversationMessage
import ru.tensor.sbis.communicator.crm.conversation.interactor.contract.CRMConversationInteractor
import ru.tensor.sbis.communicator.crm.conversation.presentation.presenter.contracts.CRMConversationMessagePanelPresenterContract
import ru.tensor.sbis.communicator.crm.conversation.presentation.presenter.dispatcher.CRMConversationDataDispatcher
import ru.tensor.sbis.communicator.crm.conversation.presentation.presenter.dispatcher.CRMConversationState
import ru.tensor.sbis.communicator.crm.conversation.presentation.ui.contracts.CRMConversationMessagePanelView
import ru.tensor.sbis.communicator.crm.conversation.presentation.ui.viewmodel.CRMConversationViewModel
import ru.tensor.sbis.communicator.generated.SendMessageResult
import ru.tensor.sbis.consultations.generated.ConsultationActionsFlags
import ru.tensor.sbis.consultations.generated.ConsultationActionsFlags.CAN_QUICK_REPLY
import ru.tensor.sbis.message_panel.attachments.viewer.DefaultViewerSliderArgsFactory
import ru.tensor.sbis.message_panel.contract.attachments.ViewerSliderArgsFactory
import ru.tensor.sbis.message_panel.model.ClearOption
import timber.log.Timber
import java.util.*
import ru.tensor.sbis.message_panel.model.CoreConversationInfo as MessagePanelConversationInfo

/**
 * Реализация делегата презентера по панели сообщений чата CRM.
 *
 * @author da.zhukov
 */
internal class CRMConversationMessagePanelPresenter(
    interactor: CRMConversationInteractor,
    coreConversationInfo: CRMCoreConversationInfo,
    dataDispatcher: CRMConversationDataDispatcher,
    private val viewModel: CRMConversationViewModel?
) : BaseConversationMessagePanelPresenter<
        CRMConversationMessagePanelView, CRMConversationInteractor,
        CRMConversationMessage, CRMConversationState, CRMConversationData,
        CRMCoreConversationInfo, CRMConversationDataDispatcher
        >(interactor, coreConversationInfo, dataDispatcher),
    CRMConversationMessagePanelPresenterContract<CRMConversationMessagePanelView> {

    override var messagePanelCoreConversationInfo: MessagePanelConversationInfo? = null

    override val viewerSliderArgsFactory: ViewerSliderArgsFactory
        get() = DefaultViewerSliderArgsFactory

    override val isConversationDisabled: Boolean
        get() = conversationData?.isCompletedChat == true

    private val canQuickReply: Boolean
        get() {
            val canQuickReplyFlag = coreConversationInfo.allowedMenuOptions?.contains(CAN_QUICK_REPLY) ?: false
            val isOperator = coreConversationInfo.crmConsultationCase is CRMConsultationCase.Operator
            Timber.v("CAN_QUICK_REPLY: flag - $canQuickReplyFlag ; isOperator $isOperator")
            return canQuickReplyFlag && isOperator
        }

    override fun handleConversationDataChanges(conversationData: CRMConversationData) {
        super.handleConversationDataChanges(conversationData)
        isChatClosed = conversationData.isCompletedChat
        coreConversationInfo.allowedMenuOptions = conversationData.allowedActions
        coreConversationInfo.isMessagePanelVisible =
            conversationData.allowedActions?.contains(ConsultationActionsFlags.CAN_SEND_MESSAGE) == true
        coreConversationInfo.isCompleted = conversationData.isCompletedChat

        if (coreConversationInfo.allowedMenuOptions?.contains(ConsultationActionsFlags.CAN_VIEW) == false) {
            onMessagePanelDisabled()
        }
        changeButtonsVisibilityOnDataChanges(conversationData)
        fillPanelCoreConversationInfo()
        viewModel?.prepareQuickReplyViews?.onNext(canQuickReply)
    }

    private fun changeButtonsVisibilityOnDataChanges(conversationData: CRMConversationData) {
        viewModel?.showNextButton?.onNext(
            conversationData.nextConsultationUUID ?: NIL_UUID
        )
        viewModel?.showTakeButton?.onNext(
            conversationData.allowedActions?.contains(ConsultationActionsFlags.CAN_TAKE) ?: false
        )
        viewModel?.showReopenButton?.onNext(
            conversationData.allowedActions?.contains(ConsultationActionsFlags.CAN_REOPEN) ?: false
        )
        viewModel?.showHistoryButton?.onNext(
            conversationData.isHistory && !conversationData.isNew
        )
    }

    /**
     * Заполнение модели данных панели сообщений при наличии валидного uuid чата.
     * Если инициализировать панель сообщений с null conversationUuid,
     * то отвалится загрузка драфта вместе с прикреплением вложений
     */
    private fun fillPanelCoreConversationInfo() {
        coreConversationInfo.conversationUuid?.let {
            val isOperator = coreConversationInfo.crmConsultationCase is CRMConsultationCase.Operator
            messagePanelCoreConversationInfo =
                MessagePanelConversationInfo(
                    isTextRequired = conversationState.isNewConversation,
                    conversationUuid = it,
                    isNewConversation = conversationState.isNewConversation,
                    isChat = true,
                    showRecipientsPanel = isOperator,
                    inviteSupported = isOperator,
                    clearOnSendOptions = EnumSet.of(ClearOption.CLEAR_RECIPIENTS),
                    recipientSelectionUseCase = RecipientSelectionUseCase.ChatConsultation(it),
                    showQuickReplyButton = canQuickReply,
                )
        }
    }

    override fun initMessagePanelController(view: CRMConversationMessagePanelView) {
        super.initMessagePanelController(view)
        controller?.onTextChanged = { newText ->
            if (canQuickReply) {
                viewModel?.requestQuickRepliesOnTextInput?.onNext(newText)
            }
        }
    }

    override fun handleConversationEvent(event: ConversationEvent) {
        super.handleConversationEvent(event)
        when (event) {
            ConversationEvent.CRM_CHAT_CREATED,
            ConversationEvent.CRM_CHAT_IS_EXISTS -> onChatCreated()
            else -> Unit
        }
    }

    /**
     * Обработка события перехода от нового чата к существующему.
     * Событие должно приходить в момент, когда отправилось первое сообщение в новом чате,
     * или когда в процессе создания чата с заведением загрузились уже существующие с ним сообщения.
     */
    private fun onChatCreated() {
        fillPanelCoreConversationInfo()
        updateMessagePanelData()
    }

    override fun onMessageSent(sendResult: SendMessageResult) {
        dataDispatcher.doIf(conversationState.isNewConversation && sendResult.status.errorCode == ErrorCode.SUCCESS) {
            updateConversationState(conversationState.copy(isNewConversation = false))
            sendConversationEvent(ConversationEvent.CRM_CHAT_CREATED)
        }
        super.onMessageSent(sendResult)
    }

    override fun displayViewState(view: CRMConversationMessagePanelView) {
        super.displayViewState(view)
        updateMessagePanelData()
    }

    /**
     * Обновить данные в панели сообщений.
     */
    private fun updateMessagePanelData() {
        fillPanelCoreConversationInfo()
        // по механикам панели для disable состояния необходимо передавать null conversationInfo
        controller!!.setConversationInfo(if (isChatClosed) null else messagePanelCoreConversationInfo)
    }

    override fun handleConversationAvailabilityChanges(isDisable: Boolean) {
        mView?.run {
            val messagePanelIsDisable = isDisable ||
                    conversationData?.isCompletedChat == true ||
                    coreConversationInfo.isCompleted ||
                    !coreConversationInfo.isMessagePanelVisible

            if (messagePanelIsDisable) {
                viewModel?.showMessagePanel?.onNext(false)
                hideKeyboard()
                onMessagePanelDisabled()
            } else {
                viewModel?.showMessagePanel?.onNext(true)
                onMessagePanelEnabled()
            }
        }
    }

    override fun pasteTextInMessagePanel(text: String) {
        val needLineBreak = controller?.viewModel?.liveData?.messageText?.value?.isNotEmpty() ?: false
        controller?.viewModel?.liveData?.concatMessageText(
            StringBuilder().apply {
                doIf(needLineBreak) { appendLine() }
                append(text)
            }.toString()
        )
    }

    override fun replaceTextInMessagePanel(text: String) {
        controller?.viewModel?.liveData?.setMessageText(text)
    }

    override fun sendGreetingMessage(text: String) {
        if (controller?.viewModel?.attachmentPresenter?.attachments?.isEmpty() == true &&
            controller?.viewModel?.liveData?.recipientsUuidList?.isEmpty() == true
        ) {
            viewModel?.viewModelScope?.launch {
                interactor.sendGreetingMessage(coreConversationInfo.conversationUuid, text)
            }
        } else {
            replaceTextInMessagePanel(text)
            controller?.sendMessage()
        }
    }
}
