package ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.dialogs.information

import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import ru.tensor.sbis.common.R
import ru.tensor.sbis.common.generated.ErrorCode
import ru.tensor.sbis.common.util.NetworkUtils
import ru.tensor.sbis.common.util.storeIn
import ru.tensor.sbis.communication_decl.selection.recipient.manager.RecipientSelectionResultManager
import ru.tensor.sbis.communicator.generated.DataRefreshedThemeParticipantsControllerCallback
import ru.tensor.sbis.communicator.generated.ThemeParticipantsFilter
import ru.tensor.sbis.communicator.themes_registry.ui.themeParticipants.ThemeParticipantsListFilter
import ru.tensor.sbis.communicator.themes_registry.ui.themeParticipants.model.ThemeParticipantListItem
import ru.tensor.sbis.mvp.data.model.PagedListResult
import ru.tensor.sbis.mvp.interactor.crudinterface.BaseListAbstractTwoWayPaginationPresenter
import ru.tensor.sbis.mvp.interactor.crudinterface.command.BaseListObservableCommand
import ru.tensor.sbis.mvp.interactor.crudinterface.event.EventData
import ru.tensor.sbis.mvp.interactor.crudinterface.subscribing.SubscriptionManager
import ru.tensor.sbis.platform.generated.Subscription
import timber.log.Timber
import java.util.UUID

/**
 * Презентер для работы с экраном информации о диалоге.
 *
 * @author da.zhukov
 */
internal class DialogInformationPresenter(
    private var conversationName: String,
    private val isNewDialog: Boolean,
    private val conversationUuid: UUID,
    private val videoCallParticipantsUuids: ArrayList<UUID>?,
    private val interactor: DialogInformationInteractor,
    filter: ThemeParticipantsListFilter,
    private val recipientSelectionManager: RecipientSelectionResultManager,
    subscriptionManager: SubscriptionManager,
    networkUtils: NetworkUtils,
) : BaseListAbstractTwoWayPaginationPresenter<DialogInformationContract.View,
        ThemeParticipantListItem,
        ThemeParticipantsListFilter,
        ThemeParticipantsFilter,
        DataRefreshedThemeParticipantsControllerCallback>(filter, subscriptionManager, networkUtils),
    DialogInformationContract.Presenter {

    companion object {
        private const val DIALOG_INFORMATION_EVENT_NAME = "DialogInformationSyncEvent"
        private const val NETWORK_EVENT_KEY = "network"
        private const val AVAILABLE_EVENT_VALUE = "available"
        private const val ERROR_EVENT_KEY = "error"
    }

    private val disposer = CompositeDisposable()
    private val initialRefreshCallbackSubscription = CompositeDisposable()
    private var subscribed: Boolean = false
    private val currentConversationName = conversationName

    init {
        mFilter.theme = conversationUuid
        val subscriptionHolder = mutableListOf<Subscription>()
        refreshCallbackSubscription
            .map { subscriptionHolder.add(it) }
            .doOnDispose { subscriptionHolder.firstOrNull()?.disable() }
            .subscribe()
            .storeIn(initialRefreshCallbackSubscription)
        updateDataList(true)
    }

    /** @SelfDocumented */
    override fun attachView(view: DialogInformationContract.View) {
        superAttachView(view)
    }

    /** @SelfDocumented */
    override fun onRefreshCallback(params: HashMap<String, String>?) {
        when {
            params?.get(NETWORK_EVENT_KEY) == AVAILABLE_EVENT_VALUE -> {
                runOnUiThread { onNetworkConnected() }
            }
            params?.get(ERROR_EVENT_KEY) == NETWORK_EVENT_KEY       -> {
                runOnUiThread { notifySyncFailed(R.string.common_update_error, true) }
            }
            else                                                    -> {
                super.onRefreshCallback(params)
            }
        }
    }

    /**
     * Функция, обеспечивающая сохранение изменений в названии диалога
     * на экране информации о диалоге после нажатия на соответствующую кнопку.
     */
    override fun onDoneButtonClicked() {
        val dialogTitle = conversationName.trim()
        interactor.setDialogTitle(conversationUuid, dialogTitle)
            .subscribe { commandStatus ->
                if (ErrorCode.SUCCESS == commandStatus.errorCode) {
                    mView?.finishWithStringResult(dialogTitle)
                    this.conversationName = dialogTitle
                } else {
                    mView?.showToast(commandStatus.errorMessage)
                }
            }
            .storeIn(disposer)
        unsubscribeEventManager()
    }

    /** @SelfDocumented */
    override fun getEmptyViewErrorId(): Int = ru.tensor.sbis.design.R.string.design_empty_search_error_string

    /** @SelfDocumented */
    override fun isNeedToDisplayViewState(): Boolean = true

    /** @SelfDocumented */
    override fun displayViewState(view: DialogInformationContract.View) {
        super.displayViewState(view)

        view.run {
            updateDataList(dataList, mDataListOffset)
            setDialogTitle(conversationName)
        }
    }

    /** @SelfDocumented */
    override fun getListObservableCommand(): BaseListObservableCommand<out PagedListResult<ThemeParticipantListItem>, ThemeParticipantsFilter, DataRefreshedThemeParticipantsControllerCallback> =
        interactor.themeParticipantsCommandWrapper.listCommand

    /** @SelfDocumented */
    override fun onEvent(eventData: EventData) {
        super.onEvent(eventData)
        if (eventData.isEvent(DIALOG_INFORMATION_EVENT_NAME)) {
            updateDataList(false)
        }
    }

    /** @SelfDocumented */
    override fun configureSubscriptions(batch: SubscriptionManager.Batch) {
        super.configureSubscriptions(batch)
        batch.subscribeOn(DIALOG_INFORMATION_EVENT_NAME)
    }

    /** @SelfDocumented */
    override fun onDestroy() {
        disposer.dispose()
        super.onDestroy()
    }

    /** @SelfDocumented */
    override fun showEmptyViewIfNeeded(
        view: DialogInformationContract.View,
        dataList: MutableList<ThemeParticipantListItem>?,
        errorMessageRes: Int,
        errorDetailsRes: Int
    ) {
        super.showEmptyViewIfNeeded(view, dataList, errorMessageRes, errorDetailsRes)
        dataList?.let {
            if (!mShowOlderProgress && it.isEmpty()) {
                mView?.showMessageInEmptyView(ru.tensor.sbis.communicator.design.R.string.communicator_no_dialog_participants_to_display)
            }
        }
    }

    /**
     * Функция обновления списка участников диалога в зависимости от вида диалога:
     * Внутри видеозвонка, обычный новый диалог или в старом диалоге внесли изменения.
     */
    override fun updateDataList(fromPullToRefresh: Boolean) {
        when {
            !videoCallParticipantsUuids.isNullOrEmpty() -> {
                interactor.getThemeParticipantList(videoCallParticipantsUuids)
                    .subscribeOnParticipantsResult()
                    .storeIn(disposer)
            }
            isNewDialog -> {
                val recipientUuids: List<UUID> = recipientSelectionManager.selectionResult.data.allPersonsUuids
                if (recipientUuids.isNotEmpty()) {
                    interactor.getThemeParticipantList(recipientUuids)
                } else {
                    interactor.getRelevantMessageReceivers(conversationUuid)
                }
                    .subscribeOnParticipantsResult()
                    .storeIn(disposer)
            }
            else -> super.updateDataList(fromPullToRefresh)
        }
    }

    private fun Single<List<ThemeParticipantListItem.ThemeParticipant>>.subscribeOnParticipantsResult(): Disposable =
        doFinally { mView?.hideLoading() }
            .subscribe(
                {
                    mDataList = it // чтобы список восстановился после поворота экрана
                    mView?.setParticipants(it)
                }, {
                    Timber.e(it)
                })

    /** @SelfDocumented */
    override fun getDataRefreshCallback(): DataRefreshedThemeParticipantsControllerCallback {
        return object : DataRefreshedThemeParticipantsControllerCallback() {
            override fun onEvent(param: HashMap<String, String>) {
                onRefreshCallback(param)
            }
        }
    }

    /**
     * Копипаста super.AttachView() чтобы убрать лишний вызов list на контроллер
     */
    @Suppress("ProtectedInFinal")
    protected fun superAttachView(view: DialogInformationContract.View) {
        mView = view

        if (isNeedToDisplayViewState) {
            displayViewState(view)
        }

        if (isNeedToRestoreScrollPosition) {
            view.scrollToPosition(mFirstVisibleItem)
        }

        if (mShowLoadingProcess || mShowSyncProcess) {
            view.showLoading()
        } else {
            view.hideLoading()
        }

        view.showOlderLoadingProgress(mShowOlderProgress)
        view.showNewerLoadingProgress(mHasNewerPage)

        if (mMissedErrorResId != -1) {
            view.showLoadingError(mMissedErrorResId)
            mMissedErrorResId = -1
        }

        if (!subscribed) {
            mSubscriptionManager.addConsumer(::onEvent)
            val batch: SubscriptionManager.Batch = mSubscriptionManager.batch()
            val subscription = refreshCallbackSubscription
                .doOnSubscribe { initialRefreshCallbackSubscription.dispose() }
            batch.manage(null, subscription, true)
            configureSubscriptions(batch)
            batch.doAfterSubscribing {
                mPrepared = true
            }
            batch.subscribe()
            subscribed = true
        }
        mSubscriptionManager.resume()
    }

    /** @SelfDocumented */
    override fun getEmptyViewLoadingErrorCommentId(): Int =
        R.string.common_no_network_available_check_connection

    /** @SelfDocumented */
    override fun onItemClick(profileUuid: UUID) {
        mView?.finishWithUuidResult(profileUuid)
    }

    /** @SelfDocumented */
    override fun onItemPhotoClick(profileUuid: UUID) {
        mView?.openProfile(profileUuid)
    }

    /** @SelfDocumented */
    override fun onStartConversationClick(profileUuid: UUID) {
        mView?.startConversation(profileUuid)
    }

    /** @SelfDocumented */
    override fun onStartVideoCallClick(profileUuid: UUID) {
        mView?.startCall(profileUuid)
    }

    /** @SelfDocumented */
    override fun isNewDialog(): Boolean = isNewDialog

    /**
     * Обработчик изменений в поле названия диалога
     */
    override fun onDialogTitleChanged(title: String) {
        conversationName.let {
            val isTitleChange = it != title
            if (isTitleChange) {
                conversationName = title
            }
            val actionDoneButtonIsVisible =
                (isTitleChange && title != currentConversationName) && (title.isNotBlank() || title.isEmpty())
            if (isTitleChange) mView?.changeActionDoneButtonVisibility(actionDoneButtonIsVisible)
            mView?.changeClearTitleButtonVisibility(title.isNotEmpty())
        }
    }
}