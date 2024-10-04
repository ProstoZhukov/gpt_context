package ru.tensor.sbis.design.period_picker.view.period_picker.big.ui

import com.arkivanov.mvikotlin.core.binder.BinderLifecycleMode
import com.arkivanov.mvikotlin.extensions.coroutines.bind
import com.arkivanov.mvikotlin.extensions.coroutines.events
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.arkivanov.mvikotlin.extensions.coroutines.states
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.map
import ru.tensor.sbis.design.period_picker.view.period_picker.big.PeriodPickerFragment
import ru.tensor.sbis.design.period_picker.view.period_picker.big.ui.PeriodPickerView.Factory
import ru.tensor.sbis.design.period_picker.view.period_picker.big.ui.PeriodPickerView.Event
import ru.tensor.sbis.design.period_picker.view.period_picker.big.ui.PeriodPickerView.Model
import ru.tensor.sbis.design.period_picker.view.period_picker.big.store.Intent
import ru.tensor.sbis.design.period_picker.view.period_picker.big.store.PeriodPickerStoreFactory
import ru.tensor.sbis.design.period_picker.view.period_picker.big.store.PeriodPickerStore.State
import ru.tensor.sbis.design.period_picker.view.period_picker.big.store.PeriodPickerStore
import ru.tensor.sbis.mvi_extension.attachBinder
import ru.tensor.sbis.mvi_extension.provideStore
import ru.tensor.sbis.mvi_extension.router.buffer.BufferStatePolicy
import ru.tensor.sbis.mvi_extension.router.navigator.WeakLifecycleNavigator

/**
 * Посредник между функциональностью бизнес-логики [PeriodPickerStore] и UI [PeriodPickerView].
 *
 * @author mb.kruglova
 */
internal class PeriodPickerController @AssistedInject constructor(
    @Assisted private val fragment: PeriodPickerFragment,
    private val periodPickerStoreFactory: PeriodPickerStoreFactory,
    private val router: PeriodPickerRouter,
    periodPickerViewFactory: Factory
) {
    private val store = fragment.provideStore {
        periodPickerStoreFactory.create(it)
    }

    private val eventToIntent: Event.() -> Intent = { eventToIntent() }

    init {
        router.attachNavigator(
            WeakLifecycleNavigator(
                fragment,
                bufferStatePolicy = BufferStatePolicy.ViewModel(PERIOD_PICKER_KEY, fragment)
            )
        )

        fragment.attachBinder(
            BinderLifecycleMode.CREATE_DESTROY,
            periodPickerViewFactory
        ) { view ->
            bind {
                view.events.map(eventToIntent) bindTo store
                store.states.map(stateToModel) bindTo view
                store.labels bindTo { label -> label.handle(router) }
            }
        }
    }

    private val stateToModel: State.() -> Model = {
        Model(
            mode,
            startInitialPeriod ?: startPresetPeriod,
            endInitialPeriod ?: endPresetPeriod,
            startPeriod,
            endPeriod
        )
    }

    companion object {
        private const val PERIOD_PICKER_KEY = "PERIOD_PICKER_KEY"
    }
}