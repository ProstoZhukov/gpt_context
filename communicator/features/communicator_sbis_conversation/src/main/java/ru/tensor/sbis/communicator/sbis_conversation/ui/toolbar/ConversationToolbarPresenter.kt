package ru.tensor.sbis.communicator.sbis_conversation.ui.toolbar

import androidx.lifecycle.viewModelScope
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.SerialDisposable
import kotlinx.coroutines.launch
import org.apache.commons.lang3.StringUtils
import ru.tensor.sbis.common.generated.ErrorCode
import ru.tensor.sbis.common.util.DeviceConfigurationUtils
import ru.tensor.sbis.common.util.ResourceProvider
import ru.tensor.sbis.common.util.storeIn
import ru.tensor.sbis.communication_decl.communicator.event.MultiPersonsTitleEvent
import ru.tensor.sbis.communication_decl.communicator.event.SinglePersonTitleEvent
import ru.tensor.sbis.communication_decl.complain.data.ComplainUseCase
import ru.tensor.sbis.communication_decl.model.ConversationType.DOCUMENT_CONVERSATION
import ru.tensor.sbis.communication_decl.model.ConversationType.VIDEO_CONVERSATION
import ru.tensor.sbis.communication_decl.selection.recipient.manager.RecipientSelectionResultManager
import ru.tensor.sbis.communicator.base.conversation.data.model.ParticipantsData
import ru.tensor.sbis.communicator.base.conversation.data.model.ToolbarData
import ru.tensor.sbis.communicator.base.conversation.data.model.ToolbarTitleEditingState
import ru.tensor.sbis.communicator.base.conversation.presentation.presenter.dispatcher.ConversationEvent
import ru.tensor.sbis.communicator.base.conversation.presentation.presenter.toolbar.BaseConversationToolbarPresenter
import ru.tensor.sbis.communicator.base.conversation.ui.user_typing.UsersTypingView.UsersTypingData
import ru.tensor.sbis.communicator.base.conversation.ui.user_typing.UsersTypingView.UsersTypingData.UsersType
import ru.tensor.sbis.communicator.base.conversation.ui.user_typing.UsersTypingView.UsersTypingData.UsersType.ANY
import ru.tensor.sbis.communicator.base.conversation.ui.user_typing.UsersTypingView.UsersTypingData.UsersType.SINGLE_USER
import ru.tensor.sbis.communicator.base.conversation.ui.user_typing.UsersTypingView.UsersTypingData.UsersType.TWO_USERS
import ru.tensor.sbis.communicator.common.conversation.ConversationRouter
import ru.tensor.sbis.communicator.common.conversation.ConversationToolbarEventManager
import ru.tensor.sbis.communicator.common.themes_registry.dialog_info.document_plate_view.DocumentPlateViewModel
import ru.tensor.sbis.communicator.common.util.doIf
import ru.tensor.sbis.communicator.sbis_conversation.CommunicatorSbisConversationPlugin.customizationOptions
import ru.tensor.sbis.communicator.sbis_conversation.CommunicatorSbisConversationPlugin.singletonComponent
import ru.tensor.sbis.communicator.sbis_conversation.data.CoreConversationInfo
import ru.tensor.sbis.communicator.sbis_conversation.data.model.ConversationData
import ru.tensor.sbis.communicator.sbis_conversation.data.model.ConversationMessage
import ru.tensor.sbis.communicator.sbis_conversation.interactor.ConversationInteractor
import ru.tensor.sbis.communicator.sbis_conversation.ui.ConversationDataDispatcher
import ru.tensor.sbis.communicator.sbis_conversation.ui.ConversationPresenterImpl
import ru.tensor.sbis.communicator.sbis_conversation.ui.ConversationState
import ru.tensor.sbis.communicator.sbis_conversation.ui.conversation_option.ConversationOption
import ru.tensor.sbis.communicator.sbis_conversation.ui.conversation_option.ConversationOption.ADD_MEMBER
import ru.tensor.sbis.communicator.sbis_conversation.ui.conversation_option.ConversationOption.CHANGE_DIALOG_NAME
import ru.tensor.sbis.communicator.sbis_conversation.ui.conversation_option.ConversationOption.CHAT_INFORMATION
import ru.tensor.sbis.communicator.sbis_conversation.ui.conversation_option.ConversationOption.COMPLAIN
import ru.tensor.sbis.communicator.sbis_conversation.ui.conversation_option.ConversationOption.COPY_LINK
import ru.tensor.sbis.communicator.sbis_conversation.ui.conversation_option.ConversationOption.CREATE_TASK
import ru.tensor.sbis.communicator.sbis_conversation.ui.conversation_option.ConversationOption.DELETE_CONVERSATION
import ru.tensor.sbis.communicator.sbis_conversation.ui.conversation_option.ConversationOption.DIALOG_INFORMATION
import ru.tensor.sbis.communicator.sbis_conversation.ui.conversation_option.ConversationOption.GO_TO_GROUP
import ru.tensor.sbis.communicator.sbis_conversation.ui.conversation_option.ConversationOption.GO_TO_PROJECT
import ru.tensor.sbis.communicator.sbis_conversation.ui.conversation_option.ConversationOption.HIDE_CHAT
import ru.tensor.sbis.communicator.sbis_conversation.ui.conversation_option.ConversationOption.LEAVE_CHAT
import ru.tensor.sbis.communicator.sbis_conversation.ui.conversation_option.ConversationOption.SELECT_RECIPIENTS
import ru.tensor.sbis.communicator.sbis_conversation.ui.conversation_option.ConversationOption.SETTINGS
import ru.tensor.sbis.communicator.sbis_conversation.ui.conversation_option.ConversationOption.UNHIDE_CHAT
import ru.tensor.sbis.communicator.sbis_conversation.ui.conversation_option.ConversationOption.UNHIDE_DIALOG
import ru.tensor.sbis.communicator.sbis_conversation.ui.viewmodel.ConversationViewModel
import ru.tensor.sbis.communicator.sbis_conversation.utils.ResumeEventHelper
import ru.tensor.sbis.design.confirmation_dialog.ConfirmationButtonId
import ru.tensor.sbis.design.confirmation_dialog.ConfirmationDialogStyle
import ru.tensor.sbis.design.profile_decl.person.InitialsStubData
import ru.tensor.sbis.design.profile_decl.person.PersonData
import ru.tensor.sbis.design.profile_decl.util.PersonNameTemplate
import ru.tensor.sbis.design.utils.DebounceActionHandler
import ru.tensor.sbis.edo_decl.document.Document
import ru.tensor.sbis.edo_decl.document.DocumentType
import ru.tensor.sbis.feature_ctrl.SbisFeatureService
import ru.tensor.sbis.localfeaturetoggle.data.FeatureSet
import ru.tensor.sbis.localfeaturetoggle.domain.LocalFeatureToggleService
import ru.tensor.sbis.message_panel.R
import ru.tensor.sbis.person_decl.profile.model.ActivityStatus
import ru.tensor.sbis.person_decl.profile.model.SbisPersonViewData
import ru.tensor.sbis.person_decl.profile.model.SbisPersonViewInitialsStubData
import ru.tensor.sbis.persons.ContactVM
import ru.tensor.sbis.persons.PersonName
import ru.tensor.sbis.persons.util.formatName
import timber.log.Timber
import java.util.UUID
import ru.tensor.sbis.communicator.design.R as RCommunicatorDesign
import ru.tensor.sbis.design.R as RDesign

/**
 * Презентер тулбара реестра сообщений
 *
 * @param interactor           интерактор сообщений
 * @param coreConversationInfo основная информация о диалоге/чате
 * @param dataDispatcher       диспетчер событий в реестре сообщений
 * @param titleEventManager    менеджер событий тулбара
 */
internal class ConversationToolbarPresenterImpl(
    interactor: ConversationInteractor,
    coreConversationInfo: CoreConversationInfo,
    dataDispatcher: ConversationDataDispatcher,
    private val resourceProvider: ResourceProvider,
    private val viewModel: ConversationViewModel?,
    private val titleEventManager: ConversationToolbarEventManager?,
    private val recipientSelectionResultManager: RecipientSelectionResultManager? = null,
    private val localFeatureService: LocalFeatureToggleService? = null,
    private val featureService: SbisFeatureService? = null
) : BaseConversationToolbarPresenter<
        ConversationToolbarContract.View, ConversationInteractor, ConversationMessage,
        ConversationState, ConversationData, CoreConversationInfo, ConversationDataDispatcher
    >(interactor, coreConversationInfo, dataDispatcher),
    ConversationToolbarContract.Presenter<ConversationToolbarContract.View> {

    private var router: ConversationRouter? = null
    private val debounceActionHandler = DebounceActionHandler()

    private var isInviteDialog: Boolean? = null
    private var document: Document? = null
    private var activityStatus: ActivityStatus? = null
    private var statusSubtitle: String? = null
    private var participantUuidForStatus: UUID? = null
    private var isParticipantHasAccess: Boolean = true
    private var isNotifyPersonalOnly: Boolean = false
    private var isGroupConversation: Boolean = false
    private var isTwoDialogParticipants: Boolean = false
    private var isClosedChat: Boolean = false
    private var isConversationLocked = false

    private var resumeEventDelegate = ResumeEventHelper(DeviceConfigurationUtils.isTablet(resourceProvider.mContext))
    private var isKeyboardOpened: Boolean = false
    private var needRestoreKeyboard: Boolean = false
    private var needRestorePopupVisibility: Boolean = false
    private var isExitRequired: Boolean = false
    private var isTitleVisible = true

    private var conversationOptions: List<ConversationOption> = emptyList()

    private val activityStatusSubscription = SerialDisposable().also { compositeDisposable.add(it) }

    private val hasConversationName: Boolean
        get() = !toolbarData?.conversationName.isNullOrBlank()

    private val isPersonalDialog: Boolean
        get() = !coreConversationInfo.isChat && toolbarData?.isSingleParticipant == true && !isGroupConversation

    private val needShowActivityStatus: Boolean
        get() = isPersonalDialog && !coreConversationInfo.isChat

    private val isUserOffline: Boolean
        get() = activityStatus != ActivityStatus.ONLINE_WORK && activityStatus != ActivityStatus.ONLINE_HOME

    private val isTaskCreationFeatureAvailable: Boolean
        get() = singletonComponent.dependency.tasksCreateFeature != null

    private val isSwipeBackAvailable: Boolean
        get() = !conversationState.audioRecordState.isVisible && conversationState.threadCreationServiceObject == null

    /**
     * Условие по которому показываем информацию о документе
     */
    private val isDocumentDialog: Boolean
        get() = document != null &&
            !coreConversationInfo.isChat &&
            coreConversationInfo.conversationType != VIDEO_CONVERSATION

    private var editingState: ToolbarTitleEditingState =
        if (coreConversationInfo.creationThreadInfo != null) {
            ToolbarTitleEditingState.ENABLED
        } else {
            ToolbarTitleEditingState.DISABLED
        }

    private val filesTasksDialogFeatureOn: Boolean
        get() = featureService?.isActive(FILES_TASKS_DIALOG_CLOUD_FEATURE) == true ||
            localFeatureService?.isFeatureActivated(FeatureSet.FILES_TASKS_DIALOG) == true

    init {
        postViolationEvent(isEmpty = true)
        presetToolbarData()
        subscribeOnFeatureUpdates()
    }

    private fun subscribeOnFeatureUpdates() {
        viewModel?.viewModelScope?.launch {
            featureService?.getFeatureInfoFlow(listOf(FILES_TASKS_DIALOG_CLOUD_FEATURE))?.collect {
                updateConversationMenuOptions()
            }
        }
    }

    override fun attachView(view: ConversationToolbarContract.View) {
        super.attachView(view)
        if (!isTitleVisible) view.hideTitle()
    }

    override fun handleConversationDataChanges(conversationData: ConversationData) {
        super.handleConversationDataChanges(conversationData)

        conversationData.let {
            val wasGroup = isGroupConversation
            isInviteDialog = it.isInviteDialog
            document = it.document?.apply {
                doIf(title.isEmpty()) {
                    title = coreConversationInfo.docInfo?.documentTitle ?: StringUtils.EMPTY
                }
            }
            isGroupConversation = it.isGroupConversation
            isNotifyPersonalOnly = it.isNotifyPersonalOnly
            isClosedChat = it.isClosedChat
            isTwoDialogParticipants = !coreConversationInfo.isChat && it.toolbarData?.participantsData?.participants?.size == 2
            isConversationLocked = it.isLocked == true

            if (editingState != toolbarData?.editingState) {
                toolbarData = toolbarData?.copy(editingState = editingState)
            }

            when {
                hasConversationName -> Unit
                needShowActivityStatus -> {
                    toolbarData = toolbarData?.copy(
                        subtitle = statusSubtitle?.takeIf { subtitle -> subtitle.isNotBlank() }
                            ?: DOTS_FOR_UNLOAD_SUBTITLE
                    )
                }
                else -> {
                    activityStatus = null
                    statusSubtitle = null
                    participantUuidForStatus = null
                }
            }

            if (wasGroup != it.isGroupConversation) {
                typingData = typingData.copy(usersType = getTypingUsersType())
            }

            updateActivityStatus(it)
            updateConversationMenuOptions()
        }

        postViolationEvent()
    }

    override fun handleConversationEvent(event: ConversationEvent) {
        super.handleConversationEvent(event)
        if (event == ConversationEvent.BLOCK_MESSAGE_SENDING) {
            isTitleVisible = false
            mView?.hideTitle()
        }
    }

    override fun onTypingUsersChanged(users: List<String>) {
        typingData = UsersTypingData(users, getTypingUsersType())
        mView?.setTypingUsers(typingData)
        checkActivityStatusForUpdate()
    }

    private fun getTypingUsersType(): UsersType =
        when {
            !isGroupConversation -> SINGLE_USER
            isTwoDialogParticipants -> TWO_USERS
            else -> ANY
        }

    private fun checkActivityStatusForUpdate() {
        val uuid = participantUuidForStatus ?: return
        if (typingData.usersType == SINGLE_USER &&
            typingData.hasData &&
            isUserOffline &&
            isParticipantHasAccess
        ) {
            interactor.forceUpdateActivityStatus(uuid)
                .subscribe()
                .storeIn(compositeDisposable)
        }
    }

    private fun updateActivityStatus(conversationData: ConversationData) {
        // Не грузим статусы для чатов и групповых диалогов.
        if (!needShowActivityStatus) return
        // Если нет uuid участника - выходим.
        val participant = conversationData.participants?.firstOrNull()
        val userUuid = participant?.uuid ?: return
        // Если мы уже подписаны на статус участнка - не сбрасываем подписку.
        if (participantUuidForStatus == userUuid && isParticipantHasAccess == participant.isHasAccess) return
        isParticipantHasAccess = participant.isHasAccess

        interactor.subscribeActivityObserver(userUuid)
            .flatMap { profileActivityStatus ->
                interactor.createToolbarSubtitle(conversationData, profileActivityStatus)
                    .toObservable()
                    .map { profileActivityStatus.activityStatus to it }
            }
            .doOnSubscribe { participantUuidForStatus = userUuid }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ (status, subtitle) ->
                if (needShowActivityStatus) {
                    activityStatus = status
                    statusSubtitle = subtitle
                    if (!hasConversationName) {
                        toolbarData = toolbarData?.copy(subtitle = subtitle)
                        postViolationEvent(status = subtitle)
                    }
                } else {
                    postViolationEvent()
                }

                mView?.let(::displayViewState)
            }, {
                Timber.d(it, "Error on ${ConversationPresenterImpl::class.java.canonicalName} activity status observe.")
            })
            .storeIn(activityStatusSubscription)
    }

    override fun beforeToolbarClick() {
        if (!conversationState.isThreadCreation) {
            mView?.forceHideKeyboard()
        }
    }

    override fun onTitleTextClick() {
        if (conversationState.isThreadCreation) showEditTitle()
    }

    override fun onToolbarClick() {
        val participants = toolbarData?.participantsData?.participants
        when {
            conversationState.isThreadCreation -> showEditTitle()
            conversationState.audioRecordState.isVisible -> mView?.showCancelRecordingConfirmationDialog()
            isConversationLocked || participants.isNullOrEmpty() -> Unit
            else -> showConversationMembers()
        }
    }

    override fun onTitlePhotoClick() {
        val participants = toolbarData?.participantsData?.participants?.map { it.uuid }
        when {
            conversationState.audioRecordState.isVisible -> mView?.showCancelRecordingConfirmationDialog()
            isConversationLocked || participants.isNullOrEmpty() -> Unit
            participants.size == 1 -> router?.showProfile(participants[0])
            else -> onToolbarClick()
        }
    }

    override fun setDialogTitle(text: String) {
        coreConversationInfo.conversationUuid?.let {
            interactor.setDialogTitle(
                it,
                text
            ).subscribe { commandStatus ->
                if (commandStatus.errorMessage.isNotEmpty()) {
                    mView?.showToast(commandStatus.errorMessage)
                }
            }
                .storeIn(compositeDisposable)
        }
    }

    override fun onCompleteTitleEditClicked(newTitle: CharSequence) {
        interactor.setDialogTitle(coreConversationInfo.conversationUuid!!, newTitle.toString())
            .subscribe { commandStatus ->
                hideEditTitle(errorMessage = commandStatus.errorMessage)
            }.storeIn(compositeDisposable)
    }

    /**
     * Показать участников чата/диалога
     */
    private fun showConversationMembers() {
        val toolbarData = toolbarData ?: return
        document?.let {
            if (it.id == null) it.id = -1
        }
        val isVideoConversation = coreConversationInfo.conversationType == VIDEO_CONVERSATION
        val videoCallParticipants = if (isVideoConversation) {
            mutableListOf<UUID>().also { list ->
                toolbarData.participantsData.participants.forEach {
                    list.add(it.uuid)
                }
            } as ArrayList<UUID>
        } else {
            null
        }

        router?.showConversationMembers(
            coreConversationInfo.conversationUuid!!,
            toolbarData.subtitle,
            conversationState.isNewConversation && !coreConversationInfo.isChat,
            coreConversationInfo.isChat,
            toolbarData.conversationName,
            conversationAccess.chatPermissions,
            videoCallParticipants,
            isGroupConversation,
            toolbarData.photoDataList,
            toolbarData.participantsData.participants.takeIf { !isGroupConversation && it.size == 1 }?.first()
        )
    }

    private fun showTaskCreation() {
        dataDispatcher.createTaskEvent()
    }

    override fun onHideChatConfirmed() {
        interactor.hideChat(coreConversationInfo.conversationUuid!!)
            .subscribe { commandStatus ->
                if (ErrorCode.SUCCESS == commandStatus.errorCode) {
                    router?.exit()
                } else {
                    mView?.showToast(RCommunicatorDesign.string.communicator_channel_remove_failure)
                        ?: dataDispatcher.updateConversationState(
                            conversationState.copy(missedToastErrorRes = RCommunicatorDesign.string.communicator_channel_remove_failure)
                        )
                }
            }.storeIn(compositeDisposable)
    }

    override fun onToolbarMenuIconClicked() = DebounceActionHandler.INSTANCE.handle {
        if (!conversationState.audioRecordState.isVisible) {
            updateConversationMenuOptions()
            mView?.showConversationOptionsMenu(conversationOptions)
        } else {
            mView?.showCancelRecordingConfirmationDialog()
        }
    }

    private fun updateConversationMenuOptions() {
        conversationOptions = when {
            coreConversationInfo.isChat -> getChatOptionMenu()
            else -> getDialogOptionMenu()
        }
    }

    private val canDeleteConversation: Boolean
        get() = !conversationState.isNewConversation &&
            coreConversationInfo.conversationType != VIDEO_CONVERSATION &&
            coreConversationInfo.conversationType != DOCUMENT_CONVERSATION

    private fun getDialogOptionMenu() =
        buildList {
            add(SELECT_RECIPIENTS)
            add(CHANGE_DIALOG_NAME)
            add(COPY_LINK)
            if (coreConversationInfo.archivedConversation) add(UNHIDE_DIALOG)
            if (isTaskCreationFeatureAvailable) add(CREATE_TASK)
            if (filesTasksDialogFeatureOn) add(DIALOG_INFORMATION)
            if (customizationOptions.complainEnabled) add(COMPLAIN)
            if (canDeleteConversation) add(DELETE_CONVERSATION)
        }

    private fun getChatOptionMenu(): List<ConversationOption> {
        val permissions = conversationAccess.chatPermissions
        return buildList {
            if (document != null) {
                if (DocumentType.PROJECT == document!!.type) add(GO_TO_PROJECT)
                if (DocumentType.SOCNET_GROUP == document!!.type) add(GO_TO_GROUP)
            }
            if (isGroupConversation) {
                if (permissions?.canAddParticipant == true) add(ADD_MEMBER)
                if (!isClosedChat) add(SETTINGS)
            }
            add(COPY_LINK)
            if (filesTasksDialogFeatureOn) add(CHAT_INFORMATION)
            if (customizationOptions.complainEnabled) add(COMPLAIN)
            if (!coreConversationInfo.archivedConversation) add(HIDE_CHAT)
            if (coreConversationInfo.archivedConversation) add(UNHIDE_CHAT)
            if (permissions?.canQuitFromChat == true) add(LEAVE_CHAT)
        }
    }

    override fun onQuitAndHideChatConfirmed() {
        interactor.quitAndHideChat(coreConversationInfo.conversationUuid!!)
            .subscribe { commandStatus ->
                if (ErrorCode.SUCCESS == commandStatus.errorCode) {
                    router?.exit()
                } else {
                    mView?.showToast(RCommunicatorDesign.string.communicator_channel_leave_failure)
                }
            }.storeIn(compositeDisposable)
    }

    override fun onQuitChatConfirmed() {
        interactor.quitChat(coreConversationInfo.conversationUuid!!)
            .subscribe { commandStatus ->
                if (ErrorCode.SUCCESS == commandStatus.errorCode) {
                    router?.exit()
                } else {
                    mView?.showToast(RCommunicatorDesign.string.communicator_channel_leave_failure)
                }
            }.storeIn(compositeDisposable)
    }

    override fun onConversationOptionSelected(conversationOption: ConversationOption) {
        when (conversationOption) {
            ADD_MEMBER -> {
                dataDispatcher.updateConversationState(
                    conversationState.copy(addRecipientsToChat = true)
                )
                dataDispatcher.sendConversationEvent(ConversationEvent.SAVE_RECIPIENTS)
                router?.showAddChatParticipants(coreConversationInfo.conversationUuid!!)
            }
            SETTINGS -> {
                dataDispatcher.updateConversationState(conversationState.copy(isChatSettingsShown = true))
                router?.showChatSettings(
                    coreConversationInfo.conversationUuid,
                    false,
                    conversationState.isNewConversation
                )
            }
            CHANGE_DIALOG_NAME -> mView?.showDialogTopicInput(toolbarData?.conversationName)
            HIDE_CHAT -> mView?.showHideChatConfirmation()
            LEAVE_CHAT -> mView?.showLeaveChatConfirmationDialog()
            UNHIDE_CHAT -> unhideChat()
            UNHIDE_DIALOG -> restoreDialog()
            DELETE_CONVERSATION -> mView?.showDeleteConversationConfirmation()
            COPY_LINK -> copyLink()
            CREATE_TASK -> showTaskCreation()
            GO_TO_GROUP -> openDocument()
            GO_TO_PROJECT -> openDocument()
            COMPLAIN -> onComplainClicked()
            SELECT_RECIPIENTS -> {
                dataDispatcher.updateConversationState(
                    conversationState.copy(isChoosingRecipients = true)
                )
                dataDispatcher.sendConversationEvent(ConversationEvent.SELECT_RECIPIENTS)
            }
            DIALOG_INFORMATION, CHAT_INFORMATION -> showConversationMembers()
        }
    }

    override fun openDocument() = debounceActionHandler.handle {
        if (document?.isAccessible == true) {
            if (isKeyboardOpened) needRestoreKeyboard = true
            if (coreConversationInfo.conversationType == DOCUMENT_CONVERSATION) {
                router?.exit()
            } else {
                router?.showDocument(document!!)
            }
        } else {
            mView?.showOpenDocumentErrorNotification()
        }
    }

    private fun unhideChat() {
        coreConversationInfo.conversationUuid?.let {
            interactor.unhideChat(it).subscribe(
                { coreConversationInfo.archivedConversation = false },
                { error -> Timber.e(error, "Error when trying to restore chat: ") }
            ).storeIn(compositeDisposable)
        }
    }

    private fun restoreDialog() {
        coreConversationInfo.conversationUuid?.let {
            interactor.restoreDialog(it).subscribe(
                { coreConversationInfo.archivedConversation = false },
                { error -> Timber.e(error, "Error when trying to restore dialog: ") }
            ).storeIn(compositeDisposable)
        }
    }

    private fun copyLink() {
        coreConversationInfo.conversationUuid?.let {
            interactor.getUrlById(it)
                .subscribe { url ->
                    mView?.copyLink(url)
                }.storeIn(compositeDisposable)
        }
    }

    private fun presetToolbarData() {
        if (toolbarData != null) return
        val needTryCreateInitialData = coreConversationInfo.toolbarInitialData?.isEmpty != false &&
            (!coreConversationInfo.isChat || coreConversationInfo.isPrivateChatCreation)
        if (needTryCreateInitialData) {
            recipientSelectionResultManager?.selectionResult?.data?.allPersons?.also {
                val prefetchCount = 10
                val participants = it.take(prefetchCount)
                val hiddenParticipants = (participants.size - prefetchCount).coerceAtLeast(0)
                val names = participants.map { person ->
                    person.name.formatName(
                        if (it.size > 1) {
                            PersonNameTemplate.SURNAME_N
                        } else {
                            PersonNameTemplate.SURNAME_NAME
                        }
                    )
                }
                coreConversationInfo.toolbarInitialData = ToolbarData(
                    photoDataList = participants.map { contact ->
                        PersonData(
                            contact.uuid,
                            contact.photoUrl,
                            contact.initialsStubData?.let { stubData ->
                                InitialsStubData(
                                    stubData.initials,
                                    stubData.initialsBackgroundColor
                                )
                            }
                        )
                    },
                    participantsData = ParticipantsData(
                        participants = participants.map { person ->
                            ContactVM().apply {
                                uuid = person.uuid
                                name = with(person.name) {
                                    PersonName(firstName, lastName, patronymicName)
                                }
                            }
                        },
                        names = names,
                        hiddenParticipantCount = hiddenParticipants
                    ),
                    title = names.joinToString(),
                    subtitle = when {
                        coreConversationInfo.isPrivateChatCreation -> resourceProvider.getString(R.string.message_panel_channel_private)
                        it.size == 1 -> DOTS_FOR_UNLOAD_SUBTITLE
                        else -> resourceProvider.getQuantityString(
                            R.plurals.message_panel_chat_participants,
                            it.size,
                            it.size
                        )
                    },
                    isChat = coreConversationInfo.isChat,
                    editingState = editingState
                )
            }
        }
        showInitialData()
    }

    private fun showInitialData() {
        coreConversationInfo.toolbarInitialData?.let {
            toolbarData = coreConversationInfo.toolbarInitialData
            if (it.isSingleParticipant) {
                updateActivityStatus(
                    ConversationData(
                        participants = it.participantsData.participants.map { contact ->
                            contact.apply { isHasAccess = isParticipantHasAccess }
                        }
                    )
                )
            }
            if (coreConversationInfo.isPrivateChatCreation) {
                conversationOptions = getChatOptionMenu()
            }
            mView?.also(::displayViewState)
        }
    }

    override fun displayViewState(view: ConversationToolbarContract.View) {
        super.displayViewState(view)
        view.setHasActivityStatus(needShowActivityStatus)
        if (!conversationAccess.isAvailable) {
            viewModel?.showMenuIcon?.onNext(false)
            return
        }

        if (conversationOptions.isEmpty()) {
            viewModel?.showMenuIcon?.onNext(false)
        } else {
            if (coreConversationInfo.isChat) {
                viewModel?.showMenuIcon?.onNext(!conversationState.isNewConversation)
            } else {
                // Для диалога меню показывается всегда, независимо от того, это существуюший или новый диалог
                viewModel?.showMenuIcon?.onNext(true)
            }
            if (isDocumentDialog) {
                viewModel?.showDocumentTitle?.onNext(true)
                val documentPlateData = createDocumentPlateViewModel(isDocumentDialog)
                view.setDocumentPlateData(documentPlateData)
            }
        }
    }

    /**
     * Создать модель для таблички документа
     */
    private fun createDocumentPlateViewModel(isDocumentDialog: Boolean): DocumentPlateViewModel {
        val emptyDocumentPlateData = DocumentPlateViewModel(
            StringUtils.EMPTY,
            StringUtils.EMPTY,
            StringUtils.EMPTY,
            RDesign.string.design_mobile_icon_sabydoc
        )
        return if (isDocumentDialog) {
            val documentPlateData = emptyDocumentPlateData.copy(
                soloDocumentTitle = document!!.title,
                documentTitle = document!!.name,
                documentSubTitle = document!!.text
            )
            when (document!!.type) {
                DocumentType.DISC_FOLDER  ->
                    documentPlateData.copy(iconRes = RDesign.string.design_mobile_icon_folder)
                DocumentType.SOCNET_GROUP ->
                    documentPlateData.copy(iconRes = RDesign.string.design_mobile_icon_person_with_ties)
                else                      -> documentPlateData
            }
        } else emptyDocumentPlateData
    }

    override fun setRouter(router: ConversationRouter?) {
        this.router = router
    }

    override fun onKeyboardAppears(keyboardHeight: Int) {
        isKeyboardOpened = true
    }

    override fun onKeyboardDisappears(keyboardHeight: Int) {
        isKeyboardOpened = false
    }

    override fun viewIsStarted() {
        super.viewIsStarted()
        updateSwipeBackAvailability()
        resumeEventDelegate.viewIsStarted()
        restorePopupVisibilityState()
    }

    override fun viewIsResumed() {
        super.viewIsResumed()
        resumeEventDelegate.viewIsResumed()
        //Действия, необходимые для четкого распознавания события onResume
        //при возвращении с другого экрана для восстановления клавиатуры
        if (needRestoreKeyboard && resumeEventDelegate.isResumeAfterPause) {
            needRestoreKeyboard = false
            mView?.showKeyboard()
        }
    }

    override fun viewIsPaused() {
        super.viewIsPaused()
        resumeEventDelegate.viewIsPaused()
    }

    override fun viewIsStopped() {
        super.viewIsStopped()
        resumeEventDelegate.viewIsStopped()
    }

    private fun restorePopupVisibilityState() {
        if (needRestorePopupVisibility) {
            needRestorePopupVisibility = false
        }
    }

    override fun restorePopupMenuVisibility(wasVisible: Boolean) {
        needRestorePopupVisibility = wasVisible
    }

    override fun handleConversationStateChanges(currentState: ConversationState?, newState: ConversationState) {
        super.handleConversationStateChanges(currentState, newState)

        when {
            currentState?.audioRecordState?.isVisible != newState.audioRecordState.isVisible -> {
                mView?.apply {
                    changeToolbarCollageEnable(isEnabled = !newState.audioRecordState.isVisible)
                    updateSwipeBackAvailability()
                }
            }
            currentState.threadCreationServiceObject != newState.threadCreationServiceObject -> {
                updateSwipeBackAvailability()
                if (!newState.isThreadCreation && editingState != ToolbarTitleEditingState.DISABLED) hideEditTitle()
            }
        }
    }

    private fun updateSwipeBackAvailability() {
        mView?.changeSwipeBackAvailability(isAvailable = isSwipeBackAvailable)
    }

    private fun showEditTitle() {
        editingState = ToolbarTitleEditingState.ENABLED
        toolbarData = toolbarData?.copy(editingState = editingState)
        mView?.also(::displayViewState)
        mView?.focusEditTitle()
    }

    private fun hideEditTitle(errorMessage: String? = null) {
        editingState = if (conversationState.isThreadCreation) {
            ToolbarTitleEditingState.COMPLETED
        } else {
            ToolbarTitleEditingState.DISABLED
        }
        toolbarData = toolbarData?.copy(
            editingState = editingState,
            showOnlyTitle = conversationState.isThreadCreation && errorMessage.isNullOrEmpty()
        )
        mView?.also(::displayViewState)
        if (isKeyboardOpened) {
            mView?.focusMessagePanel()
        }
        if (!errorMessage.isNullOrEmpty()) {
            mView?.showToast(errorMessage)
        }
    }

    override fun onCancelRecordingDialogResult(isConfirmed: Boolean) {
        mView?.apply {
            if (isConfirmed) {
                cancelMessageRecording()
                if (isExitRequired) {
                    router?.exit()
                }
            } else {
                isExitRequired = false
            }
        }
    }

    override fun onBackPressed(): Boolean =
        when {
            isExitRequired -> false
            conversationState.audioRecordState.isVisible -> {
                isExitRequired = true
                mView?.showCancelRecordingConfirmationDialog()
                true
            }
            conversationState.isThreadCreation -> {
                isExitRequired = true
                mView?.showOkCancelDialog(
                    message = resourceProvider.getString(RCommunicatorDesign.string.communicator_thread_exit_confirm_dialog_message),
                    comment = resourceProvider.getString(RCommunicatorDesign.string.communicator_thread_exit_confirm_dialog_comment),
                    tag = THREAD_EXIT_CONFIRMATION_DIALOG_TAG,
                    style = ConfirmationDialogStyle.WARNING
                )
                true
            }
            else -> {
                false
            }
        }

    override fun onConfirmationDialogButtonClicked(tag: String?, id: String) {
        when (tag) {
            THREAD_EXIT_CONFIRMATION_DIALOG_TAG -> {
                if (id == ConfirmationButtonId.OK.toString()) {
                    router?.exit()
                } else {
                    isExitRequired = false
                }
            }
        }
    }

    private fun onComplainClicked() {
        mView?.showComplainDialogFragment(
            ComplainUseCase.Conversation(
                coreConversationInfo.conversationUuid!!,
                coreConversationInfo.isChat
            )
        )
    }

    @Suppress("DEPRECATION")
    private fun postViolationEvent(isEmpty: Boolean = false, status: String? = null) {
        val eventManager = titleEventManager ?: return
        if (!eventManager.hasObservers) return

        fun ToolbarData.mapToPersonViewData(): List<SbisPersonViewData> {
            participantsData.participants.map {
                SbisPersonViewData(
                    it.uuid,
                    it.rawPhoto,
                    it.activityStatus,
                    SbisPersonViewInitialsStubData(
                        it.initialsStubData.initials,
                        it.initialsStubData.initialsBackgroundColor,
                        it.initialsStubData.initialsBackgroundColorRes
                    )
                )
            }
            return emptyList()
        }

        with(toolbarData ?: return) {
            val event = when {
                isEmpty -> SinglePersonTitleEvent(null, null, null)
                status != null -> SinglePersonTitleEvent(
                    mapToPersonViewData(),
                    participantsData.names.firstOrNull(),
                    status
                )
                isPersonalDialog -> SinglePersonTitleEvent(
                    mapToPersonViewData(),
                    participantsData.names.firstOrNull(),
                    subtitle
                )
                else -> MultiPersonsTitleEvent(
                    mapToPersonViewData(),
                    participantsData.names,
                    participantsData.hiddenParticipantCount
                )
            }
            titleEventManager.postEvent(event)
        }
    }
}

/**
 * Временное решение для избежания смаргивания подзаголовка тулбара,
 * в котором отображется статус пользователя в сценарии переключения диалогов на планшете.
 */
internal const val DOTS_FOR_UNLOAD_SUBTITLE = "..."
private const val THREAD_EXIT_CONFIRMATION_DIALOG_TAG = "THREAD_EXIT_CONFIRMATION_DIALOG_TAG"
private const val FILES_TASKS_DIALOG_CLOUD_FEATURE = "files_tasks_dialog"