package ru.tensor.sbis.calendar_decl.calendar

import androidx.fragment.app.Fragment
import ru.tensor.sbis.calendar_decl.calendar.data.CalendarMode
import ru.tensor.sbis.calendar_decl.calendar.data.CalendarOpeningParams
import ru.tensor.sbis.plugin_struct.feature.Feature

/** Провайдер хостового фрагмента календаря */
interface CalendarMainFragmentProvider : Feature {

    /** Получить хостовый фрагмент календаря с параметрами [calendarOpeningParams] */
    fun getCalendarMainFragment(calendarOpeningParams: CalendarOpeningParams = CalendarOpeningParams(calendarMode = CalendarMode.CALENDAR)): Fragment
}