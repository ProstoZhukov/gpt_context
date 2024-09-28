package ru.tensor.sbis.business_card_list.store

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.SimpleBootstrapper
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import ru.tensor.sbis.business_card_list.contract.BusinessCardListInteractor
import ru.tensor.sbis.business_card_list.contract.internal.list.BusinessCardListStore
import ru.tensor.sbis.business_card_list.contract.internal.list.BusinessCardListStore.Intent
import ru.tensor.sbis.business_card_list.contract.internal.list.BusinessCardListStore.Label
import ru.tensor.sbis.business_card_list.contract.internal.list.BusinessCardListStore.State
import ru.tensor.sbis.business_card_list.di.view.BusinessCardListViewModel
import ru.tensor.sbis.business_card_list.domain.command.BusinessCardListFilter
import ru.tensor.sbis.business_card_list.domain.command.BusinessCardListStubFactory
import ru.tensor.sbis.business_card_list.presentation.view.ClicksWrapper
import java.util.UUID
import javax.inject.Inject

/** Фабрика создающая [BusinessCardListStore], также здесь определяется логика обработки всех событий */
internal class BusinessCardListStoreFactory @Inject constructor(
    private val storeFactory: StoreFactory,
    private val queueFilter: BusinessCardListFilter,
    private val listViewModel: BusinessCardListViewModel,
    private val clicksWrapper: ClicksWrapper,
    private val stubFactory: BusinessCardListStubFactory,
    private val businessCardInteractor: BusinessCardListInteractor
) {

    /**
     * Метод создания store
     */
    fun create(): BusinessCardListStore =
        object : BusinessCardListStore,
            Store<Intent, State, Label> by storeFactory.create(
                name = "BusinessCardListStoreFactory",
                initialState = State(),
                executorFactory = {
                    ExecutorImpl(
                        listViewModel,
                        queueFilter,
                        clicksWrapper,
                        stubFactory,
                        businessCardInteractor
                    )
                },
                reducer = ReducerImpl(),
                bootstrapper = SimpleBootstrapper(Action.Init),
            ) {}

    internal class ExecutorImpl(
        private val listViewModel: BusinessCardListViewModel,
        businessCardFilter: BusinessCardListFilter,
        private val clicksWrapper: ClicksWrapper,
        private val stubFactory: BusinessCardListStubFactory,
        private val businessCardInteractor: BusinessCardListInteractor
    ) : CoroutineExecutor<Intent, Action, State, Message, Label>() {

        private val exceptionHandler = CoroutineExceptionHandler { _, error ->
            val errorMessage = error.localizedMessage ?: error.message ?: "Unknown error"
            publish(Label.ShowPinError(errorMessage))
            error.printStackTrace()
        }

        override fun executeAction(action: Action, getState: () -> State) {
            if (action == Action.Init) {
                listOf(
                    clicksWrapper.linkShareClicks.onEach { publish(Label.ToLinkShare(it)) },
                    clicksWrapper.businessCardClicks.onEach { publish(Label.NavigateToSingleCard(it)) },
                    clicksWrapper.businessCardPinnedClicks.onEach { onPinnedClick(it.first, it.second) }
                ).forEach { it.launchIn(scope) }
            }
        }

        override fun executeIntent(intent: Intent, getState: () -> State) = intent.handle(this)

        /**@SelfDocumented*/
        internal fun back() = publish(Label.NavigateBack)

        private fun onPinnedClick(businessCardUuid: UUID, pinState: Boolean) {
            scope.launch(exceptionHandler) {
                // Ожидаем выполнение операции
                businessCardInteractor.onPinStateChanged(businessCardUuid, pinState)
            }
            listViewModel.refresh()
        }
    }


    /**@SelfDocumented*/
    internal sealed interface Action {
        object Init : Action
    }

    private class ReducerImpl : Reducer<State, Message> {
        private val handlers: Map<Message, (State) -> State> = mapOf(
            Message.InitList to { state -> state.copy(needToInitList = true) }
        )

        override fun State.reduce(msg: Message): State {
            return handlers[msg]?.invoke(this) ?: this
        }
    }

    /**@SelfDocumented*/
    internal sealed interface Message {
        object InitList : Message
    }
}