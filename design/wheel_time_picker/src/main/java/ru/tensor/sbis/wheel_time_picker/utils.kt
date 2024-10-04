package ru.tensor.sbis.wheel_time_picker

import org.joda.time.LocalDateTime
import java.util.Calendar
import java.util.GregorianCalendar
import java.util.concurrent.TimeUnit
import kotlin.properties.ObservableProperty
import kotlin.reflect.KProperty

val DEFAULT_MAX_TIME_OFF_VALUE = TimeUnit.HOURS.toMillis(8)
const val MAX_POSSIBLE_DAY_IN_MONTH = 31
const val MAX_YEARS_COUNT = 3000
const val MIN_YEARS_COUNT = 1000

/** Слушатель изменений свойства */
internal inline fun <T> observable(initValue: T, crossinline onUpdate: (newValue: T) -> Unit) =
    object : ObservableProperty<T>(initValue) {
        init {
            onUpdate(initValue)
        }

        override fun afterChange(property: KProperty<*>, oldValue: T, newValue: T) = onUpdate(newValue)
    }

/** @SelfDocumented */
internal fun getMidnight(date: LocalDateTime): LocalDateTime {
    return date.plusDays(1).withHourOfDay(0).withMinuteOfHour(0)
}

/**
 * Получение количества дней в месяце.
 * @param month - номер месяца (январь = 0, декабрь = 11).
 * @param year  - год.
 */
internal fun getDaysCountInMonth(month: Int, year: Int): Int {
    val cal = GregorianCalendar(year, month, 1)
    return cal.getActualMaximum(Calendar.DAY_OF_MONTH)
}