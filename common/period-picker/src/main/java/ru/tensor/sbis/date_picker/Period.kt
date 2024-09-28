package ru.tensor.sbis.date_picker

import android.annotation.SuppressLint
import android.os.Parcel
import org.apache.commons.lang3.builder.HashCodeBuilder
import ru.tensor.sbis.common.util.date.DateFormatTemplate
import ru.tensor.sbis.common.util.date.DateFormatUtils
import ru.tensor.sbis.date_picker.free.items.HistoryPeriod
import ru.tensor.sbis.date_picker.range.CalendarDayRange
import ru.tensor.sbis.date_picker.range.CalendarMonthRange
import ru.tensor.sbis.date_picker.range.monthRangeTo
import ru.tensor.sbis.date_picker.range.rangeTo
import java.io.Serializable
import java.util.*

const val NOT_SPECIFIED = -1

fun format(date: Date): String = DateFormatUtils.format(date, DateFormatTemplate.DATE_SPLIT_BY_POINTS_WITH_SHORT_YEAR)!!

@SuppressLint("ParcelCreator")
/**
 * Класс - обертка, содержащий начальную и конечную дату периода и вспомогательные методы для работы периодом дат.
 * @param dateFrom начало периода
 * @param dateTo конец периода
 * @param fakeDateTo фейковое значение конца периода (необходимо в случаях, когда конец периода dateTo не указан, но нужно корректно отобразить/сохранить выбранное значение)
 *
 * @author mb.kruglova
 */
class Period @JvmOverloads constructor(
    var dateFrom: Calendar?,
    var dateTo: Calendar?,
    var fakeDateTo: Calendar? = null
) : Serializable {

    val yearFrom: Int
        get() = dateFrom?.year ?: NOT_SPECIFIED
    val yearTo: Int
        get() = dateTo?.year ?: NOT_SPECIFIED
    val monthFrom: Int
        get() = dateFrom?.month ?: NOT_SPECIFIED
    val monthTo: Int
        get() = dateTo?.month ?: NOT_SPECIFIED

    val hasFrom: Boolean
        get() = dateFrom != null
    val hasTo: Boolean
        get() = dateTo != null
    val hasFromAndTo: Boolean
        get() = dateFrom != null && dateTo != null

    val fromFormatted: String
        get() = dateFrom?.let { format(it.time) } ?: ""
    val toFormatted: String
        get() = dateTo?.let { format(it.time) } ?: ""

    val from: Date?
        get() = dateFrom?.time
    val to: Date?
        get() = dateTo?.time

    val dayRange: CalendarDayRange?
        get() {
            return when {
                hasFromAndTo -> dateFrom!!..dateTo!!
                hasFrom -> dateFrom!!..dateFrom!!
                else -> null
            }
        }

    val monthRange: CalendarMonthRange?
        get() {
            return when {
                hasFromAndTo -> dateFrom!!.monthRangeTo(dateTo!!)
                hasFrom -> dateFrom!!.monthRangeTo(dateFrom!!)
                else -> null
            }
        }

    val containsMonthOrMore: Boolean
        get() {
            if (monthRange == null || dayRange == null) return false

            monthRange!!.forEach { month ->
                val startDayOfMonth = (month.clone() as Calendar).apply { dayOfMonth = 1 }
                val endDayOfMonth =
                    (month.clone() as Calendar).apply { dayOfMonth = month.getActualMaximum(Calendar.DAY_OF_MONTH) }
                if (dayRange!!.contains(startDayOfMonth) && dayRange!!.contains(endDayOfMonth)) {
                    return true
                }
            }
            return false
        }

    constructor(parcel: Parcel) : this(parcel.readSerializable() as Calendar?, parcel.readSerializable() as Calendar?)

    /**
     * Проверяет, в обратном ли порялдке даты "от" и "до"
     * @return true если даты "от" и "до" не в порядке возрастания.
     */
    private fun isFromAndToInReverseOrder() =
        (dateFrom?.time?.time ?: 0) > (dateTo?.time?.time ?: Long.MAX_VALUE)

    /**
     * Меняет местами даты "от" и "до".
     */
    private fun swapFromAndTo() {
        dateFrom = dateTo.also { dateTo = dateFrom }
    }

    fun createMiddle(default: Calendar) = when {
        (hasFrom && !hasTo) -> dateFrom!!.clone() as Calendar
        (!hasFrom && hasTo) -> dateTo!!.clone() as Calendar
        hasFromAndTo -> (dateFrom!!.clone() as Calendar).also {
            it.timeInMillis = (dateTo!!.timeInMillis + it.timeInMillis) / 2
        }
        else -> default.clone() as Calendar
    }

    operator fun minus(other: Period): Period {
        val dayRange = dayRange ?: return this
        val otherDayRange = other.dayRange ?: return this
        val diff = dayRange.minus(otherDayRange).sorted()
        return Period(diff.firstOrNull(), diff.lastOrNull())
    }

    companion object {
        fun create(): Period {
            return Period(null, null)
        }

        fun createFrom(key: PeriodsVMKey, selected: Int): Period {
            return when (key.mode) {
                Mode.YEAR -> {
                    val from = GregorianCalendar(key.year, selected, 1)
                    val fakeTo = GregorianCalendar(key.year, selected, 1)
                    fakeTo.set(Calendar.DAY_OF_MONTH, fakeTo.getActualMaximum(Calendar.DAY_OF_MONTH))
                    Period(from, null, fakeTo)
                }
                Mode.MONTH -> {
                    val day = GregorianCalendar(key.year, key.month, selected)
                    Period(day, null, day)
                }
                Mode.FREE -> throw IllegalArgumentException("Недопустимый режим")
            }
        }

        fun createFromAndTo(period: Period, key: PeriodsVMKey, selected: Int): Period {
            when (key.mode) {
                Mode.YEAR -> {
                    period.dateTo = GregorianCalendar(key.year, selected, 1)
                    if (period.isFromAndToInReverseOrder()) {
                        period.swapFromAndTo()
                        period.dateFrom?.run {
                            set(Calendar.DAY_OF_MONTH, getActualMinimum(Calendar.DAY_OF_MONTH))
                        }
                    }
                    period.dateTo?.run {
                        set(Calendar.DAY_OF_MONTH, getActualMaximum(Calendar.DAY_OF_MONTH))
                    }
                }
                Mode.MONTH -> {
                    period.dateTo = GregorianCalendar(key.year, key.month, selected)
                    if (period.isFromAndToInReverseOrder()) {
                        period.swapFromAndTo()
                    }
                }
                Mode.FREE -> throw IllegalArgumentException("Недопустимый режим")
            }
            return period
        }

        fun fromYear(year: Int): Period {
            val from = GregorianCalendar(year, Calendar.JANUARY, 1)
            val to = GregorianCalendar(year, Calendar.DECEMBER, 31)
            return Period(from, to)
        }

        fun fromHalfYear(year: Int, halfYear: Int): Period {
            val from = GregorianCalendar(year, halfYear * 6, 1)
            val to = GregorianCalendar(year, halfYear * 6 + 5, 1)
            to.set(Calendar.DAY_OF_MONTH, to.getActualMaximum(Calendar.DAY_OF_MONTH))
            return Period(from, to)
        }

        fun fromQuarter(year: Int, quarter: Int): Period {
            val from = GregorianCalendar(year, quarter * 3, 1)
            val to = GregorianCalendar(year, quarter * 3 + 2, 1)
            to.set(Calendar.DAY_OF_MONTH, to.getActualMaximum(Calendar.DAY_OF_MONTH))
            return Period(from, to)
        }

        fun fromMonth(year: Int, month: Int): Period {
            val from = GregorianCalendar(year, month, 1)
            val to = GregorianCalendar(year, month, 1)
            to.set(Calendar.DAY_OF_MONTH, to.getActualMaximum(Calendar.DAY_OF_MONTH))
            return Period(from, to)
        }

        fun fromMonth(year: Int, month: Int, dayFrom: Int, dayTo: Int): Period {
            val from = GregorianCalendar(year, month, dayFrom)
            val to = GregorianCalendar(year, month, dayTo)
            return Period(from, to)
        }

        fun fromDay(year: Int, month: Int, day: Int): Period {
            val date = GregorianCalendar(year, month, day)
            return Period(date, date)
        }

        fun fromHistoryPeriod(period: HistoryPeriod) = Period(period.from.toCalendar(), period.to?.toCalendar())

        fun fromDate(from: Date, to: Date? = null) = Period(from.toCalendar(), to?.toCalendar() ?: from.toCalendar())

        fun fromYearRange(meanDate: Calendar, yearRange: Int): Period {
            val from = meanDate.clone() as Calendar
            from.set(Calendar.YEAR, from.year - yearRange)
            val to = meanDate.clone() as Calendar
            to.set(Calendar.YEAR, to.year + yearRange)
            return Period(from, to)
        }
    }

    override fun toString(): String {
        return StringBuilder()
            .append("from: ")
            .append(dateFrom?.time?.let { format(it) } ?: "not specified")
            .append(" to: ")
            .append(dateTo?.time?.let { format(it) } ?: "not specified")
            .toString()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        val that = other as Period

        val dateFrom = dateFrom
        val dateTo = dateTo
        val thatDateFrom = that.dateFrom
        val thatDateTo = that.dateTo
        val dateFromEquals =
            (dateFrom == null && thatDateFrom == null) || (dateFrom != null && thatDateFrom != null && dateFrom sameDay thatDateFrom)
        val dateToEquals =
            (dateTo == null && thatDateTo == null) || (dateTo != null && thatDateTo != null && dateTo sameDay thatDateTo)
        return dateFromEquals && dateToEquals
    }

    override fun hashCode(): Int {
        return HashCodeBuilder()
            .append(dateFrom)
            .append(dateTo)
            .toHashCode()
    }
}