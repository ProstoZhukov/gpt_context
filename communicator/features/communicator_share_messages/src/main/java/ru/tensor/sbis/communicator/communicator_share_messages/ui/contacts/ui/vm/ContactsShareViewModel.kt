package ru.tensor.sbis.communicator.communicator_share_messages.ui.contacts.ui.vm

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.reactivex.disposables.SerialDisposable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.tensor.sbis.common.data.DependencyProvider
import ru.tensor.sbis.common.util.UUIDUtils
import ru.tensor.sbis.common.util.storeIn
import ru.tensor.sbis.communication_decl.selection.recipient.data.RecipientId
import ru.tensor.sbis.communication_decl.selection.recipient.data.RecipientPersonId
import ru.tensor.sbis.communicator.common.util.share.quick_share.QuickShareHelper
import ru.tensor.sbis.communicator.communicator_share_messages.ui.contacts.domain.ContactsShareEvent.*
import ru.tensor.sbis.communicator.communicator_share_messages.ui.contacts.domain.ContactsShareEvent.ContactsShareMessagePanelEvent.*
import ru.tensor.sbis.communicator.communicator_share_messages.ui.contacts.domain.ContactsShareEvent.ContactsShareSelectionEvent.*
import ru.tensor.sbis.communicator.communicator_share_messages.ui.contacts.domain.ContactsShareReducer
import ru.tensor.sbis.communicator.communicator_share_messages.ui.contacts.ui.utils.getMessageText
import ru.tensor.sbis.communicator.communicator_share_messages.ui.contacts.ui.vm.data.SendContactsShareData
import ru.tensor.sbis.communicator.communicator_share_messages.ui.contacts.ui.vm.live_data.ContactsShareLiveData
import ru.tensor.sbis.communicator.communicator_share_messages.ui.contacts.ui.vm.live_data.ContactsShareViewLiveData
import ru.tensor.sbis.communicator.communicator_share_messages.utils.ContactsInfoUtil
import ru.tensor.sbis.communicator.communicator_share_messages.utils.OfflineLinksUtil
import ru.tensor.sbis.communicator.declaration.send_message.SendMessageManager
import ru.tensor.sbis.communicator.declaration.send_message.SendMessageUseCase
import ru.tensor.sbis.communicator.generated.DataRefreshedMessageControllerCallback
import ru.tensor.sbis.communicator.generated.MessageController
import ru.tensor.sbis.design_selection.contract.customization.selected.SelectedItemClickDelegate
import ru.tensor.sbis.design_selection.contract.data.SelectionItem
import ru.tensor.sbis.design_selection.contract.data.SelectionPersonItem
import ru.tensor.sbis.design_selection.contract.listeners.SelectionDelegate
import ru.tensor.sbis.message_panel.contract.MessagePanelController
import ru.tensor.sbis.persons.ContactVM
import ru.tensor.sbis.profiles.generated.EmployeeProfileController
import ru.tensor.sbis.platform.generated.Subscription
import ru.tensor.sbis.toolbox_decl.share.ShareData
import ru.tensor.sbis.toolbox_decl.share.content.ShareMenuDelegate
import ru.tensor.sbis.toolbox_decl.share.content.data.ShareMenuLoadingState
import ru.tensor.sbis.verification_decl.login.LoginInterface
import java.util.UUID

/**
 * Вью-модель экрана шаринга недавним контактам.
 *
 * @author vv.chekurda
 */
internal class ContactsShareViewModel(
    private val reducer: ContactsShareReducer,
    private val liveData: ContactsShareLiveData,
    private val sendMessageManager: SendMessageManager,
    private val sendMessageUseCase: SendMessageUseCase,
    private val employeeProfileController: DependencyProvider<EmployeeProfileController>,
    private val quickShareHelper: QuickShareHelper,
    private val contactsInfoUtil: ContactsInfoUtil,
    private val loginInterface: LoginInterface,
    private val offlineLinksUtil: OfflineLinksUtil
) : ViewModel(),
    SelectedItemClickDelegate,
    ContactsShareViewLiveData by liveData {

    private lateinit var menuController: ShareMenuDelegate
    private lateinit var selectionDelegate: SelectionDelegate
    private var messageCallbackSubscription: Subscription? = null
    private var currentConversation: UUID? = null

    private val selectionDelegateDisposable = SerialDisposable()

    init {
        liveData.sendShareData.collectOnVMScope(::sendMessage)
        liveData.loadSelectedPersonData.collectOnVMScope(::loadSelectedPersonData)
        liveData.createDraftDialog.collectOnVMScope(::createDraftDialog)
    }

    fun setSelectionDelegate(delegate: SelectionDelegate) {
        if (::selectionDelegate.isInitialized) return
        selectionDelegate = delegate
        liveData.unselectItem.collectOnVMScope(selectionDelegate::unselectItem)
        delegate.selectedItemsWatcher
            .subscribe { data ->
                val mappedData = data.copy(items = data.items.map { it.clearedHighlights })
                reducer.reduce(OnSelectedDataChanged(mappedData)) }
            .storeIn(selectionDelegateDisposable)
    }

    fun setShareMenuController(controller: ShareMenuDelegate) {
        if (::menuController.isInitialized) return
        menuController = controller
        with(liveData) {
            finishShare.collectOnVMScope {
                menuController.dismiss()
            }
            menuBackButtonVisibility.collectOnVMScope(menuController::changeBackButtonVisibility)
            menuNavPanelVisibility.collectOnVMScope(menuController::changeNavPanelVisibility)
            changeHeightMode.collectOnVMScope(menuController::changeHeightMode)
            menuController.bottomOffset.collectOnVMScope(::setBottomOffset)
        }
    }

    fun setMessagePanelController(controller: MessagePanelController<Any, Any, Any>) {
        with(controller) {
            onFocusChanged = { isFocused -> reducer.reduce(OnMessagePanelFocusChanged(isFocused)) }
            onMessageSending = { reducer.reduce(OnSendMessageClicked) }
            onTextChanged = { newText -> reducer.reduce(OnMessagePanelTextChanged(newText)) }
        }
    }

    fun onBackPressed(): Boolean {
        reducer.reduce(OnBackPressed)
        return true
    }

    override fun onUnselectClicked(item: SelectionItem) {
        reducer.reduce(OnUnselectClicked(item))
    }

    private fun sendMessage(data: SendContactsShareData) {
        menuController.changeLoadingState(ShareMenuLoadingState.Loading)
        viewModelScope.launch {
            pushQuickShareContacts(data.recipients)
            subscribeOnSendingMessageState()
            sendConversationMessage(data)
        }
    }

    private suspend fun sendConversationMessage(data: SendContactsShareData) {
        val attachments = data.shareData.files.map(Uri::parse)
        val recipients = data.recipients.map { (it.id as RecipientId).uuid }
        val shareText = when (data.shareData) {
            is ShareData.Contacts -> contactsInfoUtil.getTextContacts(attachments)
            is ShareData.OfflineLink -> offlineLinksUtil.getLinkFromOfflineFile(attachments.first())
            else -> data.shareData.text?.toString().orEmpty()
        }
        val conversationUuid = requireNotNull(liveData.conversationUuid.value)
        val messageText = getMessageText(shareText = shareText, comment = data.comment)

        currentConversation = conversationUuid
        sendMessageUseCase.updateDraftDialog(conversationUuid, recipients)
        sendMessageManager.sendConversationMessage(
            conversationUUID = conversationUuid,
            messageText = messageText,
            recipients = recipients
        )
    }

    private fun subscribeOnSendingMessageState() {
        messageCallbackSubscription = MessageController.instance()
            .dataRefreshed()
            .subscribe(object : DataRefreshedMessageControllerCallback() {
                override fun onEvent(param: HashMap<String, String>) {
                    val themeUuid = param[THEME_ID_KEY]
                    val messageStatus = param[SEND_MESSAGE_STATUS_EVENT_KEY]
                    if (UUIDUtils.equals(themeUuid, currentConversation) && messageStatus == SEND_MESSAGE_SENT_KEY) {
                        menuController.changeLoadingState(ShareMenuLoadingState.Done)
                    }
                }
            })
    }

    private fun pushQuickShareContacts(items: List<SelectionItem>) {
        val contacts = items.filterIsInstance<SelectionPersonItem>().map {
            ContactVM().apply {
                uuid = (it.id as RecipientPersonId).uuid
                name = it.personName
                rawPhoto = it.photoData.photoUrl
            }
        }
        quickShareHelper.pushContactQuickShareTargets(contacts)
    }

    private fun loadSelectedPersonData(personUuid: UUID) {
        viewModelScope.launch(Dispatchers.IO) {
            val profile = employeeProfileController.get().getEmployeeProfileFromCache(personUuid)
            withContext(Dispatchers.Main) {
                reducer.reduce(ContactQuickShareEvent.OnProfileLoaded(profile))
            }
        }
    }

    private fun createDraftDialog(shareData: ShareData) {
        viewModelScope.launch {
            val currentUserUuid = loginInterface.getCurrentAccount()?.personId.let(UUIDUtils::fromString)
                ?: UUIDUtils.NIL_UUID
            val isFilesSharing = shareData !is ShareData.Contacts && shareData !is ShareData.OfflineLink
            val attachments = if (isFilesSharing) shareData.files.map(Uri::parse) else emptyList()
            val conversationUuid = sendMessageUseCase.addNewDialogAttachments(
                recipients = listOf(currentUserUuid),
                attachments = attachments
            )
            reducer.reduce(OnDraftDialogCreated(conversationUuid))
        }
    }

    override fun onCleared() {
        super.onCleared()
        selectionDelegateDisposable.dispose()
        messageCallbackSubscription?.disable()
    }

    private fun <T> Flow<T>.collectOnVMScope(collector: FlowCollector<T>) {
        viewModelScope.launch {
            collect(collector)
        }
    }
}

private const val THEME_ID_KEY = "theme_id"
private const val SEND_MESSAGE_STATUS_EVENT_KEY = "message_status"
private const val SEND_MESSAGE_SENT_KEY = "sent"