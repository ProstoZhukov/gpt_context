package ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.year.calendar.listeners

import ru.tensor.sbis.design.period_picker.view.period_picker.details.ui.mode.year.YearModePeriodPickerView.Event
import java.util.GregorianCalendar

/**
 * Слушатель событий заголовков годов в шапке календаря.
 *
 * @author mb.kruglova
 */
internal class YearLabelListener(
    private val dispatch: (Event) -> Unit
) {
    /** Кликнуть по заголовку года в шапке календаря. */
    internal fun onClickYearLabel(year: Int, month: Int) {
        val date = GregorianCalendar(year, month, 1)

        dispatch(Event.ClickYearLabel(date))
    }

    /** Обновить заголовки годов в шапке календаря. */
    internal fun onUpdateYearLabel(year: Int) {
        dispatch(Event.UpdateYearLabel(year))
    }

    /** Догрузить календарь. */
    internal fun onReloadCalendar(isNextPage: Boolean, year: Int) {
        dispatch(Event.ReloadCalendar(isNextPage, year))
    }
}