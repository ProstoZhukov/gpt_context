package ru.tensor.sbis.communicator.communicator_crm_chat_list.store

import com.arkivanov.essenty.statekeeper.StateKeeper
import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.SimpleBootstrapper
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import kotlinx.coroutines.launch
import kotlinx.coroutines.rx2.asFlow
import ru.tensor.sbis.communication_decl.crm.CRMConsultationCase
import ru.tensor.sbis.communication_decl.crm.CRMConsultationOpenParams
import ru.tensor.sbis.communicator.communicator_crm_chat_list.CRMChatListPlugin
import ru.tensor.sbis.communicator.communicator_crm_chat_list.data.CRMChatListFilterHolder
import ru.tensor.sbis.communicator.communicator_crm_chat_list.data.helper.CRMCollectionSynchronizeHelper
import ru.tensor.sbis.communicator.communicator_crm_chat_list.store.CRMChatListStore.Intent
import ru.tensor.sbis.communicator.communicator_crm_chat_list.store.CRMChatListStore.Label
import ru.tensor.sbis.communicator.communicator_crm_chat_list.store.CRMChatListStore.State
import ru.tensor.sbis.communicator.communicator_crm_chat_list.ui.CRMChatListComponentFactory
import ru.tensor.sbis.communicator.communicator_crm_chat_list.ui.folders.CRMChatListFoldersInteractor
import ru.tensor.sbis.communicator.communicator_crm_chat_list.utils.launchAndCollect
import ru.tensor.sbis.communicator.communicator_crm_chat_list.utils.swipe_menu.CompleteMenuItem
import ru.tensor.sbis.communicator.communicator_crm_chat_list.utils.swipe_menu.DeleteMenuItem
import ru.tensor.sbis.communicator.communicator_crm_chat_list.utils.swipe_menu.MenuItem
import ru.tensor.sbis.communicator.communicator_crm_chat_list.utils.swipe_menu.TakeMenuItem
import ru.tensor.sbis.communicator.declaration.crm.model.CRMChatFilterModel
import ru.tensor.sbis.consultations.generated.ConsultationGroupType
import ru.tensor.sbis.consultations.generated.ConsultationGroupType.UNKNOWN
import ru.tensor.sbis.consultations.generated.ConsultationGroupType.WITHOUT_OPERATOR
import ru.tensor.sbis.design.SbisMobileIcon
import ru.tensor.sbis.design_notification.popup.SbisPopupNotificationStyle
import ru.tensor.sbis.mvi_extension.create
import ru.tensor.sbis.communicator.design.R as RCommunicatorDesign

/**
 * Фабрика стора чатов CRM.
 *
 * @author da.zhukov
 */
internal class CRMChatListStoreFactory(
    private val storeFactory: StoreFactory,
    private val listComponentFactory: CRMChatListComponentFactory,
    private val filterHolder: CRMChatListFilterHolder,
    private val crmChatListInteractor: CRMChatListInteractor,
    private val collectionSynchronizeHelper: CRMCollectionSynchronizeHelper,
    private val crmChatListFoldersInteractor: CRMChatListFoldersInteractor
) {

    /** @SelfDocumented */
    fun create(stateKeeper: StateKeeper): CRMChatListStore =
        object :
            CRMChatListStore,
            Store<Intent, State, Label> by storeFactory.create(
                stateKeeper = stateKeeper,
                name = CRM_CHAT_LIST_STORE_NAME,
                initialState = State(filterModel = filterHolder.getCurrentFilterModel(), filters = filterHolder.getCurrentFilters()),
                bootstrapper = SimpleBootstrapper(
                    Action.LoadData,
                    Action.InitSubscriptions,
                    Action.CheckNetwork
                ),
                executorFactory = {
                    ExecutorImpl(
                        listComponentFactory,
                        filterHolder,
                        crmChatListInteractor,
                        collectionSynchronizeHelper,
                        crmChatListFoldersInteractor
                    )
                },
                reducer = ReducerImpl()
            ) {}

    private sealed interface Action {
        object LoadData : Action
        object InitSubscriptions : Action
        object CheckNetwork : Action
    }

    private sealed interface Message {
        data class UpdateSearchQuery(val query: String?) : Message
        data class UpdateSearchQueryFilters(val filters: List<String>) : Message
        data class UpdateFilterModel(val filterModel: CRMChatFilterModel) : Message
        data class UpdateGroupType(val groupType: ConsultationGroupType) : Message
        data class UpdateFolderTitle(val folderTitle: String) : Message
        object UpdateSearchPanelState : Message
        data class ChangeTakeOldestFabVisibility(val show: Boolean) : Message
    }

    private class ExecutorImpl(
        private val listComponentFactory: CRMChatListComponentFactory,
        private val filter: CRMChatListFilterHolder,
        private val crmChatListInteractor: CRMChatListInteractor,
        private val collectionSynchronizeHelper: CRMCollectionSynchronizeHelper,
        private val crmChatListFoldersInteractor: CRMChatListFoldersInteractor
    ) : CoroutineExecutor<Intent, Action, State, Message, Label>() {

        val networkLabel by lazy {
            Label.ShowInformer(
                RCommunicatorDesign.string.communicator_sync_error_message,
                SbisPopupNotificationStyle.ERROR,
                SbisMobileIcon.Icon.smi_WiFiNone.character.toString()
            )
        }
        val availableFiltersForShowFab by lazy { setOf(UNKNOWN, WITHOUT_OPERATOR) }

        override fun executeAction(action: Action, getState: () -> State) = when (action) {
            Action.LoadData -> executeIntent(Intent.InitialLoading())
            Action.InitSubscriptions -> initSubscriptions()
            Action.CheckNetwork -> checkNetworkState()
        }

        private fun initSubscriptions() {
            scope.launch {
                launch {
                    CRMChatListPlugin.networkUtils.get().networkStateObservable().asFlow().collect {
                        if (!it) publish(networkLabel)
                    }
                }
                launchAndCollect(crmChatListInteractor.onTakeOldestButtonVisibilityFlow()) {
                    dispatch(Message.ChangeTakeOldestFabVisibility(it))
                }
            }
        }

        private fun checkNetworkState() {
            scope.launch {
                if (!CRMChatListPlugin.networkUtils.get().isConnected) publish(networkLabel)
            }
        }

        override fun executeIntent(intent: Intent, getState: () -> State) = when (intent) {
            is Intent.InitialLoading -> {
                listComponentFactory.get()?.reset()
                dispatch(Message.UpdateSearchQuery(intent.query))
                dispatch(Message.UpdateGroupType(intent.groupType))
            }
            is Intent.SearchQuery -> {
                filter.setQuery(intent.query)
                listComponentFactory.get()?.reset()
                dispatch(Message.UpdateSearchQuery(intent.query))
            }
            is Intent.ChangeCurrentFolder -> {
                filter.setGroupType(intent.groupType)
                collectionSynchronizeHelper.cancel()
                listComponentFactory.get()?.reset()
                dispatch(Message.UpdateGroupType(intent.groupType))
                dispatch(Message.UpdateFolderTitle(intent.folderTitle))
                checkShowTakeOldestFab()
            }
            is Intent.HandleSwipeMenuItemClick -> handleSwipeMenuItemClick(intent.menuItem)
            is Intent.OpenConsultation -> publish(Label.OpenConsultation(intent.consultationParams))
            is Intent.ShowInformer -> publish(Label.ShowInformer(intent.msg, intent.style, intent.icon))
            is Intent.CreateConsultation -> publish(Label.CreateConsultation(intent.consultationParams))
            is Intent.OpenSearchPanel -> dispatch(Message.UpdateSearchPanelState)
            is Intent.OpenFilters -> publish(Label.OpenFilters(intent.filterModel))
            is Intent.ApplyFilter -> {
                filter.applyFilter(intent.filterModel)
                filter.applyFilterTitle(intent.filters)
                crmChatListFoldersInteractor.updateFolders()
                collectionSynchronizeHelper.cancel()
                listComponentFactory.get()?.reset()
                dispatch(Message.UpdateSearchQueryFilters(intent.filters))
                dispatch(Message.UpdateFilterModel(intent.filterModel))
            }
            is Intent.CheckShowTakeOldestFab -> checkShowTakeOldestFab()
            is Intent.TakeOldestConsultation -> {
                scope.launch {
                    val uuid = crmChatListInteractor.takeOldest()
                    uuid?.let {
                        publish(
                            Label.OpenConsultation(
                                CRMConsultationOpenParams(
                                    crmConsultationCase = CRMConsultationCase.Operator(
                                        originUuid = it,
                                        viewId = filter.viewId
                                    ),
                                    isCompleted = false,
                                    needBackButton = intent.needBackButton,
                                    isHistoryMode = false
                                )
                            )
                        )
                    }
                }
                Unit
            }
            is Intent.TakeOldestFabVisibilityChanged -> publish(Label.ChangeTakeOldestFabVisibility(intent.isVisible))
        }

        private fun handleSwipeMenuItemClick(item: MenuItem) {
            when (item) {
                is DeleteMenuItem -> scope.launch {
                    runCatching { deleteConsultation(item) }.onFailure(::onError)
                }
                is TakeMenuItem -> scope.launch {
                    runCatching { takeConsultation(item) }.onFailure(::onError)
                }
                is CompleteMenuItem -> scope.launch {
                    runCatching { completeConsultation(item) }.onFailure(::onError)
                }
            }
        }

        private suspend fun deleteConsultation(item: MenuItem) {
            crmChatListInteractor.deleteConsultation(item.consultationId)
        }

        private suspend fun takeConsultation(item: MenuItem) {
            crmChatListInteractor.takeConsultation(item.consultationId)
        }

        private suspend fun completeConsultation(item: MenuItem) {
            crmChatListInteractor.completeConsultation(item.consultationId)
        }

        private fun onError(error: Throwable) {
            // Пока свели логику к ios, после доработки на БЛ будем обрабатывать конкретные исключения.
            publish(networkLabel)
        }

        private fun checkShowTakeOldestFab() {
            val filter = filter.invoke()
            if (availableFiltersForShowFab.contains(filter.groupType) && filter.clientIds.isEmpty()) {
                scope.launch {
                    val isTakeOldest = crmChatListInteractor.getIsTakeOldest()
                    dispatch(Message.ChangeTakeOldestFabVisibility(isTakeOldest))
                }
            } else {
                dispatch(Message.ChangeTakeOldestFabVisibility(false))
            }
        }
    }

    private class ReducerImpl : Reducer<State, Message> {
        override fun State.reduce(msg: Message): State = when (msg) {
            is Message.UpdateSearchQuery -> copy(query = msg.query)
            is Message.UpdateGroupType -> copy(groupType = msg.groupType)
            is Message.UpdateFolderTitle -> copy(folderTitle = msg.folderTitle)
            is Message.UpdateSearchPanelState -> copy(searchPanelIsOpen = true)
            is Message.UpdateSearchQueryFilters -> copy(filters = msg.filters)
            is Message.UpdateFilterModel -> copy(filterModel = msg.filterModel)
            is Message.ChangeTakeOldestFabVisibility -> copy(fabVisible = msg.show)
        }
    }
}
const val CRM_CHAT_LIST_STORE_NAME = "CRMChatListStore"
