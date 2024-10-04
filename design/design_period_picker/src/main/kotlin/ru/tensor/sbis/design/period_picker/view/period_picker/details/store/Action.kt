package ru.tensor.sbis.design.period_picker.view.period_picker.details.store

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.cancellable
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.tensor.sbis.design.period_picker.decl.SbisPeriodPickerDayCountersRepository
import ru.tensor.sbis.design.period_picker.decl.SbisPeriodPickerDayCustomTheme
import ru.tensor.sbis.design.period_picker.decl.SbisPeriodPickerRange
import ru.tensor.sbis.design.period_picker.view.models.MarkerType
import ru.tensor.sbis.design.period_picker.view.period_picker.details.domain.CalendarStorageRepository
import ru.tensor.sbis.design.period_picker.view.period_picker.details.store.PeriodPickerStore.Label
import ru.tensor.sbis.design.period_picker.view.period_picker.details.store.PeriodPickerStore.State
import ru.tensor.sbis.design.period_picker.view.utils.dayOfMonth
import ru.tensor.sbis.design.period_picker.view.utils.firstDay
import ru.tensor.sbis.design.period_picker.view.utils.getNextDate
import ru.tensor.sbis.design.period_picker.view.utils.lastDay
import ru.tensor.sbis.design.period_picker.view.utils.rangeTo
import ru.tensor.sbis.design.period_picker.view.utils.removeTime
import java.util.Calendar

/**
 * Список действий бизнес-логики.
 *
 * @author mb.kruglova
 */
internal sealed interface Action {

    /** @SelfDocumented */
    fun handle(
        executor: Executor,
        scope: CoroutineScope,
        stateCallback: () -> State,
        repository: CalendarStorageRepository,
        dayCountersRepository: SbisPeriodPickerDayCountersRepository?
    ) = Unit

    /** Действие загрузки хранилища данных календаря. */
    class LoadCalendarStorage(
        private val displayedRange: SbisPeriodPickerRange,
        private val markerType: MarkerType,
        private val isCompact: Boolean,
        private val isDayAvailable: ((Calendar) -> Boolean)?,
        private val dayCustomTheme: ((Calendar) -> SbisPeriodPickerDayCustomTheme),
        private val anchorDate: Calendar?,
        private val isBottomPosition: Boolean
    ) : Action {

        override fun handle(
            executor: Executor,
            scope: CoroutineScope,
            stateCallback: () -> State,
            repository: CalendarStorageRepository,
            dayCountersRepository: SbisPeriodPickerDayCountersRepository?
        ) {
            scope.launch {
                val state = stateCallback()
                val startPeriod = state.startPeriod
                val endPeriod = state.endPeriod
                val startCalendarDate = getStartCalendarDate(startPeriod)
                val endCalendarDate = getEndCalendarDate(endPeriod)

                val storage = repository.getCalendarStorage(
                    state.startCalendar ?: startCalendarDate,
                    state.endCalendar ?: endCalendarDate,
                    displayedRange,
                    isCompact,
                    markerType,
                    isDayAvailable,
                    dayCustomTheme
                )
                storage.selectPeriod(startPeriod, endPeriod)
                executor.dispatchMessage(
                    Message.LoadCalendar(
                        storage,
                        startCalendarDate,
                        endCalendarDate,
                        startPeriod,
                        endPeriod
                    )
                )
            }
        }

        /** @SelfDocumented */
        private fun getStartCalendarDate(startPeriod: Calendar?): Calendar {
            val currentDate = anchorDate ?: Calendar.getInstance()
            var date =
                if (startPeriod != null && startPeriod < currentDate) {
                    startPeriod.removeTime()
                } else {
                    currentDate.removeTime()
                }

            if (date.timeInMillis !in displayedRange.start.timeInMillis..displayedRange.end.timeInMillis) {
                date = if (isBottomPosition) displayedRange.end else displayedRange.start
            }

            val newLimit = (displayedRange.start.clone() as Calendar).apply { dayOfMonth = 1 }

            return getNextDate(date, newLimit, false, isCompact).removeTime()
        }

        /** @SelfDocumented */
        private fun getEndCalendarDate(endPeriod: Calendar?): Calendar {
            val currentDate = anchorDate ?: Calendar.getInstance()
            var date =
                if (endPeriod != null && endPeriod > currentDate) {
                    endPeriod.removeTime()
                } else {
                    currentDate.removeTime()
                }

            if (date.timeInMillis !in displayedRange.start.timeInMillis..displayedRange.end.timeInMillis) {
                date = if (isBottomPosition) displayedRange.end else displayedRange.start
            }

            val newLimit = (displayedRange.end.clone() as Calendar).apply {
                set(Calendar.DAY_OF_MONTH, getActualMaximum(Calendar.DAY_OF_MONTH))
            }

            return getNextDate(date, newLimit, true, isCompact).removeTime()
        }
    }

    /** Действие инициализации счетчиков. */
    object InitCounters : Action {
        override fun handle(
            executor: Executor,
            scope: CoroutineScope,
            stateCallback: () -> State,
            repository: CalendarStorageRepository,
            dayCountersRepository: SbisPeriodPickerDayCountersRepository?
        ) {
            val firstDayOfCurrMonth = Calendar.getInstance().apply {
                set(Calendar.DAY_OF_MONTH, firstDay)
            }
            val lastDayOfCurrMonth = Calendar.getInstance().apply {
                set(Calendar.DAY_OF_MONTH, lastDay)
            }
            val range = firstDayOfCurrMonth.rangeTo(lastDayOfCurrMonth)

            executor.countersJob?.cancel()
            executor.countersJob = scope.launch {
                withContext(Dispatchers.IO) {
                    dayCountersRepository?.getDayCountersFlow(range)?.distinctUntilChanged()?.cancellable()
                        ?.collect { counters ->
                            withContext(Dispatchers.Main) {
                                val state = stateCallback()
                                if (state.counters?.size != counters.size || state.counters.keys != counters.keys) {
                                    val newCounters = counters
                                        .filterKeys {
                                            it.timeInMillis in range.start.timeInMillis..range.endInclusive.timeInMillis
                                        }
                                        .filterValues { it > 0 }
                                    if (newCounters.isNotEmpty()) {
                                        updateCounters(state, newCounters)
                                        executor.publishLabel(Label.UpdateSelection(state.calendarStorage))
                                        executor.dispatchMessage(Message.GetCounters(newCounters))
                                    }
                                }
                            }
                        }
                }
            }
        }

        /** @SelfDocumented */
        private fun updateCounters(
            state: State,
            newCounters: Map<Calendar, Int>
        ) {
            newCounters.let {
                state.calendarStorage.resetCounters(it.keys)
            }
            state.calendarStorage.setCounters(newCounters)
        }
    }
}