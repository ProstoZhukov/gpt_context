package ru.tensor.sbis.communicator.sbis_conversation.ui.message

import CommunicatorPushKeyboardHelper
import android.annotation.SuppressLint
import androidx.tracing.Trace
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.SerialDisposable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.runningReduce
import kotlinx.coroutines.launch
import ru.tensor.sbis.attachments.decl.AllowedActionResolver
import ru.tensor.sbis.attachments.decl.isFolder
import ru.tensor.sbis.attachments.decl.v2.DefAttachmentListComponentConfig
import ru.tensor.sbis.attachments.decl.v2.DefAttachmentListEntity
import ru.tensor.sbis.attachments.decl.v2.DefAttachmentListParams
import ru.tensor.sbis.attachments.generated.FileInfoViewModel
import ru.tensor.sbis.common.generated.CommandStatus
import ru.tensor.sbis.common.generated.ErrorCode
import ru.tensor.sbis.common.lifecycle.AppLifecycleTracker
import ru.tensor.sbis.common.util.ClipboardManager
import ru.tensor.sbis.common.util.UUIDUtils
import ru.tensor.sbis.common.util.UrlUtils
import ru.tensor.sbis.common.util.storeIn
import ru.tensor.sbis.communication_decl.communicator.media.MediaPlayer
import ru.tensor.sbis.communication_decl.communicator.ui.ConversationFromRegistryParams
import ru.tensor.sbis.communication_decl.communicator.ui.ConversationViewMode
import ru.tensor.sbis.communication_decl.complain.data.ComplainUseCase
import ru.tensor.sbis.communication_decl.model.ConversationType
import ru.tensor.sbis.communication_decl.selection.recipient.manager.RecipientSelectionResultManager
import ru.tensor.sbis.communicator.base.conversation.data.model.MessageAction
import ru.tensor.sbis.communicator.base.conversation.data.model.getAudioRecordMessageActionsList
import ru.tensor.sbis.communicator.base.conversation.data.model.getDefaultMessageActionsList
import ru.tensor.sbis.communicator.base.conversation.presentation.crud.ConversationListComponent
import ru.tensor.sbis.communicator.base.conversation.presentation.presenter.MessageCollectionFilter
import ru.tensor.sbis.communicator.base.conversation.presentation.presenter.dispatcher.ConversationEvent
import ru.tensor.sbis.communicator.base.conversation.presentation.presenter.messages.BaseConversationMessagesPresenter
import ru.tensor.sbis.communicator.common.conversation.ConversationPrefetchManager
import ru.tensor.sbis.communicator.common.conversation.ConversationRouter
import ru.tensor.sbis.communicator.common.conversation.data.Message
import ru.tensor.sbis.communicator.common.conversation_preview.ConversationPreviewMenuAction.BaseConversationPreviewMenuAction.Go
import ru.tensor.sbis.communicator.common.conversation_preview.ConversationPreviewMenuAction.BaseConversationPreviewMenuAction.Unpin
import ru.tensor.sbis.communicator.common.conversation_preview.ConversationPreviewMenuAction.MessageConversationPreviewMenuAction
import ru.tensor.sbis.communicator.common.conversation_preview.ConversationPreviewMenuAction.MessageConversationPreviewMenuAction.Copy
import ru.tensor.sbis.communicator.common.push.MessagesPushManager
import ru.tensor.sbis.communicator.common.push.SubscribeOnNotification
import ru.tensor.sbis.communicator.common.push.ThemeSubscribeFromNotification
import ru.tensor.sbis.communicator.common.push.UnsubscribeFromNotification
import ru.tensor.sbis.communicator.common.util.castTo
import ru.tensor.sbis.communicator.common.util.message_search.ThemeMessageSearchApi
import ru.tensor.sbis.communicator.core.data.events.MessagesEvent
import ru.tensor.sbis.communicator.core.firebase_metrics.MetricsDispatcher
import ru.tensor.sbis.communicator.core.firebase_metrics.MetricsType
import ru.tensor.sbis.communicator.core.utils.MessageUtils
import ru.tensor.sbis.communicator.design.R
import ru.tensor.sbis.communicator.generated.AttachmentViewModel
import ru.tensor.sbis.communicator.generated.CreateDraftDialogResult
import ru.tensor.sbis.communicator.generated.DataRefreshedMessageControllerCallback
import ru.tensor.sbis.communicator.generated.MessageFilter
import ru.tensor.sbis.communicator.generated.MessageRemovableType
import ru.tensor.sbis.communicator.generated.ServiceType
import ru.tensor.sbis.communicator.sbis_conversation.CommunicatorSbisConversationPlugin
import ru.tensor.sbis.communicator.sbis_conversation.adapters.MediaMessageActionListener
import ru.tensor.sbis.communicator.sbis_conversation.adapters.MessageAccessButtonListener
import ru.tensor.sbis.communicator.sbis_conversation.adapters.MessageThreadActionListener
import ru.tensor.sbis.communicator.sbis_conversation.data.CoreConversationInfo
import ru.tensor.sbis.communicator.sbis_conversation.data.model.ConversationData
import ru.tensor.sbis.communicator.sbis_conversation.data.model.ConversationMessage
import ru.tensor.sbis.communicator.sbis_conversation.data.model.ConversationServiceMessage
import ru.tensor.sbis.communicator.sbis_conversation.interactor.ConversationInteractor
import ru.tensor.sbis.communicator.sbis_conversation.interactor.data.ConversationDataInteractor
import ru.tensor.sbis.communicator.sbis_conversation.ui.ConversationDataDispatcher
import ru.tensor.sbis.communicator.sbis_conversation.ui.ConversationState
import ru.tensor.sbis.communicator.sbis_conversation.ui.crud.ConversationPrefetchManagerImpl
import ru.tensor.sbis.communicator.sbis_conversation.ui.message.delegates.BackStackMessageHighlights
import ru.tensor.sbis.communicator.sbis_conversation.ui.message.delegates.ConversationActionDelegate
import ru.tensor.sbis.communicator.sbis_conversation.ui.viewmodel.ConversationViewModel
import ru.tensor.sbis.crud4.defaultViewPostSize
import ru.tensor.sbis.crud4.view.datachange.DataChange
import ru.tensor.sbis.design.cloud_view.content.phone_number.PhoneNumberClickListener
import ru.tensor.sbis.design.cloud_view.utils.thread.CloudThreadHelper
import ru.tensor.sbis.design.confirmation_dialog.ConfirmationButtonId
import ru.tensor.sbis.design.message_view.model.MessageType
import ru.tensor.sbis.design.message_view.model.getThreadData
import ru.tensor.sbis.design.stubview.StubViewContent
import ru.tensor.sbis.edo_decl.document.Document
import ru.tensor.sbis.feature_ctrl.SbisFeatureService
import ru.tensor.sbis.localfeaturetoggle.domain.LocalFeatureToggleService
import ru.tensor.sbis.platform.generated.Subscription
import ru.tensor.sbis.verification_decl.login.LoginInterface
import timber.log.Timber
import java.util.Objects
import java.util.UUID

/**
 * Презентер секции списка сообщений на crud коллекции.
 *
 * @author vv.chekurda
 */
internal class ConversationMessagesPresenter(
    collectionComponent: ConversationListComponent<ConversationMessage>,
    interactor: ConversationInteractor,
    coreConversationInfo: CoreConversationInfo,
    dataDispatcher: ConversationDataDispatcher,
    collectionFilter: MessageCollectionFilter,
    clipboardManager: ClipboardManager,
    private val messagesPushManager: MessagesPushManager,
    private val recipientSelectionManager: RecipientSelectionResultManager? = null,
    private val viewModel: ConversationViewModel?,
    private val appLifecycleTracker: AppLifecycleTracker,
    private val mediaPlayer: MediaPlayer? = null,
    private val loginInterface: LoginInterface,
    private val localFeatureService: LocalFeatureToggleService? = null,
    private val featureService: SbisFeatureService? = null,
    private val actionDelegate: ConversationActionDelegate,
    private val prefetchManager: ConversationPrefetchManager,
    communicatorPushKeyboardHelper: CommunicatorPushKeyboardHelper,
) : BaseConversationMessagesPresenter<
        ConversationMessagesContract.View, ConversationInteractor, ConversationMessage,
        MessageFilter, ConversationState, ConversationData,
        ConversationDataInteractor.ConversationDataResult, CoreConversationInfo, ConversationDataDispatcher, DataRefreshedMessageControllerCallback
        >(collectionComponent, interactor, coreConversationInfo, dataDispatcher,
        clipboardManager, collectionFilter, communicatorPushKeyboardHelper),
    ConversationMessagesContract.Presenter<ConversationMessagesContract.View>,
    PhoneNumberSelectionItemListener by actionDelegate,
    PhoneNumberClickListener by actionDelegate,
    PhoneNumberVerificationErrorHandler by actionDelegate,
    MessageAccessButtonListener by actionDelegate,
    ConversationSingAndAcceptHandler by actionDelegate,
    MediaMessageActionListener by actionDelegate,
    MessageThreadActionListener by actionDelegate {

    private var router: ConversationRouter? = null

    private val initialLoadingSubscription = SerialDisposable()
    private var markAllMessagesDisposable = SerialDisposable()
    private val serviceMessageNamesDisposable = SerialDisposable()
    private val serviceUnfoldDisposable = SerialDisposable()
    private val phoneVerificationDisposable = SerialDisposable()

    private var document: Document? = null

    private var messageToSign: Message? = null
    private var isFirstMessage: Boolean = false

    private var isClosedChat: Boolean = false
    private var isLocked: Boolean = false

    private var pinnedChatMessage: Message? = null
    private var canUnpinChatMessage: Boolean = false
    private var isChannelAdminPermissions: Boolean = false

    private var isKeyboardWaiting: Boolean = false

    private val needToHighlightMessageRelevantMessage: Boolean
        get() = coreConversationInfo.highlightMessage &&
            !isMessageHighlighted &&
            coreConversationInfo.messageUuid != null &&
            dataList.find { it.uuid == coreConversationInfo.messageUuid } != null

    private var cachedRefreshCallbackSubscriptionRequest: Observable<Subscription>? = null

    private val channelHelper = ChannelConversationDataHelper(::loadConversationData)

    override val canShowStub: Boolean
        get() = super.canShowStub && !isVideoConversation

    private val isVideoConversation: Boolean
        get() = !conversationState.isNewConversation && coreConversationInfo.conversationType == ConversationType.VIDEO_CONVERSATION

    private val isExistingConversation: Boolean
        get() = !conversationState.isNewConversation && coreConversationInfo.conversationUuid != null

    private val isNewDialog: Boolean
        get() = coreConversationInfo.conversationUuid == null && !coreConversationInfo.isChat

    private val isNewChat: Boolean
        get() = coreConversationInfo.conversationUuid == null && coreConversationInfo.isChat

    /**
     * Признак отображения панели записи аудиосообщения.
     */
    private val isAudioRecordVisible: Boolean
        get() = conversationState.audioRecordState.isVisible

    override val conversationUuid: UUID?
        get() = coreConversationInfo.conversationUuid

    init {
        startInitialLoading()
        setupComplainServiceCallback()
        viewModelScope.launch(Dispatchers.Default) {
            subscribeOnAppForegroundEvents()
        }
        viewModelScope.launch(Dispatchers.Main) {
            BackStackMessageHighlights.highlightThread.collect(::tryHighlightThread)
        }
    }

    override fun initCollection(conversationUuid: UUID) {
        super.initCollection(conversationUuid)
        prefetchList()
    }

    private fun prefetchList() {
        val filter = collectionFilter.getMessageFilter()
        val prefetchCommand = (prefetchManager as ConversationPrefetchManagerImpl).prefetchListCommand(filter)
        Trace.beginAsyncSection("MessagesPresenter.prefetchList ${prefetchCommand != null}", 0)
        prefetchCommand
            ?.subscribe {
                Trace.endAsyncSection("MessagesPresenter.prefetchList true", 0)
                if (dataList.isEmpty() && it.dataList.isNotEmpty()) {
                    updateDataList(it.dataList)
                }
            }?.storeIn(compositeDisposable)

        if (prefetchCommand == null) {
            Trace.endAsyncSection("MessagesPresenter.prefetchList false", 0)
        }
    }

    private fun setupComplainServiceCallback() {
        CommunicatorSbisConversationPlugin.singletonComponent.dependency.complainServiceProvider?.also {
            it.getComplainService().getBlockedListChangedObservable()
                .subscribe { onRefresh() }
                .storeIn(compositeDisposable)
        }
    }

    override fun attachView(view: ConversationMessagesContract.View) {
        Trace.beginAsyncSection("MessagesPresenter.attachView", 0)
        val isFirstAttach = isFirstAttachView
        super.attachView(view)
        if (isFirstAttach) {
            tryApplyInitialData()
        } else {
            loadConversationData()
            onThemeAfterOpenedWithCurrentFilter()
        }
        actionDelegate.initView(view)
        Trace.endAsyncSection("MessagesPresenter.attachView", 0)
    }

    override fun handleConversationEvent(event: ConversationEvent) {
        super.handleConversationEvent(event)
        // Для показа копии исходного сообщения драфта
        when (event) {
            ConversationEvent.SHOW_THREAD_CREATION -> actionDelegate.threadActionDelegate.showThreadCreation(
                conversationState.selectedMessage!!.uuid,
                recipientSelectionManager?.selectionResult?.data?.allPersonsUuids.orEmpty()
            )
            else -> Unit
        }
    }

    override fun detachView() {
        super.detachView()
        coreConversationInfo.conversationUuid?.let { onThemeClosed(it) }
        actionDelegate.initView(null)
    }

    override fun viewIsStarted() {
        coreConversationInfo.conversationUuid?.let {
            messagesPushManager.executeAction(ThemeSubscribeFromNotification(coreConversationInfo.isChat))
            messagesPushManager.executeAction(UnsubscribeFromNotification(it))
        }
        channelHelper.onLifecycleStateChanged(ChannelConversationDataHelper.LifecycleState.STARTED)
    }

    override fun viewIsStopped() {
        coreConversationInfo.conversationUuid?.let {
            messagesPushManager.executeAction(SubscribeOnNotification(it))
        }
        channelHelper.onLifecycleStateChanged(ChannelConversationDataHelper.LifecycleState.STOPPED)
    }

    override fun viewIsResumed() {
        channelHelper.onLifecycleStateChanged(ChannelConversationDataHelper.LifecycleState.RESUMED)
    }

    override fun viewIsPaused() {
        channelHelper.onLifecycleStateChanged(ChannelConversationDataHelper.LifecycleState.PAUSED)
    }

    override fun onDestroy() {
        super.onDestroy()
        initialLoadingSubscription.dispose()
        markAllMessagesDisposable.dispose()
        serviceMessageNamesDisposable.dispose()
        serviceUnfoldDisposable.dispose()
        phoneVerificationDisposable.dispose()
        interactor.clearReferences()
        cachedRefreshCallbackSubscriptionRequest = null
        mediaPlayer?.release()
        actionDelegate.clear()
    }

    /**
     * Подсветить тред в случае его наличия в списке сообщений.
     */
    private fun tryHighlightThread(threadRootMessageUuid: UUID) {
        if (dataList.find { it.uuid == threadRootMessageUuid } != null) {
            highlightThreadMessage(threadRootMessageUuid)
        }
    }

    override fun startInitialLoading() {
        val docInfo = coreConversationInfo.docInfo
        var recipientsUuids: List<UUID> = coreConversationInfo.recipientsUuids.orEmpty()

        when {
            isVideoConversation -> {
                val currentAccountUUID = loginInterface.getCurrentAccount()?.uuid
                when {
                    recipientsUuids.isNotEmpty() -> {
                        disableProgress()
                        isFirstMessage = true
                    }
                    // Когда у нас есть ссылка на диалог, то он корректно откроется обычным методом,
                    // но только при условии, что у нас в получателях никого...
                    coreConversationInfo.conversationUuid != null -> {
                        val conversationUuid = coreConversationInfo.conversationUuid!!
                        setNewConversationFlag(false)
                        startInitialMessagesLoading(conversationUuid)
                    }
                    // В случае, когда до нас не дошла инфа по получателям и по uuid диалога, мы добавляем в список
                    // получателей самих себя, чтобы createDraftDialogIfNotExists смог получить нужный диалог
                    currentAccountUUID != null -> {
                        recipientsUuids = recipientsUuids + currentAccountUUID
                    }
                    else -> Unit
                }

                // Для видеосовещания возможна ситуация когда conversationUuid == null, но мы получим его здесь
                interactor.createDraftDialogIfNotExists(
                    coreConversationInfo.conversationUuid,
                    docInfo?.documentUuid,
                    docInfo?.documentType,
                    coreConversationInfo.folderUuid,
                    null,
                    recipientsUuids
                )
                    .subscribe({
                        val conversationUuid = it.first.dialog!!.uuid
                        coreConversationInfo.conversationUuid = conversationUuid
                        dataDispatcher.sendConversationEvent(ConversationEvent.DIALOG_CREATED)
                        startInitialMessagesLoading(conversationUuid)
                    }, Timber::e)
                    .storeIn(initialLoadingSubscription)
            }
            isExistingConversation -> {
                setNewConversationFlag(false)
                startInitialMessagesLoading(coreConversationInfo.conversationUuid!!)
            }
            isNewDialog -> {
                disableProgress()
                isFirstMessage = true
                applyMessageListStyle = coreConversationInfo.creationThreadInfo == null
                setNewConversationFlag(true)
                if (recipientsUuids.isEmpty()) {
                    recipientsUuids = recipientSelectionManager?.selectionResult?.data?.allPersonsUuids.orEmpty()
                }
                val recipientsUuidsSingle: Single<List<UUID>> =
                    when {
                        recipientsUuids.isEmpty() -> {
                            recipientSelectionManager!!.getSelectionResultObservable()
                                .firstOrError()
                                .map { it.data.allPersonsUuids }
                        }
                        else -> Single.just(recipientsUuids)
                    }
                recipientsUuidsSingle.flatMap {
                    interactor.createDraftDialogIfNotExists(
                        coreConversationInfo.conversationUuid,
                        docInfo?.documentUuid,
                        docInfo?.documentType,
                        coreConversationInfo.folderUuid,
                        null,
                        it,
                        threadInfo = coreConversationInfo.creationThreadInfo
                    )
                }
                    .doAfterTerminate { loadConversationData() }
                    .subscribe(::handleDraftDialogCreation) { error ->
                        Timber.w(error, "Failed to create draft dialog")
                    }.storeIn(initialLoadingSubscription)
            }
            isNewChat -> {
                if (coreConversationInfo.isPrivateChatCreation) {
                    dataDispatcher.updateConversationState(conversationState.copy(isPrivateChat = true))
                    makePrivateChat()
                }
                disableProgress()
                isFirstMessage = true
                setNewConversationFlag(false)
            }
        }
    }

    private fun startInitialMessagesLoading(conversationUuid: UUID) {
        initCollection(conversationUuid)
        getLoadConversationDataObservable(observeOnMain = false)
            .doOnNext {
                initialConversationData = it
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                ::processInitialConversationLoadingResult,
                ::processConversationDataLoadingError
            ).storeIn(compositeDisposable)
    }

    private fun makePrivateChat() {
        interactor.getPrivateChatUUID(coreConversationInfo.recipientsUuids!![0])
            .subscribe({
                coreConversationInfo.conversationUuid = it
                startInitialMessagesLoading(coreConversationInfo.conversationUuid!!)
                dataDispatcher.run {
                    updateConversationState(conversationState.copy(isNewConversation = false, isPrivateChatCreation = true))
                    sendConversationEvent(ConversationEvent.CHAT_CREATED)
                }
            }, {
                mView?.finishConversationActivityWithCommonError()
                Timber.e(it, "Failed to get private chat uuid.")
            }
            ).storeIn(compositeDisposable)
    }

    /**
     * Попытаться обработать инициализирующие данные по переписке,
     * которые с фонового потока уже записаны в поле.
     * С помощью этой механики первый фрейм после attachView рисуется уже по реальным данным контроллера.
     */
    private fun tryApplyInitialData() {
        if (!isInitialConversationDataLoading) return
        initialConversationData?.also(::processInitialConversationLoadingResult)
    }

    private fun processInitialConversationLoadingResult(dataResult: ConversationDataInteractor.ConversationDataResult) {
        if (!isInitialConversationDataLoading) return
        processConversationDataLoadingResult(dataResult)
    }

    override fun updateDataList(newList: List<ConversationMessage>) {
        if (isInitialConversationDataLoading) {
            // Не устанавливаем сообщения до получения данных о самой переписке.
            initialMessagesResult = newList
            return
        }
        val isNotEmpty = newList.isNotEmpty()
        Trace.beginAsyncSection("MessagesPresenter.updateDataList isNotEmpty $isNotEmpty", 0)
        super.updateDataList(newList)
        Trace.endAsyncSection("MessagesPresenter.updateDataList isNotEmpty $isNotEmpty", 0)
    }

    override fun onAfterUpdateDataList(event: DataChange<ConversationMessage>) {
        super.onAfterUpdateDataList(event)
        dropPrivateChatCreationStateIfNeed()
        tryHighlightRelevantMessage()
    }

    private fun handleDraftDialogCreation(result: Pair<CreateDraftDialogResult, List<UUID>>) {
        val dialogResult = result.first
        val conversationUuid = dialogResult.dialog!!.uuid
        recipientSelectionManager!!.preselect(result.second)
        coreConversationInfo.conversationUuid = conversationUuid

        startInitialMessagesLoading(conversationUuid)

        messagesPushManager.executeAction(UnsubscribeFromNotification(conversationUuid))

        if (dialogResult.threadStartMessageCopyId != null) {
            val threadInfo = coreConversationInfo.creationThreadInfo!!
            val threadServiceObject = threadInfo.getSendServiceObject(dialogResult.threadStartMessageCopyId!!)
            dataDispatcher.updateConversationState(
                conversationState.copy(threadCreationServiceObject = threadServiceObject.toString())
            )
            dataDispatcher.sendConversationEvent(ConversationEvent.THREAD_DRAFT_CREATED)
        } else {
            dataDispatcher.sendConversationEvent(ConversationEvent.DIALOG_CREATED)
        }
    }

    override fun swapDataList(dataList: List<ConversationMessage>) {
        super.swapDataList(dataList)
        updateCloudThreadHelper(dataList)
    }

    private fun tryHighlightRelevantMessage() {
        if (needToHighlightMessageRelevantMessage) {
            val messageIndex = dataList.indexOfFirst { it.uuid == coreConversationInfo.messageUuid }
            val threadMessage = dataList.getOrNull(messageIndex - 1)
                ?.takeIf { it.viewData.type == MessageType.THREAD_MESSAGE }
            if (threadMessage != null) {
                highlightMessage(messageUuid = threadMessage.uuid, scrollPosition = messageIndex)
            } else {
                highlightMessage(messageUuid = coreConversationInfo.messageUuid!!, scrollPosition = -1)
            }
        }
    }

    private fun highlightThreadMessage(rootMessageUuid: UUID) {
        val messageIndex = dataList.indexOfFirst { it.uuid == rootMessageUuid }
        val threadMessage = dataList.getOrNull(messageIndex - 1)
            ?.takeIf { it.viewData.type == MessageType.THREAD_MESSAGE }
        if (threadMessage != null) {
            highlightMessage(messageUuid = threadMessage.uuid, scrollPosition = -1)
        }
    }

    /**
     * Обновить информацию об исходящих тредах для костыльной отрисовки облачков в ширину треда.
     */
    private fun updateCloudThreadHelper(dataList: List<ConversationMessage>) {
        dataList.forEachIndexed { index, conversationMessage ->
            val threadData = conversationMessage.viewData.getThreadData()
            if (threadData != null && threadData.isOutgoing) {
                val prevMessage = dataList.getOrNull(index + 1)
                if (prevMessage?.message != null && prevMessage.isOutgoing()) {
                    CloudThreadHelper.addOutcomeThread(prevMessage.uuid, threadData)
                }
            }
        }
    }

    /**
     * Блокировка прогресса в пустой не новой переписке, заглушка при этом не отображается -
     * скрывается прогресс и отображается пустой список.
     * Легальные сценарии: переписка по видеозвонку, создание личного канала,
     * повторное открытие обсуждения по документу.
     */
    private fun disableProgressForEmptyConversation(metaData: HashMap<String, String>?) {
        metaData?.let {
            if (dataList.isEmpty()
                && MessagesEvent.HAS_NOT_NEWER.isExistsIn(it)
                && MessagesEvent.HAS_NOT_OLDER.isExistsIn(it)
            ) {
                disableProgress()
                mView?.setMessagesListStyle(true)
            }
        }
    }

    /**
     * Разблакировка прогресса после первого сообщения в новом диалоге.
     */
    private fun enableProgressAfterFirstMessage() {
        if (isFirstMessage && dataList.isNotEmpty()) {
            isFirstMessage = false
            enableProgress()
        }
    }

    /**
     * Сбросить состояние создания нового приватного чата, если появились сообщения по чату
     */
    private fun dropPrivateChatCreationStateIfNeed() {
        if (conversationState.isPrivateChatCreation && dataList.isNotEmpty()) {
            dataDispatcher.updateConversationState(conversationState.copy(isPrivateChatCreation = false))
        }
    }

    override fun updateMessagesListStyle(isNewConversationStyle: Boolean) {
        super.updateMessagesListStyle(conversationState.isNewConversation || conversationState.isPrivateChatCreation)
    }

    /**
     * Обработка параметров пришедших от контроллера в [onRefreshCallback]
     */
    override fun handleCallbackParams(params: HashMap<String, String>) {
        MetricsDispatcher.stopTrace(MetricsType.FIREBASE_SEND_AND_GET_NEW_MESSAGE)
        super.handleCallbackParams(params)
    }

    /**
     * Прикладная подписка на изменения [ConversationData] по шине [ConversationDataDispatcher],
     * подиску на [ConversationEvent], [ConversationState] и на редактируемое сообщение
     * смотреть в [BaseConversationMessagesPresenter.subscribeOnDataDispatch]
     */
    override fun handleConversationDataChanges(conversationData: ConversationData) {
        super.handleConversationDataChanges(conversationData)
        document = conversationData.document
        //нужно обновить список сообщений, если поменялся тип переписки (личная/групповая)
        if (collectionFilter.isGroupConversation != conversationData.isGroupConversation) {
            checkGroupConversationFilterState(conversationData.isGroupConversation)
        }
        mView?.setFastScrollDownUnreadCounterValue(unreadCounterValue)

        channelHelper.isChannel = coreConversationInfo.isChat
        if (coreConversationInfo.isChat) {
            pinnedChatMessage = conversationData.pinnedChatMessage
            canUnpinChatMessage = conversationData.canUnpinChatMessage
            mView?.showPinnedChatMessage(pinnedChatMessage, canUnpinChatMessage)

            conversationData.conversationAccess.chatPermissions?.let {
                isChannelAdminPermissions = it.canChangeAdministrators
            }
            val isPrivateChat = conversationState.isPrivateChat
            if (isPrivateChat != conversationData.isPrivateChat) {
                dataDispatcher.updateConversationState(
                    conversationState.copy(isPrivateChat = conversationData.isPrivateChat)
                )
            }
        }
    }

    private fun setNewConversationFlag(newConversation: Boolean) {
        dataDispatcher.updateConversationState(conversationState.copy(isNewConversation = newConversation))
        dataDispatcher.sendConversationEvent(ConversationEvent.UPDATE_VIEW)
    }

    override fun getLoadConversationDataObservable(
        needCloudSync: Boolean,
        observeOnMain: Boolean
    ): Observable<ConversationDataInteractor.ConversationDataResult> {
        val func = if (observeOnMain) {
            interactor::loadConversationData
        } else {
            interactor::backgroundLoadConversationData
        }
        return func.invoke(
            coreConversationInfo.conversationUuid,
            coreConversationInfo.getDocumentUUID(),
            null,
            defaultViewPostSize,
            coreConversationInfo.isChat,
            coreConversationInfo.conversationType == ConversationType.CONSULTATION
        )
    }

    override fun processConversationDataLoadingResult(conversationDataResult: ConversationDataInteractor.ConversationDataResult) {
        Trace.beginAsyncSection("MessagesPresenter.processConversationDataLoadingResult", 0)
        conversationDataResult.conversationData.also {
            if (isInitialConversationDataLoading) {
                checkGroupConversationFilterState(it.isGroupConversation)
            }
            unreadCounterValue = it.unreadCount
            isClosedChat = it.isClosedChat == true
            isLocked = it.isLocked == true

            when (conversationDataResult.commandStatus.errorCode) {
                ErrorCode.SUCCESS -> {
                    enableProgress()
                    Trace.beginAsyncSection("MessagesPresenter.handleConversationData", 0)
                    dataDispatcher.updateData(it)
                    Trace.endAsyncSection("MessagesPresenter.handleConversationData", 0)
                    viewModel?.isLocked?.onNext(it.isLocked == true)
                }
                ErrorCode.NOT_AVAILABLE -> {
                    dataDispatcher.updateData(it)
                    handleConversationDataLoadingStatusError(conversationDataResult.commandStatus.errorMessage)
                    dataDispatcher.sendConversationEvent(ConversationEvent.BLOCK_MESSAGE_SENDING)
                }
                ErrorCode.DIALOG_REMOVED -> {
                    mView?.notifyDialogRemoved()
                }
                else -> Unit
            }
        }
        val tryApplyInitialList = isInitialConversationDataLoading
        isInitialConversationDataLoading = false
        if (tryApplyInitialList) {
            initialMessagesResult?.also(::updateDataList)
        }
        Trace.endAsyncSection("MessagesPresenter.processConversationDataLoadingResult", 0)
    }

    private fun handleConversationDataLoadingStatusError(errorMessage: String) {
        mView?.apply {
            showControllerErrorMessage(errorMessage)
        } ?: run {
            dataDispatcher.updateConversationState(conversationState.copy(missedLoadingErrorFromController = errorMessage))
        }
    }

    override fun processConversationDataLoadingError(throwable: Throwable) {
        Timber.e(throwable)
        mView?.apply {
            showStubView(R.string.communicator_conversation_data_loading_error)
        } ?: run {
            dataDispatcher.updateConversationState(conversationState.copy(missedLoadingErrorRes = R.string.communicator_conversation_data_loading_error))
        }
    }

    override fun onChatCreatedFromDialog() {
        coreConversationInfo.isChat = true
        dataDispatcher.run {
            updateConversationState(conversationState.copy(isNewConversation = false))
            sendConversationEvent(ConversationEvent.CHAT_CREATED)
        }
    }

    override fun enableProgress() {
        if (!isFirstMessage) super.enableProgress()
    }

    override fun setRouter(router: ConversationRouter?) {
        this.router = router
        actionDelegate.initRouter(router)
    }

    override fun removeMessageFromList(messageUuid: UUID) {
        super.removeMessageFromList(messageUuid)

        val currentPlaying = mediaPlayer?.getMediaInfo()?.mediaSource?.uuid
        if (messageUuid == currentPlaying) {
            mediaPlayer?.stop()
        }
    }

    private fun showMessageInfo(message: Message) {
        router?.showMessageInformationScreen(
            coreConversationInfo.conversationUuid!!,
            message.uuid,
            collectionFilter.isGroupConversation,
            coreConversationInfo.isChat
        )
    }

    private fun pinChatMessage(message: Message) {
        interactor.pinMessage(coreConversationInfo.conversationUuid!!, message.uuid)
            .subscribe()
            .storeIn(compositeDisposable)
    }

    override fun unpinChatMessage() {
        interactor.unpinMessage(coreConversationInfo.conversationUuid!!)
            .subscribe()
            .storeIn(compositeDisposable)
    }

    override fun onFastScrollDownPressed() {
        val currentUnreadCount = unreadCounterValue
        super.onFastScrollDownPressed()
        if (currentUnreadCount > 0 && coreConversationInfo.viewMode == ConversationViewMode.FULL) {
            interactor.markAllMessages(coreConversationInfo.conversationUuid!!)
                .subscribe(
                    { unreadCounterValue = 0 },
                    { exception -> Timber.w(exception, "Failed to mark all messages as read") }
                )
                .storeIn(markAllMessagesDisposable)
        }
    }

    override fun onQuoteOrPinnedMessageLongClicked(messageUuid: UUID) {
        messageForPreview = messageUuid
        router?.showConversationPreview(
            ConversationFromRegistryParams(
                conversationUuid = conversationUuid!!,
                messageUuid = messageUuid,
                isChat = coreConversationInfo.isChat,
                conversationViewMode = ConversationViewMode.PREVIEW
            ),
            getPreviewMessageActionsList(canUnpinChatMessage)
        )
    }

    override fun displayViewState(view: ConversationMessagesContract.View) {
        super.displayViewState(view)
        if (!conversationAccess.isAvailable) {
            view.showStubView(R.string.communicator_conversation_not_available)
            return
        }
        if (coreConversationInfo.isChat) {view
            view.showPinnedChatMessage(pinnedChatMessage, canUnpinChatMessage)
        }
    }

    override fun getMessageActionsList(conversationMessage: ConversationMessage): List<MessageAction> =
        when {
            isClosedChat || isLocked || conversationMessage.message == null -> emptyList()
            conversationState.audioRecordState.isSendPreparing -> getAudioRecordMessageActionsList(
                conversationMessage.message,
                coreConversationInfo.isChat,
                conversationAccess.chatPermissions
            )
            else -> getDefaultMessageActionsList(
                message = conversationMessage.message,
                complainEnabled = CommunicatorSbisConversationPlugin.customizationOptions.complainEnabled,
                isChat = coreConversationInfo.isChat,
                isMessagePinned = Objects.equals(conversationMessage.uuid, pinnedChatMessage?.uuid),
                permissions = conversationAccess.chatPermissions
            )
        }

    /**@SelfDocumented*/
    fun currentActionIsEdit(option: Int): Boolean {
        return if (actionsList == null) {
            false
        } else {
            actionsList!![option] == MessageAction.EDIT
        }
    }

    /**@SelfDocumented*/
    fun currentActionIsQuote(option: Int): Boolean {
        return if (actionsList == null) {
            false
        } else {
            actionsList!![option] == MessageAction.QUOTE
        }
    }

    /**@SelfDocumented*/
    fun invokeForSelectedMessage(function: ((UUID) -> Unit)?) {
        function?.invoke(conversationState.selectedMessage?.uuid!!)
    }

    /**@SelfDocumented*/
    fun invokeForSelectedMessage(function: ((themeUuid: UUID, messageUuid: UUID, countersUuid: UUID, showKeyboard: Boolean) -> Unit)?) {
        function?.invoke(
            coreConversationInfo.conversationUuid!!,
            conversationState.selectedMessage?.uuid!!,
            UUIDUtils.NIL_UUID,
            true
        )
    }

    override fun performMessageAction(action: MessageAction) {
        when (action) {
            MessageAction.EDIT -> {
                dataDispatcher.sendConversationEvent(ConversationEvent.EDIT_MESSAGE)
            }
            MessageAction.FORCE_RESEND -> forceResendMessage()
            MessageAction.DELETE -> deleteMessage()
            MessageAction.COPY -> copySelectedMessageTextToClipboard()
            MessageAction.THREAD -> {
                actionDelegate.threadActionDelegate.showThreadCreation(requireNotNull(conversationState.selectedMessage?.message))
            }
            MessageAction.QUOTE -> {
                dataDispatcher.sendConversationEvent(ConversationEvent.QUOTE_MESSAGE)
            }
            MessageAction.INFO -> showMessageInfo(conversationState.selectedMessage!!.message!!)
            MessageAction.PIN -> pinChatMessage(conversationState.selectedMessage!!.message!!)
            MessageAction.UNPIN -> unpinChatMessage()
            MessageAction.REPORT -> {
                mView?.showComplainDialogFragment(
                    ComplainUseCase.ConversationMessage(
                        conversationState.selectedMessage!!.message!!.uuid,
                        coreConversationInfo.conversationUuid!!,
                        coreConversationInfo.isChat
                    )
                )
            }
            else -> Unit
        }
    }

    override fun onScrollStateChanged(state: Int) {
        if (isAudioRecordVisible) return
        super.onScrollStateChanged(state)
    }

    /** Цитирование сообщения, если ведется запись аудио, то нужно будет его завершить */
    override fun onMessageQuotedBySwipe(message: ConversationMessage) {
        when {
            conversationState.audioRecordState.isRecording -> {
                mView?.showCancelRecordingConfirmationDialog()
            }
            conversationState.audioRecordState.isSendPreparing -> {
                notifyMessageSelected(message)
                dataDispatcher.sendConversationEvent(ConversationEvent.QUOTE_MESSAGE)
            }
            else -> {
                if (lastKeyboardHeight == 0) {
                    isKeyboardWaiting = true
                }
                mView?.onMessageQuoted()
                notifyMessageSelected(message)
                dataDispatcher.sendConversationEvent(ConversationEvent.QUOTE_MESSAGE)
            }
        }
    }

    override fun onMessageSelected(conversationMessage: ConversationMessage) {
        if (coreConversationInfo.viewMode != ConversationViewMode.FULL) return
        if (conversationState.audioRecordState.isRecording) {
            mView?.showCancelRecordingConfirmationDialog()
        } else {
            super.onMessageSelected(conversationMessage)
        }
    }

    override fun onMessageErrorStatusClicked(conversationMessage: ConversationMessage) {
        if (conversationState.audioRecordState.isRecording) {
            mView?.showCancelRecordingConfirmationDialog()
        } else {
            onMessageErrorStatusClickedInternal(conversationMessage)
        }
    }

    override fun onMessagePanelHeightChanged(difference: Int, isFirstLayout: Boolean) {
        mView?.changeListViewBottomPadding(
            difference = difference,
            withScroll = !isFirstLayout,
            addWithKeyboard = isKeyboardWaiting
        )
    }

    private fun deleteMessage() {
        val removableType = conversationState.selectedMessage!!.removableType ?: return
        when (removableType) {
            MessageRemovableType.REMOVABLE -> deleteMessageForAll()
            MessageRemovableType.REMOVABLE_PERMANENTLY -> {
                if (isChannelAdminPermissions) mView?.showPopupDeleteMessageForAll()
                else mView?.showPopupDeleteMessageForMe()
            }
            MessageRemovableType.REMOVABLE_PERMANENTLY_FOR_ALL -> mView?.showPopupDeleteMessageForAll()
            else -> Timber.e("Unexpected type $removableType")
        }
    }

    @SuppressLint("DefaultLocale")
    override fun onMessageAttachmentClicked(message: Message, attachment: AttachmentViewModel) {
        if (!isAudioRecordVisible) {
            mView?.forceHideKeyboard()
            val selectedFileInfo: FileInfoViewModel = attachment.fileInfoViewModel
            if (selectedFileInfo.isFolder) {
                val folderId: UUID = selectedFileInfo.id
                router?.showFolder(
                    DefAttachmentListComponentConfig(
                        AllowedActionResolver.AlwaysTrue(),
                        DefAttachmentListParams(
                            DefAttachmentListEntity.LocalFolder(
                                catalogId = message.uuid,
                                cloudObjectId = null,
                                id = folderId,
                                localId = selectedFileInfo.attachId,
                                localRedactionId = selectedFileInfo.redId,
                                blObjectName = UrlUtils.FILE_SD_OBJECT,
                                name = selectedFileInfo.title,
                            )
                        )
                    )
                )

            } else {
                router?.showViewerSlider(
                    MessageUtils.createViewerSliderArgs(
                        coreConversationInfo.conversationUuid!!,
                        message,
                        attachment,
                        CommunicatorSbisConversationPlugin.singletonComponent.dependency.analyticsUtilProvider?.getAnalyticsUtil()
                            ?.castTo()
                    )
                )
            }
        } else {
            mView?.showCancelRecordingConfirmationDialog()
        }
    }

    override fun onAcceptSigningButtonClicked(data: ConversationMessage) {
        if (!isAudioRecordVisible) {
            dataList.find { it.uuid == data.uuid }?.message?.also {
                messageToSign = it
                actionDelegate.singAndAcceptHelper.signMessage(messageToSign)
            }
        } else {
            mView?.showCancelRecordingConfirmationDialog()
        }
    }

    override fun onRejectSigningButtonClicked(data: ConversationMessage) {
        actionDelegate.singAndAcceptHelper.onRejectSigningButtonClicked(data)
    }

    override fun onViewerSliderClosed() {
        // Вынужденная мера, контроллер сообщений никогда не сможет знать о новых подписях на уже отправленных файлах
        onRefresh()
    }

    override fun showVerificationPhoneDialog() {
        super.showVerificationPhoneDialog()
        onPhoneVerificationRequired(null)
    }

    override fun onConfirmationDialogButtonClicked(tag: String?, id: String) {
        when (tag) {
            PHONE_VERIFICATION_CONFIRMATION_DIALOG_TAG -> {
                if (id == ConfirmationButtonId.YES.toString()) {
                    subscribeOnPhoneSuccessVerification()
                    router?.showPhoneVerification()
                }
            }
            else -> super<BaseConversationMessagesPresenter>.onConfirmationDialogButtonClicked(tag, id)
        }
    }

    private fun subscribeOnPhoneSuccessVerification() {
        viewModel!!.phoneVerification
            .subscribe {
                selectUndeliveredMessage()
                conversationState.selectedMessage?.let {
                    forceResendMessage()
                }
            }
            .storeIn(phoneVerificationDisposable)
    }

    /**
     * Запомнить модель неотправленного сообщения для повторной отправки
     */
    private fun selectUndeliveredMessage() {
        conversationState.selectedMessage = dataList.firstOrNull { message ->
            message.uuid == conversationState.resendMessageUuid
        }
    }

    override fun handleUndeliveredMessageUuid(messageUuid: UUID) {
        conversationState.resendMessageUuid = messageUuid
    }

    override fun onMessageResent(messageUuid: UUID, status: CommandStatus) {
        super.onMessageResent(messageUuid, status)
        conversationState.resendMessageUuid = null
    }

    override fun onLinkClicked() {
        mView?.forceHideKeyboard()
    }

    override fun onPhotoClicked(senderUuid: UUID) {
        if (!isAudioRecordVisible) {
            mView?.forceHideKeyboard()
            router?.showProfile(senderUuid)
        } else {
            mView?.showCancelRecordingConfirmationDialog()
        }
    }

    override fun onSenderNameClicked(senderUuid: UUID) {
        if (!isAudioRecordVisible) {
            dataDispatcher.addRecipient(senderUuid)
        } else {
            mView?.showCancelRecordingConfirmationDialog()
        }
    }

    override fun updateStubContent(stubContent: StubViewContent) {
        viewModel?.currentStub?.onNext(stubContent)
    }

    override fun updateStubVisibility(isVisible: Boolean) {
        viewModel?.showStub?.onNext(isVisible)
    }

    /** обработка сервисного сообщения */
    override fun onServiceMessageClicked(position: Int) {
        val conversationServiceMessage = dataList[position].conversationServiceMessage
            ?: return

        if (conversationServiceMessage.isServiceGroup()) {
            onGroupedServiceMessageClicked(conversationServiceMessage)
        } else {
            onSingleServiceMessageClicked(conversationServiceMessage)
        }
    }

    private fun onGroupedServiceMessageClicked(message: ConversationServiceMessage) {
        if (!message.read) readServiceMessageGroup(message)
        message.expandServiceGroupAction.invoke()
    }

    private fun onSingleServiceMessageClicked(message: ConversationServiceMessage) {
        when (message.serviceMessage!!.type) {
            ServiceType.CHAT_CLOSED -> {
                interactor.restoreChat(
                    coreConversationInfo.conversationUuid!!
                )
                    .subscribe(
                        { loadConversationData() },
                        { Timber.e(it, "Error trying to restore chat") }
                    ).storeIn(compositeDisposable)
            }

            ServiceType.ADDED_CHAT_PARTICIPANTS,
            ServiceType.REMOVED_CHAT_PARTICIPANTS,
            ServiceType.ADDED_CHAT_ADMINS,
            ServiceType.REMOVED_CHAT_ADMINS -> {
                message.serviceMessage.personList?.let { personList ->
                    val countToAdd = if (personList.foldedCount < MAX_PARTICIPANTS_COUNT_TO_OPEN_ON_MORE_CLICK) {
                        personList.foldedCount
                    } else {
                        MAX_PARTICIPANTS_COUNT_TO_OPEN_ON_MORE_CLICK
                    }
                    if (countToAdd > 0 && personList.unfoldedCount < MAX_PARTICIPANTS_COUNT_TO_OPEN_ON_MORE_CLICK) {
                        val personListNewCount = personList.unfoldedCount + countToAdd
                        serviceMessageNamesDisposable.set(
                            interactor.loadServiceMessageNames(
                                message.uuid,
                                personListNewCount
                            )
                                .subscribe { message ->
                                    val replacePosition =
                                        findMessageIndexInListByUuid(message.conversationServiceMessage!!.uuid)
                                    if (replacePosition >= 0) {
                                        mView?.notifyItemsChanged(replacePosition, 1)
                                    }
                                }
                        )
                    }

                }

            }
            else -> { /*ignore other types*/ }
        }
    }

    private fun readServiceMessageGroup(serviceMessage: ConversationServiceMessage) {
        interactor.markGroupServiceMessageAsRead(conversationUuid!!, listOfNotNull(serviceMessage.serviceMessageGroup))
            .ignoreElements()
            .subscribe()
            .storeIn(compositeDisposable)
    }

    // TODO("Удалить в других мнестах использования")
    override fun updateList() = Unit

    override fun showConversationMembers() {
        mView?.showConversationMembers()
    }

    override fun onParticipantsScreenClosed() {
        if (coreConversationInfo.isChat) {
            loadConversationData()
        }
    }

    override fun onKeyboardAppears(keyboardHeight: Int) {
        lastKeyboardHeight = keyboardHeight
        if (keyboardHeight > 0 && isKeyboardWaiting) {
            isKeyboardWaiting = false
        }
    }

    override fun onKeyboardDisappears(keyboardHeight: Int) {
        lastKeyboardHeight = 0
        isKeyboardWaiting = false
    }

    override fun close() {
        router?.exit()
    }

    override fun handleConversationStateChanges(currentState: ConversationState?, newState: ConversationState) {
        super.handleConversationStateChanges(currentState, newState)
        if (currentState?.audioRecordState?.isVisible != newState.audioRecordState.isVisible) {
            CommunicatorSbisConversationPlugin.singletonComponent.messageDecoratedLinkOpener?.checkLinkAvailability =
                if (newState.audioRecordState.isVisible) ::checkLinkAvailability else null
        }
    }

    override fun handleConversationPreviewAction(conversationPreviewMenuAction: MessageConversationPreviewMenuAction) {
        when (conversationPreviewMenuAction) {
            is Go -> messageForPreview?.let { onQuoteClicked(it) }
            is Unpin -> {
                unpinChatMessage()
            }
            is Copy -> copyMessageTextForPreview()
            else -> Unit
        }
        messageForPreview = null
    }

    override fun prepareSearchMode(): ThemeMessageSearchApi {
        return router?.prepareSearchMode(conversationUuid!!)!!
    }

    private fun copyMessageTextForPreview() {
        messageForPreview?.let { messageUuid ->
            interactor.getMessageText(conversationUuid!!, messageUuid)
                .subscribe(
                    { copySelectedMessageTextToClipboard(it.messageText) },
                    { mView?.showToast(R.string.communicator_message_copied_error) }
                ).storeIn(compositeDisposable)
        }
    }

    private fun checkLinkAvailability(): Boolean =
        if (isAudioRecordVisible) {
            mView?.showCancelRecordingConfirmationDialog()
            false
        } else {
            true
        }

    private suspend fun subscribeOnAppForegroundEvents() {
        AppLifecycleTracker.appForegroundStateFlow
            // Интересует переход из background в foreground
            .runningReduce { prev, current -> !prev && current }
            .drop(1)
            .filter { it }
            .collectLatest {
                onThemeAfterOpenedWithCurrentFilter()
            }
    }

    override fun getRefreshCallbackSubscription(): Observable<Subscription> {
        return cachedRefreshCallbackSubscriptionRequest ?:
        super.getRefreshCallbackSubscription().cache().also {
            cachedRefreshCallbackSubscriptionRequest = it
        }
    }

    companion object {
        private const val MAX_PARTICIPANTS_COUNT_TO_OPEN_ON_MORE_CLICK = 10
        const val PHONE_VERIFICATION_CONFIRMATION_DIALOG_TAG = "PHONE_VERIFICATION_CONFIRMATION_DIALOG_TAG"
    }

    /**
     * Вспомогательный класс для намеренной подстраховки контроллера в обновлении информации о канале.
     * В частности хромают события обновления в сценарии добавления участников в канал.
     * Прокидывание шин событий на другую активити дорогостоящие доработки по трудозатратам и для архитектуры,
     * поэтому принято решение обновлять информацию о переписке из кэша, когда экран сообщений выходит из состояния Paused,
     * что релевантно для открытия любых активити поверх экрана переписки.
     *
     * @property updateConversationData метод для обновления данных по переписке.
     */
    private class ChannelConversationDataHelper(
        private val updateConversationData: () -> Unit
    ) {

        enum class LifecycleState {
            STARTED,
            RESUMED,
            PAUSED,
            STOPPED
        }

        private var needUpdateConversationData: Boolean = false

        /**@SelfDocumented*/
        var isChannel: Boolean = false

        /**@SelfDocumented*/
        fun onLifecycleStateChanged(state: LifecycleState) {
            if (!isChannel) return
            needUpdateConversationData = when (state) {
                LifecycleState.PAUSED -> true
                LifecycleState.RESUMED -> {
                    if (needUpdateConversationData) {
                        updateConversationData()
                    }
                    false
                }
                else -> false
            }
        }
    }
}