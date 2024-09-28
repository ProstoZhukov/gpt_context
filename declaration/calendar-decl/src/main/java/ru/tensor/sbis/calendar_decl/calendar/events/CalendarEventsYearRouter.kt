package ru.tensor.sbis.calendar_decl.calendar.events

import java.util.*

/**
 * Интерфейс роутера для экрана событий на год
 */
interface CalendarEventsYearRouter : CalendarCustomisedRouter {

    fun yearEventsFragmentToMonthEventsFragment(year: Int, month: Int, profileUuid: UUID?)

    fun yearEventsFragmentToStatistics(profileUuid: UUID?, date: GregorianCalendar?)
}