package ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.month

import android.view.View
import com.arkivanov.mvikotlin.core.binder.BinderLifecycleMode
import com.arkivanov.mvikotlin.extensions.coroutines.bind
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import ru.tensor.sbis.design.period_picker.view.period_picker.details.store.PeriodPickerStoreFactory
import ru.tensor.sbis.design.period_picker.view.period_picker.details.store.PeriodPickerStore
import ru.tensor.sbis.design.period_picker.view.period_picker.big.store.PeriodPickerStore as HostPeriodPickerStore
import ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.month.MonthModePeriodPickerView.*
import ru.tensor.sbis.mvi_extension.attachBinder
import ru.tensor.sbis.mvi_extension.provideStore
import com.arkivanov.mvikotlin.extensions.coroutines.events
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.arkivanov.mvikotlin.extensions.coroutines.states
import kotlinx.coroutines.flow.map
import ru.tensor.sbis.design.period_picker.view.period_picker.details.model.LabelParams
import ru.tensor.sbis.design.period_picker.view.period_picker.details.store.Intent
import ru.tensor.sbis.mvi_extension.findStore

/**
 * Посредник между функциональностью бизнес логики [PeriodPickerStore] и UI [MonthModePeriodPickerView].
 *
 * @author mb.kruglova
 */
internal class MonthModePeriodPickerController @AssistedInject constructor(
    @Assisted private val fragment: MonthModePeriodPickerFragment,
    viewFactory: (View) -> MonthModePeriodPickerView,
    private val storeFactory: PeriodPickerStoreFactory
) {
    private val store = fragment.provideStore {
        storeFactory.create(it)
    }

    private val hostStore = fragment.findStore(HostPeriodPickerStore::class)

    private val eventToIntent: Event.() -> Intent = { eventToIntent() }

    init {
        fragment.attachBinder(
            BinderLifecycleMode.CREATE_DESTROY,
            viewFactory
        ) { view ->
            bind {
                view.events.map(eventToIntent) bindTo store
                store.states.map(stateToModel) bindTo view
                store.labels bindTo { label ->
                    label.handle(
                        fragment,
                        LabelParams(
                            fragment.isCompact,
                            fragment.isFragment,
                            getHostStore()
                        )
                    )
                }
            }
        }
    }

    private val stateToModel: PeriodPickerStore.State.() -> Model = {
        Model(calendarStorage, startPeriod, endPeriod, counters, isSingleClick)
    }

    private fun getHostStore() = if (fragment.isCompact) null else hostStore.value
}