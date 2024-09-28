package ru.tensor.sbis.communicator.communicator_share_messages.ui.messages.store

import android.net.Uri
import androidx.core.net.toUri
import com.arkivanov.essenty.statekeeper.StateKeeper
import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.SimpleBootstrapper
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.tensor.sbis.common.navigation.NavxId
import ru.tensor.sbis.communication_decl.communicator.share.ConversationDirectShareArgs
import ru.tensor.sbis.communicator.common.data.theme.ConversationMapper
import ru.tensor.sbis.communicator.common.data.theme.ConversationModel
import ru.tensor.sbis.communicator.common.util.castTo
import ru.tensor.sbis.communicator.common.util.share.quick_share.QuickShareHelper
import ru.tensor.sbis.communicator.communicator_share_messages.ui.messages.data.MessagesShareState
import ru.tensor.sbis.communicator.communicator_share_messages.ui.messages.store.MessagesShareStore.Intent
import ru.tensor.sbis.communicator.communicator_share_messages.ui.messages.store.MessagesShareStore.Label
import ru.tensor.sbis.communicator.communicator_share_messages.ui.messages.store.MessagesShareStore.State
import ru.tensor.sbis.communicator.communicator_share_messages.utils.ContactsInfoUtil
import ru.tensor.sbis.communicator.communicator_share_messages.utils.OfflineLinksUtil
import ru.tensor.sbis.communicator.declaration.send_message.SendMessageManager
import ru.tensor.sbis.communicator.declaration.send_message.SendMessageUseCase
import ru.tensor.sbis.communicator.generated.ChatController
import ru.tensor.sbis.communicator.generated.ThemeController
import ru.tensor.sbis.communicator.generated.ThemeFilter
import ru.tensor.sbis.toolbox_decl.share.ShareData
import ru.tensor.sbis.toolbox_decl.share.content.ShareMenuDelegate
import ru.tensor.sbis.toolbox_decl.share.content.data.ShareMenuHeightMode
import ru.tensor.sbis.toolbox_decl.share.content.data.ShareMenuLoadingState
import timber.log.Timber
import java.util.UUID
import javax.inject.Inject
import ru.tensor.sbis.mvi_extension.create as createWithStateKeeper

/**
 * Фабрика [MessagesShareStore]
 * Содержит реализацию бизнес-логики экрана.
*
* @author dv.baranov
*/
internal class MessagesShareStoreFactory @Inject constructor(
    private val storeFactory: StoreFactory,
    private val sendMessageManager: SendMessageManager,
    private val sendMessageUseCase: SendMessageUseCase,
    private val shareData: ShareData,
    private val conversationMapper: ConversationMapper,
    private val directShareUuid: String?,
    private val contactsInfoUtil: ContactsInfoUtil,
    private val quickShareHelper: QuickShareHelper,
    private val offlineLinksUtil: OfflineLinksUtil
) {
    private var isSharingContactOrOfflineLink: Boolean = false

    private lateinit var menuController: ShareMenuDelegate

    private val dispatcherIO = Dispatchers.IO

    private val isNeedAddAttachments: Boolean by lazy { shareData.files.isNotEmpty() && !isSharingContactOrOfflineLink }
    private val chatController: ChatController by lazy { ChatController.instance() }
    private val themeController: ThemeController by lazy { ThemeController.instance() }
    private val isDirectShare: Boolean = directShareUuid != null

    private val ConversationModel.targetAttachmentsDraftUuid: UUID
        get() = uuid.takeIf { !isNews }
            ?: requireNotNull(documentUuid)

    /** @SelfDocumented */
    fun create(stateKeeper: StateKeeper): MessagesShareStore =
        object :
            MessagesShareStore,
            Store<Intent, State, Label> by storeFactory.createWithStateKeeper(
                stateKeeper = stateKeeper,
                name = MESSAGES_SHARE_STORE_NAME,
                initialState = State(),
                bootstrapper = SimpleBootstrapper(),
                executorFactory = ::ExecutorImpl,
                reducer = ReducerImpl
            ) {}

    private sealed interface Action

    private sealed interface Message {
        object GoToConversationSelectionState : Message
        data class GoToEnteringCommentState(
            val conversationModel: ConversationModel
        ) : Message
        object GoToSendingMessageState : Message

        data class SetSendButtonEnabled(val isEnabled: Boolean = false) : Message
        data class OnMessagePanelFocusChanged(val isFocused: Boolean) : Message
        data class OnTextChanged(val newText: CharSequence) : Message
    }

    private inner class ExecutorImpl : CoroutineExecutor<Intent, Action, State, Message, Label>() {

        override fun executeAction(action: Action, getState: () -> State) {}

        override fun executeIntent(intent: Intent, getState: () -> State) = when (intent) {
            is Intent.GoToConversationSelectionState -> {
                if (directShareUuid != null) {
                    val targetUUID = shareData.castTo<ConversationDirectShareArgs>()?.directShareUuid
                        ?: UUID.fromString(directShareUuid.removePrefix(NavxId.CHATS.name.lowercase()))
                    scope.launch {
                        val conversationModel = getConversationModel(targetUUID, true)
                        if (conversationModel != null) {
                            goToEnteringCommentState(conversationModel)
                        } else {
                            publish(Label.EndShare)
                            Timber.e("Channel direct share error - conversation model is null")
                        }
                    }
                }
                Unit
            }
            is Intent.HandleSelectionResult -> {
                goToEnteringCommentState(intent.result)
            }
            is Intent.SendButtonClicked -> {
                scope.launch { sendMessage(getState()) }
                with(menuController) {
                    changeLoadingState(ShareMenuLoadingState.Loading)
                    changeHeightMode(ShareMenuHeightMode.Short)
                    changeBackButtonVisibility(false)
                }
                dispatch(Message.GoToSendingMessageState)
            }
            is Intent.EndShare -> {
                cancelAddAttachments(getState().selectedConversation?.targetAttachmentsDraftUuid)
                finishShare()
            }
            is Intent.ShareClosedByUser -> {
                if (getState().shareState != MessagesShareState.SENDING) {
                    cancelAddAttachments(getState().selectedConversation?.targetAttachmentsDraftUuid)
                }
                finishShare()
            }
            is Intent.InitMenuController -> initShareMenuController(intent.controller)
            is Intent.NavigateBack -> onBackPressed(getState())
            is Intent.OnMessagePanelFocusChanged -> dispatch(Message.OnMessagePanelFocusChanged(intent.isFocused))
            is Intent.OnTextChanged -> {
                if (getState().shareState != MessagesShareState.SENDING) {
                    dispatch(Message.OnTextChanged(intent.newText))
                } else { Unit }
            }
        }

        private fun goToEnteringCommentState(selectedConversation: ConversationModel) {
            isSharingContactOrOfflineLink = shareData is ShareData.Contacts || shareData is ShareData.OfflineLink
            scope.launch {
                if (isNeedAddAttachments) {
                    addConversationAttachments(selectedConversation)
                } else {
                    dispatch(Message.SetSendButtonEnabled(true))
                }
                dispatch(Message.GoToEnteringCommentState(selectedConversation))
                menuController.changeNavPanelVisibility(false)
                menuController.changeBackButtonVisibility(!isDirectShare)
            }
        }

        private suspend fun addConversationAttachments(selectedConversation: ConversationModel) {
            dispatch(Message.SetSendButtonEnabled(false))
            withContext(dispatcherIO) {
                val attachments = shareData.files.toUriList()
                if (attachments.isNotEmpty()) {
                    sendMessageUseCase.addConversationAttachments(
                        selectedConversation.targetAttachmentsDraftUuid,
                        attachments
                    )
                }
            }
            dispatch(Message.SetSendButtonEnabled(true))
        }

        private suspend fun sendMessage(state: State) = withContext(dispatcherIO) {
            val text = getMessageText(state.messagePanelText.toString())
            state.selectedConversation?.let {
                sendMessageManager.sendConversationMessage(
                    it.uuid,
                    it.documentUuid,
                    text
                )
                if (it.isChatForView && it.title.isNotEmpty()) {
                    quickShareHelper.pushChannelQuickShareTargets(
                        uuid = it.uuid,
                        title = it.title,
                        photoUrl = it.photoUrl
                    )
                }
            }
        }

        private suspend fun getMessageText(comment: String): String = StringBuilder().apply {
            val shareText = shareData.text.toString()
            if (comment.isNotEmpty()) appendLine(comment)
            if (shareText.isNotEmpty()) append(shareText)
            if (shareData is ShareData.Contacts) {
                append(contactsInfoUtil.getTextContacts(shareData.files.toUriList()))
            }
            if (shareData is ShareData.OfflineLink) {
                append(offlineLinksUtil.getLinkFromOfflineFile(shareData.files.toUriList().first()))
            }
        }.toString()

        private fun cancelAddAttachments(conversationUuid: UUID?) {
            if (conversationUuid != null) {
                scope.launch {
                    withContext(dispatcherIO) {
                        sendMessageUseCase.clearDraft(conversationUuid)
                        sendMessageUseCase.cancelAddAttachments(conversationUuid)
                    }
                }
            }
        }

        private suspend fun getConversationModel(uuid: UUID, isChannel: Boolean): ConversationModel? =
            withContext(dispatcherIO) {
                if (isChannel) {
                    chatController.getListOfChats(arrayListOf(uuid))
                        .data
                        .firstOrNull()
                        ?.let {
                            conversationMapper.applyToChat(it)
                        }
                } else {
                    themeController.refresh(ThemeFilter().apply { theme = uuid })
                        .result
                        .firstOrNull()
                        ?.let {
                            conversationMapper.apply(it)
                        }
                }
            }

        private fun onBackPressed(state: State) = when (state.shareState) {
            MessagesShareState.CONVERSATION_SELECTION -> {
                cancelAddAttachments(state.selectedConversation?.targetAttachmentsDraftUuid)
                publish(Label.EndShare)
            }
            MessagesShareState.ENTERING_COMMENT -> {
                cancelAddAttachments(state.selectedConversation?.targetAttachmentsDraftUuid)
                if (!isDirectShare) {
                    menuController.changeBackButtonVisibility(false)
                    menuController.changeNavPanelVisibility(true)
                    dispatch(Message.GoToConversationSelectionState)
                } else {
                    publish(Label.EndShare)
                }
            }
            MessagesShareState.SENDING -> {
                menuController.changeBackButtonVisibility(false)
            }
        }
    }

    private object ReducerImpl : Reducer<State, Message> {
        override fun State.reduce(msg: Message): State = when (msg) {
            is Message.GoToConversationSelectionState -> State(
                messagePanelText = messagePanelText
            )
            is Message.GoToEnteringCommentState -> copy(
                shareState = MessagesShareState.ENTERING_COMMENT,
                selectedConversation = msg.conversationModel
            )
            is Message.GoToSendingMessageState -> copy(
                shareState = MessagesShareState.SENDING
            )
            is Message.SetSendButtonEnabled -> copy(
                isSendButtonEnabled = msg.isEnabled
            )
            is Message.OnMessagePanelFocusChanged -> copy(
                isMessagePanelFocused = msg.isFocused
            )
            is Message.OnTextChanged -> copy(
                messagePanelText = msg.newText
            )
        }
    }

    private fun initShareMenuController(controller: ShareMenuDelegate) {
        if (::menuController.isInitialized) return
        menuController = controller
    }

    private fun finishShare() {
        if (::menuController.isInitialized) menuController.dismiss()
    }

    private fun List<String>.toUriList(): List<Uri> = this.map { it.toUri() }
}

private const val MESSAGES_SHARE_STORE_NAME = "MESSAGES_SHARE_STORE_NAME"
