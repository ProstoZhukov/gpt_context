package ru.tensor.sbis.design.period_picker.view.period_picker.details.store

import ru.tensor.sbis.design.period_picker.view.models.CalendarStorage
import ru.tensor.sbis.design.period_picker.view.models.SelectionType
import ru.tensor.sbis.design.period_picker.view.models.SelectionType.NO_SELECTION
import java.util.Calendar
import ru.tensor.sbis.design.period_picker.view.period_picker.details.store.PeriodPickerStore.State

/**
 * Список обработанных Executor'ом действий и намерений.
 *
 * @author mb.kruglova
 */
internal sealed interface Message {

    /** @SelfDocumented */
    fun reduce(state: State): State

    /** Сообщение о загрузке календаря. */
    class LoadCalendar(
        private val storage: CalendarStorage,
        private val startCalendar: Calendar,
        private val endCalendar: Calendar,
        private val dateFrom: Calendar?,
        private val dateTo: Calendar?
    ) : Message {
        override fun reduce(state: State): State {
            return state.copy(
                calendarStorage = storage,
                startCalendar = startCalendar,
                endCalendar = endCalendar,
                startPeriod = dateFrom,
                endPeriod = dateTo
            )
        }
    }

    /** Сообщение о выборе дня в календаре. */
    class SelectDay(
        private val startDate: Calendar,
        private val endDate: Calendar?,
        private val type: SelectionType
    ) : Message {
        override fun reduce(state: State): State {
            with(state) {
                return copy(
                    startPeriod = startDate,
                    endPeriod = endDate,
                    selectionType = type
                )
            }
        }
    }

    /** Сообщение о выборе периода в календаре. */
    class SelectPeriod(
        private val dateFrom: Calendar?,
        private val dateTo: Calendar?,
        private val type: SelectionType
    ) : Message {
        override fun reduce(state: State): State {
            return state.copy(
                startPeriod = dateFrom,
                endPeriod = dateTo,
                selectionType = type
            )
        }
    }

    /** Сообщение о получении счётчиков. */
    class GetCounters(private val newCounters: Map<Calendar, Int>) : Message {

        override fun reduce(state: State): State {
            return state.copy(counters = newCounters)
        }
    }

    /** Сообщение о сбросе выделения периода. */
    object ResetSelection : Message {
        override fun reduce(state: State): State {
            return state.copy(
                startPeriod = null,
                endPeriod = null,
                selectionType = NO_SELECTION
            )
        }
    }

    /** Сообщение об обновлении начальной даты календаря. */
    class UpdateCalendarStartDate(
        private val storage: CalendarStorage,
        private val date: Calendar
    ) : Message {
        override fun reduce(state: State): State {
            return state.copy(
                calendarStorage = storage,
                startCalendar = date
            )
        }
    }

    /** Сообщение об обновлении конечной даты календаря. */
    class UpdateCalendarEndDate(
        private val storage: CalendarStorage,
        private val date: Calendar
    ) : Message {
        override fun reduce(state: State): State {
            return state.copy(
                calendarStorage = storage,
                endCalendar = date
            )
        }
    }
}