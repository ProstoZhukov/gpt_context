package ru.tensor.sbis.communicator.themes_registry.ui.conversationparticipants.dialogparticipants.presentation.presenter

import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import ru.tensor.sbis.common.util.NetworkUtils
import ru.tensor.sbis.common.util.scroll.ScrollHelper
import ru.tensor.sbis.common.util.storeIn
import ru.tensor.sbis.communication_decl.selection.recipient.manager.RecipientSelectionResultManager
import ru.tensor.sbis.communicator.generated.DataRefreshedThemeParticipantsControllerCallback
import ru.tensor.sbis.communicator.generated.ThemeParticipantsFilter
import ru.tensor.sbis.communicator.themes_registry.ui.conversationparticipants.dialogparticipants.contract.DialogParticipantsInteractor
import ru.tensor.sbis.communicator.themes_registry.ui.conversationparticipants.dialogparticipants.contract.DialogParticipantsViewContract
import ru.tensor.sbis.communicator.themes_registry.ui.themeParticipants.ThemeParticipantsListFilter
import ru.tensor.sbis.communicator.themes_registry.ui.themeParticipants.model.ThemeParticipantListItem
import ru.tensor.sbis.design.utils.DebounceActionHandler
import ru.tensor.sbis.mvp.data.model.PagedListResult
import ru.tensor.sbis.mvp.interactor.crudinterface.BaseSearchableListAbstractTwoWayPaginationPresenter
import ru.tensor.sbis.mvp.interactor.crudinterface.command.BaseListObservableCommand
import ru.tensor.sbis.mvp.interactor.crudinterface.event.EventData
import ru.tensor.sbis.mvp.interactor.crudinterface.subscribing.SubscriptionManager
import ru.tensor.sbis.platform.generated.Subscription
import timber.log.Timber
import java.util.*
import ru.tensor.sbis.common.R as RCommon
import ru.tensor.sbis.communicator.design.R as RCommunicatorDesign
import ru.tensor.sbis.design.R as RDesign

/**
 * Presenter участников диалога.
 *
 * @param conversationUuid UUID диалога.
 * @param isNewDialog true, если новый диалог, false иначе.
 * @param isFromCollage true, если открыт через коллаж, false иначе.
 * @param videoCallParticipantsUuids uuid участников видеозвонка.
 * @param interactor интерактор экрана участников диалога.
 * @param filter фильтр для CRUD-фасада.
 * @param recipientSelectionManager менджер для работы с выбором получателей.
 * @param subscriptionManager менеджер для управления подписками и событиями контроллера.
 * @param networkUtils @SelfDocumented.
 */
internal class DialogParticipantsPresenter constructor(
    private val conversationUuid: UUID,
    private val isNewDialog: Boolean,
    private val isFromCollage: Boolean,
    private val videoCallParticipantsUuids: ArrayList<UUID>?,
    private val interactor: DialogParticipantsInteractor,
    filter: ThemeParticipantsListFilter,
    private val recipientSelectionManager: RecipientSelectionResultManager,
    subscriptionManager: SubscriptionManager,
    networkUtils: NetworkUtils,
    scrollHelper: ScrollHelper
) : BaseSearchableListAbstractTwoWayPaginationPresenter<DialogParticipantsViewContract.View,
        ThemeParticipantListItem,
        ThemeParticipantsListFilter,
        ThemeParticipantsFilter,
        DataRefreshedThemeParticipantsControllerCallback>(filter, subscriptionManager, networkUtils, scrollHelper),
    DialogParticipantsViewContract.Presenter {

    companion object {
        private const val DIALOG_PARTICIPANTS_EVENT_NAME = "DialogParticipantsSyncEvent"
        private const val NETWORK_EVENT_KEY = "network"
        private const val AVAILABLE_EVENT_VALUE = "available"
        private const val ERROR_EVENT_KEY = "error"
    }

    private val disposer = CompositeDisposable()
    private val initialRefreshCallbackSubscription = CompositeDisposable()
    private var subscribed: Boolean = false

    init {
        mFilter.theme = conversationUuid

        if (!isNewDialog && !isFromCollage) {
            val subscriptionHolder = mutableListOf<Subscription>()
            refreshCallbackSubscription
                .map { subscriptionHolder.add(it) }
                .doOnDispose { subscriptionHolder.firstOrNull()?.disable() }
                .subscribe()
                .storeIn(initialRefreshCallbackSubscription)
        }
        updateDataList(true)
    }

    /** @SelfDocumented */
    override fun onRefreshCallback(params: HashMap<String, String>?) {
        when {
            params?.get(NETWORK_EVENT_KEY) == AVAILABLE_EVENT_VALUE -> {
                runOnUiThread { onNetworkConnected() }
            }
            params?.get(ERROR_EVENT_KEY) == NETWORK_EVENT_KEY -> {
                runOnUiThread { notifySyncFailed(RCommon.string.common_update_error, true) }
            }
            else -> { super.onRefreshCallback(params) }
        }
    }

    /** @SelfDocumented */
    override fun getEmptyViewErrorId(): Int = RDesign.string.design_empty_search_error_string

    /** @SelfDocumented */
    override fun isNeedToDisplayViewState(): Boolean = true

    /** @SelfDocumented */
    override fun displayViewState(view: DialogParticipantsViewContract.View) {
        super.displayViewState(view)
        view.updateDataList(dataList, mDataListOffset)
    }

    /** @SelfDocumented */
    override fun getListObservableCommand(): BaseListObservableCommand<out PagedListResult<ThemeParticipantListItem>, ThemeParticipantsFilter, DataRefreshedThemeParticipantsControllerCallback> =
        interactor.themeParticipantsCommandWrapper.listCommand

    /** @SelfDocumented */
    override fun onEvent(eventData: EventData) {
        super.onEvent(eventData)
        if (eventData.isEvent(DIALOG_PARTICIPANTS_EVENT_NAME)) {
            updateDataList(false)
        }
    }

    /** @SelfDocumented */
    override fun configureSubscriptions(batch: SubscriptionManager.Batch) {
        super.configureSubscriptions(batch)
        batch.subscribeOn(DIALOG_PARTICIPANTS_EVENT_NAME)
    }

    /** @SelfDocumented */
    override fun onDestroy() {
        disposer.dispose()
        super.onDestroy()
    }

    /** @SelfDocumented */
    override fun showEmptyViewIfNeeded(
        view: DialogParticipantsViewContract.View,
        dataList: MutableList<ThemeParticipantListItem>?,
        errorMessageRes: Int,
        errorDetailsRes: Int
    ) {
        super.showEmptyViewIfNeeded(view, dataList, errorMessageRes, errorDetailsRes)
        dataList?.let {
            if (!mShowOlderProgress && it.isEmpty()) {
                mView?.showMessageInEmptyView(RCommunicatorDesign.string.communicator_no_dialog_participants_to_display)
            }
        }
    }

    override fun getEmptyViewLoadingErrorCommentId(): Int =
        RCommon.string.common_no_network_available_check_connection

    override fun updateDataList(fromPullToRefresh: Boolean) {
        when {
            !videoCallParticipantsUuids.isNullOrEmpty() -> {
                interactor.getThemeParticipantList(videoCallParticipantsUuids)
                    .subscribeOnParticipantsResult()
                    .storeIn(disposer)
            }
            isNewDialog -> {
                val recipientUuids = recipientSelectionManager.selectionResult.data.allPersonsUuids
                if (recipientUuids.isNotEmpty()) {
                    interactor.getThemeParticipantList(recipientUuids)
                } else {
                    interactor.getRelevantMessageReceivers(conversationUuid)
                }
                    .subscribeOnParticipantsResult()
                    .storeIn(disposer)
            }
            isFromCollage -> {
                interactor.getRelevantMessageReceivers(conversationUuid)
                    .subscribeOnParticipantsResult()
                    .storeIn(disposer)
            }
            else -> {
                super.updateDataList(fromPullToRefresh)
            }
        }
    }

    private fun Single<List<ThemeParticipantListItem.ThemeParticipant>>.subscribeOnParticipantsResult(): Disposable =
        doFinally { mView?.hideLoading() }
            .subscribe(
                {
                    mDataList = it // чтобы список восстановился после поворота экрана
                    mView?.setParticipants(it)
                    mView?.updateListPaddingsIfNeed()
                },
                {
                    Timber.e(it)
                }
            )

    /** @SelfDocumented */
    override fun onItemClick(profileUuid: UUID) {
        when {
            isFromCollage -> onItemPhotoClick(profileUuid)
            else -> mView?.finishWithUuidResult(profileUuid)
        }
    }

    /** @SelfDocumented */
    override fun onItemPhotoClick(profileUuid: UUID) {
        DebounceActionHandler.INSTANCE.handle {
            mView?.openProfile(profileUuid)
        }
    }

    override fun onStartConversationClick(profileUuid: UUID) {
        mView?.startConversation(profileUuid)
    }

    override fun onStartVideoCallClick(profileUuid: UUID) {
        mView?.startCall(profileUuid)
    }

    /** @SelfDocumented */
    override fun isNewDialog() = isNewDialog

    override fun getDataRefreshCallback(): DataRefreshedThemeParticipantsControllerCallback {
        return object : DataRefreshedThemeParticipantsControllerCallback() {
            override fun onEvent(param: HashMap<String, String>) {
                onRefreshCallback(param)
            }
        }
    }

    override fun attachView(view: DialogParticipantsViewContract.View) {
        superAttachView(view)
    }

    /**
     * Копипаста super.AttachView() чтобы убрать лишний вызов list на контроллер
     */
    @Suppress("ProtectedInFinal")
    protected fun superAttachView(view: DialogParticipantsViewContract.View) {
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
}
