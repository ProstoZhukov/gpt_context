package ru.tensor.sbis.communicator.crm.conversation.presentation.presenter.message

import CommunicatorPushKeyboardHelper
import android.text.SpannableString
import androidx.fragment.app.Fragment
import io.reactivex.Observable
import io.reactivex.internal.functions.Functions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.runningReduce
import kotlinx.coroutines.launch
import ru.tensor.sbis.common.generated.ErrorCode
import ru.tensor.sbis.common.lifecycle.AppLifecycleTracker
import ru.tensor.sbis.common.util.ClipboardManager
import ru.tensor.sbis.common.util.UUIDUtils.NIL_UUID
import ru.tensor.sbis.common.util.storeIn
import ru.tensor.sbis.communication_decl.complain.data.ComplainUseCase
import ru.tensor.sbis.communication_decl.crm.CRMConsultationCase
import ru.tensor.sbis.communication_decl.crm.CRMConsultationCreationParams
import ru.tensor.sbis.communication_decl.crm.CRMConsultationParams
import ru.tensor.sbis.communicator.base.conversation.presentation.presenter.MessageCollectionFilter
import ru.tensor.sbis.communicator.base.conversation.presentation.crud.ConversationListComponent
import ru.tensor.sbis.communicator.base.conversation.presentation.presenter.messages.BaseConversationMessagesPresenter
import ru.tensor.sbis.communicator.base.conversation.data.model.MessageAction
import ru.tensor.sbis.communicator.base.conversation.data.model.getCRMMessageActionsList
import ru.tensor.sbis.communicator.base.conversation.presentation.presenter.dispatcher.ConversationEvent
import ru.tensor.sbis.communicator.common.analytics.CRMChatWorkEvent.OpenHistoryView
import ru.tensor.sbis.communicator.common.analytics.QuickGreeting
import ru.tensor.sbis.communicator.common.conversation.data.Message
import ru.tensor.sbis.communicator.common.push.MessagesPushManager
import ru.tensor.sbis.communicator.common.push.SubscribeOnNotification
import ru.tensor.sbis.communicator.common.push.UnsubscribeFromNotification
import ru.tensor.sbis.communicator.common.util.castTo
import ru.tensor.sbis.communicator.core.data.events.MessagesEvent
import ru.tensor.sbis.communicator.core.utils.MessageUtils
import ru.tensor.sbis.communicator.crm.conversation.CRMConversationPlugin
import ru.tensor.sbis.communicator.crm.conversation.CRMConversationPlugin.crmConversationDependency
import ru.tensor.sbis.communicator.crm.conversation.presentation.ui.CRMMessageCollectionFilter
import ru.tensor.sbis.communicator.crm.conversation.data.CRMConversationData
import ru.tensor.sbis.communicator.crm.conversation.data.CRMCoreConversationInfo
import ru.tensor.sbis.communicator.crm.conversation.data.model.CRMConversationMessage
import ru.tensor.sbis.communicator.crm.conversation.data.model.CRMServiceMessage
import ru.tensor.sbis.communicator.crm.conversation.interactor.contract.CRMConversationInteractor
import ru.tensor.sbis.communicator.crm.conversation.interactor.contract.CRMConversationInteractor.CRMConversationDataResult
import ru.tensor.sbis.communicator.crm.conversation.presentation.presenter.dispatcher.CRMConversationDataDispatcher
import ru.tensor.sbis.communicator.crm.conversation.presentation.presenter.dispatcher.CRMConversationState
import ru.tensor.sbis.communicator.crm.conversation.presentation.ui.viewmodel.CRMConversationViewModel
import ru.tensor.sbis.communicator.crm.conversation.router.CRMConversationRouter
import ru.tensor.sbis.communicator.crm.conversation.utils.toConsultationChatType
import ru.tensor.sbis.communicator.generated.AttachmentViewModel
import ru.tensor.sbis.communicator.generated.DataRefreshedMessageControllerCallback
import ru.tensor.sbis.communicator.generated.MessageFilter
import ru.tensor.sbis.communicator.generated.ServiceType
import ru.tensor.sbis.consultations.generated.ConsultationActionsFlags
import ru.tensor.sbis.consultations.generated.SourceViewModel
import ru.tensor.sbis.crud4.view.datachange.DataChange
import ru.tensor.sbis.crud4.view.datachange.SetItems
import ru.tensor.sbis.design.message_view.content.crm_views.greetings_view.GreetingsViewDataUtil
import ru.tensor.sbis.design.message_view.content.crm_views.rate_view.ConsultationRateType
import ru.tensor.sbis.design.message_view.model.MessageType
import ru.tensor.sbis.design.stubview.StubViewContent
import ru.tensor.sbis.localfeaturetoggle.domain.LocalFeatureToggleService
import timber.log.Timber
import java.util.UUID
import ru.tensor.sbis.communicator.design.R as RCommunicatorDesign

/**
 * Реализация делегата презентера по секции сообщений чата CRM.
 *
 * @author da.zhukov
 */
internal class CRMConversationMessagesPresenter(
    collectionComponent: ConversationListComponent<CRMConversationMessage>,
    interactor: CRMConversationInteractor,
    coreConversationInfo: CRMCoreConversationInfo,
    dataDispatcher: CRMConversationDataDispatcher,
    clipboardManager: ClipboardManager,
    collectionFilter: MessageCollectionFilter,
    private val router: CRMConversationRouter,
    private val messagesPushManager: MessagesPushManager,
    appLifecycleTracker: AppLifecycleTracker,
    private val viewModel: CRMConversationViewModel,
    private val featureToggleService: LocalFeatureToggleService,
    communicatorPushKeyboardHelperProvider: CommunicatorPushKeyboardHelper,
) : BaseConversationMessagesPresenter<
        CRMConversationMessagesView, CRMConversationInteractor, CRMConversationMessage,
        MessageFilter, CRMConversationState, CRMConversationData,
        CRMConversationDataResult, CRMCoreConversationInfo, CRMConversationDataDispatcher, DataRefreshedMessageControllerCallback
        >(
    collectionComponent,
    interactor,
    coreConversationInfo,
    dataDispatcher,
    clipboardManager,
    collectionFilter,
    communicatorPushKeyboardHelperProvider
),
    CRMConversationMessagesPresenterContract<CRMConversationMessagesView> {

    private val scope = CoroutineScope(Dispatchers.Default)
    private val analyticsUtil = crmConversationDependency?.analyticsUtilProvider?.getAnalyticsUtil()

    private var source: SourceViewModel? = null

    private val canTakeChat: Boolean
        get() = coreConversationInfo.allowedMenuOptions?.contains(ConsultationActionsFlags.CAN_TAKE) == true
    private var isGreetingSent: Boolean = false

    init {
        collectionFilter.castTo<CRMMessageCollectionFilter>()!!
            .setIsOperatorCase(coreConversationInfo.crmConsultationCase is CRMConsultationCase.Operator)

        startInitialLoading()
        scope.launch {
            subscribeOnAppForegroundEvents(appLifecycleTracker)
        }
        initConsultationServiceSubscription()
    }

    private var isCompleteChat: Boolean = initialConversationData?.conversationData?.isCompletedChat == true

    override val isConsultationCompleted: Boolean
        get() = coreConversationInfo.isCompleted

    override suspend fun getGreetings() {
        coreConversationInfo.conversationUuid?.let {
            viewModel.greetingsSubject.onNext(interactor.getGreetings(it))
        }
    }

    private fun initConsultationServiceSubscription() {
        interactor.subscribeConsultationChangedCallback()
            .subscribe(::handleConsultationChanged)
            .storeIn(compositeDisposable)
    }

    private fun handleConsultationChanged(chatId: UUID) {
        if (coreConversationInfo.conversationUuid == chatId) {
            loadConversationData()
        }
    }

    override fun viewIsStarted() = Unit

    override fun viewIsStopped() = Unit

    override fun viewIsResumed() {
        changePushSubscription(needSubscription = false)
    }

    override fun viewIsPaused() {
        changePushSubscription(needSubscription = true)
    }

    override fun attachView(view: CRMConversationMessagesView) {
        super.attachView(view)
        router.init(mView as Fragment)
    }

    override fun detachView() {
        super.detachView()
        coreConversationInfo.conversationUuid?.let { onThemeClosed(it) }
    }

    override fun onDestroy() {
        super.onDestroy()
        router.detachRouter()
        scope.cancel()
    }

    override fun startInitialLoading() {
        val conversationUuid = coreConversationInfo.conversationUuid
        if (conversationUuid != null) {
            setNewConversationFlag(false)
            startInitialMessagesLoading(conversationUuid)
        } else {
            disableProgress()
            setNewConversationFlag(true)
            createConsultation()
        }
        mView?.let(::displayViewState)
    }

    private fun setNewConversationFlag(newConversation: Boolean) {
        dataDispatcher.updateConversationState(conversationState.copy(isNewConversation = newConversation))
        dataDispatcher.sendConversationEvent(ConversationEvent.UPDATE_VIEW)
    }

    private fun startInitialMessagesLoading(conversationUuid: UUID) {
        initCollection(conversationUuid)
        coreConversationInfo.conversationUuid = conversationUuid
        // Здесь при старте нужно сделать синк, чтобы после этого контроллер мог предоставить верную
        // информацию об allowedActions у консультации.
        getLoadConversationDataObservable(true)
            .doOnNext {
                initialConversationData = it
            }
            .doAfterTerminate {
                isInitialConversationDataLoading = false
            }.subscribe(
                ::processInitialConversationLoadingResult,
                ::processConversationDataLoadingError
            )
            .storeIn(compositeDisposable)
    }

    private fun processInitialConversationLoadingResult(dataResult: CRMConversationDataResult) {
        if (!isInitialConversationDataLoading) return
        processConversationDataLoadingResult(dataResult)
        coreConversationInfo.allowedMenuOptions = dataResult.conversationData.allowedActions
        if (coreConversationInfo.allowedMenuOptions?.contains(ConsultationActionsFlags.CAN_VIEW) == false) {
            mView?.showCantViewStub()
        }
        initialMessagesResult?.also(::updateDataList)
        if (dataResult.conversationData.isDraft) {
            setNewConversationFlag(true)
            disableProgress()
        }
    }

    override fun scrollBottomOnAllItemsChanged(event: SetItems<CRMConversationMessage>) {
        if (!canTakeChat) {
            super.scrollBottomOnAllItemsChanged(event)
        }
    }

    override fun initialScrollToRelevantMessage() {
        when {
            !canTakeChat -> {
                super.initialScrollToRelevantMessage()
            }
            isContainsGreetingsMessage(dataList) && dataList.size > 1 -> {
                mView?.setRelevantMessagePosition(1)
            }
            else -> {
                mView?.setRelevantMessagePosition(0)
            }
        }
    }

    override fun insertGreetingsButtonsInMessageList(withNotify: Boolean) {
        (isContainsGreetingsMessage(dataList) || isGreetingSent) && return

        val newList = dataList.toMutableList()
        newList.add(
            0,
            getGreetingsMessage(newList.firstOrNull()),
        )
        swapDataList(newList)
        mView?.setRelevantMessagePosition(1)
        if (withNotify) {
            mView?.updateDataList(newList, 0)
        }
    }

    private fun removeGreetingsButtons() {
        if (isContainsGreetingsMessage(dataList)) {
            val newList = dataList.toMutableList()
            newList.removeAll { it.viewData.type == MessageType.GREETINGS_BUTTONS }
            swapDataList(newList)
            mView?.updateDataList(newList, 0)
        }
    }

    private fun restoreGreetingsButtonsInMessageList(prevDataList: List<CRMConversationMessage>) {
        !canTakeChat && return

        val isPrevListContains = isContainsGreetingsMessage(prevDataList)
        if (isPrevListContains) {
            insertGreetingsButtonsInMessageList(withNotify = false)
        }
    }

    private fun isContainsGreetingsMessage(dataList: List<CRMConversationMessage>): Boolean =
        dataList.take(3).any { it.viewData.type == MessageType.GREETINGS_BUTTONS }

    private fun getGreetingsMessage(newestMessage: CRMConversationMessage?): CRMConversationMessage {
        val greetings = viewModel.greetings.blockingFirst()
        return CRMConversationMessage(
            conversationServiceMessage = CRMServiceMessage(
                uuid = NIL_UUID,
                timestampSent = newestMessage?.timestampSent ?: 0,
                forMe = newestMessage?.isForMe() ?: false,
                outgoing = newestMessage?.isOutgoing() ?: false,
                read = true,
                text = SpannableString(""),
                icon = null,
            ),
            viewData = GreetingsViewDataUtil.createViewData(greetings)
        )
    }

    private fun createConsultation() {
        with(coreConversationInfo) {
            interactor.createConsultation(crmConsultationCase)
                .subscribe(::handleConsultationCreation) { Timber.e(it) }
                .storeIn(compositeDisposable)
        }
    }

    override fun openNewConsultation() {
        mView?.forceHideKeyboard()
        router.openNewConsultation(
            CRMConsultationCreationParams(
                crmConsultationCase = getCorrectCrmConsultationCase(coreConversationInfo.crmConsultationCase)
            )
        )
    }

    /**
     * Для CRMConsultationOpenParams originUuid - это uuid переписки, а нам для создания новой переписки
     * по CRMConsultationCase.Client и CRMConsultationCase.SalePoint необходим sourceId.
     */
    private fun getCorrectCrmConsultationCase(case: CRMConsultationCase): CRMConsultationCase {
        if (case.originUuid == coreConversationInfo.sourceId) return case
        val correctCase = when (case) {
            is CRMConsultationCase.Client -> case.copy(originUuid = coreConversationInfo.sourceId!!)
            is CRMConsultationCase.SalePoint -> case.copy(originUuid = coreConversationInfo.sourceId!!)
            else -> case
        }
        return correctCase
    }

    override fun openNextConsultation(chatParams: CRMConsultationParams) {
        router.openNextConsultation(chatParams)
    }

    override fun onRateRequestButtonClicked(
        messageUUID: UUID,
        consultationRateType: ConsultationRateType,
        disableComment: Boolean
    ) {
        router.openRateScreen(messageUUID, consultationRateType, disableComment)
    }

    override fun onGreetingClicked(title: String) {
        if (canTakeChat) {
            sendGreetingAnalyticEvent(true)
            takeConsultation()
            viewModel.selectedGreeting = title
            isGreetingSent = true
            removeGreetingsButtons()
        } else {
            sendGreetingAnalyticEvent(false)
            mView?.sendGreetingMessage(title)
        }
    }

    private fun sendGreetingAnalyticEvent(
        selectedWithTakeConsultation: Boolean,
    ) = analyticsUtil?.sendAnalytics(
        QuickGreeting(
            selectedWithTakeConsultation,
        ),
    )

    override fun onBeforeUpdateDataList(event: DataChange<CRMConversationMessage>) {
        val oldList = dataList
        super.onBeforeUpdateDataList(event)
        restoreGreetingsButtonsInMessageList(oldList)
    }

    override fun swapDataList(dataList: List<CRMConversationMessage>) {
        super.swapDataList(dataList)
        if (dataList.firstOrNull()?.conversationServiceMessage?.serviceType == ServiceType.CONSULTATION_ADD_OPERATOR) {
            viewModel.selectedGreeting?.let { mView?.sendGreetingMessage(it) }
            viewModel.selectedGreeting = null
        }
    }

    private fun handleConsultationCreation(result: CRMConversationDataResult) {
        result.conversationData.conversationUUID?.let {
            startInitialMessagesLoading(it)
        }
    }

    private suspend fun subscribeOnAppForegroundEvents(appLifecycleTracker: AppLifecycleTracker) {
        appLifecycleTracker.appForegroundStateFlow
            // Интересует переход из background в foreground
            .runningReduce { prev, current -> !prev && current }
            .filter { it }
            .collectLatest {
                onThemeAfterOpenedWithCurrentFilter()
            }
    }

    /**
     * Подписка/отписка от пушей по текущему чату
     * @param needSubscription true, если пуши должны отображаться
     */
    private fun changePushSubscription(needSubscription: Boolean) {
        coreConversationInfo.conversationUuid == null && return
        val conversationUuid = if (coreConversationInfo.crmConsultationCase !is CRMConsultationCase.Operator) {
            coreConversationInfo.conversationUuid
        } else {
            null
        }
        if (needSubscription && conversationUuid != null) {
            messagesPushManager.executeAction(SubscribeOnNotification(conversationUuid))
        } else {
            messagesPushManager.executeAction(UnsubscribeFromNotification(conversationUuid))
        }
    }

    override fun onMessageAttachmentClicked(message: Message, attachment: AttachmentViewModel) {
        router.showViewerSlider(
            MessageUtils.createViewerSliderArgs(
                coreConversationInfo.conversationUuid!!,
                message,
                attachment
            )
        )
    }

    override fun onChatBotButtonClicked(serviceMessageUuid: UUID, title: String) {
        coreConversationInfo.conversationUuid?.let {
            scope.launch {
                interactor.onChatBotButtonClick(it, serviceMessageUuid, title)
            }
        }
    }

    override fun scrollToBotButtons() {
        mView?.scrollToBottom(skipScrollToPosition = false, withHide = false)
    }

    override fun getLoadConversationDataObservable(
        needCloudSync: Boolean,
        observeOnMain: Boolean
    ): Observable<CRMConversationDataResult> =
        interactor.loadConversationData(
            coreConversationInfo.crmConsultationCase.castTo<CRMConsultationCase.Operator>()?.viewId,
            coreConversationInfo.conversationUuid!!,
            needCloudSync,
            coreConversationInfo.crmConsultationCase.toConsultationChatType()
        )

    override fun handleThemeCallbackParams(params: HashMap<String, String>) {
        super.handleThemeCallbackParams(params)
        checkChatUuidChanges(params)
    }

    /**
     * Необходимо отлавливать события с ThemeController об изменениb uuid текущей консультации для замены,
     * по алгоритмам работы.
     * uuid текущего чата может измениться в любой момент, поэтому необходимо отлавливать этот момент.
     */
    private fun checkChatUuidChanges(params: HashMap<String, String>) {
        if (params[MessagesEvent.OLD_THEME_UUID.type] == coreConversationInfo.conversationUuid.toString()
            && params.containsKey(MessagesEvent.THEME_UUID_CHANGED.type)) {
            updateChatUuid(UUID.fromString(params[MessagesEvent.THEME_UUID_CHANGED.type]))
            dataDispatcher.sendConversationEvent(ConversationEvent.UPDATE_VIEW)
            resetFilter()
        }
    }

    private fun updateChatUuid(uuid: UUID) {
        coreConversationInfo.conversationUuid = uuid
        collectionFilter.setThemeUuid(uuid)
        onThemeAfterOpenedWithCurrentFilter()
        changePushSubscription(needSubscription = false)
    }

    override fun processConversationDataLoadingResult(conversationDataResult: CRMConversationDataResult) {
        conversationDataResult.conversationData.also {
            if (source != it.source) {
                source = it.source
                collectionFilter.castTo<CRMMessageCollectionFilter>()?.setSource(source)
                resetFilter()
            }
            checkGroupConversationFilterState(conversationDataResult.conversationData.isGroupConversation)
            coreConversationInfo.crmConsultationCase is CRMConsultationCase.Operator

            val errorCode: ErrorCode = conversationDataResult.commandStatus.errorCode
            if (errorCode == ErrorCode.SUCCESS) {
                if (!conversationState.isNewConversation) enableProgress()
                dataDispatcher.updateData(it)
            } else if (errorCode == ErrorCode.NOT_AVAILABLE) {
                dataDispatcher.updateData(it)
            }
        }
    }

    override fun updateDataList(newList: List<CRMConversationMessage>) {
        if (isInitialConversationDataLoading) {
            // Не устанавливаем сообщения до получения данных о самой переписке.
            initialMessagesResult = newList
            return
        }
        super.updateDataList(newList)
    }

    override fun processConversationDataLoadingError(throwable: Throwable) {
        Timber.e(throwable)
        mView?.apply {
            showStubView(RCommunicatorDesign.string.communicator_conversation_data_loading_error)
        } ?: run {
            dataDispatcher.updateConversationState(conversationState.copy(missedLoadingErrorRes = RCommunicatorDesign.string.communicator_conversation_data_loading_error))
        }
    }

    /** Цитирование сообщения, если ведется запись аудио, то нужно будет его завершить */
    override fun onMessageQuotedBySwipe(message: CRMConversationMessage) {
        mView?.onMessageQuoted()
        notifyMessageSelected(message)
        dataDispatcher.sendConversationEvent(ConversationEvent.QUOTE_MESSAGE)
    }

    override fun updateStubContent(stubContent: StubViewContent) {
        viewModel.currentStub.onNext(stubContent)
    }

    override fun updateStubVisibility(isVisible: Boolean) {
        viewModel.showStub.onNext(isVisible)
    }

    override fun handleConversationDataChanges(conversationData: CRMConversationData) {
        super.handleConversationDataChanges(conversationData)
        viewModel.channelUuid = conversationData.channel?.id
        unreadCounterValue = conversationData.unreadCount
        isCompleteChat = conversationData.isCompletedChat
        mView?.setFastScrollDownUnreadCounterValue(unreadCounterValue)
        updateHistoryView(conversationData)
    }

    private fun updateHistoryView(conversationData: CRMConversationData) {
        val needPrepared = conversationData.isHistory
        val userId = conversationData.authorId ?: NIL_UUID
        val excludeId = conversationData.conversationUUID ?: NIL_UUID
        val needShow = conversationData.isHistory && conversationData.isNew
        viewModel.prepareHistoryView.onNext(
            Triple(
                needPrepared,
                userId,
                excludeId
            )
        )
        viewModel.showHistoryView.onNext(needShow)
    }

    override fun showQuickReplyView() {
        viewModel.showQuickReplyOnButton.onNext(true)
    }

    override fun hideQuickReplyView() {
        viewModel.showQuickReplyOnButton.onNext(false)
    }

    override fun onMessageErrorStatusClicked(conversationMessage: CRMConversationMessage) {
        onMessageErrorStatusClickedInternal(conversationMessage)
    }

    override fun performMessageAction(action: MessageAction) {
        when (action) {
            MessageAction.EDIT -> dataDispatcher.sendConversationEvent(ConversationEvent.EDIT_MESSAGE)
            MessageAction.QUOTE -> dataDispatcher.sendConversationEvent(
                ConversationEvent.QUOTE_MESSAGE
            )
            MessageAction.FORCE_RESEND -> forceResendMessage()
            MessageAction.DELETE -> mView?.showPopupDeleteMessageForAll()
            MessageAction.COPY -> copySelectedMessageTextToClipboard()
            MessageAction.REPORT -> mView?.showComplainDialogFragment(
                ComplainUseCase.ConversationMessage(
                    conversationState.selectedMessage!!.message!!.uuid,
                    coreConversationInfo.conversationUuid!!,
                    coreConversationInfo.isChat
                )
            )
            else -> Unit
        }
    }

    override fun getMessageActionsList(conversationMessage: CRMConversationMessage): List<MessageAction> =
        getCRMMessageActionsList(
            message = conversationMessage.message!!,
            complainEnabled = CRMConversationPlugin.customizationOptions.complainEnabled,
            isCompleteChat = isCompleteChat
        )

    override fun onLinkClicked() {
        mView?.forceHideKeyboard()
    }

    override fun takeConsultation() {
        coreConversationInfo.conversationUuid?.let {
            interactor.takeConsultation(it).subscribe(Functions.EMPTY_ACTION, Timber::e)
                .storeIn(compositeDisposable)
        }
    }

    override fun reopenConsultation() {
        coreConversationInfo.conversationUuid?.let {
            interactor.reopenConsultation(it).subscribe(Functions.EMPTY_ACTION, Timber::e)
                .storeIn(compositeDisposable)
        }
    }

    override fun openHistoryView() {
        analyticsUtil?.sendAnalytics(OpenHistoryView)
        mView?.showHistoryView()
    }

    override fun onHistoryViewClosed() {
        viewModel.showHistoryButton.onNext(true)
    }
}