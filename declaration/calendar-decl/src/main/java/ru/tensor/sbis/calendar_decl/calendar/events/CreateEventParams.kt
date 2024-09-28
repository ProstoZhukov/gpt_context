package ru.tensor.sbis.calendar_decl.calendar.events

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import org.joda.time.LocalDate
import org.joda.time.LocalDateTime

/** Параметры создания карточки события */
@Parcelize
data class CreateEventParams(
    /** дата начала */
    val dateFrom: LocalDateTime? = null,
    /** дата окончания */
    val dateTo: LocalDateTime? = null,
    /** отображаемая дата в календаре */
    val showDate: LocalDate = LocalDate.now(),
    /** для отпуска флаг true - если клик в ячейку, или клик в сетку. в других случаях false */
    val inTime: Boolean = true,
    /** набор допустимых событий, в качестве значений использовать EventType.name из контроллера календаря */
    val allowedEvents: List<String> = emptyList(),
    /** Дата на которую смотреть принятых сотрудников */
    val employeeFromDate: LocalDate? = LocalDate.now(),
    /** Дата на которую смотреть принятых сотрудников */
    val employeeToDate: LocalDate? = LocalDate.now(),
    /** Варианты открытия карточки создания события */
    val calendarOpenEventCardsVariant: CalendarOpenEventCardVariants = CalendarOpenEventCardVariants.DATE,
    /** Начало рабочего дня */
    val workStartTime: LocalDateTime? = null,
    /** Окончание рабочего дня */
    val workEndTime: LocalDateTime? = null,
    /** Помеченный маркером тип события, в качестве значения использовать EventType.name из контроллера календаря */
    val markedEventType: String? = null,
    /** Выбранный тип события, в качестве значения использовать EventType.name из контроллера календаря */
    var selectedEventType: String? = null,
    /** параметры создания события */
    val arg: CalendarDayEventParam = CalendarDayEventParam.Empty
) : Parcelable
