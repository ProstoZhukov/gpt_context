package ru.tensor.sbis.design.period_picker.view.period_picker.details.store

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.cancellable
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jetbrains.annotations.TestOnly
import ru.tensor.sbis.design.period_picker.decl.SbisPeriodPickerRange
import ru.tensor.sbis.design.period_picker.decl.SbisPeriodPickerMode.MONTH as MONTH_MODE
import ru.tensor.sbis.design.period_picker.decl.SbisPeriodPickerMode.YEAR as YEAR_MODE
import ru.tensor.sbis.design.period_picker.view.models.SelectionType
import ru.tensor.sbis.design.period_picker.view.models.SelectionType.*
import ru.tensor.sbis.design.period_picker.view.period_picker.details.model.IntentParams
import ru.tensor.sbis.design.period_picker.view.utils.CalendarDayRange
import ru.tensor.sbis.design.period_picker.view.period_picker.details.store.PeriodPickerStore.Label
import ru.tensor.sbis.design.period_picker.view.utils.checkAndResetPreviousSelection
import ru.tensor.sbis.design.period_picker.view.utils.getLastDateOfYear
import ru.tensor.sbis.design.period_picker.view.utils.getNewPeriod
import ru.tensor.sbis.design.period_picker.view.utils.getNextDate
import ru.tensor.sbis.design.period_picker.view.utils.getPresetPeriod
import ru.tensor.sbis.design.period_picker.view.utils.isClosePeriodPicker
import ru.tensor.sbis.design.period_picker.view.utils.lastDayOfMonth
import ru.tensor.sbis.design.period_picker.view.utils.month
import ru.tensor.sbis.design.period_picker.view.utils.removeTime
import ru.tensor.sbis.design.period_picker.view.utils.year
import java.util.Calendar
import java.util.GregorianCalendar

/**
 * Намерения и их обработка.
 *
 * @author mb.kruglova
 */
internal sealed interface Intent {

    /** @SelfDocumented */
    fun handle(
        executor: Executor,
        scope: CoroutineScope,
        state: PeriodPickerStore.State,
        params: IntentParams
    ) = Unit

    /** Намерение выбора дня в календаре. */
    data class SelectDay(val date: Calendar) : Intent {
        override fun handle(
            executor: Executor,
            scope: CoroutineScope,
            state: PeriodPickerStore.State,
            params: IntentParams
        ) {
            var (startPresetPeriod, endPresetPeriod) = getPresetPeriod(
                if (params.isFragment && state.selectionType == COMPLETE_SELECTION) null else state.startPeriod,
                date
            )

            if (checkAndResetPreviousSelection(
                    state.selectionType,
                    DAY,
                    state.startPeriod,
                    state.endPeriod,
                    state.calendarStorage
                )
            ) {
                startPresetPeriod = date
                endPresetPeriod = null
            }

            val (startPeriod, endPeriod) = getNewPeriod(startPresetPeriod, endPresetPeriod)

            val end = endPeriod ?: startPeriod
            if (isClosePeriodPicker(state.isSingleClick, endPeriod)) {
                if (!params.isFragment) {
                    executor.publishLabel(Label.ClosePeriodPicker(SbisPeriodPickerRange(startPeriod, end)))
                } else {
                    state.calendarStorage.selectDays(startPeriod, end)

                    executor.publishLabel(
                        Label.UpdatePeriod(
                            state.calendarStorage,
                            SbisPeriodPickerRange(startPeriod, end)
                        )
                    )
                }
                endPresetPeriod = end
                executor.dispatchMessage(Message.SelectDay(startPresetPeriod, endPresetPeriod, COMPLETE_SELECTION))
            } else {
                state.calendarStorage.selectDays(startPeriod, endPeriod)
                executor.publishLabel(Label.UpdateCalendar(state.calendarStorage, startPeriod, end, MONTH_MODE))
                executor.dispatchMessage(Message.SelectDay(startPresetPeriod, endPresetPeriod, DAY))
            }
        }
    }

    /** Намерение выбора месяца в календаре. */
    data class SelectMonth(val date: Calendar) : Intent {
        override fun handle(
            executor: Executor,
            scope: CoroutineScope,
            state: PeriodPickerStore.State,
            params: IntentParams
        ) {
            var (startPresetPeriod, endPresetPeriod) = getPresetPeriod(state.startPeriod, date)

            if (checkAndResetPreviousSelection(
                    state.selectionType,
                    MONTH,
                    state.startPeriod,
                    state.endPeriod,
                    { state.startPeriod?.year?.let { getLastDateOfYear(it) } },
                    state.calendarStorage
                )
            ) {
                startPresetPeriod = date
                endPresetPeriod = null
            }

            val (startPeriod, endPeriod) = getNewPeriod(startPresetPeriod, endPresetPeriod) {
                GregorianCalendar(it.year, it.month, it.lastDayOfMonth)
            }

            state.calendarStorage.selectQuantum(startPeriod, endPeriod)

            val end = endPeriod ?: GregorianCalendar(startPeriod.year, startPeriod.month, startPeriod.lastDayOfMonth)
            if (isClosePeriodPicker(state.isSingleClick, endPeriod)) {
                executor.publishLabel(Label.ClosePeriodPicker(SbisPeriodPickerRange(startPeriod, end)))
                executor.dispatchMessage(Message.SelectPeriod(startPeriod, endPeriod, COMPLETE_SELECTION))
            } else {
                executor.publishLabel(Label.UpdateCalendar(state.calendarStorage, startPeriod, end, YEAR_MODE))
                executor.dispatchMessage(Message.SelectPeriod(startPeriod, endPeriod, MONTH))
            }
        }
    }

    /** Намерение выбора кванта в календаре. */
    data class SelectQuantum(
        val dateFrom: Calendar,
        val dateTo: Calendar,
        val type: SelectionType
    ) : Intent {
        override fun handle(
            executor: Executor,
            scope: CoroutineScope,
            state: PeriodPickerStore.State,
            params: IntentParams
        ) {
            checkAndResetPreviousSelection(
                state.selectionType,
                type,
                state.startPeriod,
                state.endPeriod,
                state.calendarStorage
            )

            state.calendarStorage.selectQuantum(dateFrom, dateTo)
            executor.publishLabel(Label.ClosePeriodPicker(SbisPeriodPickerRange(dateFrom, dateTo)))
            executor.dispatchMessage(Message.SelectPeriod(dateFrom, dateTo, COMPLETE_SELECTION))
        }
    }

    /** Намерение выбора года в календаре. */
    data class SelectYear(val date: Calendar) : Intent {
        override fun handle(
            executor: Executor,
            scope: CoroutineScope,
            state: PeriodPickerStore.State,
            params: IntentParams
        ) {
            var (startPresetPeriod, endPresetPeriod) = getPresetPeriod(state.startPeriod, date)

            if (checkAndResetPreviousSelection(
                    state.selectionType,
                    YEAR,
                    state.startPeriod,
                    state.endPeriod,
                    {
                        (state.startPeriod?.clone() as? Calendar)?.apply {
                            set(Calendar.DAY_OF_MONTH, getActualMaximum(Calendar.DAY_OF_MONTH))
                        }
                    },
                    state.calendarStorage
                )
            ) {
                startPresetPeriod = date
                endPresetPeriod = null
            }

            val (startPeriod, endPeriod) = getNewPeriod(startPresetPeriod, endPresetPeriod) {
                getLastDateOfYear(it.year)
            }

            state.calendarStorage.selectYear(startPeriod, endPeriod)

            val end = endPeriod ?: getLastDateOfYear(startPeriod.year)
            if (isClosePeriodPicker(state.isSingleClick, endPeriod)) {
                executor.publishLabel(Label.ClosePeriodPicker(SbisPeriodPickerRange(startPeriod, end)))
                executor.dispatchMessage(Message.SelectPeriod(startPeriod, endPeriod, COMPLETE_SELECTION))
            } else {
                executor.publishLabel(
                    Label.UpdateCalendar(state.calendarStorage, startPeriod, end, YEAR_MODE, startPeriod)
                )
                executor.dispatchMessage(Message.SelectPeriod(startPeriod, endPeriod, YEAR))
            }
        }
    }

    /** Намерение выбора периода, кратному месяцу, в календаре. */
    data class SelectMonthPeriod(val dateFrom: Calendar, val dateTo: Calendar) : Intent {
        override fun handle(
            executor: Executor,
            scope: CoroutineScope,
            state: PeriodPickerStore.State,
            params: IntentParams
        ) {
            checkAndResetPreviousSelection(
                state.selectionType,
                MONTH,
                state.startPeriod,
                state.endPeriod,
                state.calendarStorage
            )

            if (params.isFragment) {
                state.calendarStorage.selectDays(dateFrom, dateTo)
                executor.publishLabel(
                    Label.UpdatePeriod(
                        state.calendarStorage,
                        SbisPeriodPickerRange(dateFrom, dateTo)
                    )
                )
            } else {
                executor.publishLabel(Label.ClosePeriodPicker(SbisPeriodPickerRange(dateFrom, dateTo)))
            }

            executor.dispatchMessage(Message.SelectPeriod(dateFrom, dateTo, COMPLETE_SELECTION))
        }
    }

    /** Намерение выбора произвольного периода в календаре. */
    data class SelectPeriod(
        val dateFrom: Calendar,
        val dateTo: Calendar
    ) : Intent {
        override fun handle(
            executor: Executor,
            scope: CoroutineScope,
            state: PeriodPickerStore.State,
            params: IntentParams
        ) {
            state.calendarStorage.selectPeriod(dateFrom, dateTo)
            executor.publishLabel(Label.UpdateSelection(state.calendarStorage))
            executor.dispatchMessage(Message.SelectPeriod(dateFrom, dateTo, PRESET_SELECTION))
        }
    }

    /** Намерение обновить заголовки годов. */
    data class UpdateYearLabel(val year: Int) : Intent {
        override fun handle(
            executor: Executor,
            scope: CoroutineScope,
            state: PeriodPickerStore.State,
            params: IntentParams
        ) {
            state.calendarStorage.updateMarkedYears(year)
            executor.publishLabel(Label.UpdateSelection(state.calendarStorage))
        }
    }

    /** Намерение обновления счётчиков. */
    class UpdateCounters(
        val range: CalendarDayRange,
        @TestOnly val dispatcher: CoroutineDispatcher? = null
    ) : Intent {

        override fun handle(
            executor: Executor,
            scope: CoroutineScope,
            state: PeriodPickerStore.State,
            params: IntentParams
        ) {
            executor.countersJob?.cancel()
            executor.countersJob = scope.launch {
                withContext(dispatcher ?: Dispatchers.IO) {
                    params.dayCountersRepository?.getDayCountersFlow(range)?.distinctUntilChanged()?.cancellable()
                        ?.collect { counters ->
                            withContext(Dispatchers.Main) {
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

        private fun updateCounters(
            state: PeriodPickerStore.State,
            newCounters: Map<Calendar, Int>
        ) {
            newCounters.let {
                state.calendarStorage.resetCounters(it.keys)
            }
            state.calendarStorage.setCounters(newCounters)
        }
    }

    /** Намерение сброса выделения периода. */
    object ResetSelection : Intent {
        override fun handle(
            executor: Executor,
            scope: CoroutineScope,
            state: PeriodPickerStore.State,
            params: IntentParams
        ) {
            state.calendarStorage.deselectPeriod(state.startPeriod, state.endPeriod ?: state.startPeriod)
            executor.publishLabel(Label.UpdateSelection(state.calendarStorage))
            executor.dispatchMessage(Message.ResetSelection)
        }
    }

    /** Намерение обновить позицию календаря на текущую дату. */
    object UpdateScrollToCurrentDay : Intent {
        override fun handle(
            executor: Executor,
            scope: CoroutineScope,
            state: PeriodPickerStore.State,
            params: IntentParams
        ) {
            executor.publishLabel(Label.UpdateScrollToCurrentDay)
        }
    }

    /** Намерение догрузить календарь. */
    data class ReloadCalendar(
        val isNextPage: Boolean,
        val year: Int? = null
    ) : Intent {

        companion object {
            // Количество дней.
            private const val DAY_AMOUNT = 1

            // Количество лет.
            private const val YEAR_AMOUNT = 2
        }

        override fun handle(
            executor: Executor,
            scope: CoroutineScope,
            state: PeriodPickerStore.State,
            params: IntentParams
        ) {
            if (checkCalendar(state)) return

            executor.countersJob?.cancel()
            executor.reloadingJob?.cancel()
            executor.reloadingJob = scope.launch {
                val currentStart = state.startCalendar ?: Calendar.getInstance().apply { set(Calendar.DAY_OF_MONTH, 1) }
                val currentEnd = state.endCalendar ?: Calendar.getInstance().apply {
                    set(Calendar.DAY_OF_MONTH, getActualMaximum(Calendar.DAY_OF_MONTH))
                }

                val newStart: Calendar = getStartDate(
                    currentStart,
                    currentEnd,
                    params.displayedRange.start,
                    isNextPage,
                    params.isCompact
                )
                val newEnd: Calendar = getEndDate(
                    currentStart,
                    currentEnd,
                    params.displayedRange.end,
                    isNextPage,
                    params.isCompact
                )

                if (newStart.timeInMillis < newEnd.timeInMillis) {
                    val newData =
                        params.repository.getCalendarStorage(
                            newStart,
                            newEnd,
                            params.displayedRange,
                            params.isCompact,
                            params.markerType,
                            params.isDayAvailable,
                            params.dayCustomTheme
                        )
                    val storage = params.repository.addDataToStorage(state.calendarStorage, newData, isNextPage)
                    executor.publishLabel(Label.ReloadCalendar(newData, isNextPage))
                    if (isNextPage) {
                        executor.dispatchMessage(Message.UpdateCalendarEndDate(storage, newEnd))
                    } else {
                        executor.dispatchMessage(Message.UpdateCalendarStartDate(storage, newStart))
                    }
                }
            }
        }

        /** Проверить календарь на необходимость дозагрузки. */
        private fun checkCalendar(state: PeriodPickerStore.State): Boolean {
            val startYear = state.startCalendar?.year
            val endYear = state.endCalendar?.year

            return year != null && startYear != null && endYear != null &&
                (isNextPage && endYear >= year + YEAR_AMOUNT || !isNextPage && startYear <= year - YEAR_AMOUNT)
        }

        private fun getStartDate(
            start: Calendar,
            end: Calendar,
            limit: Calendar,
            isNextPage: Boolean,
            isCompact: Boolean
        ): Calendar {
            return if (isNextPage) {
                getNextDay(end, DAY_AMOUNT)
            } else {
                getNextDate(start, limit, false, isCompact)
            }
        }

        private fun getEndDate(
            start: Calendar,
            end: Calendar,
            limit: Calendar,
            isNextPage: Boolean,
            isCompact: Boolean
        ): Calendar {
            return if (isNextPage) {
                getNextDate(end, limit, true, isCompact)
            } else {
                getNextDay(start, -DAY_AMOUNT)
            }
        }

        private fun getNextDay(date: Calendar, amount: Int): Calendar {
            return (date.clone() as Calendar).apply { add(Calendar.DAY_OF_MONTH, amount) }.removeTime()
        }
    }
}