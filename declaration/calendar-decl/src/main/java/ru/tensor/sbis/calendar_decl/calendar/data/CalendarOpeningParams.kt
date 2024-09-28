package ru.tensor.sbis.calendar_decl.calendar.data

import java.io.Serializable
import java.util.GregorianCalendar
import java.util.UUID

/**
 * Параметры открытия экрана календаря
 * @property profileUUID UUID пользователя, чей календарь просматриваем
 * @property departmentUUID UUID отдела пользователя, чей календарь просматриваем
 * @property date GregorianCalendar дата, на которой будет открыт календарь
 * @property calendarMode CalendarMode режим открытия календаря
 * @property openingViewType OpeningViewType тип экрана, на котором будет открыт календарь
 * @property sourceViewType SourceViewType источник
 */
data class CalendarOpeningParams(
    val profileUUID: UUID? = null,
    val departmentUUID: UUID? = null,
    val date: GregorianCalendar? = null,
    val calendarMode: CalendarMode = CalendarMode.CALENDAR,
    val openingViewType: OpeningViewType? = null,
    val sourceViewType: SourceViewType? = null
): Serializable