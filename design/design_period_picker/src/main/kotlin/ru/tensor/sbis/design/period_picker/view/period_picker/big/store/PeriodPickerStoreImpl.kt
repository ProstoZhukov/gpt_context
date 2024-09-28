package ru.tensor.sbis.design.period_picker.view.period_picker.big.store

import com.arkivanov.essenty.statekeeper.StateKeeper
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import ru.tensor.sbis.design.period_picker.decl.SbisPeriodPickerMode
import ru.tensor.sbis.design.period_picker.view.period_picker.big.store.PeriodPickerStore.Label
import ru.tensor.sbis.design.period_picker.view.period_picker.big.store.PeriodPickerStore.State
import ru.tensor.sbis.mvi_extension.create
import java.util.Calendar

/**
 * Реализация [PeriodPickerStore].
 *
 * @author mb.kruglova
 */
internal class PeriodPickerStoreImpl(
    storeFactory: StoreFactory,
    stateKeeper: StateKeeper,
    startSelectionDate: Calendar?,
    endSelectionDate: Calendar?,
    presetStartDate: Calendar?,
    presetEndDate: Calendar?,
    mode: SbisPeriodPickerMode
) : PeriodPickerStore,
    Store<Intent, State, Label> by storeFactory.create(
        stateKeeper,
        name = STORE_NAME,
        initialState = State(
            mode = mode,
            startInitialPeriod = startSelectionDate,
            endInitialPeriod = endSelectionDate,
            startPresetPeriod = presetStartDate,
            endPresetPeriod = presetEndDate
        ),
        bootstrapper = null,
        executorFactory = { Executor() },
        reducer = Reducer()
    ) {

    companion object {
        private const val STORE_NAME = "BigPeriodPickerStore"
    }
}