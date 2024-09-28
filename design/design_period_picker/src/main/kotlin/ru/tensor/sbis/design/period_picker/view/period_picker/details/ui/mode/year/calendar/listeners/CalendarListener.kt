package ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.year.calendar.listeners

import ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.year.YearModePeriodPickerView.Event
import ru.tensor.sbis.design.period_picker.view.utils.lastDayOfMonth
import ru.tensor.sbis.design.period_picker.view.utils.month
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

    /** @SelfDocumented */
    internal fun onClickMonth(year: Int, month: Int) {
        val date = GregorianCalendar(year, month, 1)

        dispatch(Event.ClickMonth(date))
    }

    /** @SelfDocumented */
    internal fun onClickQuarter(year: Int, month: Int) {
        val dateFrom = GregorianCalendar(year, month, 1)
        val preDateTo = GregorianCalendar(year, month + 2, 1)
        val dateTo = GregorianCalendar(year, preDateTo.month, preDateTo.lastDayOfMonth)

        dispatch(Event.ClickQuarter(dateFrom, dateTo))
    }

    /** @SelfDocumented */
    internal fun onClickHalfYear(year: Int, month: Int) {
        val dateFrom = GregorianCalendar(year, month, 1)
        val preDateTo = GregorianCalendar(year, month + 5, 1)
        val dateTo = GregorianCalendar(year, preDateTo.month, preDateTo.lastDayOfMonth)

        dispatch(Event.ClickHalfYear(dateFrom, dateTo))
    }

    /** Догрузить календарь. */
    internal fun onReloadCalendar(isNextPage: Boolean) {
        dispatch(Event.ReloadCalendar(isNextPage))
    }

    /** Сбросить выбранный период. */
    internal fun onResetSelectionPeriod(startPeriod: Calendar, endPeriod: Calendar) {
        dispatch(Event.ResetSelection(startPeriod, endPeriod))
    }

    /** Выбрать произвольный период. */
    internal fun onSelectPeriod(dateFrom: Calendar, dateTo: Calendar) {
        dispatch(Event.SelectPeriod(dateFrom, dateTo))
    }
}