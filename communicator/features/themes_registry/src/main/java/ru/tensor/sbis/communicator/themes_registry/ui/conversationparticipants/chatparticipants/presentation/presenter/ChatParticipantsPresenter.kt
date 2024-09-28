package ru.tensor.sbis.communicator.themes_registry.ui.conversationparticipants.chatparticipants.presentation.presenter

import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import ru.tensor.sbis.common.rx.plusAssign
import ru.tensor.sbis.common.util.NetworkUtils
import ru.tensor.sbis.common.util.asArrayList
import ru.tensor.sbis.common.util.scroll.ScrollHelper
import ru.tensor.sbis.common.util.storeIn
import ru.tensor.sbis.communication_decl.selection.recipient.manager.RecipientSelectionResultManager
import ru.tensor.sbis.communicator.common.data.model.NetworkAvailability
import ru.tensor.sbis.communicator.common.util.castTo
import ru.tensor.sbis.communicator.generated.DataRefreshedThemeParticipantsControllerCallback
import ru.tensor.sbis.communicator.generated.ParticipantRole
import ru.tensor.sbis.communicator.generated.Permissions
import ru.tensor.sbis.communicator.generated.ThemeParticipantsFilter
import ru.tensor.sbis.communicator.themes_registry.ui.conversationparticipants.chatparticipants.contract.ChatParticipantsInteractor
import ru.tensor.sbis.communicator.themes_registry.ui.conversationparticipants.chatparticipants.contract.ChatParticipantsViewContract
import ru.tensor.sbis.communicator.themes_registry.ui.conversationparticipants.chatparticipants.presentation.viewmodel.ChatParticipantsViewModel
import ru.tensor.sbis.communicator.themes_registry.ui.themeParticipants.ThemeParticipantsListFilter
import ru.tensor.sbis.communicator.themes_registry.ui.themeParticipants.model.ThemeParticipantListItem
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
import ru.tensor.sbis.message_panel.R as RMessagePanel

/**
 * Presenter участников чата.
 *
 * @param conversationUuid - UUID чата
 * @param chatPermissions - разрешения на какие-либо действия в чате
 * @param viewModel - вью-модель экрана, подробнее см. [ChatParticipantsViewModel]
 * @param interactor - интерактор экрана участников чата
 * @param filter - фильтр для CRUD-фасада
 * @param recipientSelectionManager - компонент, отвечающий за получение результатов выбора получателей для последующей отправки сообщения
 * @param networkAvailability - доступность интернета
 * @param subscriptionManager - менеджер для управления подписками и событиями контроллера
 * @param networkUtils - @SelfDocumented
 * @param scrollHelper - хэлпер, рассылающий событий скролла для последующего обновления UI при необходимости
 */
internal class ChatParticipantsPresenter constructor(
    private val conversationUuid: UUID,
    chatPermissions: Permissions,
    private val viewModel: ChatParticipantsViewModel,
    private val interactor: ChatParticipantsInteractor,
    filter: ThemeParticipantsListFilter,
    private val recipientSelectionManager: RecipientSelectionResultManager,
    private val networkAvailability: NetworkAvailability,
    subscriptionManager: SubscriptionManager,
    networkUtils: NetworkUtils,
    scrollHelper: ScrollHelper,
) : BaseSearchableListAbstractTwoWayPaginationPresenter<ChatParticipantsViewContract.View,
        ThemeParticipantListItem,
        ThemeParticipantsListFilter,
        ThemeParticipantsFilter,
        DataRefreshedThemeParticipantsControllerCallback>(filter, subscriptionManager, networkUtils, scrollHelper),
    ChatParticipantsViewContract.Presenter {

    companion object {
        private const val CHAT_PARTICIPANTS_EVENT_NAME = "ChatParticipantsSyncEvent"
        private const val NETWORK_EVENT_KEY = "network"
        private const val AVAILABLE_EVENT_VALUE = "available"
        private const val ERROR_EVENT_KEY = "error"
    }

    private var administratorsCount = 0

    private val disposer = CompositeDisposable()
    private val initialRefreshCallbackSubscription = CompositeDisposable()
    private var subscribed: Boolean = false
    private var isLastItemRemoving = false

    init {
        mFilter.theme = conversationUuid
        mFilter.folderUUID = viewModel.folderUUID
        mFilter.queryBuilder()
        viewModel.chatPermissions.value = chatPermissions
        subscribeOnRecipientSelectionDone()
        loadCurrentUserUuid()

        val subscriptionHolder = mutableListOf<Subscription>()
        refreshCallbackSubscription
            .map { subscriptionHolder.add(it) }
            .doOnDispose { subscriptionHolder.firstOrNull()?.disable() }
            .subscribe()
            .storeIn(initialRefreshCallbackSubscription)
        updateDataList(true)
    }

    /** @SelfDocumented */
    override fun onRefreshCallback(params: HashMap<String, String>?) {
        when {
            params?.get(NETWORK_EVENT_KEY) == AVAILABLE_EVENT_VALUE && !networkAvailability.get() -> {
                networkAvailability.on()
                runOnUiThread { onNetworkConnected() }
            }
            params?.get(ERROR_EVENT_KEY) == NETWORK_EVENT_KEY && networkAvailability.get() -> {
                networkAvailability.off()
                runOnUiThread { notifySyncFailed(RCommon.string.common_update_error, true) }
                updateDataList(false)
            }
            else -> { super.onRefreshCallback(params) }
        }
    }

    /** @SelfDocumented */
    override fun getEmptyViewErrorId(): Int = RDesign.string.design_empty_search_error_string

    /** @SelfDocumented */
    override fun isNeedToDisplayViewState(): Boolean = true

    /** @SelfDocumented */
    override fun displayViewState(view: ChatParticipantsViewContract.View) {
        view.updateDataList(dataList, mDataListOffset)
    }

    /** @SelfDocumented */
    override fun getListObservableCommand(): BaseListObservableCommand<
        out PagedListResult<ThemeParticipantListItem>,
        ThemeParticipantsFilter,
        DataRefreshedThemeParticipantsControllerCallback,
        > = interactor.themeParticipantsCommandWrapper.listCommand

    /**
     * Получить последнюю сущность в текущем списке для выполнения обновления снизу-вверх.
     * В случае, если данный метод возвращает null, обновление выполняется сверху-вниз.
     *
     * @return последняя сущность в текущем списке либо null если требуется обновление сверху-вниз
     */
    override fun getLastEntityForUpdate(): ThemeParticipantListItem? {
        return if (mFirstVisibleItem > 0 && dataList.size > 0 && !isLastItemRemoving) getLastThemeParticipantItem() else null
    }

    /** @SelfDocumented */
    override fun onEvent(eventData: EventData) {
        super.onEvent(eventData)
        if (eventData.isEvent(CHAT_PARTICIPANTS_EVENT_NAME)) {
            updateDataList(false)
        }
    }

    /** @SelfDocumented */
    override fun configureSubscriptions(batch: SubscriptionManager.Batch) {
        super.configureSubscriptions(batch)
        batch.subscribeOn(CHAT_PARTICIPANTS_EVENT_NAME)
    }

    /** @SelfDocumented */
    override fun onDestroy() {
        disposer.dispose()
        super.onDestroy()
    }

    /** @SelfDocumented */
    override fun showEmptyViewIfNeeded(
        view: ChatParticipantsViewContract.View,
        dataList: MutableList<ThemeParticipantListItem>?,
        errorMessageRes: Int,
        errorDetailsRes: Int,
    ) {
        dataList?.let {
            if (!mShowOlderProgress && it.isEmpty()) {
                mView?.showMessageInEmptyView(RCommunicatorDesign.string.communicator_no_dialog_participants_to_display)
            }
        } ?: super.showEmptyViewIfNeeded(view, dataList, errorMessageRes, errorDetailsRes)
    }

    override fun getEmptyViewLoadingErrorCommentId(): Int =
        RCommon.string.common_no_network_available_check_connection

    /** @SelfDocumented */
    override fun onAddClick() {
        mView?.showRecipientsSelection(conversationUuid)
    }

    /** @SelfDocumented */
    override fun onItemClick(profileUuid: UUID) {
        mView?.finishWithUuidResult(profileUuid)
    }

    /** @SelfDocumented */
    override fun onItemPhotoClick(profileUuid: UUID) {
        mView?.openProfile(profileUuid)
    }

    /** @SelfDocumented */
    override fun onChangeAdminStatusClick(chatParticipant: ThemeParticipantListItem.ThemeParticipant) {
        viewModel.isSwipeEnabled.value = false
        calcCurrentAdminsCount()

        with(chatParticipant) {
            if (role == ParticipantRole.ADMIN) {
                if (administratorsCount > 1) {
                    disposer += interactor.chatAdministratorsCommandWrapper
                        .removeAdministrators(conversationUuid, arrayListOf(employeeProfile.uuid))
                        .subscribe(
                            {
                                administratorsCount--
                                updateDataList(true)
                            },
                            { mView?.showToast(RCommunicatorDesign.string.communicator_chat_remove_administrator_failure) },
                        )
                } else {
                    mView?.showToast(RCommunicatorDesign.string.communicator_chat_remove_last_admin)
                }
            } else {
                disposer += interactor.chatAdministratorsCommandWrapper
                    .addAdministrators(conversationUuid, arrayListOf(employeeProfile.uuid))
                    .subscribe(
                        {
                            administratorsCount++
                            updateDataList(true)
                        },
                        { mView?.showToast(RCommunicatorDesign.string.communicator_chat_add_single_administrator_failure) },
                    )
            }
        }
    }

    /** @SelfDocumented */
    override fun onRemoveParticipantClick(uuid: UUID, isByDismiss: Boolean) {
        if (!isByDismiss) viewModel.isSwipeEnabled.value = false

        disposer += interactor.themeParticipantsCommandWrapper
            .removeParticipants(conversationUuid, arrayListOf(uuid))
            .subscribe({
                isLastItemRemoving = dataList.filterIsInstance<ThemeParticipantListItem.ThemeParticipant>()
                    .lastOrNull()?.employeeProfile?.uuid == uuid
                updateDataList(false)
                isLastItemRemoving = false
            }, { mView?.showToast(RCommunicatorDesign.string.communicator_channel_remove_participant_failure) })
    }

    override fun onFolderClick(folder: ThemeParticipantListItem.ThemeParticipantFolder) {
        mView?.onFolderClick(folder)
    }

    private fun loadCurrentUserUuid() {
        disposer += interactor.themeParticipantsCommandWrapper
            .getCurrentUserUUID()
            .subscribe({ viewModel.currentUserUuid.value = it }, { Timber.e(it) })
    }

    private fun subscribeOnRecipientSelectionDone() {
        disposer += recipientSelectionManager
            .getSelectionResultObservable()
            .filter { result -> !result.isCanceled && result.data.allPersonsUuids.isNotEmpty() }
            .flatMap { result ->
                interactor.themeParticipantsCommandWrapper.addParticipants(conversationUuid, result.data.allPersonsUuids.asArrayList())
                    .toObservable<Unit>()
                    .materialize()
            }
            .subscribe {
                if (it.isOnComplete) {
                    updateDataList(true)
                    recipientSelectionManager.clear()
                } else if (it.isOnError) {
                    Timber.e(it.error, "Error on add chat participants.")
                    mView?.showToast(RMessagePanel.string.message_panel_chat_add_participants_failure)
                }
            }
    }

    private fun calcCurrentAdminsCount() {
        if (administratorsCount == 0) {
            dataList.filterIsInstance<ThemeParticipantListItem.ThemeParticipant>()
                .forEach { if (it.role == ParticipantRole.ADMIN) administratorsCount++ }
        }
    }

    override fun getDataRefreshCallback(): DataRefreshedThemeParticipantsControllerCallback {
        return object : DataRefreshedThemeParticipantsControllerCallback() {
            override fun onEvent(param: HashMap<String, String>) {
                onRefreshCallback(param)
            }
        }
    }

    override fun attachView(view: ChatParticipantsViewContract.View) {
        superAttachView(view)
    }

    override fun getLoadingOlderDataObservable(
        dataModel: ThemeParticipantListItem,
        itemsCount: Int,
    ): Observable<out PagedListResult<ThemeParticipantListItem>> {
        val participantModel = getLastThemeParticipantItem()
        return super.getLoadingOlderDataObservable(participantModel ?: dataModel, itemsCount)
    }

    private fun getLastThemeParticipantItem(): ThemeParticipantListItem.ThemeParticipant? =
        dataList.findLast { it is ThemeParticipantListItem.ThemeParticipant }?.castTo()

    /**
     * При загрузке списка участников канала мы на руках имеем список, состоящий из одной папки админов, т.к. её
     * сразу отдает контроллер. Поэтому получаем дублирование при подгрузке новых данных, т.к.
     * объединяются список из одной папки и нормальный список из папки и участников.
     * Чтобы избежать дублирования будем убирать лишнюю папку перед объединением.
     */
    override fun processLoadingOlderPageResult(pagedListResult: PagedListResult<ThemeParticipantListItem>) {
        if (dataList.hasFirstItemFolder() &&
            pagedListResult.dataList.firstOrNull()?.equals(dataList.firstOrNull()) == true
        ) {
            pagedListResult.dataList.removeFirstOrNull()
        }
        super.processLoadingOlderPageResult(pagedListResult)
    }

    private fun List<Any>.hasFirstItemFolder() = this.firstOrNull() is ThemeParticipantListItem.ThemeParticipantFolder

    /**
     * Копипаста super.AttachView() чтобы убрать лишний вызов list на контроллер
     */
    @Suppress("ProtectedInFinal")
    protected fun superAttachView(view: ChatParticipantsViewContract.View) {
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
