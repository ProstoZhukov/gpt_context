package ru.tensor.sbis.communicator.base.conversation.presentation.presenter.messages

import CommunicatorPushKeyboardHelper
import android.annotation.SuppressLint
import android.util.Log
import androidx.annotation.CallSuper
import androidx.lifecycle.LiveData
import androidx.lifecycle.asFlow
import androidx.recyclerview.widget.RecyclerView
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.SerialDisposable
import io.reactivex.internal.functions.Functions
import io.reactivex.processors.PublishProcessor
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.apache.commons.lang3.StringUtils
import ru.tensor.sbis.attachments.models.AttachmentModel
import ru.tensor.sbis.common.generated.CommandStatus
import ru.tensor.sbis.common.generated.ErrorCode
import ru.tensor.sbis.common.generated.SyncStatus
import ru.tensor.sbis.common.util.ClipboardManager
import ru.tensor.sbis.common.util.UUIDUtils
import ru.tensor.sbis.common.util.logAndIgnoreError
import ru.tensor.sbis.common.util.runOnUiThread
import ru.tensor.sbis.common.util.storeIn
import ru.tensor.sbis.communicator.base.conversation.presentation.presenter.MessageCollectionFilter
import ru.tensor.sbis.communicator.base.conversation.presentation.crud.ConversationListComponent
import ru.tensor.sbis.communicator.base.conversation.presentation.presenter.contracts.BaseConversationMessagesPresenterContract
import ru.tensor.sbis.communicator.base.conversation.presentation.ui.contracts.BaseConversationMessagesView
import ru.tensor.sbis.communicator.base.conversation.data.BaseConversationData
import ru.tensor.sbis.communicator.base.conversation.data.BaseCoreConversationInfo
import ru.tensor.sbis.communicator.base.conversation.data.model.BaseConversationMessage
import ru.tensor.sbis.communicator.base.conversation.data.model.ConversationAccess
import ru.tensor.sbis.communicator.base.conversation.data.model.MessageAction
import ru.tensor.sbis.communicator.base.conversation.interactor.BaseConversationInteractor
import ru.tensor.sbis.communicator.base.conversation.presentation.adapter.holders.MessageAttachmentUploadActionsHandler
import ru.tensor.sbis.communicator.base.conversation.presentation.adapter.holders.MessageSelectionListener
import ru.tensor.sbis.communicator.base.conversation.presentation.crud.ConversationListSizeSettings
import ru.tensor.sbis.communicator.base.conversation.presentation.presenter.dispatcher.BaseConversationDataDispatcher
import ru.tensor.sbis.communicator.base.conversation.presentation.presenter.dispatcher.BaseConversationState
import ru.tensor.sbis.communicator.base.conversation.presentation.presenter.dispatcher.ConversationEvent
import ru.tensor.sbis.communicator.base.conversation.presentation.presenter.dispatcher.NewMessageState
import ru.tensor.sbis.communicator.base.conversation.utils.AttachmentErrorDialogHelper
import ru.tensor.sbis.communicator.core.data.events.MessagesEvent
import ru.tensor.sbis.communicator.design.R
import ru.tensor.sbis.communicator.generated.DataRefreshedMessageControllerCallback
import ru.tensor.sbis.communicator.generated.MessageErrorType
import ru.tensor.sbis.communicator.generated.MessageFilter
import ru.tensor.sbis.communicator.generated.ServiceMessageGroup
import ru.tensor.sbis.crud4.view.datachange.DataChange
import ru.tensor.sbis.crud4.view.datachange.ItemChanged
import ru.tensor.sbis.crud4.view.datachange.ItemInserted
import ru.tensor.sbis.crud4.view.datachange.ItemMoved
import ru.tensor.sbis.crud4.view.datachange.ItemRemoved
import ru.tensor.sbis.crud4.view.datachange.SetItems
import ru.tensor.sbis.design.buttons.base.models.style.PrimaryButtonStyle
import ru.tensor.sbis.design.confirmation_dialog.ButtonModel
import ru.tensor.sbis.design.confirmation_dialog.ConfirmationButtonId
import ru.tensor.sbis.design.stubview.StubViewContent
import ru.tensor.sbis.message_panel.MessagePanelPlugin
import ru.tensor.sbis.mvp.interactor.crudinterface.event.EventData
import ru.tensor.sbis.mvp.presenter.AbstractBasePresenter
import ru.tensor.sbis.platform.generated.Subscription
import timber.log.Timber
import java.util.UUID
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.TimeUnit
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

/**
 * Базовая реализация делегата презентера по секции сообщений переписки.
 *
 * @author vv.chekurda
 */
@SuppressLint("LogNotTimber")
@Suppress("DEPRECATION")
abstract class BaseConversationMessagesPresenter<
    VIEW : BaseConversationMessagesView<MESSAGE>,
    INTERACTOR : BaseConversationInteractor<MESSAGE>,
    MESSAGE : BaseConversationMessage,
    QUERY_FILTER,
    STATE : BaseConversationState<MESSAGE>,
    DATA : BaseConversationData,
    DATA_RESULT : BaseConversationInteractor.BaseConversationDataResult<DATA>,
    INFO : BaseCoreConversationInfo,
    DISPATCHER : BaseConversationDataDispatcher<MESSAGE, STATE, DATA>,
    CALLBACK
>(
    protected val collectionComponent: ConversationListComponent<MESSAGE>,
    protected val interactor: INTERACTOR,
    protected val coreConversationInfo: INFO,
    protected val dataDispatcher: DISPATCHER,
    protected val clipboardManager: ClipboardManager,
    protected open val collectionFilter: MessageCollectionFilter,
    private val communicatorPushKeyboardHelper: CommunicatorPushKeyboardHelper
) : AbstractBasePresenter<VIEW, EventData>(null),
    BaseConversationMessagesPresenterContract<VIEW>,
    MessageSelectionListener<MESSAGE>,
    MessageAttachmentUploadActionsHandler<MESSAGE> {

    protected val compositeDisposable = CompositeDisposable()
    protected val viewModelScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    protected var dataList = emptyList<MESSAGE>()
    private val visibleMessagesHelper = VisibleMessagesHelper(dataList)
    private val collectionHelper = MessageCollectionHelper(
        collectionComponent,
        visibleMessagesHelper
    )

    private val highlightMessageUuid: UUID?
        get() = collectionHelper.targetMessageUuid.takeIf { collectionHelper.highlightTarget }

    private var isInitialMessagesLoading = true
    protected var isPrefetchDataList = false
    protected var isInitialConversationDataLoading = true
    @set:Synchronized @get:Synchronized
    protected var initialConversationData: DATA_RESULT? = null
    protected var initialMessagesResult: List<MESSAGE>? = null

    protected var unreadCounterValue: Int = 0
    private var missedRelevantPosition: Int? = null
    protected var isFirstAttachView = true
    protected var lastKeyboardHeight: Int = 0

    protected var conversationAccess = ConversationAccess()
    protected val conversationState: STATE get() = dataDispatcher.getConversationState()

    /**
     * Текущий список действий, обрабатываемый после выбора сообщения.
     */
    protected var actionsList: List<MessageAction>? = null

    protected open val canShowStub: Boolean
        get() = !conversationState.isNewConversation && conversationAccess.isAvailable

    private var isProgressDisabled = false
    private var isProgress: Boolean = true
    private val disposerSubject = PublishSubject.create<Unit>()
    private val conversationDataLoadingSubscription = SerialDisposable()

    private val readInProgressMessageUuids = CopyOnWriteArrayList<UUID>()
    private val readInProgressServiceMessages = ArrayList<ServiceMessageGroup>()
    private val markAsReadSubject = PublishSubject.create<Pair<ArrayList<UUID>, ArrayList<ServiceMessageGroup>>>()

    private var olderLoadingDelayJob: Job? = null
    private var movingProgressDelayJob: Job? = null

    private var isGroupConversation: Boolean = coreConversationInfo.isInitAsGroupDialog

    protected var isMessageHighlighted: Boolean = false

    protected var applyMessageListStyle = true

    override var actionsMenuShown: Boolean = false

    init {
        Log.d("ConversationCollection", "init relevantMessage ${coreConversationInfo.messageUuid}")
        startBaseSubscriptions()
        subscribeMarkAsReadSubject()
        coreConversationInfo.conversationUuid?.also {
            viewModelScope.launch(Dispatchers.Default) {
                interactor.onThemeBeforeOpened(it)
            }
        }
        viewModelScope.launch(Dispatchers.Main) {
            communicatorPushKeyboardHelper.hideKeyboard.collect {
                if (it) mView?.hideKeyboard()
            }
        }
    }

    /**
     * Запуск базовых подписок для начала работы.
     */
    private fun startBaseSubscriptions() {
        getRefreshCallbackSubscription().subscribe()
            .storeIn(compositeDisposable)
        subscribeOnDataDispatch()
        subscribeOnThemeControllerUpdates()
        subscribeCollectionComponent()
    }

    private fun subscribeOnDataDispatch() {
        compositeDisposable.addAll(
            dataDispatcher.conversationDataObservable.subscribe(::handleConversationDataChanges),
            dataDispatcher.conversationEventObservable.subscribe(::handleConversationEvent),
            dataDispatcher.editMessageObservable.subscribe { handleEditedMessage() },
            dataDispatcher.conversationStateObservable.subscribe {
                handleConversationStateChanges(currentState = it.first, newState = it.second)
            }
        )
    }

    /**
     *  Подписка на ThemeController необходима для обновления информации о переписке.
     */
    private fun subscribeOnThemeControllerUpdates() {
        interactor.observeThemeControllerUpdates()
            .subscribe(::handleThemeCallbackParams)
            .storeIn(compositeDisposable)
    }

    /**
     * Обработка параметров колбэка с ThemeController
     */
    protected open fun handleThemeCallbackParams(params: HashMap<String, String>) {
        if (needUpdateDataFromThemeCallback(params)) {
            loadConversationData()
        }
    }

    protected abstract fun updateStubContent(stubContent: StubViewContent)

    protected abstract fun updateStubVisibility(isVisible: Boolean)

    /**
     * Метод инициализирующей загрузки, вызывается на attachView.
     */
    protected abstract fun startInitialLoading()

    /**
     * Геттер Observable для загрузки данных по переписке.
     * @param needCloudSync             нужна ли облачная синхронизация.
     * @return Observable<DATA_RESULT>  Observable для получения данных по переписке.
     */
    protected abstract fun getLoadConversationDataObservable(
        needCloudSync: Boolean = false,
        observeOnMain: Boolean = true
    ): Observable<DATA_RESULT>

    /**
     * Метод для обработки данных по переписке.
     * @param conversationDataResult модель результата.
     * Внимание!!
     * В случае успешного заброса обрабатывать CommandStatus модели результата,
     * наш контроллер не кидает экспшены в случае провала запроса на облако, если ничего не рухнуло.
     */
    protected abstract fun processConversationDataLoadingResult(conversationDataResult: DATA_RESULT)

    /**
     * Метод для обработки [Throwable] при загрузке данных по переписке.
     * Если он обрабатывается, значит ui или контроллер у себя где-то накосячили.
     */
    protected abstract fun processConversationDataLoadingError(throwable: Throwable)

    /**
     * Реакция на изменение [BaseConversationData] - данных по переписке
     * сработает при измении из других презентеров или делегатов по шине [BaseConversationDataDispatcher].
     * @return [Disposable]               подписка на этот observable.
     */
    @CallSuper
    protected open fun handleConversationDataChanges(conversationData: DATA) {
        conversationAccess = conversationData.conversationAccess
    }

    override fun attachView(view: VIEW) {
        super.attachView(view)
        showMissedErrorsInEmptyView()
        showBlankViewIfNeeded()

        if (isFirstAttachView) {
            onFirstAttachView(view)
            isFirstAttachView = false
        }
    }

    private fun onFirstAttachView(view: VIEW) {
        view.hideStubView()
        if (isProgress) {
            view.showLoading()
        } else {
            view.hideLoading()
        }
    }

    override fun detachView() {
        super.detachView()
        actionsMenuShown = false
    }

    override fun onRefresh() {
        mView?.resetPagingLoadingError()
        val firstVisibleItem = dataList.getOrNull(visibleMessagesHelper.firstVisibleItem)?.uuid
            ?: coreConversationInfo.messageUuid
        changeCollectionPagination(firstVisibleItem)
    }

    protected open fun initCollection(conversationUuid: UUID) {
        collectionFilter.setThemeUuid(conversationUuid)
            .setAnchorMessage(coreConversationInfo.messageUuid)
            .setIsGroupConversation(isGroupConversation)
        collectionComponent.initCollection(collectionFilter)
        onThemeAfterOpenedWithCurrentFilter()
    }

    private fun subscribeCollectionComponent() {
        subscribeOnDataChanged()
        subscribeOnThrobbers()
        subscribeOnStubs()
    }

    private fun subscribeOnDataChanged() {
        collectionComponent.dataChangeMapped
            .skip(1) // Пропуск дефолтного значения
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { event ->
                try {
                    onDataChangedEvent(event)
                } catch (ex: Exception) {
                    Timber.e(ex, "BaseConversationMessagesPresenter.updateDataList from ObserveCollection")
                }
            }.storeIn(compositeDisposable)
    }

    private fun subscribeOnThrobbers() {
        collectionComponent.centralThrobberVisibility.collectOnVM { isProgressVisible ->
            if (isProgressVisible) {
                Log.d("ConversationCollection", "showProgress")
                isProgress = true
                mView?.showLoading()
            } else {
                Log.d("ConversationCollection", "hideProgress")
                isProgress = false
            }
        }
        collectionComponent.loadNextThrobberIsVisible.collectOnVM { showOlderLoading ->
            Log.d("ConversationCollection", "loadNextThrobberIsVisible $showOlderLoading")
            handleLoadNextThrobberIsVisible(showOlderLoading)
        }
        collectionComponent.loadPreviousThrobberIsVisible.collectOnVM { showNewerLoading ->
            if (showNewerLoading && visibleMessagesHelper.isFirstItemShown == true) return@collectOnVM
            Log.d("ConversationCollection", "loadPreviousThrobberIsVisible = $showNewerLoading")
            updateFastScrollDownVisibility()
            mView?.showNewerLoadingProgress(showNewerLoading)
        }
    }

    private fun subscribeOnStubs() {
        collectionComponent.stubVisibility.collectOnVM {
            Log.d("ConversationCollection", "stubVisibility $it")
            updateStubVisibility(it)
        }
        collectionComponent.stubFactory.collectOnVM {
            val stubContent = it ?: return@collectOnVM // дефолтное значение null
            Log.d("ConversationCollection", "stubFactory $stubContent, canShowStub = $canShowStub")
            if (canShowStub) {
                updateStubContent(stubContent)
            }
        }
    }

    private fun handleLoadNextThrobberIsVisible(showOlderLoading: Boolean) {
        if (showOlderLoading && visibleMessagesHelper.isAllListVisible) {
            cancelOlderLoadingDelayJob()
            olderLoadingDelayJob = viewModelScope.launch(Dispatchers.IO) {
                delay(THROBBER_DELAY_MS)
                withContext(Dispatchers.Main) {
                    mView?.showOlderLoadingProgress(true)
                    olderLoadingDelayJob = null
                }
            }
        } else {
            cancelOlderLoadingDelayJob()
            mView?.showOlderLoadingProgress(showOlderLoading)
        }
    }

    private fun cancelOlderLoadingDelayJob() {
        olderLoadingDelayJob?.cancel()
        olderLoadingDelayJob = null
    }

    protected fun onThemeAfterOpenedWithCurrentFilter() {
        val uuid = coreConversationInfo.conversationUuid ?: return
        val messageUuid = dataList.getOrNull(visibleMessagesHelper.firstVisibleItem)?.uuid
            ?: coreConversationInfo.messageUuid
        val filter = collectionFilter.getMessageFilter(
            fromUuid = messageUuid,
            pageSize = ConversationListSizeSettings.listSize
        )
        onThemeAfterOpened(uuid, filter)
    }

    protected open fun onDataChangedEvent(event: DataChange<MESSAGE>) {
        cancelDelayedProgressForCollectionChanges()

        onBeforeUpdateDataList(event)
        mView?.updateDataList(dataList, 0)
        onAfterUpdateDataList(event)
    }

    protected open fun onBeforeUpdateDataList(event: DataChange<MESSAGE>) {
        val newList = event.allItems
        swapDataList(newList)

        if (isInitialMessagesLoading && newList.isNotEmpty()) {
            if (initialMessagesResult == null) {
                isInitialMessagesLoading = false
            } else {
                initialMessagesResult = null
            }
            newList.forEach { it.viewData.groupConversation = isGroupConversation }
            initialScrollToRelevantMessage()
        }

        if (newList.isNotEmpty() && collectionHelper.isMovingToTarget && collectionHelper.activeRequestId == null) {
            handleDataForMoving(newList)
        }

        handleEvent(event, beforeUpdate = true)
    }

    protected open fun onAfterUpdateDataList(event: DataChange<MESSAGE>) {
        handleEvent(event, beforeUpdate = false)

        if (isProgress && collectionComponent.centralThrobberVisibility.value == false) {
            mView?.hideLoading()
        }
    }

    private fun handleEvent(event: DataChange<MESSAGE>, beforeUpdate: Boolean) {
        when (event) {
            is ItemInserted -> onItemInserted(event, beforeUpdate)
            is ItemChanged -> onItemChanged(event, beforeUpdate)
            is SetItems -> onSetItems(event, beforeUpdate)
            is ItemMoved -> onItemMoved(event, beforeUpdate)
            is ItemRemoved -> onItemRemoved(event, beforeUpdate)
        }
    }

    private fun handleDataForMoving(newList: List<MESSAGE>) {
        val targetMessageUuid = collectionHelper.targetMessageUuid
        if (targetMessageUuid == null) {
            scrollToBottom()
        } else {
            val index = newList.indexOfFirst { it.uuid == targetMessageUuid }
            if (index < 0) {
                return // Не наш результат
            }
            visibleMessagesHelper.dropAtBottomState()
            if (collectionHelper.highlightTarget) {
                highlightMessage(messageUuid = newList[index].uuid, withNotify = false)
            }else {
                mView?.scrollToPosition(index)
            }
        }
        collectionHelper.clearTarget()
    }

    protected open fun updateDataList(newList: List<MESSAGE>) {
        onDataChangedEvent(SetItems(newList))
    }

    protected open fun onItemInserted(event: ItemInserted<MESSAGE>, beforeUpdate: Boolean) {
        if (beforeUpdate) {
            Log.d(
                "ConversationCollection",
                "ItemInserted size ${event.indexItemList.size} indexes ${event.indexItemList.map { "ind ${it.first}".plusMsgNumber(it.second) }}"
            )
            collectionHelper.updateVisibleIndexes(event)
            dropOlderProgressBeforeInsertion(event)
        } else {
            scrollBottomOnNewMessages(event)
        }
    }

    protected open fun onItemChanged(event: ItemChanged<MESSAGE>, beforeUpdate: Boolean) {
        if (beforeUpdate) {
            Log.d("ConversationCollection", "ItemChanged ${event.indexItemList.map { it.first }}")
            scrollBottomOnVisibleMessagesChanges(event.indexItemList.map { it.first })
        }
    }

    protected open fun onSetItems(event: SetItems<MESSAGE>, beforeUpdate: Boolean) {
        if (beforeUpdate) {
            Log.d(
                "ConversationCollection",
                "SetItems size ${event.allItems.size} ${event.allItems.map { it.uuid }}"
            )
            scrollBottomOnAllItemsChanged(event)
        } else {
            collectionHelper.isValidListData = true
        }
    }

    protected open fun onItemMoved(event: ItemMoved<MESSAGE>, beforeUpdate: Boolean) {
        if (beforeUpdate) {
            Log.d("ConversationCollection", "ItemMoved ${event.indexPairs}")
        }
    }

    protected open fun onItemRemoved(event: ItemRemoved<MESSAGE>, beforeUpdate: Boolean) {
        if (beforeUpdate) {
            Log.d("ConversationCollection", "ItemRemoved size ${event.indexes.size} indexes ${event.indexes}")
            collectionHelper.updateVisibleIndexes(event)
        }
    }

    private fun scrollBottomOnNewMessages(event: ItemInserted<MESSAGE>) {
        if (collectionHelper.atBottomOfMessages
            && (event.isNewerInsertion || event.isInsertionInVisiblePositions(visibleMessagesHelper))
            && event.indexItemList.size <= 3) {
            Log.d("ConversationCollection", "scrollBottomOnNewMessages")
            scrollToBottom()
        }
    }

    /**
     * Важный момент для пагинации в обратном направлении
     * - неободимо сначала дропнуть крутилку перед добавлением новых элементов в конец списка,
     * иначе список перепрыгнет через страницу.
     */
    private fun dropOlderProgressBeforeInsertion(event: ItemInserted<MESSAGE>) {
        if (event.isOlderInsertion) {
            mView?.showOlderLoadingProgress(false)
        }
    }

    private fun scrollBottomOnVisibleMessagesChanges(indexes: List<Long>) {
        if (collectionHelper.atBottomOfMessages) {
            val isVisibleItemsChanged = visibleMessagesHelper.isAnyVisible(indexes)
            if (isVisibleItemsChanged) {
                Log.d("ConversationCollection", "scrollBottomOnVisibleMessagesChanges")
                scrollToBottom()
            }
        }
    }

    protected open fun scrollBottomOnAllItemsChanged(event: SetItems<MESSAGE>) {
        if (dataList.isNotEmpty() && event.allItems.isNotEmpty() && collectionHelper.atBottomOfMessages) {
            Log.d("ConversationCollection", "scrollBottomOnAllItemsChanged")
            scrollToBottom()
        }
    }

    /**
     * Инициализирующий скролл к релевантному сообщению.
     */
    protected open fun initialScrollToRelevantMessage() {
        val relevantMessage = coreConversationInfo.messageUuid?.let { relevantMessageUuid ->
            dataList.firstOrNull { it.uuid == relevantMessageUuid }
        }
        val relevantPosition = relevantMessage?.let(dataList::indexOf)

        fun initialScrollToBottom() {
            setRelevantMessagePosition(0)
            scrollToBottom()
        }

        when {
            // Нет релевантного сообщения -> скролимся в конец.
            relevantPosition == null -> {
                initialScrollToBottom()
            }
            // Релевантное сообщение входящее непрочитанное -> скролимся к нему.
            !relevantMessage.isReadByMe && !relevantMessage.isOutgoing() -> {
                setRelevantMessagePosition(relevantPosition)
            }
            // Релевантное сообщение где-то в середине списка -> скролимся к нему.
            relevantPosition > 0 -> {
                setRelevantMessagePosition(relevantPosition)
            }
            // Релевантное сообщение последнее прочитанное.
            else -> {
                initialScrollToBottom()
            }
        }
    }

    private fun <T> LiveData<T>.collectOnVM(
        context: CoroutineContext = EmptyCoroutineContext,
        collector: FlowCollector<T>
    ) {
        viewModelScope.launch(context) {
            asFlow().collect(collector)
        }
    }

    override fun onScroll(
        dy: Int,
        firstVisibleItemPosition: Int,
        lastVisibleItemPosition: Int
    ) {
        collectionHelper.onScroll(dy, firstVisibleItemPosition, lastVisibleItemPosition)
    }

    /**
     * Метод запускает всю синхронизацию для переписки. Вызывается после инициализирующей загрузки с тем же фильтром.
     */
    protected fun onThemeAfterOpened(
        themeId: UUID,
        filter: MessageFilter? = null,
        isChat: Boolean = coreConversationInfo.isChat,
    ) {
        if (filter != null && themeId != filter.themeId) {
            Timber.e("Inconsistent state: $themeId vs ${filter.themeId}")
            return
        }
        interactor.onThemeAfterOpened(themeId, filter, isChat).subscribe(Functions.EMPTY_ACTION, Timber::e)
            .storeIn(compositeDisposable)
    }

    protected fun onThemeClosed(themeId: UUID) {
        interactor.onThemeClosed(themeId).subscribe(Functions.EMPTY_ACTION, Timber::e)
            .storeIn(compositeDisposable)
    }

    protected open fun handleConversationStateChanges(currentState: STATE?, newState: STATE) {
        if (currentState?.newMessageState != newState.newMessageState) {
            when (newState.newMessageState) {
                NewMessageState.SENDING -> onSendClick()
            }
        }
    }

    protected open fun handleConversationEvent(event: ConversationEvent) {
        when (event) {
            ConversationEvent.CHAT_CREATED -> {
                collectionFilter.setThemeUuid(coreConversationInfo.conversationUuid!!)
                loadConversationData()
                mView?.let { displayViewState(it) }
            }
            ConversationEvent.UPDATE_VIEW -> {
                mView?.let {
                    displayViewState(it)
                }
            }
            else                               -> Unit
        }
    }

    private fun handleEditedMessage() {
        val selectedMessage = conversationState.selectedMessage
        val editedMessagePosition = dataList.indexOf(selectedMessage)
        if (editedMessagePosition >= 0 && conversationState.editedMessage != null) {
            dataList.toMutableList()[editedMessagePosition] = conversationState.editedMessage!!
            mView?.notifyItemsChanged(editedMessagePosition, 1)
        }
    }

    /**
     *  Чтобы отображать изменения данных по переписке, мы должны ловить registry_changed и проверять affected_themes:
     *  если значение any - обновляем безусловно,
     *  если список uuid-ов, то убедившись, что uuid текущей переписки есть в списке - обновляем.
     *  Так же может прийти просто ключ theme с айдишником переписки, тогда тоже обновить.
     *  Всё просто.
     */
    private fun needUpdateDataFromThemeCallback(params: HashMap<String, String>): Boolean {
        val uuid = UUIDUtils.toString(coreConversationInfo.conversationUuid)
        return MessagesEvent.REGISTRY.isExistsIn(params) && (
            MessagesEvent.AFFECTED_THEMES_ANY.isExistsIn(params) ||
                (uuid != null && params[MessagesEvent.AFFECTED_THEMES_LIST.type]?.contains(uuid) == true)
            ) || params[MessagesEvent.THEME.type] == uuid
    }

    private fun showMissedErrorsInEmptyView() {
        if (conversationState.missedLoadingErrorRes != 0) {
            mView?.showStubView(conversationState.missedLoadingErrorRes)
            dataDispatcher.updateConversationState(conversationState.apply { missedLoadingErrorRes = 0 })
        }
        if (conversationState.missedLoadingErrorFromController != null) {
            mView?.showControllerErrorMessage(conversationState.missedLoadingErrorFromController)
            dataDispatcher.updateConversationState(conversationState.apply { missedLoadingErrorFromController = null })
        }
    }

    protected fun loadConversationData(
        needCloudSync: Boolean = false,
        observeOnMain: Boolean = true
    ) {
        getLoadConversationDataObservable(needCloudSync, observeOnMain)
            .subscribe(
                ::processConversationDataLoadingResult,
                ::processConversationDataLoadingError
            ).storeIn(conversationDataLoadingSubscription)
    }

    private val onRefreshObserver = PublishProcessor.create<HashMap<String, String>>().apply {
        observeOn(Schedulers.io())
            .subscribe(::handleCallbackParams)
            .storeIn(compositeDisposable)
    }


    protected fun onRefreshCallback(params: HashMap<String, String>?) {
        params?.also { onRefreshObserver.onNext(it) }
    }

    private fun updateLoadingErrors(event: MessagesEvent) {
        when (event) {
            MessagesEvent.DIRECTION_TO_NEWER -> {
                mView?.showNewerLoadingError()
            }
            MessagesEvent.DIRECTION_TO_OLDER -> {
                mView?.showOlderLoadingError()
            }
            MessagesEvent.DIRECTION_TO_BOTH -> {
                mView?.showOlderLoadingError()
                mView?.showNewerLoadingError()
            }
            else -> Unit
        }
    }

    protected open fun handleCallbackParams(params: HashMap<String, String>) {
        if (MessagesEvent.NETWORK_AVAILABLE.isExistsIn(params)) {
            runOnUiThread { onRefresh() }
        }

        if (!UUIDUtils.equals(params[MessagesEvent.THEME_ID.type], coreConversationInfo.conversationUuid)) {
            return
        }

        if (collectionHelper.isActiveRequestCallback(params)) {
            handleActiveRequestCallbackParams(params)
        }

        if (!MessagesEvent.OTHER_ERROR.isExistsIn(params) && !MessagesEvent.NETWORK_ERROR.isExistsIn(params)) {
            // сбрасываем заглушки пагинации при отсутствии ошибок
            runOnUiThread { mView?.resetPagingLoadingError() }
        } else {
            // в случае ошибки при пагинации показываем заглушку
            when {
                MessagesEvent.DIRECTION_TO_NEWER.isExistsIn(params) -> runOnUiThread {
                    updateLoadingErrors(MessagesEvent.DIRECTION_TO_NEWER)
                }
                MessagesEvent.DIRECTION_TO_OLDER.isExistsIn(params) -> runOnUiThread {
                    updateLoadingErrors(MessagesEvent.DIRECTION_TO_OLDER)
                }
                MessagesEvent.DIRECTION_TO_BOTH.isExistsIn(params) -> runOnUiThread {
                    updateLoadingErrors(MessagesEvent.DIRECTION_TO_BOTH)
                }
            }
        }

        when {
            MessagesEvent.UNATTACHED_PHONE_NUMBER_ERROR.isExistsIn(params) -> {
                runOnUiThread {
                    UUIDUtils.fromString(params[MessagesEvent.MESSAGE_ID.type])?.let(::handleUndeliveredMessageUuid)
                    mView?.showUnattachedPhoneError()
                }
            }
            MessagesEvent.THEME_REMOVED_PERMANENTLY.isExistsIn(params) -> {
                runOnUiThread { loadConversationData() }
                return
            }
        }
    }

    private fun getDataRefreshCallback(): DataRefreshedMessageControllerCallback {
        return object: DataRefreshedMessageControllerCallback() {
            override fun onEvent(param: java.util.HashMap<String, String>) {
                onRefreshCallback(param)
            }
        }
    }

    protected open fun getRefreshCallbackSubscription(): Observable<Subscription> {
        return interactor.observeMessageControllerCallbackSubscription(getDataRefreshCallback())
    }

    /**
     * Обработка uuid неотправленного сообщения
     * @param messageUuid идентификатор сообщения
     */
    protected open fun handleUndeliveredMessageUuid(messageUuid: UUID) = Unit

    /**
     * Выставить позицию релевантного сообщения для первой отрисовки на конкретной позиции.
     *
     * @param position позиция релевантного сообщения
     */
    private fun setRelevantMessagePosition(position: Int) {
        val relevantPosition = position.coerceAtLeast(0)
        mView?.let {
            it.setRelevantMessagePosition(relevantPosition)
            missedRelevantPosition = null
        } ?: run {
            missedRelevantPosition = relevantPosition
        }
    }

    /**
     * Подсветить сообщение [messageUuid].
     *
     * @param scrollPosition позиция для подскролла:
     * 1) null/дефолт - подскролить к позиции сообщения [messageUuid].
     * 2) -1 - не скролить
     * 3) Ваша произвольная позиция. Например, для тредов это messagePosition + 1.
     */
    protected fun highlightMessage(messageUuid: UUID, withNotify: Boolean = true, scrollPosition: Int? = null) {
        mView?.run {
            val messageIndex = dataList.indexOfFirst { it.uuid == messageUuid }
            if (messageIndex >= 0) {
                isMessageHighlighted = true
                setHighlightedMessageUuid(messageUuid)
                if (withNotify) {
                    notifyItemsChanged(messageIndex, 1)
                }
                if (scrollPosition != -1) {
                    scrollToPosition((scrollPosition ?: messageIndex))
                }
            }
        }
    }

    private fun resetMessageHighlight(messageUuid: UUID) {
        collectionHelper.clearTarget()
        mView?.apply {
            setHighlightedMessageUuid(null)
            dataList.forEachIndexed { index, item ->
                if (item.message?.uuid == messageUuid) {
                    notifyItemsChanged(index, 1)
                    return
                }
            }
        }
    }

    protected open fun swapDataList(dataList: List<MESSAGE>) {
        Log.d("ConversationCollection","swapDataList size ${dataList.size}")
        this.dataList = dataList
        collectionHelper.onDataListChanged(dataList)
        visibleMessagesHelper.onDataListChanged(dataList)
    }

    override fun isNeedToDisplayViewState(): Boolean = true

    override fun onFirstItemShownStateChanged(shown: Boolean, atBottomOfItem: Boolean) {
        visibleMessagesHelper.onFirstItemShownStateChanged(
            isFirstItemShown = shown,
            atBottomOfList = shown && atBottomOfItem
        )
        updateFastScrollDownVisibility()
    }

    private fun updateFastScrollDownVisibility() {
        mView?.run {
            val show = visibleMessagesHelper.isFirstItemShown == false || collectionHelper.isNewerProgressVisible
            if (show) {
                showFastScrollDownButton()
            } else {
                hideFastScrollDownButton()
            }
        }
    }

    override fun onKeyboardAppears(keyboardHeight: Int) {
        lastKeyboardHeight = keyboardHeight
        mView?.apply {
            setListViewBottomPadding(keyboardHeight)
            scrollListView(keyboardHeight, false)
        }
    }

    override fun onKeyboardDisappears(keyboardHeight: Int) {
        if (lastKeyboardHeight != 0) {
            resetUIState(keyboardHeight)
            lastKeyboardHeight = 0
        }
    }

    override fun onMessagePanelHeightChanged(difference: Int, isFirstLayout: Boolean) {
        mView?.changeListViewBottomPadding(difference, !isFirstLayout)
    }

    override fun deleteMessageForAll() {
        val messageUuid = conversationState.selectedMessage!!.message!!.uuid
        resetSelectedMessage()

        interactor.deleteMessageForEveryone(coreConversationInfo.conversationUuid!!, messageUuid).subscribe(
            { removeMessageFromList(messageUuid) },
            { error -> Timber.w(error) }
        ).storeIn(compositeDisposable)
    }

    override fun deleteMessageOnlyForMe() =
        deleteMessageForMe()

    private fun resetUIState(keyboardHeight: Int) {
        mView?.apply {
            setListViewBottomPadding(0)
            scrollListView(-keyboardHeight, false)
        }
    }

    private fun showBlankViewIfNeeded() {
        mView?.ignoreProgress(isProgressDisabled)
    }

    protected open fun enableProgress() {
        isProgressDisabled = false
        showBlankViewIfNeeded()
    }

    protected fun disableProgress() {
        isProgressDisabled = true
        showBlankViewIfNeeded()
    }

    override fun displayViewState(view: VIEW) {
        super.displayViewState(view)
        view.run {
            setHighlightedMessageUuid(highlightMessageUuid)
            setFastScrollDownUnreadCounterValue(unreadCounterValue)
            // Предотвращение установки первых данных до окончания их загрузки из кэша,
            // чтобы лишний раз не запускать анимацию показа фрагмента с крутилкой
            if (!isFirstAttachView) {
                missedRelevantPosition?.let(::setRelevantMessagePosition)
                updateDataList(dataList, 0)
            }
            if (applyMessageListStyle) updateMessagesListStyle(conversationState.isNewConversation)
        }
    }

    /**
     * Обновить стиль переписки
     *
     * @param isNewConversationStyle true, если переписка должна отображаться, как новая
     */
    protected open fun updateMessagesListStyle(isNewConversationStyle: Boolean) {
        mView?.setMessagesListStyle(isNewConversationStyle)
    }

    private fun onSendClick() {
        if (!collectionHelper.atBottomOfMessages && conversationState.editedMessage == null) {
            onFastScrollDownPressed()
        }
    }

    override fun onFastScrollDownPressed() {
        if (collectionHelper.isMovingToTarget && collectionHelper.targetMessageUuid == null) return
        if(coreConversationInfo.isFullViewMode) {
            unreadCounterValue = 0
            mView?.setFastScrollDownUnreadCounterValue(unreadCounterValue)
        }
        moveToBottomOfConversation()
    }

    protected fun scrollToBottom() {
        mView?.scrollToBottom(skipScrollToPosition = true, withHide = false)
    }

    override fun onMessageActionClick(actionOrder: Int) {
        if (conversationState.selectedMessage == null) {
            Timber.d("Message action clicked but there are no selected message")
            return
        }

        val action = actionsList?.getOrNull(actionOrder) ?: kotlin.run {
            Timber.w("There is no selected action in actions list")
            return
        }
        actionsList = null
        performMessageAction(action)
    }

    /**
     * Обработать выбранный элемент списка действий с сообщением (удалить/редактировать и тд)
     *
     * @param action элемент списка действий
     */
    protected abstract fun performMessageAction(action: MessageAction)

    override fun onMessageSelected(conversationMessage: MESSAGE) {
        notifyMessageSelected(conversationMessage)

        actionsList = getMessageActionsList(conversationMessage)
        if (!actionsList.isNullOrEmpty()) {
            mView?.showMessageActionsList(conversationMessage, actionsList!!)
        } else {
            copySelectedMessageTextToClipboard()
        }
    }

    protected fun onMessageErrorStatusClickedInternal(conversationMessage: MESSAGE) {
        if (actionsMenuShown) return
        conversationState.selectedMessage = conversationMessage
        showErrorSendConfirmationDialog(conversationMessage)
    }

    private fun showErrorSendConfirmationDialog(conversationMessage: MESSAGE) {
        conversationState.selectedMessage = conversationMessage
        interactor.getMessageError(conversationMessage.uuid)
            .subscribe(
                { errorResult ->
                    when (errorResult.errorCode) {
                        MessageErrorType.NO_SPECIFIED_PHONE_NUMBER -> {
                            mView?.showUnattachedPhoneError(errorResult.errorText)
                        }
                        MessageErrorType.OTHER_ERROR -> {
                            mView?.showConfirmationDialog(
                                text = errorResult.errorText.takeIf { it.isNotEmpty() },
                                buttons = getErrorSendConfirmationDialogButtons(),
                                tag = ERROR_SEND_CONFIRMATION_DIALOG_TAG
                            )
                        }
                    }
                },
                { Timber.e(it) }
            ).storeIn(compositeDisposable)
    }

    private fun getErrorSendConfirmationDialogButtons(): List<ButtonModel<ConfirmationButtonId>> =
        listOf(
            ButtonModel(
                id = ConfirmationButtonId.OK,
                labelRes = R.string.communicator_confirmation_dialog_error_send_ok
            ),
            ButtonModel(
                id = ConfirmationButtonId.YES,
                labelRes = R.string.communicator_confirmation_dialog_error_send_repeat,
                style = PrimaryButtonStyle,
                isPrimary = true
            )
        )

    override fun onConfirmationDialogButtonClicked(tag: String?, id: String) {
        when (tag) {
            ERROR_SEND_CONFIRMATION_DIALOG_TAG -> {
                if (id == ConfirmationButtonId.YES.toString()) {
                    forceResendMessage()
                }
            }
            MESSAGE_ATTACHMENT_ERROR_CONFIRMATION_DIALOG_TAG -> {
                if (id == ConfirmationButtonId.YES.toString()) {
                    forceResendMessage()
                }
            }
        }
    }

    protected fun notifyMessageSelected(conversationMessage: MESSAGE) {
        if (conversationState.newMessageState == NewMessageState.EDITING) {
            dataDispatcher.sendConversationEvent(ConversationEvent.STOP_EDITING)
        }

        dataDispatcher.updateConversationState(
            conversationState.apply { selectedMessage = conversationMessage }
        )
    }

    /**
     * Получить список действий для выбранного сообщения
     *
     * @param conversationMessage модель выбранного сообщения
     * @return список действий с выбранным сообщением
     */
    protected abstract fun getMessageActionsList(conversationMessage: MESSAGE): List<MessageAction>

    protected fun copySelectedMessageTextToClipboard() {
        val text = conversationState.selectedMessage?.message?.textForCopy?.toString() ?: StringUtils.EMPTY
        clipboardManager.copyToClipboard(text)
        if (text.isNotEmpty()) {
            mView?.showToast(R.string.communicator_message_copied)
        }
        resetSelectedMessage()
    }

    private fun resetSelectedMessage() {
        dataDispatcher.updateConversationState(conversationState.apply { selectedMessage = null })
        actionsList = null
    }

    /**
     * Повторить отправку выбранного сообщения
     */
    protected fun forceResendMessage() {
        val selectedMessage = conversationState.selectedMessage!!
        resetSelectedMessage()
        val resendMessagePosition = dataList.indexOfFirst { it.uuid == selectedMessage.uuid }
        if (resendMessagePosition == -1) return

        forceResendMessage(selectedMessage.uuid)
    }

    /**
     * Повторить отправку сообщения.
     */
    private fun forceResendMessage(messageUuid: UUID) {
        interactor.forceResendMessage(messageUuid)
            .subscribe { status ->
                onMessageResent(messageUuid, status)
            }.storeIn(compositeDisposable)
    }

    /**
     * Обработка успешной переотправки сообщения/
     */
    protected open fun onMessageResent(messageUuid: UUID, status: CommandStatus) {
        resetSelectedMessage()
        if (status.errorCode != ErrorCode.SUCCESS) {
            Timber.e("MessageController.forceResendMessage error: ${status.errorMessage}")
        }
    }

    override fun onDeleteUploadClicked(message: MESSAGE, attachmentModel: AttachmentModel) {
        interactor.cancelUploadAttachment(message.uuid, attachmentModel.id.localId)
            .subscribe(
                { status ->
                    if (status.errorCode != ErrorCode.SUCCESS) {
                        Timber.e("MessageController.cancelUploadAttachment error: ${status.errorMessage}")
                    }
                }, { error ->
                    Timber.e("MessageController.cancelUploadAttachment error: $error")
                }
            )
            .storeIn(compositeDisposable)
    }

    override fun onRetryUploadClicked(message: MESSAGE, attachmentModel: AttachmentModel) {
        forceResendMessage(message.uuid)
    }

    override fun onErrorUploadClicked(message: MESSAGE, attachmentModel: AttachmentModel, errorMessage: String) {
        conversationState.selectedMessage = message
        mView?.showConfirmationDialog(
            text = AttachmentErrorDialogHelper.getConfirmationDialogTitle(
                MessagePanelPlugin.resourceProvider.get(),
                errorMessage
            ),
            buttons = getMessageAttachmentErrorConfirmationDialogButtons(),
            tag = MESSAGE_ATTACHMENT_ERROR_CONFIRMATION_DIALOG_TAG
        )
    }

    private fun getMessageAttachmentErrorConfirmationDialogButtons(): List<ButtonModel<ConfirmationButtonId>> =
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

    /**
     * Удалить выбранное сообщение из текущей переписки
     */
    private fun deleteMessageForMe() {
        val messageUuid = conversationState.selectedMessage!!.message!!.uuid
        resetSelectedMessage()

        interactor.deleteMessageForMe(coreConversationInfo.conversationUuid!!, messageUuid)
            .subscribe { messageListResult ->
                if (ErrorCode.SUCCESS == messageListResult.status.errorCode) {
                    removeMessageFromList(messageUuid)
                    resetSelectedMessage()
                }
            }?.storeIn(compositeDisposable)
    }

    /**
     * Удалить сообщение из списка локально
     * @param messageUuid uuid сообщения
     */
    protected open fun removeMessageFromList(messageUuid: UUID) = Unit

    override fun onQuoteClicked(quotedMessageUuid: UUID) {
        moveToMessage(quotedMessageUuid)
    }

    override fun onPinnedMessageClicked(messageUuid: UUID) {
        moveToMessage(messageUuid)
    }

    private fun moveToMessage(messageUuid: UUID) {
        if (collectionHelper.isMovingToTarget || actionsMenuShown) return
        val messageIndex = dataList.indexOfFirst { it.uuid == messageUuid }
        if (messageIndex >= 0) {
            highlightMessage(messageUuid)
        } else {
            navigateToMessage(messageUuid)
        }
    }

    protected fun checkGroupConversationFilterState(isGroup: Boolean) {
        if (isGroupConversation == isGroup) return
        isGroupConversation = isGroup
        collectionFilter.setIsGroupConversation(isGroup)
        if (isInitialConversationDataLoading) {
            initialMessagesResult?.forEach { it.viewData.groupConversation = isGroup }
        }
        resetFilter()

        updateMessageGroupStateLocal(isGroup)
    }

    private fun updateMessageGroupStateLocal(isGroup: Boolean) {
        dataList.map { it.groupConversation = isGroup }
        mView?.updateDataListWithoutNotification(dataList, 0)
        mView?.notifyItemsChanged(0, dataList.size)
        if (visibleMessagesHelper.atBottomOfList) scrollToBottom()
    }

    protected fun moveToBottomOfConversation() {
        if (collectionHelper.isMovingToTarget && collectionHelper.targetMessageUuid == null) return
        val hasNewerData = collectionHelper.hasNewerPage || collectionHelper.isNewerProgressVisible
        if (hasNewerData) {
            navigateToMessage(null)
        } else {
            scrollToBottom()
        }
    }

    protected fun navigateToMessage(messageUuid: UUID? = null) {
        collectionHelper.setupTarget(messageUuid, messageUuid != null)
        val requestId = collectionHelper.activeRequestId

        val messageFilter = collectionFilter.getMessageFilter(
            fromUuid = messageUuid,
            pageSize = ConversationListSizeSettings.listSize / 2,
            requestId = requestId
        )
        viewModelScope.launch(Dispatchers.Main) {
            val result = interactor.checkMessagesArea(messageFilter)
            if (requestId != collectionHelper.activeRequestId) return@launch

            when (result.errorCode) {
                ErrorCode.SUCCESS -> {
                    collectionHelper.cancelActiveRequestId()
                    collectionHelper.onFastScrollDown()
                    changeCollectionPagination(messageUuid)
                }
                ErrorCode.MESSAGE_ID_NOT_FOUND -> {
                    collectionHelper.clearTarget()
                    mView?.showToast(R.string.communicator_quoted_message_was_deleted)
                }
                ErrorCode.SYNC_IN_PROGRESS -> {
                    // waiting callback
                }
                else -> Unit
            }
        }
    }

    private fun handleActiveRequestCallbackParams(params: HashMap<String, String>) {
        viewModelScope.launch(Dispatchers.Main) {
            when {
                MessagesEvent.QUOTED_MESSAGE_REMOVED.isExistsIn(params) -> {
                    collectionHelper.clearTarget()
                    mView?.showToast(R.string.communicator_quoted_message_was_deleted)
                }
                MessagesEvent.NETWORK_ERROR.isExistsIn(params) -> {
                    collectionHelper.clearTarget()
                    mView?.showToast(R.string.communicator_signing_network_error)
                }
                else -> {
                    collectionHelper.cancelActiveRequestId()
                    changeCollectionPagination(collectionHelper.targetMessageUuid)
                }
            }
        }
    }

    protected fun changeCollectionPagination(messageUuid: UUID? = null) {
        collectionFilter.setAnchorMessage(messageUuid)
        resetFilter()
    }

    private fun startDelayedProgressForCollectionChanges() {
        cancelDelayedProgressForCollectionChanges()

        movingProgressDelayJob = viewModelScope.launch {
            delay(COLLECTION_CHANGES_PROGRESS_DELAY_MS)
            withContext(Dispatchers.Main) {
                swapDataList(emptyList())
                mView?.showLoading()
                mView?.updateDataList(emptyList(), 0)
            }
        }
    }

    private fun cancelDelayedProgressForCollectionChanges() {
        movingProgressDelayJob?.cancel()
        movingProgressDelayJob = null
    }

    protected fun resetFilter() {
        collectionHelper.isValidListData = false
        collectionComponent.resetForce(collectionFilter)
    }

    override fun onQuoteLongClicked(enclosingMessageUuid: UUID) {
        dataList.find { it.uuid == enclosingMessageUuid }?.let {
            onMessageSelected(it)
        }
    }

    protected fun findMessageIndexInListByUuid(uuid: UUID): Int {
        dataList.forEachIndexed { index, conversationMessage ->
            if (UUIDUtils.equals(conversationMessage.uuid, uuid)) {
                return index
            }
        }
        return -1
    }

    override fun onScrollStateChanged(state: Int) {
        if (state == RecyclerView.SCROLL_STATE_DRAGGING && lastKeyboardHeight > 0) {
            mView?.hideKeyboard()
        }
    }

    override fun onItemsLaidOut(topmostItemPosition: Int, bottommostItemPosition: Int) {
        visibleMessagesHelper.onVisibleItemsChanged(first = bottommostItemPosition, last = topmostItemPosition)

        var realPositionInList: Int
        var message: BaseConversationMessage?
        val conversationMessages = dataList
        val unreadMessageUuids = ArrayList<UUID>()
        val unreadServiceMessageGroups = ArrayList<ServiceMessageGroup>()
        var unreadServiceMessagesInGroupsCount = 0
        var group: ServiceMessageGroup?
        var groupReadInProgress: Boolean
        for (position in bottommostItemPosition..topmostItemPosition) {
            realPositionInList = position

            message =
                if (realPositionInList >= 0 && realPositionInList < conversationMessages.size) conversationMessages[realPositionInList] else null
            group = message?.conversationServiceMessage?.serviceMessageGroup
            groupReadInProgress = false
            if (group != null) {
                for (i in readInProgressServiceMessages.indices.reversed()) {
                    if (readInProgressServiceMessages[i].firstMessageUuid == group.firstMessageUuid) {
                        groupReadInProgress = true
                        break
                    }
                }
            }

            if (message == null ||
                message.getSyncStatus() != SyncStatus.SUCCEEDED ||
                readInProgressMessageUuids.contains(message.uuid) ||
                groupReadInProgress) {
                continue
            }

            if (!message.isReadByMe) {
                if (coreConversationInfo.isChat) {
                    if (group != null && group.unreadCount > 0) {
                        message.setRead(true)
                        unreadServiceMessagesInGroupsCount += group.unreadCount
                        unreadServiceMessageGroups.add(group)
                    } else if (!message.isOutgoing()) {
                        message.setRead(true)
                        unreadMessageUuids.add(message.uuid)
                    }
                } else {
                    if (message.isForMe()) {
                        message.setRead(true)
                        unreadMessageUuids.add(message.uuid)
                    }
                }
            }
        }
        if (unreadMessageUuids.isNotEmpty() || unreadServiceMessageGroups.isNotEmpty()) {
            readInProgressMessageUuids.addAll(unreadMessageUuids)
            readInProgressServiceMessages.addAll(unreadServiceMessageGroups)
            if (coreConversationInfo.isFullViewMode) {
                markAsReadSubject.onNext(Pair(
                    ArrayList(readInProgressMessageUuids),
                    ArrayList(readInProgressServiceMessages)
                ))
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.dispose()
        viewModelScope.cancel()
        conversationDataLoadingSubscription.dispose()
        disposerSubject.onNext(Unit)
        disposerSubject.onComplete()
        collectionComponent.dispose()
    }

    private fun subscribeMarkAsReadSubject() {
        markAsReadSubject
            .filter { it.first.isNotEmpty() }
            .configureReadSubject()
            .map { it.first }
            .flatMap { uuids ->
                interactor.markMessagesAsRead(uuids).logAndIgnoreError()
            }.subscribe { result ->
                if (result.status.errorCode == ErrorCode.SUCCESS) {
                    readInProgressMessageUuids.clear()
                    unreadCounterValue = result.unreadCount
                }
            }.storeIn(compositeDisposable)

        markAsReadSubject
            .filter { it.second.isNotEmpty() }
            .configureReadSubject()
            .map { it.second }
            .flatMap {
                interactor.markGroupServiceMessageAsRead(coreConversationInfo.conversationUuid!!, it)
                    .logAndIgnoreError()
            }.subscribe { groupStatusPair -> readInProgressServiceMessages.remove(groupStatusPair.first) }.storeIn(compositeDisposable)
    }

    //region ListController для секции переписки
    override fun knownHead(): Boolean = true

    override fun knownTail(): Boolean = !collectionHelper.hasOlderPage

    override fun onVisibleRangeChanged(firstVisible: Int, lastVisible: Int, direction: Int) {
        onScroll(direction, firstVisible, lastVisible)
    }

    protected open fun <T> Observable<T>.configureReadSubject(): Observable<T> =
        if (!coreConversationInfo.isChat) subscribeOn(Schedulers.io())
        else throttleLast(MARK_MESSAGES_INTERVAL_SECONDS, TimeUnit.SECONDS, Schedulers.io())
}

private fun String.plusMsgNumber(message: BaseConversationMessage): String =
    message.testMessageNumber()
        .takeIf { it.isNotEmpty() }
        ?.let { plus(" msgNum $it") }
        ?: this
private fun BaseConversationMessage.testMessageNumber(): String {
    val split = this.message?.textForCopy?.split("№ ") ?: return ""
    return if (split.size > 2) {
        split.last()
    } else {
        ""
    }
}

private const val MARK_MESSAGES_INTERVAL_SECONDS = 2L
private const val ERROR_SEND_CONFIRMATION_DIALOG_TAG = "ERROR_SEND_CONFIRMATION_DIALOG_TAG"
private const val MESSAGE_ATTACHMENT_ERROR_CONFIRMATION_DIALOG_TAG = "MESSAGE_ATTACHMENT_ERROR_CONFIRMATION_DIALOG_TAG"
private const val THROBBER_DELAY_MS = 2000L
private const val COLLECTION_CHANGES_PROGRESS_DELAY_MS = 1000L