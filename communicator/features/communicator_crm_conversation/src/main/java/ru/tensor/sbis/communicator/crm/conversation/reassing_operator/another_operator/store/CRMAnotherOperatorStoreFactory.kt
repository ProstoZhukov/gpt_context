package ru.tensor.sbis.communicator.crm.conversation.reassing_operator.another_operator.store

import com.arkivanov.essenty.statekeeper.StateKeeper
import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.SimpleBootstrapper
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import kotlinx.coroutines.launch
import ru.tensor.sbis.communicator.common.util.castTo
import ru.tensor.sbis.communicator.crm.conversation.reassing_operator.another_operator.data.CRMAnotherOperatorFilterHolder
import ru.tensor.sbis.communicator.crm.conversation.reassing_operator.another_operator.store.CRMAnotherOperatorStore.Intent
import ru.tensor.sbis.communicator.crm.conversation.reassing_operator.another_operator.store.CRMAnotherOperatorStore.Label
import ru.tensor.sbis.communicator.crm.conversation.reassing_operator.another_operator.store.CRMAnotherOperatorStore.State
import ru.tensor.sbis.communicator.crm.conversation.reassing_operator.another_operator.ui.CRMAnotherOperatorListComponentFactory
import ru.tensor.sbis.communicator.declaration.crm.providers.CRMAnotherOperatorParams
import ru.tensor.sbis.consultations.generated.ConsultationException
import ru.tensor.sbis.consultations.generated.SyncErrorCode
import ru.tensor.sbis.mvi_extension.create
import java.util.UUID

/**
 * Фабрика стора переназначения оператору.
 *
 * @author da.zhukov
 */
internal class CRMAnotherOperatorStoreFactory(
    private val storeFactory: StoreFactory,
    private val listComponentFactory: CRMAnotherOperatorListComponentFactory,
    private val filterHolder: CRMAnotherOperatorFilterHolder,
    private val crmAnotherOperatorInteractor: CRMAnotherOperatorInteractor,
    private val params: CRMAnotherOperatorParams
) {

    /** @SelfDocumented */
    fun create(stateKeeper: StateKeeper): CRMAnotherOperatorStore = object :
        CRMAnotherOperatorStore,
        Store<Intent, State, Label> by storeFactory.create(
            stateKeeper = stateKeeper,
            name = CRM_ANOTHER_OPERATOR_STORE_NAME,
            initialState = State(filter = params.channelName),
            bootstrapper = SimpleBootstrapper(),
            executorFactory = {
                ExecutorImpl(
                    listComponentFactory,
                    filterHolder,
                    crmAnotherOperatorInteractor
                )
            },
            reducer = ReducerImpl()
        ) {}

    private sealed interface Action

    private sealed interface Message {
        data class UpdateSearchQuery(val query: String?) : Message
        data class UpdateSearchQueryFilterr(val filter: String) : Message
    }

    private class ExecutorImpl(
        private val listComponentFactory: CRMAnotherOperatorListComponentFactory,
        private val filterHolder: CRMAnotherOperatorFilterHolder,
        private val crmAnotherOperatorInteractor: CRMAnotherOperatorInteractor
    ) : CoroutineExecutor<Intent, Action, State, Message, Label>() {

        override fun executeIntent(intent: Intent, getState: () -> State) = when (intent) {
            is Intent.InitialLoading -> {
                listComponentFactory.get()?.reset()
                dispatch(Message.UpdateSearchQuery(intent.query))
            }
            is Intent.OnItemClick -> {
                reassignConsultationToOperator(intent.operatorId)
            }
            is Intent.SearchQuery -> {
                filterHolder.setQuery(intent.query)
                listComponentFactory.get()?.reset()
                dispatch(Message.UpdateSearchQuery(intent.query))
            }
            is Intent.BackButtonClick -> {
                publish(Label.BackButtonClick)
            }
            is Intent.FilterClick -> {
                publish(Label.FilterClick)
            }
            is Intent.ApplyFilter -> {
                filterHolder.setChannel(intent.channelId)
                listComponentFactory.get()?.reset()
                dispatch(Message.UpdateSearchQueryFilterr(intent.filter))
            }
        }

        private fun reassignConsultationToOperator(operatorId: UUID) {
            scope.launch {
                runCatching {
                    crmAnotherOperatorInteractor.reassignConsultationToOperator(operatorId)
                }.onFailure(::onError).onSuccess {
                    publish(Label.BackButtonClick)
                }
            }
        }

        private fun onError(error: Throwable) {
            error.castTo<ConsultationException>()?.let {
                when (it.code) {
                    SyncErrorCode.NO_NETWORK -> publish(Label.ShowNetworkError)
                    SyncErrorCode.NO_RIGHTS, SyncErrorCode.OTHER -> Unit
                }
            }
        }
    }

    private class ReducerImpl : Reducer<State, Message> {
        override fun State.reduce(msg: Message): State = when (msg) {
            is Message.UpdateSearchQuery -> copy(query = msg.query)
            is Message.UpdateSearchQueryFilterr -> copy(filter = msg.filter)
        }
    }
}
const val CRM_ANOTHER_OPERATOR_STORE_NAME = "CRMReassignStore"