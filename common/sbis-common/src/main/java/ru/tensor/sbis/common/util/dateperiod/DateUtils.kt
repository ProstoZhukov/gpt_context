package ru.tensor.sbis.common.util.dateperiod

import java.util.*

/**
 * Создаёт объект [Calendar] для этой даты
 *
 * @return [Calendar], соответствующий этой дате
 */
fun Date.toCalendar(): Calendar {
    val calendar = Calendar.getInstance()
    calendar.time = this
    return calendar
}

/**
 * Создаёт экземпляр [Calendar], задавая текущую дату для указанного часового пояса
 *
 * @return [Calendar] с копией даты в заданном часовом поясе
 */
internal fun Calendar.copyDateOnly(targetTimeZone: TimeZone = TimeZone.getDefault()): Calendar {
    val copy = Calendar.getInstance(targetTimeZone)
    copy.set(get(Calendar.YEAR), get(Calendar.MONTH), get(Calendar.DATE), 0, 0, 0)
    copy.set(Calendar.MILLISECOND, 0)
    return copy
}

/**
 * Создаёт копию [Calendar]
 *
 * @return копия [Calendar]
 */
internal fun Calendar.copy(): Calendar = Calendar.getInstance(timeZone).apply {
    time = this@copy.time
}

/**
 * Создаёт копию для заданной даты, но с нулевым временем
 *
 * @return копия даты с нулевым временем
 */
fun Date.withoutTime(): Date {
    val date = toCalendar()
    return createDateWithoutTime(date.get(Calendar.YEAR),
            date.get(Calendar.MONTH),
            date.get(Calendar.DAY_OF_MONTH))
}

/**
 * Создаёт экземпляр [Date] для заданной даты с нулевым временем
 *
 * @param year год
 * @param month месяц
 * @param dayOfMonth день месяца
 * @return [Date] с нулевым временем
 */
internal fun createDateWithoutTime(year: Int, month: Int, dayOfMonth: Int): Date {
    val date = Date(0).toCalendar().apply {
        set(year, month, dayOfMonth, 0, 0, 0)
    }
    return date.time
}

/**
 * Возвращает [TimeZone], соответствующую UTC
 *
 * @return UTC [TimeZone]
 */
fun getUtcTimeZone(): TimeZone = TimeZone.getTimeZone("UTC")