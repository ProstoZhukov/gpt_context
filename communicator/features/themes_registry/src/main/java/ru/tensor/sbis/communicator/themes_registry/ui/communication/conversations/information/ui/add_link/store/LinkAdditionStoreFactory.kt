package ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.information.ui.add_link.store

import com.arkivanov.essenty.statekeeper.StateKeeper
import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.SimpleBootstrapper
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import kotlinx.coroutines.launch
import ru.tensor.sbis.common.generated.ErrorCode
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.information.ui.add_link.store.LinkAdditionStore.Intent
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.information.ui.add_link.store.LinkAdditionStore.Label
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.information.ui.add_link.store.LinkAdditionStore.State
import ru.tensor.sbis.communicator.themes_registry.ui.communication.conversations.information.ui.links_list.store.ConversationLinksListInteractor
import timber.log.Timber
import javax.inject.Inject
import ru.tensor.sbis.mvi_extension.create as createWithStateKeeper

/**
 * Фабрика [LinkAdditionStore]
 * Содержит реализацию бизнес-логики экрана добавления ссылки.
 *
 * @author dv.baranov
 */
internal class LinkAdditionStoreFactory @Inject constructor(
    private val storeFactory: StoreFactory,
    private val interactor: ConversationLinksListInteractor
) {

    /** @SelfDocumented */
    fun create(stateKeeper: StateKeeper): LinkAdditionStore =
        object :
            LinkAdditionStore,
            Store<Intent, State, Label> by storeFactory.createWithStateKeeper(
                stateKeeper = stateKeeper,
                name = STORE_NAME,
                initialState = State(),
                bootstrapper = SimpleBootstrapper(),
                executorFactory = ::ExecutorImpl,
                reducer = ReducerImpl
            ) {}

    private sealed interface Action

    private sealed interface Message {

        /** Изменение значения в поле ввода. */
        data class InputValueChanged(val inputValue: String) : Message
    }

    private inner class ExecutorImpl : CoroutineExecutor<Intent, Action, State, Message, Label>() {

        override fun executeAction(action: Action, getState: () -> State) {}

        override fun executeIntent(intent: Intent, getState: () -> State) = when (intent) {
            is Intent.InputValueChanged -> {
                dispatch(Message.InputValueChanged(intent.inputValue))
            }
            Intent.SaveLink -> {
                scope.launch {
                    runCatching {
                        val status = interactor.add(getState().inputValue.trim())
                        if (status.errorCode == ErrorCode.SUCCESS) {
                            publish(Label.Close)
                        }
                    }.onFailure { error -> Timber.e(error) }
                }
                Unit
            }
        }
    }

    private object ReducerImpl : Reducer<State, Message> {
        override fun State.reduce(msg: Message): State = when (msg) {
            is Message.InputValueChanged -> copy(inputValue = msg.inputValue)
        }
    }
}

private const val STORE_NAME = "STORE_NAME"