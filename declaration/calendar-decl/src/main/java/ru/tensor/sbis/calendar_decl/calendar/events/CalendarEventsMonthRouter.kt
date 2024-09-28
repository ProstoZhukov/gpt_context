package ru.tensor.sbis.calendar_decl.calendar.events

import java.util.*

/**
 * Интерфейс роутера для экрана событий на месяц
 */
interface CalendarEventsMonthRouter : CalendarCustomisedRouter {

    /**
     * Переход с экрана событий по месяцам на экран событий по годам
     * @param year Int год
     * @param profileUuid UUID? профиль, для которого просматриваем календарь
     */
    fun monthEventsFragmentToYearEventsFragment(year: Int, profileUuid: UUID?)

    /**
     * Переход с экрана событий по месяцам на экран событий по дням
     * @param year Int год
     * @param month Int месяц
     * @param day Int день
     * @param profileUuid  UUID? профиль, для которого просматриваем календарь
     */
    fun monthEventsFragmentToWeekEventsFragment(year: Int, month: Int, day: Int, profileUuid: UUID?)

    /**
     * Переход на экран статистики событий
     * @param profileUuid UUID? профиль, для которого просматриваем календарь
     * @param date GregorianCalendar? дата для просмотра статистики
     */
    fun monthEventsFragmentToStatistics(profileUuid: UUID?, date: GregorianCalendar?)

    /** Переход на нужный экран после нажатия на снэкбар активности */
    fun executeMonthActivitySnackbarTransition(date: GregorianCalendar?, profileUuid: UUID?)
}