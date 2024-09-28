package ru.tensor.sbis.localfeaturetoggle.presentation.store

import com.arkivanov.essenty.statekeeper.StateKeeper
import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.SimpleBootstrapper
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import ru.tensor.sbis.localfeaturetoggle.data.Feature
import ru.tensor.sbis.localfeaturetoggle.domain.LocalFeatureToggleService
import javax.inject.Inject
import ru.tensor.sbis.mvi_extension.create

/**
 * Отвечает за генерацию изменение состояния интерфейса или источника данных
 * в ответ на события(Intent) от интерфейса или источника данных.
 *
 * @author mb.kruglova
 */
internal class LocalFeatureToggleStoreFactory @Inject constructor(
    private val storeFactory: StoreFactory,
    private val localFeatureToggleService: LocalFeatureToggleService
) {

    fun create(stateKeeper: StateKeeper): LocalFeatureToggleStore = object :
        LocalFeatureToggleStore,
        Store<LocalFeatureToggleStore.Intent, LocalFeatureToggleStore.State, LocalFeatureToggleStore.Label> by
        storeFactory.create(
            stateKeeper,
            name = "LocalFeatureToggleStore",
            initialState = LocalFeatureToggleStore.State(),
            bootstrapper = SimpleBootstrapper(Action.GetFeatureList),
            executorFactory = { ExecutorImpl(localFeatureToggleService) },
            reducer = ReducerImpl(),
            saveStateSupplier = {
                it.copy(listItems = emptyList())
            }
        ) {}

    private sealed interface Action {
        /**
         * Действие получения списка фич
         */
        object GetFeatureList : Action
    }

    private sealed interface Message {
        class UpdateListItems(val features: List<Feature>) : Message
    }

    private class ExecutorImpl(private val localFeatureToggleService: LocalFeatureToggleService) :
        CoroutineExecutor<LocalFeatureToggleStore.Intent,
            Action,
            LocalFeatureToggleStore.State,
            Message,
            LocalFeatureToggleStore.Label>() {

        override fun executeAction(action: Action, getState: () -> LocalFeatureToggleStore.State) {
            if (action is Action.GetFeatureList) {
                executeIntent(
                    LocalFeatureToggleStore.Intent.GetFeatureList
                )
            }
        }

        override fun executeIntent(
            intent: LocalFeatureToggleStore.Intent,
            getState: () -> LocalFeatureToggleStore.State
        ) {
            when (intent) {
                is LocalFeatureToggleStore.Intent.SwitchItem -> {
                    if (intent.feature.isActivated != intent.isActivated) {
                        intent.feature.isActivated = intent.isActivated
                        localFeatureToggleService.updateFeature(intent.feature, intent.isActivated)
                    }
                }

                is LocalFeatureToggleStore.Intent.GetFeatureList -> {
                    dispatch(Message.UpdateListItems(localFeatureToggleService.allFeatures))
                }
            }
        }
    }

    private class ReducerImpl : Reducer<LocalFeatureToggleStore.State, Message> {
        override fun LocalFeatureToggleStore.State.reduce(msg: Message): LocalFeatureToggleStore.State =
            when (msg) {
                is Message.UpdateListItems -> copy(listItems = msg.features)
            }
    }
}