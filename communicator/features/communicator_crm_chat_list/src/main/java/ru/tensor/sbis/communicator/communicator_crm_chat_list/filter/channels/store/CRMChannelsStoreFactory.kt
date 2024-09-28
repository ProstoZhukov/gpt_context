package ru.tensor.sbis.communicator.communicator_crm_chat_list.filter.channels.store

import com.arkivanov.essenty.statekeeper.StateKeeper
import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.SimpleBootstrapper
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import kotlinx.coroutines.launch
import ru.tensor.sbis.communication_decl.crm.CrmChannelFilterType
import ru.tensor.sbis.communication_decl.crm.CrmChannelListCase
import ru.tensor.sbis.communication_decl.crm.CrmChannelListCase.CrmChannelConsultationCase
import ru.tensor.sbis.communication_decl.crm.CrmChannelListCase.CrmChannelFilterCase
import ru.tensor.sbis.communication_decl.crm.CrmChannelListCase.CrmChannelReassignCase
import ru.tensor.sbis.communicator.common.util.castTo
import ru.tensor.sbis.communicator.communicator_crm_chat_list.filter.channels.data.CRMChannelsFilterHolder
import ru.tensor.sbis.communicator.communicator_crm_chat_list.filter.channels.store.CRMChannelsStore.Intent
import ru.tensor.sbis.communicator.communicator_crm_chat_list.filter.channels.store.CRMChannelsStore.Label
import ru.tensor.sbis.communicator.communicator_crm_chat_list.filter.channels.store.CRMChannelsStore.State
import ru.tensor.sbis.communicator.communicator_crm_chat_list.filter.channels.ui.CRMChannelsListComponentFactory
import ru.tensor.sbis.communicator.communicator_crm_chat_list.ui.CRMChatListNotificationHelper
import ru.tensor.sbis.communicator.communicator_crm_chat_list.utils.toCrmChannelType
import ru.tensor.sbis.consultations.generated.ConsultationException
import ru.tensor.sbis.consultations.generated.SyncErrorCode
import ru.tensor.sbis.mvi_extension.create
import java.util.UUID

/**
 * Фабрика стора переназначения чата CRM.
 *
 * @author da.zhukov
 */
internal class CRMChannelsStoreFactory(
    private val storeFactory: StoreFactory,
    private val listComponentFactory: CRMChannelsListComponentFactory,
    private val filterHolder: CRMChannelsFilterHolder,
    private val crmChannelsInteractor: CRMChannelsInteractor,
    private val case: CrmChannelListCase,
    private val notificationHelper: CRMChatListNotificationHelper
) {

    /** @SelfDocumented */
    fun create(stateKeeper: StateKeeper): CRMChannelsStore = object :
        CRMChannelsStore,
        Store<Intent, State, Label> by storeFactory.create(
            stateKeeper = stateKeeper,
            name = CRM_REASSIGN_STORE_NAME,
            initialState = State(),
            bootstrapper = SimpleBootstrapper(),
            executorFactory = {
                ExecutorImpl(
                    listComponentFactory,
                    filterHolder,
                    crmChannelsInteractor,
                    case,
                    notificationHelper
                )
            },
            reducer = ReducerImpl()
        ) {}

    private sealed interface Action

    private sealed interface Message {
        data class UpdateSearchQuery(val query: String?) : Message
        data class UpdateCurrentFolderView(val folderTitle: String, val currentFolderViewIsVisible: Boolean) : Message
    }

    private class ExecutorImpl(
        private val listComponentFactory: CRMChannelsListComponentFactory,
        private val filter: CRMChannelsFilterHolder,
        private val crmChannelsInteractor: CRMChannelsInteractor,
        private val case: CrmChannelListCase,
        private val notificationHelper: CRMChatListNotificationHelper
    ) : CoroutineExecutor<Intent, Action, State, Message, Label>() {

        override fun executeIntent(intent: Intent, getState: () -> State) = when (intent) {
            is Intent.InitialLoading -> {
                listComponentFactory.get()?.reset()
                dispatch(Message.UpdateSearchQuery(intent.query))
                dispatch(Message.UpdateCurrentFolderView(intent.folderTitle, intent.currentFolderViewIsVisible))
            }
            is Intent.OnItemSuccessClick -> {
                if (case is CrmChannelReassignCase) {
                    reassignConsultation(
                        intent.id,
                        intent.parentId
                    )
                } else {
                    publish(Label.OnChannelOperatorItemClick(intent.id, intent.channelName))
                }
            }
            is Intent.OnItemClick -> {
                if (intent.needOpenFolder) {
                    filter.setParentId(arrayListOf(intent.id))
                    filter.setGroupType(intent.groupType)
                    filter.setQuery(null)
                    listComponentFactory.get()?.reset()
                    dispatch(Message.UpdateSearchQuery(null))
                } else {
                    when (case) {
                        is CrmChannelReassignCase -> {
                            reassignConsultation(
                                intent.channelId,
                                intent.operatorGroupId
                            )
                        }
                        is CrmChannelConsultationCase -> {
                            publish(
                                Label.OnChannelConsultationItemClick(
                                    Triple(
                                        case.originUuid,
                                        intent.id,
                                        intent.itemType.toCrmChannelType()
                                    )
                                )
                            )
                        }
                        is CrmChannelFilterCase -> {
                            if (case.type == CrmChannelFilterType.OPERATOR) {
                                publish(Label.OnChannelOperatorItemClick(intent.id, intent.name))
                            } else {
                                Unit
                            }
                        }
                        else -> Unit
                    }
                }
            }
            is Intent.OnItemCheckClick -> {
                publish(Label.OnItemCheckClick(intent.result))
            }
            is Intent.SearchQuery -> {
                onChangeQuery(intent.query)
            }
            is Intent.UpdateCurrentFolderView -> {
                dispatch(
                    Message.UpdateCurrentFolderView(
                        intent.folderTitle,
                        intent.currentFolderViewIsVisible
                    )
                )
            }
            is Intent.BackButtonClick -> {
                publish(Label.BackButtonClick)
            }
            is Intent.CurrentFolderViewClick -> {
                val parentId = intent.parentId?.let { arrayListOf(intent.parentId) } ?: arrayListOf()
                filter.setParentId(parentId)
                filter.setGroupType(intent.groupType)
                listComponentFactory.get()?.reset()
                dispatch(Message.UpdateCurrentFolderView(intent.parentName, intent.needShowFolder))
            }
        }

        private fun reassignConsultation(
            channelId: UUID?,
            operatorGroupId: UUID?
        ) {
            if (case !is CrmChannelFilterCase) {
                scope.launch {
                    runCatching {
                        crmChannelsInteractor.reassignConsultation(
                            channelId,
                            operatorGroupId
                        )
                    }.onFailure(::onError).onSuccess {
                        publish(Label.BackButtonClick)
                    }
                }
            }
        }

        private fun onChangeQuery(newQuery: String?) {
            if (filter.invoke().searchStr != newQuery) {
                filter.setQuery(newQuery)
                listComponentFactory.get()?.reset()
                dispatch(Message.UpdateSearchQuery(newQuery))
            }
        }

        private fun onError(error: Throwable) {
            error.castTo<ConsultationException>()?.let {
                when (it.code) {
                    SyncErrorCode.NO_NETWORK -> notificationHelper.showNetworkError()
                    SyncErrorCode.NO_RIGHTS, SyncErrorCode.OTHER -> Unit
                }
            }
        }
    }

    private class ReducerImpl : Reducer<State, Message> {
        override fun State.reduce(msg: Message): State = when (msg) {
            is Message.UpdateSearchQuery -> copy(query = msg.query)
            is Message.UpdateCurrentFolderView -> copy(
                currentFolderViewIsVisible = msg.currentFolderViewIsVisible,
                folderTitle = msg.folderTitle
            )
        }
    }
}
const val CRM_REASSIGN_STORE_NAME = "CRMChannelsStore"
