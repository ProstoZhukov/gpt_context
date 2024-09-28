package ru.tensor.sbis.design.period_picker.view.period_picker.details.store

import com.arkivanov.essenty.statekeeper.StateKeeper
import com.arkivanov.mvikotlin.core.store.SimpleBootstrapper
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import ru.tensor.sbis.design.period_picker.decl.SbisPeriodPickerRange
import ru.tensor.sbis.design.period_picker.decl.SbisPeriodPickerSelectionType
import ru.tensor.sbis.design.period_picker.decl.SbisPeriodPickerDayCountersRepository
import ru.tensor.sbis.design.period_picker.decl.SbisPeriodPickerDayCustomTheme
import ru.tensor.sbis.design.period_picker.view.period_picker.details.domain.CalendarStorageRepository
import ru.tensor.sbis.design.period_picker.view.models.MarkerType
import ru.tensor.sbis.design.period_picker.view.period_picker.details.store.PeriodPickerStore.Label
import ru.tensor.sbis.design.period_picker.view.period_picker.details.store.PeriodPickerStore.State
import ru.tensor.sbis.design.period_picker.view.utils.removeTime
import ru.tensor.sbis.mvi_extension.create
import java.util.Calendar

/**
 * Реализация [PeriodPickerStore].
 *
 * @author mb.kruglova
 */
internal class PeriodPickerStoreImpl(
    storeFactory: StoreFactory,
    repository: CalendarStorageRepository,
    stateKeeper: StateKeeper,
    actions: List<Action>,
    selectionType: SbisPeriodPickerSelectionType,
    markerType: MarkerType,
    isCompact: Boolean,
    dayCountersRepository: SbisPeriodPickerDayCountersRepository?,
    displayedRange: SbisPeriodPickerRange,
    startSelectionDate: Calendar?,
    endSelectionDate: Calendar?,
    presetStartDate: Calendar?,
    presetEndDate: Calendar?,
    isDayAvailable: ((Calendar) -> Boolean)?,
    dayCustomTheme: ((Calendar) -> SbisPeriodPickerDayCustomTheme),
    isFragment: Boolean
) : PeriodPickerStore,
    Store<Intent, State, Label> by storeFactory.create(
        stateKeeper,
        name = STORE_NAME,
        initialState = State(
            isSingleClick = selectionType is SbisPeriodPickerSelectionType.Single,
            startPeriod = startSelectionDate?.removeTime() ?: presetStartDate?.removeTime(),
            endPeriod = endSelectionDate?.removeTime() ?: presetEndDate?.removeTime()
        ),
        bootstrapper = SimpleBootstrapper(*(actions.toTypedArray())),
        executorFactory =
        {
            Executor(
                repository,
                markerType,
                isCompact,
                dayCountersRepository,
                displayedRange,
                isDayAvailable,
                isFragment,
                dayCustomTheme
            )
        },
        reducer = Reducer()
    ) {

    companion object {
        private const val STORE_NAME = "PeriodPickerStore"
    }
}