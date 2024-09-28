package ru.tensor.sbis.calendar_decl.calendar

import android.content.Intent
import ru.tensor.sbis.calendar_decl.calendar.data.CalendarMode
import ru.tensor.sbis.calendar_decl.calendar.data.CalendarOpeningParams
import ru.tensor.sbis.plugin_struct.feature.Feature

interface CalendarMainActivityProvider : Feature {

    /**
     * Получить intent экрана календаря
     * @param calendarOpeningParams CalendarOpeningParams параметры открытия экрана календаря
     */
    fun getCalendarMainActivityIntent(calendarOpeningParams: CalendarOpeningParams = CalendarOpeningParams(calendarMode = CalendarMode.CALENDAR)): Intent
}