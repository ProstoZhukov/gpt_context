package ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.chats.settings

import android.net.Uri
import com.facebook.common.util.UriUtil
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.PublishSubject
import ru.tensor.sbis.common.generated.ErrorCode
import ru.tensor.sbis.common.util.NetworkUtils
import ru.tensor.sbis.common.util.UUIDUtils
import ru.tensor.sbis.common.util.asArrayList
import ru.tensor.sbis.common.util.storeIn
import ru.tensor.sbis.common.util.uri.UriWrapper
import ru.tensor.sbis.communication_decl.selection.recipient.manager.RecipientSelectionResult
import ru.tensor.sbis.communication_decl.selection.recipient.manager.RecipientSelectionResultManager
import ru.tensor.sbis.communicator.core.data.events.MessagesEvent
import ru.tensor.sbis.communicator.generated.ChatNotificationOptions
import ru.tensor.sbis.communicator.generated.ChatResult
import ru.tensor.sbis.communicator.generated.Permissions
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.chats.settings.adapter.viewholder.ChatSettingsContactItem
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.chats.settings.adapter.viewholder.ChatSettingsItem
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.chats.settings.chat_types.ChatSettingsParticipationTypeOptions
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.chats.settings.chat_types.ChatSettingsTypeOptions
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.chats.settings.chat_types.toChannelType
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.chats.settings.chat_types.toChatSettingsParticipantTypeOptions
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.chats.settings.chat_types.toChatSettingsTypeOptions
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.chats.settings.chat_types.toParticipationType
import ru.tensor.sbis.communicator.themes_registry.ui.themeParticipants.model.ThemeParticipant
import ru.tensor.sbis.mvp.interactor.crudinterface.BaseListAbstractTwoWayPaginationPresenter
import ru.tensor.sbis.mvp.interactor.crudinterface.filter.ListFilter
import ru.tensor.sbis.mvp.interactor.crudinterface.subscribing.SubscriptionManager
import ru.tensor.sbis.persons.ContactVM
import timber.log.Timber
import java.util.*
import java.util.concurrent.TimeUnit
import ru.tensor.sbis.common.R as RCommon
import ru.tensor.sbis.communicator.design.R as RCommunicatorDesign
import ru.tensor.sbis.design.R as RDesign
import ru.tensor.sbis.message_panel.R as RMessagePanel

const val PROGRESS_TIMEOUT_MILLIS = 300L

const val HIDE_PROGRESS_CODE = 0
const val SUCCESSFULLY_FINISHED_CODE = -1

/**
 * Базовый презентер настроек чата
 *
 * @param interactor                интерактор настроек чата
 * @param uriWrapper                класс для работы с URI
 * @param recipientSelectionManager компонент, отвечающий за получение результатов выбора участников/администраторов чата
 * @param isNewChat                 true, если новый чат
 * @param chatUuid                  UUID чата
 * @param draftChat                 true, если драфтовый чат
 * @param filter                    фильтр для CRUD-фасада
 * @param subscriptionManager       менеджер для управления подписками и событиями контроллера
 * @param networkUtils              @SelfDocumented
 *
 * @author vv.chekurda
 */
internal abstract class BaseChatSettingsPresenter<LIST_FILTER : ListFilter, FILTER, CALLBACK>(
    protected val interactor: ChatSettingsInteractor,
    private val uriWrapper: UriWrapper,
    protected val recipientSelectionManager: RecipientSelectionResultManager,
    protected val isNewChat: Boolean,
    protected var chatUuid: UUID?,
    protected val draftChat: Boolean,
    filter: LIST_FILTER,
    subscriptionManager: SubscriptionManager,
    networkUtils: NetworkUtils,
) : BaseListAbstractTwoWayPaginationPresenter<ChatSettingsContract.View, ChatSettingsItem, LIST_FILTER, FILTER, CALLBACK>
    (filter, subscriptionManager, networkUtils),
    ChatSettingsContract.Presenter {

    @Suppress("MemberVisibilityCanBePrivate")
    protected var loadingDisposable: CompositeDisposable = CompositeDisposable()
    private var retainingDisposable: Disposable? = null
    private var recipientSelectionResultDisposable: Disposable? = null

    private var chatSettingsInfo: ChatSettingsInfo =
        ChatSettingsInfo(
            null,
            null,
            false,
            ChatNotificationOptions(),
            ChatSettingsTypeOptions.OPEN,
            ChatSettingsParticipationTypeOptions.FOR_ALL,
            ChatSettingsParticipationTypeOptions.FOR_ALL
        )
    protected var userModel: ContactVM? = null
    private var creatorName: String = ""
    private var chatPersonsList: MutableList<ThemeParticipant> = ArrayList()
    private var isAdmin: Boolean = false
    private var creationTimestamp: Long = 0L
    private var permissions: Permissions? = null
    private var isOwnAdminStatusChanged = false
    private var updateInProgress: Boolean = false

    private val mProgressOperationSubject: PublishSubject<Int> = PublishSubject.create()
    private val mProgressOperationDisposable: Disposable
    private val minorDisposables: CompositeDisposable = CompositeDisposable()

    protected var selectedParticipantUuids: List<UUID> = listOf()

    override var isSwipeEnabled: Boolean = false

    init {
        subscribeOnThemeControllerUpdates()

        mProgressOperationDisposable =
            mProgressOperationSubject.debounce(PROGRESS_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { stringRes ->
                        mView?.let {
                            when {
                                stringRes > 0 -> it.showProgressDialog(stringRes)
                                stringRes == HIDE_PROGRESS_CODE -> it.hideProgressDialog()
                                stringRes == SUCCESSFULLY_FINISHED_CODE -> handleSuccessResult()
                            }
                        }
                    },
                    {
                        Timber.e(it)
                    },
                    {
                        mView?.hideProgressDialog()
                    },
                )
        subscribeOnRecipientSelectionDone()

        if (!isNewChat && !draftChat && chatUuid != null) {
            loadChatData(chatUuid!!)
        }
        if (draftChat) {
            initDraftParticipants()
        }
    }

    private fun initDraftParticipants() {
        onRecipientsCollectionChanged(recipientSelectionManager.selectionResult)
    }

    private fun handleSuccessResult() {
        if (selectedParticipantUuids.isNotEmpty()) recipientSelectionManager.clear()
        mView?.finish(chatUuid!!)
    }

    private fun loadChatData(chatUuid: UUID) {
        loadingDisposable.add(
            interactor.loadChat(chatUuid)
                .subscribe({ chat ->
                    run {
                        val photoUrl: String = chat.photoUrl
                        permissions = chat.chatPermissions
                        creationTimestamp = chat.createdTimestamp
                        loadingDisposable.add(
                            interactor.getConversationData(chatUuid).subscribe({ conversationResult ->
                                conversationResult.data?.let { data ->
                                    chatSettingsInfo = ChatSettingsInfo(
                                        avatarUrl = photoUrl,
                                        chatName = chat.chatName,
                                        isChatAvatarAdded = chat.canDeleteChatPhoto,
                                        notificationOptions = chat.notificationOptions,
                                        chatType = data.channelType.toChatSettingsTypeOptions(),
                                        participationType = data.participationType.toChatSettingsParticipantTypeOptions(),
                                        savedParticipationType =
                                        data.participationType.toChatSettingsParticipantTypeOptions(),
                                    )
                                    mView?.let {
                                        displayViewState(it)
                                    }
                                }
                            }, { Timber.e(it) }),
                        )
                        if (!isNewChat) {
                            chat.creatorUuid?.let { uuid ->
                                loadingDisposable.add(
                                    interactor.loadProfile(uuid)
                                        .subscribe({ contactVM ->
                                            saveCreatorName(contactVM)
                                            mView?.let {
                                                displayViewState(it)
                                            }
                                        }, {
                                            Timber.e(it)
                                        }),
                                )
                            }
                        }
                        if (isSwipeEnabled != ((permissions?.canChangeAdministrators == true) && !isNewChat)) {
                            isOwnAdminStatusChanged = true
                            isSwipeEnabled = !isSwipeEnabled
                            mView?.setSwipeEnabled(isSwipeEnabled)
                        } else {
                            isOwnAdminStatusChanged = false
                        }
                        mView?.let {
                            it.setChatName(chat.chatName)
                            displayViewState(it)
                        }
                    }
                }, { Timber.e(it) }),
        )
    }

    private fun subscribeOnRecipientSelectionDone() {
        recipientSelectionResultDisposable = recipientSelectionManager
            .getSelectionResultObservable()
            .subscribe(::onRecipientsCollectionChanged) { error ->
                Timber.d(error, "Failed to change recipients selection in ${BaseChatSettingsPresenter::class.java.simpleName}")
            }
    }

    private fun onRecipientsCollectionChanged(result: RecipientSelectionResult) {
        if (!result.isSuccess || result.data.allPersonsUuids.isEmpty()) return
        selectedParticipantUuids = result.data.allPersonsUuids
        if (!isNewChat && !draftChat) {
            minorDisposables.add(
                interactor.chatAdministratorsCommandWrapper
                    .addAdministrators(chatUuid!!, result.data.allPersonsUuids.asArrayList())
                    .subscribe({
                        updateDataList(false)
                    }, {
                        Timber.e(it)
                        mView?.showToast(if (isNewChat) RMessagePanel.string.message_panel_chat_add_participants_failure else RMessagePanel.string.message_panel_chat_add_participants_failure)
                    }),
            )
        } else {
            updateDataList(false)
        }
    }

    /** @SelfDocumented */
    override fun attachView(view: ChatSettingsContract.View) {
        super.attachView(view)
        view.setPersonListTitle(if (isNewChat) RCommunicatorDesign.string.communicator_chat_participants else RCommunicatorDesign.string.communicator_chat_admins)
    }

    /** @SelfDocumented */
    protected fun getParticipantsFromRecipientSelection(): List<UUID> {
        return selectedParticipantUuids
    }

    /** @SelfDocumented */
    override fun onDestroy() {
        loadingDisposable.dispose()
        retainingDisposable?.dispose()
        recipientSelectionManager.clear()
        recipientSelectionResultDisposable?.dispose()
        minorDisposables.dispose()
        mProgressOperationDisposable.dispose()

        super.onDestroy()
    }

    /** @SelfDocumented */
    override fun onItemClick(profileUuid: UUID) {
        mView?.openProfile(profileUuid)
    }

    /** @SelfDocumented */
    override fun onChatNameChanged(name: String) {
        if (chatSettingsInfo.chatName != name) {
            chatSettingsInfo.chatName = name
            mView?.setChatName(name, false)
        }
    }

    override fun getChatName(): String = chatSettingsInfo.chatName ?: ""

    /** @SelfDocumented */
    override fun setDataFromMyProfile() {
        loadingDisposable.add(
            interactor.loadMyProfile()
                .subscribe(
                    { contactVM ->
                        userModel = contactVM
                        if (isNewChat) {
                            saveCreatorName(contactVM)
                        }
                        mView?.let {
                            displayViewState(it)
                        }
                        if (draftChat) {
                            updateDataList(false)
                        }
                    },
                    { processContactsLoadingError(it) },
                ),
        )
    }

    /** @SelfDocumented */
    override fun closeChat() {
        chatUuid?.let { it ->
            minorDisposables.add(
                interactor.closeChat(it)
                    .subscribe(
                        { commandStatus ->
                            run {
                                if (ErrorCode.SUCCESS == commandStatus.errorCode) {
                                    mView?.cancel()
                                } else {
                                    mView?.showToast(RCommunicatorDesign.string.communicator_channel_close_failure)
                                }
                            }
                        },
                        { Timber.e(it) },
                    ),
            )
        }
    }

    /** @SelfDocumented */
    override fun onRemoveAdminClick(admin: ThemeParticipant) {
        if (dataList.size == 1) {
            mView?.showToast(RCommunicatorDesign.string.communicator_chat_remove_last_admin)
        } else {
            minorDisposables.add(
                interactor.chatAdministratorsCommandWrapper.removeAdministrators(chatUuid!!, arrayListOf(admin.employeeProfile.uuid))
                    .subscribe(
                        {
                            chatPersonsList.remove(admin)
                        },
                        {
                            Timber.e(it, "Error on add chat admins.")
                            mView?.showToast(RCommunicatorDesign.string.communicator_chat_remove_administrator_failure)
                        },
                    ),
            )
        }
    }

    private fun deleteAvatar() {
        chatUuid?.let { uuid ->
            minorDisposables.add(
                interactor.deleteAvatar(uuid)
                    .subscribe(
                        { commandStatus ->
                            if (commandStatus.errorCode != ErrorCode.SUCCESS) {
                                mView?.showToast(RCommunicatorDesign.string.communicator_chat_remove_avatar_failure)
                            }
                        },
                        {
                            Timber.e(it, "Error on delete chat avatar.")
                            mView?.showToast(RCommunicatorDesign.string.communicator_chat_remove_avatar_failure)
                        },
                    ),
            )
        }
    }

    private fun processContactsLoadingError(throwable: Throwable) {
        mView?.showToast(RCommon.string.common_update_error)
        Timber.e(throwable)
    }

    /** @SelfDocumented */
    override fun onAddPersonButtonClicked() {
        mView?.showChoosingRecipients(chatUuid, !isNewChat)
    }

    override fun onDoneButtonClicked() {
        if (chatSettingsInfo.savedParticipationType == ChatSettingsParticipationTypeOptions.FOR_ALL &&
            chatSettingsInfo.participationType == ChatSettingsParticipationTypeOptions.ONLY_EMPLOYEES
            ) {
            mView?.showOnlyEmployeesTypeConfirmation()
        } else {
            updateChat()
        }
    }

    override fun updateChat() {
        // Если chatSettingsInfo.avatarUrl является NetworkUri, значит, на облаке уже есть данный аватар.
        // В этом случае на контроллер не нужно передавать avatarArray и fileName, поэтому в uri сетаем null
        val uri = chatSettingsInfo.avatarUrl?.let {
            if (it.isBlank() || UriUtil.isNetworkUri(Uri.parse(it))) null else it
        }
        val avatarArray = uriWrapper.getByteArrayForUrl(uri)
        if (uri != null && avatarArray == null) {
            mView?.showToast(RCommon.string.attach_photo_error)
            return
        }
        val fileName = if (uri == null) null else uriWrapper.getFileByUriString(uri)?.name

        // Единственный кейс, при котором нужно вызвать метод удаления аватара у контроллера, это когда при заходе
        // в настройки чата уже есть загруженный ранее аватар, мы его удаляем (только на UI, контроллер не в курсе ещё!),
        // и сохраняем изменения. В остальных случаях (выбор другого аватара, например), в контроллер полетит новый аватар,
        // который перезатрёт старый, поэтому дополнительно вызывать метод удаления аватара не нужно
        if (!isNewChat && chatSettingsInfo.avatarUrl == null && !chatSettingsInfo.isChatAvatarAdded) {
            deleteAvatar()
        }

        val chatName = chatSettingsInfo.chatName?.trim()
        if (chatName.isNullOrEmpty()) {
            mView?.setEditNameViewBackgroundColor(true)
            mView?.showToast(RCommunicatorDesign.string.communicator_warning_enter_channel_name)
            return
        } else {
            mView?.setEditNameViewBackgroundColor(false)
        }

        if (!updateInProgress) {
            mProgressOperationSubject.onNext(RDesign.string.design_please_wait)
            updateInProgress = true
            loadingDisposable.add(
                if (draftChat || chatUuid == null) {
                    interactor.createNewChat(
                        chatName,
                        chatSettingsInfo.notificationOptions,
                        avatarArray,
                        fileName,
                        selectedParticipantUuids,
                        chatSettingsInfo.chatType.toChannelType(),
                        chatSettingsInfo.participationType.toParticipationType(),
                    ).subscribe({ chatResult -> onChatCreated(chatResult) }, ::onError)
                } else {
                    if (isNewChat) {
                        interactor.convertDialogToChat(
                            chatUuid!!,
                            chatName,
                            chatSettingsInfo.notificationOptions,
                            avatarArray,
                            fileName,
                        )
                    } else {
                        interactor.updateChat(
                            chatUuid!!,
                            chatName,
                            chatSettingsInfo.notificationOptions,
                            avatarArray,
                            fileName,
                            chatSettingsInfo.chatType.toChannelType(),
                            chatSettingsInfo.participationType.toParticipationType(),
                        )
                    }.let { single ->
                        single.subscribe(
                            { mProgressOperationSubject.onNext(SUCCESSFULLY_FINISHED_CODE) },
                            ::onError,
                        )
                    }
                },
            )
        }
        unsubscribeEventManager()
    }

    private fun onError(e: Throwable?) {
        val message = String.format("${BaseChatSettingsPresenter::class.java.canonicalName}: %s", e.toString())
        Timber.e(message)
        mView?.showToast(message)
        updateFailed()
    }

    private fun onChatCreated(chatResult: ChatResult?) {
        if (chatResult?.status?.errorCode == ErrorCode.SUCCESS) {
            chatUuid = chatResult.data!!.uuid
            handleSuccessResult()
        } else {
            updateFailed()
            Timber.e("Ошибка контроллера: %s", chatResult?.status?.errorMessage)
            mView?.showToast(RCommon.string.common_update_error)
        }
    }

    private fun updateFailed() {
        mProgressOperationSubject.onNext(0)
        updateInProgress = false
    }

    private fun saveCreatorName(creatorModel: ContactVM?) {
        creatorName = creatorModel?.let { it.name.lastName + " " + it.name.firstName } ?: ""
    }

    //region pagination
    /** @SelfDocumented */
    override fun getEmptyViewErrorId(): Int = 0

    /** @SelfDocumented */
    override fun getDataList(): MutableList<ChatSettingsItem> = chatPersonsList.map {
        ChatSettingsContactItem(it, {}, {}, false)
    }.toMutableList()

    /** @SelfDocumented */
    override fun swapDataList(dataList: MutableList<ChatSettingsItem>) {
        if (chatPersonsList !== dataList) {
            chatPersonsList = dataList.filterIsInstance<ChatSettingsContactItem>().map {
                it.participant
            }.toMutableList()
        }
    }

    //endregion

    /** @SelfDocumented */
    override fun isNeedToDisplayViewState(): Boolean = true

    /** @SelfDocumented */
    override fun changeNotificationOptions(all: Boolean, personal: Boolean, administrator: Boolean) {
        chatSettingsInfo.notificationOptions = ChatNotificationOptions(all, personal, administrator)
    }

    override fun saveNotificationOptions() {
        mView?.updateCheckboxAndSwitch(chatSettingsInfo.notificationOptions, needUpdate = false)
    }

    /** @SelfDocumented */
    override fun handleNewAvatar(imageUriString: String?) {
        mView?.updateAvatar(imageUriString)
        chatSettingsInfo.run {
            isChatAvatarAdded = imageUriString != null
            avatarUrl = imageUriString
        }
    }

    override fun onChangeChatTypeClicked() {
        val options = listOf(ChatSettingsTypeOptions.OPEN, ChatSettingsTypeOptions.PRIVATE)
        mView?.onChangeChatTypeClicked(options, chatSettingsInfo.chatType)
    }

    override fun onChangeParticipationTypeClicked() {
        val options = listOf(ChatSettingsParticipationTypeOptions.FOR_ALL, ChatSettingsParticipationTypeOptions.ONLY_EMPLOYEES)
        mView?.onChangeParticipationTypeClicked(options, chatSettingsInfo.participationType)
    }

    override fun onChatTypeSelected(newChatType: ChatSettingsTypeOptions) {
        chatSettingsInfo.run {
            chatType = newChatType
        }
    }

    override fun onChatParticipationTypeSelected(newChatType: ChatSettingsParticipationTypeOptions) {
        chatSettingsInfo.run {
            participationType = newChatType
        }
    }

    override fun onAvatarClick() {
        val avatarUrlDefined = !chatSettingsInfo.avatarUrl.isNullOrBlank()
        if ((isNewChat || permissions?.canChangePhoto == true) && !avatarUrlDefined) {
            mView?.showAvatarChangeDialog()
        } else if (avatarUrlDefined) {
            mView?.showChatAvatar(chatSettingsInfo.avatarUrl!!)
        }
    }

    override fun onAvatarLongClick() {
        if (!isNewChat && permissions?.canChangePhoto != true) return
        if (chatSettingsInfo.avatarUrl.isNullOrBlank()) {
            mView?.showAvatarChangeDialog()
        } else {
            mView?.showAvatarOptionMenu()
        }
    }

    override fun handleAvatarOption(option: AvatarMenuOption) {
        when (option) {
            AvatarMenuOption.REPLACE -> mView?.showAvatarChangeDialog()
            AvatarMenuOption.DELETE -> handleNewAvatar(null)
        }
    }

    /** @SelfDocumented */
    override fun displayViewState(view: ChatSettingsContract.View) {
        super.displayViewState(view)

        view.run {
            setToolbarData(creatorName, isNewChat, creationTimestamp)
            updateCheckboxAndSwitch(chatSettingsInfo.notificationOptions, true)
            updateChatTypeButtonsState(chatSettingsInfo.chatType, chatSettingsInfo.participationType)

            changeAddPersonsButtonVisibility(permissions?.canChangeAdministrators ?: draftChat)
            updateAvatar(chatSettingsInfo.avatarUrl)
            updateDataList(dataList, mDataListOffset)
            showCloseChatButton(permissions?.canCloseRestoreChat ?: false)
            setChatNameEditable(isNewChat || permissions?.canChangeName ?: false)
            changeActionDoneButtonVisibility(
                isNewChat || permissions?.let {
                    it.canChangeName || it.canChangeAdministrators || it.canChangePhoto
                } ?: false,
            )
        }
    }

    private fun subscribeOnThemeControllerUpdates() {
        interactor.observeThemeControllerUpdates()
            .subscribe { params ->
                val chatUuid = this.chatUuid ?: return@subscribe
                val uuid = UUIDUtils.toString(chatUuid)
                if (MessagesEvent.REGISTRY.isExistsIn(params) && (
                        MessagesEvent.AFFECTED_THEMES_ANY.isExistsIn(params) ||
                            (uuid != null && params[MessagesEvent.AFFECTED_THEMES_LIST.type]?.contains(uuid) == true)
                        ) || params[MessagesEvent.THEME.type] == uuid
                ) {
                    loadChatData(chatUuid)
                }
            }
            .storeIn(loadingDisposable)
    }
}
