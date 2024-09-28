package ru.tensor.sbis.communicator.communicator_crm_chat_list.filter.connections.store

import com.arkivanov.essenty.statekeeper.StateKeeper
import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.SimpleBootstrapper
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import ru.tensor.sbis.communicator.communicator_crm_chat_list.filter.connections.data.CRMConnectionFilterHolder
import ru.tensor.sbis.communicator.communicator_crm_chat_list.filter.connections.store.CRMConnectionListStore.Intent
import ru.tensor.sbis.communicator.communicator_crm_chat_list.filter.connections.store.CRMConnectionListStore.Label
import ru.tensor.sbis.communicator.communicator_crm_chat_list.filter.connections.store.CRMConnectionListStore.State
import ru.tensor.sbis.communicator.communicator_crm_chat_list.filter.connections.ui.CRMConnectionListComponentFactory
import ru.tensor.sbis.mvi_extension.AndroidStoreFactory
import javax.inject.Inject
import ru.tensor.sbis.mvi_extension.create as createWithStateKeeper

/**
 * Фабрика стора источников CRM.
 *
 * @author da.zhukov
 */
internal class CRMConnectionListStoreFactory @Inject constructor(
    private val storeFactory: StoreFactory,
    private val listComponentFactory: CRMConnectionListComponentFactory,
    private val crmConnectionFilterHolder: CRMConnectionFilterHolder
) {

    fun create(stateKeeper: StateKeeper): CRMConnectionListStore =
        object : CRMConnectionListStore,
            Store<Intent, State, Label> by AndroidStoreFactory(storeFactory).createWithStateKeeper(
                name = "CRMConnectionListStore",
                stateKeeper = stateKeeper,
                initialState = State(),
                bootstrapper = SimpleBootstrapper(),
                executorFactory = {
                    ExecutorImpl(
                        listComponentFactory,
                        crmConnectionFilterHolder
                    )
                },
                reducer = ReducerImpl
            ) {}

    private sealed interface Action

    private sealed interface Message {
        data class UpdateSearchQuery(val query: String?) : Message
    }

    private class ExecutorImpl(
        private val listComponentFactory: CRMConnectionListComponentFactory,
        private val crmConnectionFilterHolder: CRMConnectionFilterHolder
    ) : CoroutineExecutor<Intent, Action, State, Message, Label>() {
        override fun executeIntent(intent: Intent, getState: () -> State) = when (intent) {
            is Intent.SearchQuery -> {
                crmConnectionFilterHolder.setQuery(intent.query)
                listComponentFactory.get()?.reset()
                dispatch(Message.UpdateSearchQuery(intent.query))
            }
            is Intent.ItemSelected -> {
                publish(Label.ItemSelected(intent.idAndLabel))
            }
        }


        override fun executeAction(action: Action, getState: () -> State) {
        }
    }

    private object ReducerImpl : Reducer<State, Message> {
        override fun State.reduce(msg: Message): State =
            when (msg) {
                is Message.UpdateSearchQuery -> copy(query = msg.query)
            }
    }
}
        