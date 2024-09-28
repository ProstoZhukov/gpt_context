package ru.tensor.sbis.business_card_host.store

import com.arkivanov.mvikotlin.core.store.SimpleBootstrapper
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import kotlinx.coroutines.launch
import ru.tensor.sbis.business_card_host.contract.internal.list.BusinessCardHostInteractor
import ru.tensor.sbis.business_card_host.contract.internal.list.BusinessCardHostStore.*
import ru.tensor.sbis.business_card_host.contract.internal.list.BusinessCardHostStore
import java.util.UUID
import javax.inject.Inject

/** Фабрика создающая [BusinessCardHostStore], также здесь определяется логика обработки всех событий */
internal class BusinessCardHostStoreFactory @Inject constructor(
    private val storeFactory: StoreFactory,
    private val businessCardInteractor: BusinessCardHostInteractor,
    private val personUUID: UUID
) {

    /**
     * Метод создания store
     */
    fun create(): BusinessCardHostStore =
        object : BusinessCardHostStore,
            Store<Intent, State, Label> by storeFactory.create(
                name = "BusinessCardHostStoreFactory",
                initialState = State,
                executorFactory = { ExecutorImpl(businessCardInteractor, personUUID) },
                reducer = { _ -> this },
                bootstrapper = SimpleBootstrapper(ExecutorImpl.Action.Init),
            ) {}

    internal class ExecutorImpl(
        private val businessCardInteractor: BusinessCardHostInteractor,
        private val personUUID: UUID
    ) : CoroutineExecutor<Intent, ExecutorImpl.Action, State, Unit, Label>() {

        override fun executeAction(action: Action, getState: () -> State) {
            if (action == Action.Init) {
                scope.launch {
                    val singleBusinessCard = businessCardInteractor.getBusinessCard()
                    if (singleBusinessCard != null) {
                        publish(Label.ToBusinessCard(singleBusinessCard))
                    } else
                        publish(Label.ToBusinessCardList(personUUID))

                }
            }
        }

        /**@SelfDocumented*/
        internal sealed interface Action {
            object Init : Action
        }
    }
}