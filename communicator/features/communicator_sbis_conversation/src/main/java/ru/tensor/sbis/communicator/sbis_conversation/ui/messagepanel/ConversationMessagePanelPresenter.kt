package ru.tensor.sbis.communicator.sbis_conversation.ui.messagepanel

import androidx.fragment.app.Fragment
import io.reactivex.disposables.SerialDisposable
import io.reactivex.subjects.BehaviorSubject
import org.apache.commons.lang3.StringUtils
import ru.tensor.sbis.attachments.decl.isEncrypted
import ru.tensor.sbis.attachments.decl.isSignedByMe
import ru.tensor.sbis.attachments.generated.SignStateEnum
import ru.tensor.sbis.common.generated.ErrorCode
import ru.tensor.sbis.common.rx.livedata.value
import ru.tensor.sbis.common.util.ResourceProvider
import ru.tensor.sbis.common.util.UUIDUtils
import ru.tensor.sbis.common.util.storeIn
import ru.tensor.sbis.common_attachments.Attachment
import ru.tensor.sbis.communication_decl.model.ConversationType
import ru.tensor.sbis.communication_decl.selection.recipient.RecipientSelectionUseCase
import ru.tensor.sbis.communicator.base.conversation.presentation.presenter.dispatcher.ConversationEvent
import ru.tensor.sbis.communicator.base.conversation.presentation.presenter.dispatcher.NewMessageState
import ru.tensor.sbis.communicator.base.conversation.presentation.presenter.message_panel.BaseConversationMessagePanelPresenter
import ru.tensor.sbis.communicator.common.conversation.ConversationEventsPublisher
import ru.tensor.sbis.communicator.common.conversation.ConversationRouter
import ru.tensor.sbis.communicator.common.conversation.data.LinkDialogToTask
import ru.tensor.sbis.communicator.common.conversation.data.Message
import ru.tensor.sbis.communicator.common.conversation.data.TaskCreationResult
import ru.tensor.sbis.communicator.common.util.castTo
import ru.tensor.sbis.communicator.core.utils.MessageUtils
import ru.tensor.sbis.communicator.generated.ChatType
import ru.tensor.sbis.communicator.generated.SendMessageResult
import ru.tensor.sbis.communicator.generated.SignActions
import ru.tensor.sbis.communicator.sbis_conversation.CommunicatorSbisConversationPlugin.singletonComponent
import ru.tensor.sbis.communicator.sbis_conversation.data.CoreConversationInfo
import ru.tensor.sbis.communicator.sbis_conversation.data.model.ConversationData
import ru.tensor.sbis.communicator.sbis_conversation.data.model.ConversationMessage
import ru.tensor.sbis.communicator.sbis_conversation.interactor.ConversationInteractor
import ru.tensor.sbis.communicator.sbis_conversation.ui.ConversationDataDispatcher
import ru.tensor.sbis.communicator.sbis_conversation.ui.ConversationState
import ru.tensor.sbis.communicator.sbis_conversation.ui.GroupChatCreationState
import ru.tensor.sbis.communicator.sbis_conversation.ui.messagepanel.delegates.ConversationRecipientSelectionState
import ru.tensor.sbis.communicator.sbis_conversation.ui.messagepanel.delegates.RecipientSelectionStateDelegate
import ru.tensor.sbis.communicator.sbis_conversation.ui.viewmodel.ConversationViewModel
import ru.tensor.sbis.communication_decl.selection.recipient.manager.RecipientSelectionResultManager
import ru.tensor.sbis.communication_decl.selection.recipient.manager.RecipientSelectionResult
import ru.tensor.sbis.communicator.common.util.share.quick_share.QuickShareHelper
import ru.tensor.sbis.communicator.core.firebase_metrics.MetricsDispatcher
import ru.tensor.sbis.communicator.core.firebase_metrics.MetricsType
import ru.tensor.sbis.communicator.sbis_conversation.ui.ConversationPresenterImpl
import ru.tensor.sbis.communicator.sbis_conversation.utils.RecipientSelectionUtils
import ru.tensor.sbis.design.message_panel.audio_recorder.view.contract.AudioRecordViewState
import ru.tensor.sbis.design.message_panel.decl.recipients.data.RecipientItem
import ru.tensor.sbis.design.message_panel.video_recorder.view.contract.VideoRecordViewState
import ru.tensor.sbis.edo_decl.document.Document
import ru.tensor.sbis.localfeaturetoggle.data.FeatureSet
import ru.tensor.sbis.localfeaturetoggle.domain.LocalFeatureToggleService
import ru.tensor.sbis.message_panel.contract.MessagePanelController
import ru.tensor.sbis.message_panel.contract.MessagePanelSignDelegate
import ru.tensor.sbis.message_panel.contract.attachments.ViewerSliderArgsFactory
import ru.tensor.sbis.message_panel.helper.canAddRecipientForConversation
import ru.tensor.sbis.message_panel.model.ClearOption
import ru.tensor.sbis.message_panel.model.QuoteContent
import ru.tensor.sbis.persons.ContactVM
import ru.tensor.sbis.persons.IContactVM
import ru.tensor.sbis.tasks.feature.DiskAttachment
import timber.log.Timber
import java.util.EnumSet
import java.util.UUID
import ru.tensor.sbis.communicator.design.R as RCommunicatorDesign
import ru.tensor.sbis.message_panel.R as RMessagePanel
import ru.tensor.sbis.message_panel.model.CoreConversationInfo as MessagePanelConversationInfo

/**
 * Презентер панели сообщений
 *
 * @param interactor - интерактор сообщений
 * @param coreConversationInfo - основная информация о диалоге/чате
 * @param dataDispatcher - диспетчер событий в реестре сообщений
 * @param recipientSelectionResultManager - компонент, отвечающий за получение результатов выбора получателей для последующей отправки сообщения
 * @param conversationEventsPublisher - шина событий для публикации идентификатора диалога, в котором было отправлено сообщение
 */
internal class ConversationMessagePanelPresenter(
    interactor: ConversationInteractor,
    coreConversationInfo: CoreConversationInfo,
    dataDispatcher: ConversationDataDispatcher,
    resourceProvider: ResourceProvider,
    private val recipientSelectionResultManager: RecipientSelectionResultManager,
    private val viewModel: ConversationViewModel?,
    private val conversationEventsPublisher: ConversationEventsPublisher,
    override val viewerSliderArgsFactory: ViewerSliderArgsFactory,
    private val currentAccountUUID: UUID?,
    private val quickShareHelper: QuickShareHelper,
    private val localFeatureService: LocalFeatureToggleService? = null
) : BaseConversationMessagePanelPresenter<
        ConversationMessagePanelContract.View, ConversationInteractor, ConversationMessage,
        ConversationState, ConversationData, CoreConversationInfo, ConversationDataDispatcher
    >(interactor, coreConversationInfo, dataDispatcher, resourceProvider),
    ConversationMessagePanelContract.Presenter<ConversationMessagePanelContract.View>,
    ConversationRecipientSelectionState by RecipientSelectionStateDelegate() {

    override val isConversationDisabled: Boolean
        get() = super.isConversationDisabled || conversationData?.isLocked == true

    private val dependency = singletonComponent.dependency

    private val addChatMembersDisposable = SerialDisposable()
    private var conversationDataDraftUpdateSubscription = SerialDisposable()

    private var isGroupConversation: Boolean = false
    private var recipients: Set<IContactVM>? = null
    private var isIAmAuthor: Boolean = false
    private var document: Document? = null
    private var participants: List<ContactVM>? = null
    private var savedRecipients: List<UUID>? = null
    private var isRecipientsSelected: Boolean = false
    private var isChatFromDialogsRegistry: Boolean = coreConversationInfo.isChat && !coreConversationInfo.fromChatsRegistry
    private var isFirstDisplayViewData = true

    private var isAudioRecordButtonVisible: Boolean = true
    private var isVideoRecordButtonVisible: Boolean = true
    private var isRecordButtonsVisible: Boolean = true
        set(value) {
            field = value
            isAudioRecordButtonVisible = value
            isVideoRecordButtonVisible = value
        }
    private val isRecordAvailable: Boolean
        get() = dependency.callStateProviderFeature?.let { !it.isCallRunning() } ?: true

    /**
     * Отметка о том, что контент от пользователя доставлен в панель. После доставки, панель самостоятельно хранит
     * данные и управляет ими
     */
    private var isSharedContentDelivered = false

    private var chooseRecipientsForNewConversationShown: Boolean = false

    /**
     * Условие подстановки в панель сообщений списка получателей, переданных снаружи при открытии экрана сообщений.
     */
    private var needSetExternalRecipients: Boolean =
        coreConversationInfo.recipientUuid != null || !coreConversationInfo.recipientsUuids.isNullOrEmpty()

    /**
     * Признак диалога с самим собой. Необходим для правильного отображения кнопок подписи.
     */
    private val isDialogWithMyself: Boolean
        get() = conversationData?.participants?.let { it.size == 1 && it[0].uuid == currentAccountUUID } ?: false

    /**
     * Признак диалога 1 на 1.
     */
    private val isPersonalDialog: Boolean
        get() = !coreConversationInfo.isChat
            && conversationData?.toolbarData?.isSingleParticipant == true
            && !isGroupConversation

    private val isPanelNewDialogModeEnabled: Boolean
        get() = conversationState.isNewConversation && !openedAsThreadCreation

    private val openedAsThreadCreation: Boolean
        get() = coreConversationInfo.creationThreadInfo != null

    private val isMessagePanelEmpty: Boolean
        get() = messagePanelViewModel?.attachmentPresenter?.attachments.isNullOrEmpty() &&
                messagePanelViewModel?.liveData?.messageText?.value.isNullOrEmpty()

    /**
     * Use-case выбора получателей по текущему состоянию переписки.
     */
    private val recipientSelectionUseCase: RecipientSelectionUseCase
        get() = RecipientSelectionUtils.getSelectionUseCase(
            conversationUuid = coreConversationInfo.conversationUuid,
            isNewConversation = conversationState.isNewConversation,
            conversationType = coreConversationInfo.conversationType,
            isChat = coreConversationInfo.isChat,
            canAddParticipant = canAddRecipientForConversation(
                coreConversationInfo.isChat,
                conversationAccess.chatPermissions
            ),
            documentUuid = UUIDUtils.fromString(document?.uuid)
        )

    override val isEditAttachmentsEnabled: Boolean = true

    /**
     * Информация для инициализации панели ввода сообщений.
     *
     * Т.к. панель ввода по умолчанию выключена и информация для активации панели ввода не затирается, вызывать
     * [MessagePanelController.setConversationInfo] до появления [messagePanelCoreConversationInfo] не нужно
     * (публикуются лишние события отключения)
     */
    override var messagePanelCoreConversationInfo: MessagePanelConversationInfo? = null

    /** Делегат управления подписанием документов */
    override val signDelegate: MessagePanelSignDelegate
        get() = ConversationSignDelegate()

    init {
        viewModel?.showRecipientsPanel?.onNext(!coreConversationInfo.isPrivateChatCreation)
        if (!coreConversationInfo.recipientsUuids.isNullOrEmpty()) {
            chooseRecipientsForNewConversationShown = true
        }
        if (openedAsThreadCreation) recipientSelectionResultManager.clear()
        subscribeOnRecipientSelectionDone()
        checkAttachmentsUploadErrorsFeature()

        dataDispatcher.createTaskEventObservable.subscribe { showTaskCreation() }
            .storeIn(compositeDisposable)
    }

    private fun checkAttachmentsUploadErrorsFeature() {
        val featureService = localFeatureService ?: return
        val isActivated = featureService.isFeatureActivated(FeatureSet.ATTACHMENTS_UPLOAD_ERROR)

        interactor.setAttachmentsUploadErrors(isActivated)
            .subscribe()
            .storeIn(compositeDisposable)
    }

    override fun attachView(view: ConversationMessagePanelContract.View) {
        super.attachView(view)
        view.changeRecordEnable(
            isAudioEnabled = isAudioRecordButtonVisible,
            isVideoEnabled = isVideoRecordButtonVisible
        )
        if (needToShowRecipientSelection()) showRecipientSelectionForNewConversation()
    }

    override fun initMessagePanelController(view: ConversationMessagePanelContract.View) {
        super.initMessagePanelController(view)
        controller!!.run {
            setNewDialogModeEnabled(isPanelNewDialogModeEnabled)
            // подписка на изменение получателей должна быть установлена даже, если панель ввода не активирована
            onRecipientsChanged = { recipientList, selectedByUser ->
                if (!isRecipientsSelected && selectedByUser && recipientList.isEmpty()) {
                    isRecipientsSelected = true
                }
                if (selectedByUser || !isRecipientsSelected || isRecipientsSelected && recipientList.isNotEmpty()) {
                    recipients = LinkedHashSet(recipientList)
                }
            }
            if (coreConversationInfo.isPrivateChatCreation) changeRecipientsViewVisibility(isVisible = false)
            tryToSetExternalRecipients()
        }
    }

    /**
     * Попытаться подставить в панель сообщений список получателей, переданный снаружи при открытии переписки,
     * если это необходимо [needSetExternalRecipients].
     * Переданный панели флаг isUserSelected == true заблокирует автоматическую подстановку релевантных получателей,
     * которые могут прийти от контроллера.
     * Подставленные получатели могут автоматически сброситься после отправки сообщения.
     */
    private fun tryToSetExternalRecipients() {
        if (!needSetExternalRecipients || controller == null) return
        coreConversationInfo.also {
            val externalRecipients = it.recipientUuid?.let(::listOf)
                ?: it.recipientsUuids.takeIf { recipients -> !recipients.isNullOrEmpty() }
            externalRecipients?.let { recipients ->
                this.recipients = recipients.mapTo(mutableSetOf()) { ContactVM().apply { uuid = it } }
                controller?.setRecipients(recipients, isUserSelected = true)
                isRecipientsSelected = true
            }
            needSetExternalRecipients = false
        }
    }

    /**@SelfDocumented*/
    override fun subscribeOnDataUpdate() {
        super.subscribeOnDataUpdate()
        dataDispatcher.addRecipientObservable
            .subscribe(::addRecipient)
            .storeIn(compositeDisposable)
    }

    /**@SelfDocumented*/
    override fun handleConversationDataChanges(conversationData: ConversationData) {
        super.handleConversationDataChanges(conversationData)
        conversationData.let {
            isGroupConversation = it.isGroupConversation
            isIAmAuthor = it.isIAmAuthor
            document = it.document
            participants = it.participants
            isInArchive = it.isInArchive

            if (!isRecipientsSelected && (!coreConversationInfo.isChat || !it.recipients.isNullOrEmpty() && isChatFromDialogsRegistry)) {
                val dataRecipients: MutableList<ContactVM> = it.recipients?.toMutableList() ?: mutableListOf()
                recipients = LinkedHashSet(dataRecipients)
                isChatFromDialogsRegistry = false
            }
            fillMessagePanelCoreConversationInfo()
            checkRecordAvailability(conversationData)
        }
    }

    private fun checkRecordAvailability(conversationData: ConversationData) {
        val isSupportedChatType = conversationData.chatType == ChatType.PRIVATE ||
                conversationData.chatType == ChatType.UNKNOWN
        val isSupportedConversationType = when (coreConversationInfo.conversationType) {
            ConversationType.REGULAR,
            ConversationType.DOCUMENT_CONVERSATION,
            ConversationType.VIDEO_CONVERSATION-> true
            else -> false
        }
        val isVisible = isSupportedChatType && isSupportedConversationType

        if (isVisible != isRecordButtonsVisible) {
            isRecordButtonsVisible = isVisible
            mView?.changeRecordEnable(
                isAudioEnabled = isVisible,
                isVideoEnabled = isVisible
            )
        }
    }

    /**@SelfDocumented*/
    override fun handleConversationEvent(event: ConversationEvent) {
        super.handleConversationEvent(event)
        when (event) {
            ConversationEvent.BLOCK_MESSAGE_SENDING -> viewModel?.showMessagePanel?.onNext(false)
            ConversationEvent.SAVE_RECIPIENTS -> savedRecipients = ArrayList<UUID>(recipientSelectionResultManager.selectionResult.data.allPersonsUuids)
            ConversationEvent.SELECT_RECIPIENTS -> showRecipientSelection()
            ConversationEvent.THREAD_DRAFT_CREATED -> {
                isRecipientsSelected = true
                val preselectedParticipants = recipientSelectionResultManager.preselectedData!!.ids
                recipients = preselectedParticipants.map { ContactVM().apply { uuid = it.uuid  } }.toSet()
            }
            ConversationEvent.SELECT_THREAD_PARTICIPANTS -> {
                dataDispatcher.updateConversationState(conversationState.copy(isThreadParticipantsSelection = true))
                recipientSelectionResultManager.preselectIds(emptyList())
                conversationRouter?.showRecipientSelection(RecipientSelectionUseCase.NewDialog)
            }
            else -> Unit
        }
    }

    private fun subscribeOnRecipientSelectionDone() {
        recipientSelectionResultManager
            .getSelectionResultObservable()
            .subscribe(::onRecipientsCollectionChanged) { error ->
                Timber.d(error, "Failed to change recipients selection in ${ConversationPresenterImpl::class.java.simpleName}")
            }.storeIn(compositeDisposable)
    }

    private fun onRecipientsCollectionChanged(selectionResult: RecipientSelectionResult) {
        onRecipientSelectionResult(selectionResult)
        if (selectionResult.isCanceled || conversationState.isChatSettingsShown) {
            onRecipientsChangingCanceled()
            return
        } else if (conversationState.isThreadParticipantsSelection) {
            dataDispatcher.updateConversationState(conversationState.copy(isThreadParticipantsSelection = false))
            dataDispatcher.sendConversationEvent(ConversationEvent.SHOW_THREAD_CREATION)
            return
        }
        if (conversationState.isChoosingRecipients) {
            mView?.showKeyboard()
        }
        val recipients = selectionResult.data.allPersonsUuids
        val oldRecipients = getRecipientUuids()

        if (recipients.size == oldRecipients.size && oldRecipients.containsAll(recipients)) {
            return
        }

        if (selectionResult.isSuccess) isRecipientsSelected = recipients.isNotEmpty()

        val conversationUuid = coreConversationInfo.conversationUuid ?: return

        if (conversationState.addRecipientsToChat) {
            if (coreConversationInfo.isChat && recipients.isNotEmpty()) {
                addChatMembersDisposable.set(
                    interactor.addChatParticipants(conversationUuid, recipients)
                        .subscribe({
                            updateDraftConversationData(conversationUuid, coreConversationInfo.getDocumentUUID())
                        }, {
                            mView?.showToast(RMessagePanel.string.message_panel_chat_add_participants_failure)
                                ?: dataDispatcher.updateConversationState(conversationState.copy(missedToastErrorRes = RMessagePanel.string.message_panel_chat_add_participants_failure))
                        })
                )
                recipientSelectionResultManager.clear()
            }
        } else if (coreConversationInfo.conversationType == ConversationType.VIDEO_CONVERSATION) {
            interactor.updateDialog(conversationUuid, recipients)
                .subscribe(
                    { updateDraftConversationData(conversationUuid, coreConversationInfo.getDocumentUUID()) },
                    { error -> Timber.e(error, "Failed to update video conversation dialog") }
                ).storeIn(compositeDisposable)
            controller!!.setRecipients(recipients, isRecipientsSelected)
        } else if (conversationState.isNewConversation && !coreConversationInfo.isChat && recipients.isNotEmpty()) {
            interactor.updateDialog(conversationUuid, recipients)
                .subscribe(
                    { updateDraftConversationData(conversationUuid, coreConversationInfo.getDocumentUUID()) },
                    { error -> Timber.e(error, "Failed tp add participants to new dialog") }
                ).storeIn(compositeDisposable)
            controller!!.setRecipients(recipients, isRecipientsSelected)
        } else if (conversationState.isChoosingRecipients) {
            controller!!.setRecipients(recipients, isRecipientsSelected)
        }
        dataDispatcher.updateConversationState(
            conversationState.copy(isChoosingRecipients = false)
        )
    }

    private fun needToShowRecipientSelection() =
        !chooseRecipientsForNewConversationShown
            && conversationState.isNewConversation
            && !openedAsThreadCreation
            && recipients.isNullOrEmpty()

    private fun showRecipientSelectionForNewConversation() {
        if (chooseRecipientsForNewConversationShown) return

        conversationRouter!!.showRecipientSelection(RecipientSelectionUseCase.NewDialog)
        chooseRecipientsForNewConversationShown = true
    }

    private fun updateDraftConversationData(dialogUuid: UUID?, documentUuid: UUID?) {
        interactor.loadConversationData(
            dialogUuid,
            documentUuid,
            startMessage = null,
            messagesCount = 0,
            isChat = coreConversationInfo.isChat,
            isConsultation = coreConversationInfo.conversationType == ConversationType.CONSULTATION
        )
            .subscribe({ conversationDataResult ->
                //временное решение для отправки сообщений в новом диалоге, должно быть реализовано со стороны контроллера (поле mReceivers в getConversationData() не должно быть пустым)
                val conversationData = conversationDataResult.conversationData
                if (conversationState.isNewConversation) {
                    conversationData.participants?.let {
                        conversationData.recipients = it.map { ContactVM().apply { uuid = it.uuid } }
                    }
                    conversationData.let { dataDispatcher.updateData(it) }
                }
                dataDispatcher.updateData(conversationDataResult.conversationData)
                dataDispatcher.sendConversationEvent(ConversationEvent.UPDATE_VIEW)
            }) { Timber.e(it,"Failed to update draft conversation data") }
            .storeIn(conversationDataDraftUpdateSubscription)
    }

    override fun onRecipientsChangingCanceled() {
        dataDispatcher.updateConversationState(
            conversationState.copy(
                isChoosingRecipients = false,
                isThreadParticipantsSelection = false
            )
        )
        if (conversationState.isNewConversation && savedRecipients.isNullOrEmpty() && !openedAsThreadCreation) {
            dataDispatcher.updateConversationState(
                conversationState.copy(groupChatCreationState = GroupChatCreationState.UNDEFINED)
            )
            chooseRecipientsForNewConversationShown = false
            conversationRouter?.exit()
        }
    }

    /** Цитирование сообщения */
    override fun quoteMessage(quotedMessage: Message) {
        val senderName = MessageUtils.getSenderNameForQuote(quotedMessage)
        val text = MessageUtils.getMessageTextForQuote(quotedMessage, resourceProvider)
        dataDispatcher.updateConversationState(conversationState.apply { newMessageState = NewMessageState.QUOTING })
        @Suppress("DEPRECATION")
        controller!!.quoteMessage(
            content = QuoteContent(quotedMessage.uuid, senderName, text),
            showKeyboard = !conversationState.audioRecordState.isSendPreparing
        )
        if (!quotedMessage.outgoing) {
            val uuid = quotedMessage.senderViewData.uuid
            controller!!.setRecipients(if (uuid != null) listOf(uuid) else emptyList())
        }
    }

    override fun onDialogParticipantChoosed(profileUuid: UUID) {
        addRecipient(profileUuid)
    }

    override fun onBackPressed(): Boolean {
        if (!conversationState.audioRecordState.isVisible) {
            coreConversationInfo.conversationUuid?.let { conversationEventsPublisher.onConversationClosed(it) }
            recipientSelectionResultManager.clear()
        }
        return false
    }

    override fun viewIsResumed() {
        var addRecipientsToChat = conversationState.addRecipientsToChat
        var isChatSettingsShown = conversationState.isChatSettingsShown
        if (addRecipientsToChat) {
            addRecipientsToChat = false
            recipientSelectionResultManager.preselect(savedRecipients)
        }
        savedRecipients = ArrayList<UUID>(recipientSelectionResultManager.selectionResult.data.allPersonsUuids)

        if (isChatSettingsShown) {
            isChatSettingsShown = false
        }
        dataDispatcher.updateConversationState(
            conversationState.copy(
                addRecipientsToChat = addRecipientsToChat,
                isChatSettingsShown = isChatSettingsShown,
                ignoreKeyboardEvents = false
            )
        )
    }

    override fun messageFileSigningSuccess() {
        conversationState.signActionChosen?.let(controller!!::signMessage)
    }

    override fun messageFileSigningFailure() {
        dataDispatcher.updateConversationState(conversationState.copy(signActionChosen = null))
    }

    override fun onSignMenuItemClicked() {
        dataDispatcher.updateConversationState(
            conversationState.copy(signActionChosen = SignActions.SIGN)
        )
        signMessage()
    }

    private fun signMessage() {
        mView?.run {
            val officeAttachments = controller!!.attachmentPresenter.attachments.filter { it.isOffice }
            val attachmentsUuids: List<UUID?> = officeAttachments.map {
                    if (it.cloudAttachId != null) {
                        UUIDUtils.fromString(it.cloudAttachId)
                    } else if (!it.isUploading) {
                        UUIDUtils.fromString(it.diskFileId)
                    } else {
                        null
                    }
                }
            if (attachmentsUuids.any { it == null }) {
                showToast(RCommunicatorDesign.string.communicator_attachments_loading_to_sign)
            } else {
                val signIsNotAvailable = officeAttachments.any {
                    it.signState == SignStateEnum.MY_SIGN || it.signState == SignStateEnum.BOTH_SIGNS
                }
                if (signIsNotAvailable) {
                    showToast(RCommunicatorDesign.string.communicator_attachment_is_already_signed_by_you)
                } else {
                    showAttachmentsSigning(attachmentsUuids)
                }
            }
        }
    }

    override fun onRequestSignatureMenuItemClicked() {
        dataDispatcher.updateConversationState(
            conversationState.copy(signActionChosen = SignActions.REQUEST_SIGN)
        )
        controller!!.signMessage(SignActions.REQUEST_SIGN)
    }

    override fun onSignAndRequestMenuItemClicked() {
        dataDispatcher.updateConversationState(
            conversationState.copy(signActionChosen = SignActions.SIGN_AND_REQUEST_SIGN)
        )
        signMessage()
    }

    override fun onChangeRecipientsClick() {
        showRecipientSelection()
    }

    override fun onDeleteDialog() {
        if (isInArchive) {
            if (isIAmAuthor) {
                mView?.showPopupDeleteDialogForAll()
            } else {
                mView?.showDeletingConfirmationDialog(RMessagePanel.string.message_panel_delete_dialog_forever)
            }
        } else {
            onDialogDeletingConfirmed()
        }
    }

    override fun onDialogDeletingConfirmed() {
        if (coreConversationInfo.conversationType == ConversationType.VIDEO_CONVERSATION) {
            Timber.d("Attempt to delete video conversation")
            return
        }
        super.onDialogDeletingConfirmed()
    }

    override fun onMessageSending() {
        MetricsDispatcher.startTrace(MetricsType.FIREBASE_SEND_AND_GET_NEW_MESSAGE)
        super.onMessageSending()
        coreConversationInfo.conversationUuid?.let(conversationEventsPublisher::onMessageSent)
        if (conversationState.isThreadCreation) {
            dataDispatcher.updateConversationState(conversationState.copy(threadCreationServiceObject = null))
        }
    }

    override fun onMessageSent(sendResult: SendMessageResult) {
        dataDispatcher.updateConversationState(
            conversationState.copy(
                signActionChosen = null,
                isPrivateChatCreation = false
            )
        )
        isRecipientsSelected = false

        if (sendResult.status.errorCode == ErrorCode.SUCCESS) {
            if (conversationState.isNewConversation) {
                recipients = null
            }

            val needPushChannelDirectShareTargets = coreConversationInfo.isChat
                    && coreConversationInfo.conversationUuid != null
                    && conversationData?.toolbarData != null
            val needPushContactDirectShareTargets = isPersonalDialog && conversationData?.toolbarData != null
            when {
                needPushContactDirectShareTargets -> {
                    quickShareHelper.pushContactQuickShareTargets(conversationData?.toolbarData!!.participantsData.participants)
                }
                needPushChannelDirectShareTargets -> {
                    val context = (mView as? Fragment)?.requireContext()
                    val toolbarData = conversationData?.toolbarData!!
                    val photoDataList = toolbarData.photoDataList
                    val photoUrl: String? =
                        if (photoDataList.size == 1) photoDataList[0].photoUrl else StringUtils.EMPTY
                    context?.let {
                        quickShareHelper.pushChannelQuickShareTargets(
                            uuid = coreConversationInfo.conversationUuid!!,
                            title = toolbarData.title,
                            photoUrl = photoUrl
                        )
                    }
                }
            }
        }
        super.onMessageSent(sendResult)
    }

    override fun onRecordCompleted() {
        collapseNewConversationPanel()
    }

    private fun collapseNewConversationPanel() {
        if (!conversationState.isNewConversation) return
        dataDispatcher.updateConversationState(conversationState.copy(isNewConversation = false))
        fillMessagePanelCoreConversationInfo()
        mView?.also(::displayViewState)
    }

    override fun onAudioRecordStateChanged(state: AudioRecordViewState) {
        dataDispatcher.updateConversationState(conversationState.copy(audioRecordState = state))
    }

    override fun onVideoRecordStateChanged(state: VideoRecordViewState) {
        dataDispatcher.updateConversationState(conversationState.copy(videoRecordState = state))
    }

    private fun addRecipient(recipientUuid: UUID) {
        val recipientUuids = getRecipientUuids()
        val newRecipients = listOf(recipientUuid)
        if (!isRecipientsSelected || conversationData?.participants?.size == recipientUuids.size) {
            controller!!.setRecipients(recipients = newRecipients, isUserSelected = true)
        } else {
            controller!!.addRecipients(recipients = newRecipients, isUserSelected = true)
        }
        isRecipientsSelected = true
    }

    private fun showRecipientSelection() {
        recipientSelectionResultManager.preselect(getRecipientUuids())
        conversationRouter!!.showRecipientSelection(useCase = recipientSelectionUseCase)

        chooseRecipientsForNewConversationShown = true
    }

    private fun getRecipientUuids(): List<UUID> =
        recipients?.map { it.uuid } ?: emptyList()

    private fun getPersonsUuids(): List<UUID> =
        participants?.map { it.uuid } ?: emptyList()

    override fun detachView() {
        super.detachView()
        controller = null
    }

    override fun onDestroy() {
        super.onDestroy()
        tryDeleteDraftDialog()
        addChatMembersDisposable.dispose()
        conversationDataDraftUpdateSubscription.dispose()
        recipientSelectionResultManager.clear()
    }

    /**
     * Попытаться удалить драфтовый диалог, если он новый.
     * Удаление необходимо для корректного отображения шапки без перепрыжек,
     * когда ранее была попытка создания диалога пустого диалога с этим же набором получателей,
     * но в другом порядке.
     */
    private fun tryDeleteDraftDialog() {
        if (!coreConversationInfo.tablet && conversationState.isNewConversation && isMessagePanelEmpty) {
            interactor.deleteDraftDialog(coreConversationInfo.conversationUuid!!).subscribe()
        }
    }

    override fun setRouter(router: ConversationRouter?) {
        conversationRouter = router
    }

    override fun displayViewState(view: ConversationMessagePanelContract.View) {
        super.displayViewState(view)
        val conversationInfo = messagePanelCoreConversationInfo ?: return
        controller!!.apply {
            // обновление внутреннего состояния панели
            setConversationInfo(conversationInfo)
            if (!isSharedContentDelivered) {
                isSharedContentDelivered = true
            }
        }
        if (!conversationState.audioRecordState.isVisible) {
            controller!!.setRecipients(getRecipientUuids())
        }
    }

    /** В зависимости от [isDisable] разрешаем или запрещаем в беседе отображение клавиатуры */
    override fun handleConversationAvailabilityChanges(isDisable: Boolean) {
        if (isDisable) {
            viewModel?.showMessagePanel?.onNext(false)
            mView?.hideKeyboard()
            dataDispatcher.updateConversationState(
                conversationState.copy(keyboardShownForConversation = false)
            )
        } else {
            viewModel?.apply {
                showMessagePanel.onNext(true)
                showRecipientsPanel.onNext(!conversationState.isPrivateChat)
                if (isFirstDisplayViewData) {
                    isFirstDisplayViewData = false
                    firstDataLoaded.onNext(true)
                }
            }
        }
    }

    private fun fillMessagePanelCoreConversationInfo() {
        val isVideoConversation = coreConversationInfo.conversationType == ConversationType.VIDEO_CONVERSATION
        messagePanelCoreConversationInfo = MessagePanelConversationInfo(
            conversationType = coreConversationInfo.conversationType,
            conversationUuid = coreConversationInfo.conversationUuid,
            isGroupConversation = isGroupConversation && conversationAccess.canChooseRecipients,
            messageUuid = coreConversationInfo.messageUuid,
            folderUuid = coreConversationInfo.folderUuid,
            recipientSelectionUseCase = recipientSelectionUseCase,
            recipients = getRecipientUuids(),
            showRecipientsPanel = (conversationData?.conversationAccess?.canChooseRecipients ?: true) && !coreConversationInfo.isPrivateChatCreation,
            isRecipientsHintEnabled = (conversationState.isNewConversation || isPersonalDialog) && !isVideoConversation,
            requireCheckAllMembers = !conversationState.isNewConversation,
            inviteSupported = !conversationState.isThreadCreation && !conversationState.isNewConversation && !coreConversationInfo.isPrivateChatCreation,
            isChat = coreConversationInfo.isChat,
            chatMembers = getPersonsUuids(),
            chatPermissions = conversationAccess.chatPermissions,
            document = document?.uuid?.run(UUID::fromString),
            isNewConversation = conversationState.isNewConversation,
            isNewDialogModeEnabled = isPanelNewDialogModeEnabled,
            clearOnSendOptions = getClearOnSendOptions(),
            isMultiDialog = false,
            analyticsUsageName = if (coreConversationInfo.isChat) ANALYTICS_CHANNEL_USAGE_NAME else ANALYTICS_DIALOG_USAGE_NAME,
            messageMetaData = conversationState.threadCreationServiceObject
        )
    }

    /**
     * Опция очистки получателей после отправки сообщения должна быть только для чатов,
     * за исключением тех, что открываются из реестра диалогов
     */
    private fun getClearOnSendOptions(): EnumSet<ClearOption> =
        if (coreConversationInfo.isChat
            && isGroupConversation
            && coreConversationInfo.fromChatsRegistry
        ) {
            EnumSet.of(ClearOption.CLEAR_RECIPIENTS)
        } else {
            EnumSet.noneOf(ClearOption::class.java)
        }

    private inner class ConversationSignDelegate : MessagePanelSignDelegate {

        override fun isSignButtonVisible(attachments: List<Attachment>): Boolean {
            // по меньшей мере один файл не должен быть изображением для возможности подписывать
            return !coreConversationInfo.isChat && attachments.any { it.canSign() }
        }

        override fun onSignButtonClicked() {
            mView?.showSigningActionsMenu(isDialogWithMyself)
        }
    }

    private fun showTaskCreation() {
        val dialogUuid = controller!!.viewModel.conversationInfo.conversationUuid!!
        val attachments = controller!!.attachmentPresenter.attachments
        val diskAttachments = attachments.filter { !it.cloudAttachId.isNullOrBlank() }
        val localAttachments = (attachments - diskAttachments).filter { !it.localUrl.isNullOrBlank() }

        val otherAttachments = attachments - diskAttachments - localAttachments
        if (otherAttachments.isNotEmpty()) { Timber.e("Не обработанные вложения: ${otherAttachments}?") }

        val attachmentsFromUris: List<String> = localAttachments.mapNotNull { it.localUrl }.map { "file://$it" }
        val attachmentsFromDisk: List<DiskAttachment> = diskAttachments.map {
            DiskAttachment(
                it.sbisDiskId,
                DiskAttachment.LocalIds(it.id, it.redId, it.attachId),
                it.fileName,
                DiskAttachment.Attributes(
                    it.isFolder,
                    it.previewUrl,
                    it.foreignSignsCount ?: 0,
                    it.isSignedByMe,
                    it.isEncrypted,
                    it.size ?: 0,
                    // Тоже, как и буфферу, нашим локальным файлам нужно копирование, только поэтому.
                    isFromBuffer = it.localUrl != null,
                    isLink = it.isLink
                )
            )
        }

        val description = controller!!.viewModel.liveData.messageText.value!!

        var executors = controller!!.viewModel.liveData.recipients.castTo<BehaviorSubject<List<RecipientItem>>>()!!.value!!
            .map { it.uuid }

        // В персональном диалоге берем собеседника как исполнителя для задачи, если в панели сообщений никто не выбран
        if (executors.isEmpty() && conversationData?.isGroupConversation == false && conversationData?.participants?.size == 1) {
            executors = conversationData?.participants?.map { it.uuid } ?: emptyList()
        }

        val linkType = when {
            document != null && conversationState.isNewConversation -> null
            document != null                                        -> LinkDialogToTask.APPEND
            conversationState.isNewConversation                     -> LinkDialogToTask.LINK
            else                                                    -> LinkDialogToTask.ASK
        }
        conversationRouter?.showTaskCreation(dialogUuid, linkType, currentAccountUUID!!, executors, description, attachmentsFromUris, attachmentsFromDisk) {
            if (it == TaskCreationResult.SUCCESS) {
                controller?.viewModel?.clearPanel(true)
                mView?.scrollToBottom(skipScrollToPosition = true, withHide = true)
            }
            mView?.forceHideKeyboard()
        }
    }
}

private const val ANALYTICS_CHANNEL_USAGE_NAME = "channel"
private const val ANALYTICS_DIALOG_USAGE_NAME = "dialog"