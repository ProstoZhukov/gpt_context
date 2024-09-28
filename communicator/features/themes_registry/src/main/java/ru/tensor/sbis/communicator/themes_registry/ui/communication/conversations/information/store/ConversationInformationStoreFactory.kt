package ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.information.store

import android.content.Context
import com.arkivanov.essenty.statekeeper.StateKeeper
import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.SimpleBootstrapper
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.launch
import kotlinx.coroutines.rx2.asFlow
import kotlinx.coroutines.withContext
import org.apache.commons.lang3.StringUtils.EMPTY
import ru.tensor.sbis.common.util.UUIDUtils
import ru.tensor.sbis.common.util.asArrayList
import ru.tensor.sbis.communication_decl.conversation_information.ConversationInformationFilter
import ru.tensor.sbis.communication_decl.selection.recipient.manager.RecipientSelectionResultManager
import ru.tensor.sbis.communicator.common.util.CommunicatorActivityStatusSubscriptionInitializer
import ru.tensor.sbis.communicator.core.data.events.MessagesEvent
import ru.tensor.sbis.communicator.generated.ChatController
import ru.tensor.sbis.communicator.generated.Conversation
import ru.tensor.sbis.communicator.generated.DataRefreshedThemeControllerCallback
import ru.tensor.sbis.communicator.generated.DialogController
import ru.tensor.sbis.communicator.generated.MessageController
import ru.tensor.sbis.communicator.generated.Permissions
import ru.tensor.sbis.communicator.generated.ThemeController
import ru.tensor.sbis.communicator.themes_registry.ThemesRegistryFacade.themesRegistryDependency
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.information.data.ConversationInformationData
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.information.data.ConversationInformationTab
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.information.data.ConversationInformationTab.PARTICIPANTS
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.information.data.ConversationInformationTabsViewState
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.information.data.fab_options.ConversationInformationFabOption
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.information.data.getAvailableTabs
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.information.data.mapToScreenState
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.information.data.toAttachmentTab
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.information.data.toolbar.ConversationInformationOption.ADD_MEMBER
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.information.data.toolbar.ConversationInformationOption.COPY_LINK
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.information.data.toolbar.ConversationInformationOption.DELETE
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.information.data.toolbar.ConversationInformationToolbarData
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.information.data.toolbar.ConversationInformationToolbarState.DEFAULT
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.information.data.toolbar.ConversationInformationToolbarState.EDITING
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.information.data.toolbar.ConversationInformationToolbarState.SEARCHING
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.information.store.ConversationInformationStore.Intent
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.information.store.ConversationInformationStore.Label
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.information.store.ConversationInformationStore.State
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.information.ui.CONVERSATION_INFORMATION_SELECTION_RESULT
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.information.ui.participant_view.ConversationInformationParticipantViewData
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.information.utils.getParticipantViewData
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.information.utils.getPhotoData
import ru.tensor.sbis.design.files_picker.decl.SbisFilesPicker
import ru.tensor.sbis.design.files_picker.decl.SbisFilesPickerEvent
import ru.tensor.sbis.person_decl.profile.model.ProfileActivityStatus
import ru.tensor.sbis.profiles.generated.EmployeeProfile
import ru.tensor.sbis.profiles.generated.EmployeeProfileController
import java.util.UUID
import javax.inject.Inject
import ru.tensor.sbis.mvi_extension.create as createWithStateKeeper

/**
 * Фабрика [ConversationInformationStore].
 * Содержит реализацию бизнес-логики экрана информации диалога/канала.
 *
 * @author dv.baranov
 */
internal class ConversationInformationStoreFactory @Inject constructor(
    private val context: Context,
    private val storeFactory: StoreFactory,
    private val conversationInformationData: ConversationInformationData,
    private val activityStatusSubscription: CommunicatorActivityStatusSubscriptionInitializer,
    private val recipientSelectionResultManager: RecipientSelectionResultManager,
    private val filesPicker: SbisFilesPicker
) {

    private val dispatcherIO = Dispatchers.IO
    private val dispatcherMain = Dispatchers.Main
    private val dialogController by lazy { DialogController.instance() }
    private val chatController by lazy { ChatController.instance() }
    private val themeController by lazy { ThemeController.instance() }
    private val employeeProfileController by lazy { EmployeeProfileController.instance() }
    private val messageController by lazy { MessageController.instance() }

    /** @SelfDocumented */
    fun create(stateKeeper: StateKeeper): ConversationInformationStore =
        object :
            ConversationInformationStore,
            Store<Intent, State, Label> by storeFactory.createWithStateKeeper(
                stateKeeper = stateKeeper,
                name = STORE_NAME,
                initialState = conversationInformationData.mapToScreenState(),
                bootstrapper = SimpleBootstrapper(Action.InitScreen),
                executorFactory = ::ExecutorImpl,
                reducer = ReducerImpl(conversationInformationData)
            ) {
        }

    private sealed interface Action {

        object InitScreen : Action
    }

    private sealed interface Message {

        sealed interface EditTitle : Message {

            object Start : EditTitle

            object Cancel : EditTitle

            data class End(val newTitle: CharSequence?) : EditTitle
        }

        sealed interface Search : Message {

            object Open : Search

            data class QueryChanged(val query: String) : Search

            object Close : Search
        }

        data class TabSelected(val id: String) : Message

        data class UpdateState(
            val toolbarData: ConversationInformationToolbarData,
            val permissions: Permissions
        ) : Message

        data class FilterSelected(val filters: List<ConversationInformationFilter>) : Message

        data class UpdateParticipantViewData(val data: ConversationInformationParticipantViewData? = null) : Message

        data class ActivityStatusUpdate(val status: ProfileActivityStatus) : Message

        data class CallState(val isRunning: Boolean) : Message
    }

    private inner class ExecutorImpl : CoroutineExecutor<Intent, Action, State, Message, Label>() {

        override fun executeAction(action: Action, getState: () -> State) = when (action) {
            Action.InitScreen -> {
                if (!getState().isGroupConversation) {
                    initParticipantData()
                }
                initSubscriptions(getState)
                publish(Label.TabSelected(getState().tabsViewState.selectedTab.id))
            }
        }

        private fun initParticipantData() {
            conversationInformationData.photoDataList.firstOrNull()?.uuid?.let { uuid ->
                scope.launch {
                    withContext(dispatcherIO) {
                        val personProfile = employeeProfileController.getEmployeeProfileFromCache(uuid)
                        val participantData = personProfile?.getParticipantViewData()
                        withContext(dispatcherMain) {
                            dispatch(Message.UpdateParticipantViewData(participantData))
                        }
                        updateParticipantActivityStatus(personProfile)
                    }
                }
            }
        }

        private fun State.updateScreenByConversationDataChanged() {
            scope.launch {
                withContext(dispatcherIO) {
                    val conversationData = getConversationData().data ?: return@withContext
                    val personProfile = if (conversationData.personalDialog) {
                        employeeProfileController.getEmployeeProfileFromCache(conversationData.participants[0].uuid)
                    } else {
                        null
                    }
                    val needExitParticipantTab = tabsViewState.selectedTab == PARTICIPANTS && conversationData.personalDialog
                    if (needExitParticipantTab) {
                        exitParticipantTab()
                    }
                    updateScreenWithData(
                        conversationData,
                        personProfile?.getParticipantViewData()
                    )
                    val personChanged = personProfile?.person?.uuid != participantViewData?.photoData?.uuid
                    if (personChanged) {
                        updateParticipantActivityStatus(personProfile)
                    }
                }
            }
        }

        private fun getConversationData() = if (conversationInformationData.isChat) {
            chatController.getConversationData(conversationInformationData.conversationUuid)
        } else {
            dialogController.getConversationData(conversationInformationData.conversationUuid, null, null, null)
        }

        private suspend fun State.exitParticipantTab() = withContext(dispatcherMain) {
            val tab = tabsViewState.availableTabs.firstOrNull { it != PARTICIPANTS } ?: ConversationInformationTab.DEFAULT
            publish(Label.TabSelected(tab.id))
            dispatch(Message.TabSelected(tab.id))
        }

        private suspend fun State.updateScreenWithData(
            conversationData: Conversation,
            participantViewData: ConversationInformationParticipantViewData?
        ) {
            val subtitle = context.resources.getQuantityString(
                ru.tensor.sbis.message_panel.R.plurals.message_panel_chat_participants,
                conversationData.participantCount,
                conversationData.participantCount
            )
            val toolbarData = ConversationInformationToolbarData(
                conversationData.title,
                subtitle,
                conversationData.getPhotoData(),
                toolbarData.toolbarState,
                conversationData.participantCount > 1,
                conversationInformationData.isChat
            )
            withContext(dispatcherMain) {
                dispatch(
                    Message.UpdateState(
                        toolbarData,
                        conversationData.chatPermissions,
                    )
                )
                dispatch(
                    Message.UpdateParticipantViewData(participantViewData)
                )
            }
        }

        private suspend fun updateParticipantActivityStatus(profile: EmployeeProfile?) {
            profile?.person?.uuid?.let {
                activityStatusSubscription.observe(it).collect { status ->
                    withContext(dispatcherMain) {
                        dispatch(Message.ActivityStatusUpdate(status))
                    }
                }
            }
        }

        private fun initSubscriptions(getState: () -> State) {
            scope.launch {
                recipientSelectionResultManager
                    .getSelectionResultObservable(CONVERSATION_INFORMATION_SELECTION_RESULT)
                    .asFlow()
                    .collect { result ->
                        withContext(dispatcherIO) {
                            if (result.isCanceled) return@withContext
                            val currentParticipants = getCurrentParticipants(getState())
                            val selectionResult = result.data.allPersonsUuids.toMutableList()
                            selectionResult.removeAll(currentParticipants)
                            val currentDraft = messageController.getDraft(conversationInformationData.conversationUuid)
                            messageController.clearDraft(conversationInformationData.conversationUuid)
                            messageController.enqueueMessage2(
                                conversationInformationData.conversationUuid,
                                null,
                                EMPTY,
                                null,
                                selectionResult.asArrayList(),
                                null,
                                null,
                                null,
                                null,
                                null,
                            )
                            messageController.saveDraft(conversationInformationData.conversationUuid, currentDraft)
                            withContext(dispatcherMain) {
                                publish(Label.NavigateBack)
                            }
                        }
                    }
            }
            scope.launch(dispatcherIO) {
                val subscription = themeController.dataRefreshed().subscribe(
                    object : DataRefreshedThemeControllerCallback() {
                        override fun onEvent(param: HashMap<String, String>) {
                            if (needUpdateDataFromThemeCallback(param)) {
                                getState().updateScreenByConversationDataChanged()
                            }
                        }
                    }
                )
                try {
                    awaitCancellation()
                } finally {
                    subscription.disable()
                }
            }
            scope.launch {
                themesRegistryDependency.callStateProviderFeature?.isCallRunningFlow?.collect {
                    dispatch(Message.CallState(it))
                }
            }
            scope.launch {
                filesPicker.events.collect { event ->
                    when (event) {
                        is SbisFilesPickerEvent.OnCancel -> Unit
                        is SbisFilesPickerEvent.OnError -> Unit
                        is SbisFilesPickerEvent.OnItemsSelected -> {
                            publish(Label.AddFiles(event.selectedItems, event.compressImages))
                        }
                    }
                }
            }
        }

        private fun needUpdateDataFromThemeCallback(params: HashMap<String, String>): Boolean {
            val uuid = UUIDUtils.toString(conversationInformationData.conversationUuid)
            return MessagesEvent.REGISTRY.isExistsIn(params) && (
                MessagesEvent.AFFECTED_THEMES_ANY.isExistsIn(params) ||
                    (uuid != null && params[MessagesEvent.AFFECTED_THEMES_LIST.type]?.contains(uuid) == true)
                ) || params[MessagesEvent.THEME.type] == uuid
        }

        override fun executeIntent(intent: Intent, getState: () -> State) = when (intent) {
            Intent.NavigateBack -> { publish(Label.NavigateBack) }
            Intent.OnBackPressed -> {
                when (getState().toolbarData.toolbarState) {
                    SEARCHING -> dispatch(Message.Search.Close)
                    EDITING -> dispatch(Message.EditTitle.Cancel)
                    DEFAULT -> publish(Label.NavigateBack)
                }
            }
            Intent.EditTitle.Start -> {
                if (conversationInformationData.canChangeTitle) {
                    dispatch(Message.EditTitle.Start)
                }
                Unit
            }
            Intent.EditTitle.Cancel -> { dispatch(Message.EditTitle.Cancel) }
            is Intent.EditTitle.End -> {
                intent.newTitle?.let {
                    scope.launch {
                        withContext(dispatcherIO) {
                            dialogController.setDialogTitle(
                                conversationInformationData.conversationUuid,
                                it.toString()
                            )
                        }
                    }
                }
                dispatch(Message.EditTitle.End(intent.newTitle))
            }
            is Intent.TabSelected -> {
                if (getState().tabsViewState.selectedTab.id != intent.id) {
                    publish(Label.TabSelected(intent.id))
                    dispatch(Message.TabSelected(intent.id))
                } else { Unit }
            }
            Intent.Search.Open -> { dispatch(Message.Search.Open) }
            is Intent.Search.QueryChanged -> {
                dispatch(Message.Search.QueryChanged(intent.query))
            }
            Intent.Search.Close -> { dispatch(Message.Search.Close) }
            is Intent.MenuOptionSelected -> {
                when (intent.option) {
                    ADD_MEMBER -> { publish(Label.OpenParticipantSelection(getCurrentParticipants(getState()))) }
                    COPY_LINK -> copyLink()
                    DELETE -> {
                        publish(Label.NavigateBack)
                        deleteDialog()
                    }
                }
                Unit
            }
            is Intent.FabMenuOptionSelected -> {
                when (intent.option) {
                    ConversationInformationFabOption.ADD_FILE -> { publish(Label.ShowFilesPicker) }
                    ConversationInformationFabOption.CREATE_FOLDER -> { publish(Label.ShowFolderCreationDialog) }
                }
            }
            is Intent.CreateFolder -> {
                publish(Label.CreateFolder(intent.folderName))
            }
            Intent.OpenFilter -> { publish(Label.OpenFilter(getState().filesFilter)) }
            is Intent.FilterSelected -> { dispatch(Message.FilterSelected(intent.filters)) }
            Intent.AddButtonClicked -> {
                when (getState().tabsViewState.selectedTab) {
                    PARTICIPANTS -> {
                        publish(Label.OpenParticipantSelection(getCurrentParticipants(getState())))
                    }
                    ConversationInformationTab.LINKS -> {
                        publish(Label.OpenLinkAddition)
                    }
                    else -> {
                        publish(Label.AddButtonClicked)
                    }
                }
            }
            is Intent.StartCall -> {
                val state = getState()
                state.participantViewData?.photoData?.uuid?.let {
                    publish(Label.StartCall(listOf(it), intent.isVideo))
                }
                val recipients = state.toolbarData.photoDataList.mapNotNull { it.uuid }
                publish(Label.StartCall(recipients, intent.isVideo))
            }
            is Intent.OpenProfile -> { publish(Label.OpenProfile(intent.profileUuid)) }
            is Intent.OpenMenu -> { publish(Label.OpenMenu(intent.onOptionSelected)) }
            is Intent.ShowFabMenu -> publish(Label.ShowFabMenu(intent.optionAction))
        }

        private fun getCurrentParticipants(state: State): List<UUID> {
            val personUuid = state.participantViewData?.photoData?.uuid?.let { listOf(it) } ?: emptyList()
            val personsUuids = state.toolbarData.photoDataList.mapNotNull { it.uuid }
            return personsUuids.ifEmpty { personUuid }
        }

        private fun copyLink() = scope.launch {
            withContext(dispatcherIO) {
                val url = themeController.getUrlByUuid(conversationInformationData.conversationUuid)
                withContext(dispatcherMain) {
                    publish(Label.CopyLink(url))
                }
            }
        }

        private fun deleteDialog() = scope.launch {
            withContext(dispatcherIO) {
                dialogController.delete(listOf(conversationInformationData.conversationUuid).asArrayList(), true)
            }
        }
    }

    private inner class ReducerImpl(
        private val conversationInformationData: ConversationInformationData
    ) : Reducer<State, Message> {
        override fun State.reduce(msg: Message): State = when (msg) {
            Message.EditTitle.Start -> copy(
                toolbarData = toolbarData.copy(
                    toolbarState = EDITING
                )
            )
            Message.EditTitle.Cancel -> copy(
                toolbarData = toolbarData.copy(
                    toolbarState = DEFAULT
                ),
            )
            is Message.EditTitle.End -> copy(
                toolbarData = toolbarData.copy(
                    title = msg.newTitle ?: EMPTY,
                    toolbarState = DEFAULT
                ),
                tabsViewState = tabsViewState
            )
            is Message.TabSelected -> copy(
                tabsViewState = ConversationInformationTabsViewState(
                    msg.id.toAttachmentTab(),
                    tabsViewState.availableTabs
                )
            )
            is Message.UpdateState -> {
                copy(
                    toolbarData = msg.toolbarData,
                    isGroupConversation = msg.toolbarData.isGroup,
                    tabsViewState = ConversationInformationTabsViewState(
                        tabsViewState.selectedTab,
                        getAvailableTabs(
                            msg.permissions,
                            conversationInformationData.isChat,
                            msg.toolbarData.isGroup
                        )
                    ),
                )
            }
            Message.Search.Open -> copy(
                toolbarData = toolbarData.copy(
                    toolbarState = SEARCHING
                )
            )
            is Message.Search.QueryChanged -> copy(
                searchQuery = msg.query
            )
            Message.Search.Close -> copy(
                toolbarData = toolbarData.copy(
                    toolbarState = DEFAULT
                )
            )
            is Message.FilterSelected -> copy(
                filesFilter = msg.filters
            )
            is Message.ActivityStatusUpdate -> copy(
                participantViewData = participantViewData?.copy(
                    profileActivityStatus = msg.status
                )
            )
            is Message.CallState -> copy(
                callRunning = msg.isRunning
            )

            is Message.UpdateParticipantViewData -> copy(
                participantViewData = msg.data
            )
        }
    }
}

private const val STORE_NAME = "STORE_NAME"
