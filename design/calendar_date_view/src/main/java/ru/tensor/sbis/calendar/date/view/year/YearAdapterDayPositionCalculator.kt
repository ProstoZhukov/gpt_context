package ru.tensor.sbis.calendar.date.view.year

import android.util.Log
import androidx.collection.SparseArrayCompat
import org.joda.time.*
import ru.tensor.sbis.calendar.date.view.year.YearAdapter.Companion.DAYS_COUNT
import ru.tensor.sbis.calendar.date.view.year.YearAdapter.Companion.YEARS_THRESHOLD
import kotlin.math.ceil

/**
 * Промежуточная точка на линии времени
 * year - год
 * monthOfYear - месяц
 * dayOfMonth - день
 * daysBefore - дней от firstDate до localDate включая "пустые" дни и заголовки
 *
 * @author Roman Petrov (ra.petrov)
 */
internal data class DatePointItem(val year: Int, val monthOfYear: Int, val dayOfMonth: Int, val daysBefore: Int) {
    /**
     * Возвращает первый день месяца по свойствам
     */
    fun toMonthLocaleDate(): LocalDate = LocalDate(year, monthOfYear, 1)
}

/**
 * Вычисляем дату по позиции
 * @param firstDate - первая дата в списке. Всегда будет приведена к первому числу месяца
 */
internal class YearAdapterDayPositionCalculator(
    private var firstDate: LocalDate = DEFAULT_FIRST_DAY,
    private val initDays: Int = 1200,
    val lastDate: LocalDate? = null
) {

    // Запоминаем посчитанное
    private val calculatedBefore = SparseArrayCompat<DatePointItem>(initDays) // дата по позиции

    /*
        Месяца, используем чтобы не отсчитывать каждый раз от YearAdapter.FIRST_DAY если для каких-то месяцев уже
        поссчитали
     */
    private val previousMonths = SparseArrayCompat<DatePointItem>(initDays/365*12 + 1)

    init {
        if (firstDate.dayOfMonth != 1)
            firstDate = LocalDate(firstDate.year, firstDate.monthOfYear, 1)
    }

    private val indexOfLastDate: Int by lazy {
        if (lastDate != null) indexOfDay(lastDate) + 1 else 0
    }

    /**
     * Кол-во ячеек в адаптере
     */
    val maxDays: Int by lazy {
        if (lastDate == null) {
            DAYS_COUNT
        } else {
            indexOfLastDate
        }
    }

    /**
     * Взвращает позицию, соответствующую началу месяца, который определяется по LocalDate
     */
    fun indexOfMonth(localDate: LocalDate): Int {
        /**
         * Будем добавлять к YearAdapter.FIRST_DAY дни месяца + заголовок + пустые поля, пока не дойдём до localDate
         */
        var daysCount = 1
        var month = firstDate
        while (true) {
            val offsetDays = month.dayOfWeek - 1
            val daysInMonth = ceil((month.dayOfMonth().maximumValue + offsetDays) / 7.0) * 7 // 35 или 28
            if (month.year == localDate.year && month.monthOfYear == localDate.monthOfYear) return daysCount - 1
            month = month.plusMonths(1)
            daysCount += daysInMonth.toInt()
            daysCount++ // label
        }
    }

    /**
     * Позиция ячейки по дате [localDate]
     */
    fun indexOfDay(localDate: LocalDate) = if (localDate >= firstDate) {
        indexOfMonth(localDate) + localDate.dayOfMonth + firstDayOfMonth(localDate)
    } else if (lastDate != null && lastDate < localDate) {
        indexOfMonth(lastDate) + localDate.dayOfMonth + firstDayOfMonth(lastDate)
    } else {
        0
    }

    /**
     * Возвращает дату, в которой значим только месяц и год, по позиции
     */
    fun monthByPosition(position: Int): DatePointItem {
        calculatedBefore[position]?.let {
            return it
        }

        var cachedItem: DatePointItem? = null
        for (i in position..0) {
            previousMonths[position]?.let {
                cachedItem = it
            }
            if (cachedItem != null) break
        }

        var daysCount = cachedItem?.daysBefore ?: 0
        val month = cachedItem?.let {
            MutableDateTime(
                it.year,
                it.monthOfYear,
                it.dayOfMonth,
                0,
                0,
                0,
                0,
                DateTimeZone.UTC
            )
        } ?: MutableDateTime(
            firstDate.year,
            firstDate.monthOfYear,
            firstDate.dayOfMonth,
            0,
            0,
            0,
            0,
            DateTimeZone.UTC
        )

        while (true) {
            val offsetDays = month.dayOfWeek - 1
            // Должны получить 35 (5 недель в месяце, где к примеру 31 день) или 28 (фавраль где 01.02 это понедельник)
            val daysInMonth = ceil((month.dayOfMonth().maximumValue + offsetDays) / 7.0) * 7
            if (daysCount + daysInMonth + 1 > position) break
            month.addMonths(1)
            daysCount += daysInMonth.toInt()
            // добавляем 1, потому что учитываем название месяца
            daysCount++
        }
        val resultItem = DatePointItem(month.year, month.monthOfYear, 1, daysCount)
        calculatedBefore.put(position, resultItem)
        previousMonths.put(daysCount, resultItem)
        return resultItem
    }

    /**
     * Заполняет предварительно массив данных, чтобы потом не тормозило
     * Запустить один раз
     */
    fun preCountSpan() {
        val month = MutableDateTime(
            firstDate.year,
            firstDate.monthOfYear,
            firstDate.dayOfMonth,
            0,
            0,
            0,
            0,
            DateTimeZone.UTC
        )
        var daysCount = 0
        while (true) {
            val offsetDays = month.dayOfWeek - 1
            val daysInMonth = ceil((month.dayOfMonth().maximumValue + offsetDays) / 7.0) * 7
            val resultItem = DatePointItem(month.year, month.monthOfYear, 1, daysCount)

            month.addMonths(1)
            val fromPosition = daysCount
            daysCount += daysInMonth.toInt()
            daysCount++

            for(i in fromPosition until daysCount) calculatedBefore.put(i, resultItem)
            previousMonths.put(daysCount, resultItem)

            if (daysCount + daysInMonth + 1 > initDays) break
        }
    }

    private fun firstDayOfMonth(localDate: LocalDate): Int =
        localDate.withDayOfMonth(1).dayOfWeek - 1

    companion object {
        private val DEFAULT_FIRST_DAY = LocalDate().minusYears(YEARS_THRESHOLD).withMonthOfYear(1).withDayOfMonth(1)
    }
}