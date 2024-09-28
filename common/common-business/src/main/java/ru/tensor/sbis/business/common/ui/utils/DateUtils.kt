package ru.tensor.sbis.business.common.ui.utils

import ru.tensor.sbis.common.util.ResourceProvider
import ru.tensor.sbis.common.util.date.BaseDateUtils
import ru.tensor.sbis.common.util.dateperiod.DatePeriod
import ru.tensor.sbis.common.util.dateperiod.PeriodType
import ru.tensor.sbis.common.util.dateperiod.formatMonthShort
import ru.tensor.sbis.common.util.dateperiod.getFormattedYearWithPrefix
import ru.tensor.sbis.date_picker.toCalendar
import java.util.Calendar
import java.util.Date

/**
 * Определяет, является ли дата выходным днём
 *
 * @return является ли дата выходным днём
 */
fun Date.isWeekend(): Boolean {
    return when (toCalendar().get(Calendar.DAY_OF_WEEK)) {
        Calendar.SATURDAY, Calendar.SUNDAY -> true
        else -> false
    }
}

/**
 * Создаёт экземпляр [Date] на основе текущей даты с заданными годом, месяцем и днём
 *
 * @param year год
 * @param month месяц
 * @param dayOfMonth день месяца
 * @return [Date] с заданными значениями
 */
fun Date.set(year: Int, month: Int, dayOfMonth: Int): Date {
    val calendar = Calendar.getInstance()
    calendar.set(year, month, dayOfMonth)
    time = calendar.timeInMillis
    return this
}

/**
 * Проверяет, представляет ли заданная дата тот же день, что и дата, для которой осуществляется вызов
 *
 * @return представляет ли указанная дата тот же день, что и [this]
 */
fun Date.isTheSameDay(date: Date): Boolean {
    return BaseDateUtils.isTheSameDay(this, date)
}

/**
 * Проверяет, представляет ли заданная дата ту же неделю, что и дата, для которой осуществляется вызов
 *
 * @return представляет ли указанная дата ту же неделю, что и [this]
 */
fun Date.isTheSameWeek(date: Date): Boolean {
    return date.toCalendar().run {
        val calendar = toCalendar()
        calendar.get(Calendar.WEEK_OF_YEAR) == get(Calendar.WEEK_OF_YEAR) &&
                calendar.get(Calendar.YEAR) == get(Calendar.YEAR)
    }
}

/**
 * Возвращает для типа периода месяца другой формат, свойственный МП Бизнес
 */
fun DatePeriod.shortestFormat(resourceProvider: ResourceProvider?): String = when (type) {
    PeriodType.MONTH -> getFormattedYearWithPrefix(from, formatMonthShort(from, resourceProvider))
    else             -> shortFormat(resourceProvider)
}