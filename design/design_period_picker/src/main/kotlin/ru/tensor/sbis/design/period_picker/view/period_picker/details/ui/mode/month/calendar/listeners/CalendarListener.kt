package ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.month.calendar.listeners

import ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.month.MonthModePeriodPickerView.Event
import ru.tensor.sbis.design.period_picker.view.utils.month
import ru.tensor.sbis.design.period_picker.view.utils.rangeTo
import ru.tensor.sbis.design.period_picker.view.utils.year
import java.util.Calendar
import java.util.GregorianCalendar

/**
 * Слушатель событий календаря.
 *
 * @author mb.kruglova
 */
internal class CalendarListener(
    private val dispatch: (Event) -> Unit
) {
    /** Кликнуть по элементу. */
    internal fun onClickItem(date: Calendar, isMonthLabelItem: Boolean) {
        val dateTo = GregorianCalendar(date.year, date.month, date.getActualMaximum(Calendar.DAY_OF_MONTH))

        dispatch(
            if (isMonthLabelItem) {
                Event.ClickMonthPeriod(date, dateTo)
            } else {
                Event.ClickDay(date)
            }
        )
    }

    /** Обновить счётчики. */
    internal fun onUpdateCounters(startDate: Calendar, endDate: Calendar) {
        dispatch(Event.UpdateCounters(startDate.rangeTo(endDate)))
    }

    /** Догрузить календарь. */
    internal fun onReloadCalendar(isNextPage: Boolean) {
        dispatch(Event.ReloadCalendar(isNextPage))
    }

    /** Сбросить выбранный период. */
    internal fun onResetSelectionPeriod() {
        dispatch(Event.ResetSelection)
    }

    /** Выбрать произвольный период. */
    internal fun onSelectPeriod(dateFrom: Calendar, dateTo: Calendar) {
        dispatch(Event.SelectPeriod(dateFrom, dateTo))
    }
}