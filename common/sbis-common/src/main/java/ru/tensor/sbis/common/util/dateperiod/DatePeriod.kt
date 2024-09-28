package ru.tensor.sbis.common.util.dateperiod

import android.os.Parcel
import android.os.Parcelable
import androidx.annotation.Keep
import ru.tensor.sbis.common.util.ResourceProvider
import java.io.Serializable
import java.util.Calendar
import java.util.Date
import java.util.TimeZone

/**
 * Интервал между двумя датами
 *
 * @param fromDateSource хранит корректные день, месяц и год начальной даты
 * @param toDateSource хранит корректные день, месяц и год конечной даты
 *
 * @property calendarFrom начальная дата как [Calendar]
 * @property calendarTo конечная дата как [Calendar]
 * @property from начальная дата как [Date]
 * @property to конечная дата как [Date]
 * @property utcFrom начальная дата в UTC
 * @property utcTo конечная дата в UTC
 * @property timeZone часовой пояс периода
 */
@Keep
data class DatePeriod(
    private val fromDateSource: Calendar = Calendar.getInstance(),
    private val toDateSource: Calendar = Calendar.getInstance()
) : Parcelable,
    Serializable {

    /**
     * Создаёт период, с начальной и конечной датами, представленными [Date]
     *
     * @param from начальная дата
     * @param to конечная дата
     */
    constructor(
        from: Date,
        to: Date
    ) : this(
        from.toCalendar().copyDateOnly(),
        to.toCalendar().copyDateOnly()
    )

    val calendarFrom: Calendar
        get() = fromDateSource.copy()

    val calendarTo: Calendar
        get() = toDateSource.copy()

    val from: Date
        get() = fromDateSource.time

    val to: Date
        get() = toDateSource.time

    val utcFrom: Date
        get() = fromDateSource
            .copyDateOnly(getUtcTimeZone())
            .time

    val utcTo: Date
        get() = toDateSource
            .copyDateOnly(getUtcTimeZone())
            .time

    /**
     * Возвращает тип периода, или [PeriodType.UNDEFINED], если не удалось его определить
     * Период относится к некоторому типу, если день его начала и день конца соответствуют дням
     * начала и конца конкретного года или определённой его части
     */
    val type: PeriodType
        get() = when {
            isSpecificYear()                        -> PeriodType.YEAR
            isFirstHalfYear() || isSecondHalfYear() -> PeriodType.HALF_YEAR
            isQuarter()                             -> PeriodType.QUARTER
            isSpecificMonth()                       -> PeriodType.MONTH
            isSpecificDay()                         -> PeriodType.DAY
            else                                    -> PeriodType.UNDEFINED
        }

    val timeZone: TimeZone
        get() = fromDateSource.timeZone

    /**
     * Проверяет вхождение указанной даты в данный интервал
     *
     * @param date проверяемая дата
     * @return входит ли [date] в этот интервал
     */
    fun contains(date: Date) = date in from..to

    private fun isSpecificMonth(): Boolean {
        val isWithinOneMonth = fromDateSource.get(Calendar.YEAR) == toDateSource.get(Calendar.YEAR) &&
                fromDateSource.get(Calendar.MONTH) == toDateSource.get(Calendar.MONTH)
        return isWithinOneMonth &&
                fromDateSource.get(Calendar.DAY_OF_MONTH) == fromDateSource.getActualMinimum(Calendar.DAY_OF_MONTH) &&
                toDateSource.get(Calendar.DAY_OF_MONTH) == toDateSource.getActualMaximum(Calendar.DAY_OF_MONTH)
    }

    /**
     * Проверяет, представляет ли данный период конкретный день
     *
     * @return является ли период конкретным днём
     */
    fun isSpecificDay(): Boolean {
        return fromDateSource.get(Calendar.YEAR) == toDateSource.get(Calendar.YEAR) &&
                fromDateSource.get(Calendar.DAY_OF_YEAR) == toDateSource.get(Calendar.DAY_OF_YEAR)
    }

    /**
     * Проверяет, является ли начальная дата вчерашним днём по отношению к конечной
     *
     * @return является ли начальная дата вчерашним днём по отношению к конечной
     */
    fun isFromYesterday(): Boolean {
        return fromDateSource.get(Calendar.YEAR) == toDateSource.get(Calendar.YEAR) &&
                fromDateSource.get(Calendar.DAY_OF_YEAR) + 1 == toDateSource.get(Calendar.DAY_OF_YEAR)
    }

    /**
     * Форматирует период в коротком варианте
     *
     * @return текстовое представление периода в коротком варианте
     */
    fun shortFormat(): String {
        return when (type) {
            PeriodType.DAY       -> formatDateShort(from)
            PeriodType.MONTH     -> getFormattedYearWithPrefix(from, formatMonth(from))
            PeriodType.QUARTER   -> formatQuarter(this, shortFormat = true)
            PeriodType.HALF_YEAR -> formatHalfYear(this, shortFormat = true)
            else                 -> formatYear(this.from)
        }
    }

    /**
     * Форматирует период в длинном варианте, текстовое представление не будет локализовано
     *
     * @return текстовое представление периода в длинном варианте
     */
    @Deprecated("Более не использовать", ReplaceWith("longFormat(resourceProvider)"))
    fun longFormat(): String =
        when (type) {
            PeriodType.DAY -> longFormat(from)
            PeriodType.MONTH -> getFormattedYearWithPrefix(from, formatMonth(from))
            PeriodType.QUARTER -> formatQuarter(this)
            PeriodType.HALF_YEAR -> formatHalfYear(this)
            PeriodType.YEAR -> formatYear(from)
            PeriodType.UNDEFINED -> toString()
        }

    /**
     * Форматирует период в длинном варианте, текстовое представление будет локализовано
     *
     * @param resourceProvider провайдер ресурсов
     * @param shortMonthForDay полный или сокращенный месяц для периода равного одному дню
     * Пример: 03 февр. 2019 или 03 Февраля'2019
     * @return текстовое представление периода в длинном варианте
     */
    fun longFormat(resourceProvider: ResourceProvider, shortMonthForDay: Boolean = true): String =
        when (type) {
            PeriodType.DAY -> longFormat(from, resourceProvider, shortMonthForDay)
            PeriodType.MONTH -> getFormattedYearWithPrefix(from, formatMonth(from, resourceProvider))
            PeriodType.QUARTER -> formatQuarter(this, resourceProvider = resourceProvider)
            PeriodType.HALF_YEAR -> formatHalfYear(this, resourceProvider = resourceProvider)
            PeriodType.YEAR -> formatYear(from)
            PeriodType.UNDEFINED -> toString()
        }

    /**
     * Форматирует период в коротком варианте
     * @param resourceProvider провайдер ресурсов, если null ответ не будет локализован
     * @return текстовое представление периода в коротком варианте
     */
    fun shortFormat(resourceProvider: ResourceProvider?): String {
        return when (type) {
            PeriodType.DAY       -> formatDateShort(from)
            PeriodType.MONTH     -> getFormattedYearWithPrefix(from, formatMonth(from, resourceProvider))
            PeriodType.QUARTER   -> formatQuarter(this, shortFormat = true, resourceProvider = resourceProvider)
            PeriodType.HALF_YEAR -> formatHalfYear(this, shortFormat = true, resourceProvider = resourceProvider)
            else                 -> formatYear(this.from)
        }
    }

    /**
     * Проверяет, представляет ли данный период конкретный год
     *
     * @return является ли период конкретным годом
     */
    private fun isSpecificYear(): Boolean {
        val isWithinOneYear = fromDateSource.get(Calendar.YEAR) == toDateSource.get(Calendar.YEAR)
        return isWithinOneYear &&
                fromDateSource.get(Calendar.DAY_OF_YEAR) == fromDateSource.getActualMinimum(Calendar.DAY_OF_YEAR) &&
                toDateSource.get(Calendar.DAY_OF_YEAR) == toDateSource.getActualMaximum(Calendar.DAY_OF_YEAR)
    }

    /**
     * Проверяет, представляет ли данный период первое полугодие некоторого года
     *
     * @return является ли период первым полугодием
     */
    fun isFirstHalfYear() = isMonthsRangeWithinOneYear(Calendar.JANUARY, Calendar.JUNE)

    /**
     * Проверяет, представляет ли данный период второе полугодие некоторого года
     *
     * @return является ли период первым полугодием
     */
    private fun isSecondHalfYear() = isMonthsRangeWithinOneYear(Calendar.JULY, Calendar.DECEMBER)

    /**
     * Проверяет, представляет ли данный период первый квартал некоторого года
     *
     * @return является ли период первым кварталом
     */
    fun isFirstQuarter() = isMonthsRangeWithinOneYear(Calendar.JANUARY, Calendar.MARCH)

    /**
     * Проверяет, представляет ли данный период второй квартал некоторого года
     *
     * @return является ли период вторым кварталом
     */
    fun isSecondQuarter() = isMonthsRangeWithinOneYear(Calendar.APRIL, Calendar.JUNE)

    /**
     * Проверяет, представляет ли данный период третий квартал некоторого года
     *
     * @return является ли период третьим кварталом
     */
    fun isThirdQuarter() = isMonthsRangeWithinOneYear(Calendar.JULY, Calendar.SEPTEMBER)

    private fun isFourthQuarter() = isMonthsRangeWithinOneYear(Calendar.OCTOBER, Calendar.DECEMBER)

    /**
     * Проверяет, представляет ли данный период квартал
     *
     * @return является ли период кварталом
     */
    private fun isQuarter() = isFirstQuarter() || isSecondQuarter() || isThirdQuarter() || isFourthQuarter()

    /**
     * Получить новый период такого же типа, как и исходный, применив для середины текущего заданное
     * изменение поля соотетствующего объекта [Calendar]. Если тип периода не определён, изменение
     * просто применяется для начала и конца
     *
     * @param calendarField константа [Calendar], задающая интервал сдвига периода
     * @param amount число интервалов, которое будет добавлено к текущему периоду
     * (значение может быть отрицательным)
     * @return период, полученный путём добавления заданных интервалов к текущему
     */
    fun shiftedBy(
        calendarField: Int,
        amount: Int
    ): DatePeriod {
        val middle = Date((from.time + to.time) / 2L).toCalendar()
        middle.add(calendarField, amount)
        return when (type) {
            PeriodType.YEAR      -> fromYear(middle.time)
            PeriodType.HALF_YEAR -> fromHalfYear(middle.time)
            PeriodType.QUARTER   -> fromQuarter(middle.time)
            PeriodType.MONTH     -> fromMonth(middle.time)
            PeriodType.DAY       -> fromDay(middle.time)
            PeriodType.UNDEFINED -> {
                val resultFrom = calendarFrom
                resultFrom.add(calendarField, amount)
                val resultTo = calendarTo
                resultTo.add(calendarField, amount)
                return DatePeriod(resultFrom, resultTo)
            }
        }
    }

    /**
     * Привести период к соответствующему типу
     *
     * @param type тип периода
     * @return период переданного типа
     */
    fun toType(type: PeriodType) = fromDate(from, type)

    /**
     * Проверяет, является ли период актуальным на данный момент
     *
     * @return актуален ли период на текущую дату в его часовом поясе
     */
    fun isActualPeriod(): Boolean {
        val timeZone = fromDateSource.timeZone
        val actualDate = Calendar.getInstance(timeZone)
            .copyDateOnly(timeZone)
            .time
        return actualDate in fromDateSource.time..toDateSource.time
    }

    /**
     * Создаёт новый период на основе дат этого
     *
     * @return [DatePeriod] для тех же дат, но в текущем часовом поясе
     */
    fun toSamePeriodInDefaultTimeZone() = DatePeriod(fromDateSource.copyDateOnly(), toDateSource.copyDateOnly())

    /**
     * Создаёт период того же типа, что и этот, актуальный на текущую дату
     *
     * @return актуальный на данный момент [DatePeriod] того же типа в текущем часовом поясе
     */
    fun toActualPeriodOfSameTypeForCurrentTimeZone() = fromDate(Date(), toSamePeriodInDefaultTimeZone().type)

    /**
     * Находит разницу между первым и вторым периодом в зависимости от их типа
     *
     * @param previousPeriod сравниваемый период с тем же типом
     * @return разница текущей и сравниваемой даты.
     * Положительное число если сравниваемое меньше, отрицательное если больше и
     * ноль если периоды эквивалентны или имеют разные типы
     */
    fun between(previousPeriod: DatePeriod): Int {
        if (type != previousPeriod.type) return 0
        val previous = previousPeriod.calendarFrom
        return when (type) {
            PeriodType.YEAR  -> calendarFrom.get(Calendar.YEAR) - previous.get(Calendar.YEAR)
            PeriodType.HALF_YEAR,
            PeriodType.QUARTER,
            PeriodType.MONTH -> diffOfMonthQuarterHalfYear(previousPeriod)
            else             -> calendarFrom.get(Calendar.DAY_OF_YEAR) - previous.get(Calendar.DAY_OF_YEAR)
        }
    }

    private fun diffOfMonthQuarterHalfYear(previous: DatePeriod): Int {
        val previousYearAmount = previous.calendarFrom.get(Calendar.YEAR)
        val newYearAmount = calendarFrom.get(Calendar.YEAR)
        val (previousAmount, newAmount) = when (type) {
            PeriodType.HALF_YEAR -> {
                previousYearAmount * HALVES_IN_YEAR + previous.halfNumber() to
                        newYearAmount * HALVES_IN_YEAR + halfNumber()
            }
            PeriodType.QUARTER   -> {
                previousYearAmount * QUARTER_IN_YEAR + previous.quarterNumber() to
                        newYearAmount * QUARTER_IN_YEAR + quarterNumber()
            }
            else                 -> {
                previousYearAmount * MONTHS_IN_YEAR + previous.calendarFrom.get(Calendar.MONTH) to
                        newYearAmount * MONTHS_IN_YEAR + calendarFrom.get(Calendar.MONTH)
            }
        }
        return newAmount - previousAmount
    }

    /**
     * Получить порядковый номер квартала
     */
    private fun quarterNumber(): Int = when {
        isFirstQuarter()  -> 1
        isSecondQuarter() -> 2
        isThirdQuarter()  -> 3
        isFourthQuarter() -> 4
        else              -> 0
    }

    /**
     * Получить порядковый номер полугодия
     */
    private fun halfNumber() = when {
        isFirstHalfYear()  -> 1
        isSecondHalfYear() -> 2
        else               -> 0
    }

    override fun toString(): String {
        val fromStr = formatDateShort(this.from)
        val toStr = formatDateShort(this.to)
        return "$fromStr - $toStr"
    }

    //region Parcelable
    private constructor(`in`: Parcel) : this(
        (`in`.readSerializable() as Calendar).copyDateOnly(),
        (`in`.readSerializable() as Calendar).copyDateOnly()
    )

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(
        parcel: Parcel,
        i: Int
    ) {
        parcel.writeSerializable(fromDateSource.copyDateOnly())
        parcel.writeSerializable(toDateSource.copyDateOnly())
    }
    //endregion

    companion object {
        private const val MONTHS_IN_YEAR = 12
        private const val QUARTER_IN_YEAR = 4
        private const val HALVES_IN_YEAR = 2

        @JvmField
        val CREATOR: Parcelable.Creator<DatePeriod> = object : Parcelable.Creator<DatePeriod> {
            override fun createFromParcel(`in`: Parcel): DatePeriod {
                return DatePeriod(`in`)
            }

            override fun newArray(size: Int): Array<DatePeriod?> {
                return arrayOfNulls(size)
            }
        }

        /**
         * Создаёт период, соответствующий указанному месяцу
         *
         * @param month месяц года начиная с 0
         * @param year год
         * @return период типа [PeriodType.MONTH] для указанного месяца
         */
        @JvmStatic
        private fun fromMonth(
            month: Int,
            year: Int
        ) = fromMonthsRange(year, month, month)

        /**
         * Создаёт период, соответствующий месяцу указанной даты
         *
         * @param date дата, для месяца которой будет создан период
         * @return период типа [PeriodType.MONTH] для месяца указанной даты
         */
        @JvmStatic
        fun fromMonth(date: Date): DatePeriod {
            val year = date.toCalendar()
                .get(Calendar.YEAR)
            val month = date.toCalendar()
                .get(Calendar.MONTH)
            return fromMonth(month, year)
        }

        /**
         * Создаёт период, соответствующий указанному году
         *
         * @param year год
         * @return период типа [PeriodType.YEAR] для указанного года
         */
        @JvmStatic
        private fun fromYear(year: Int) = fromMonthsRange(year, Calendar.JANUARY, Calendar.DECEMBER)

        /**
         * Создаёт период, соответствующий году указанной даты
         *
         * @param date дата, для года которой будет создан период
         * @return период типа [PeriodType.YEAR] для года указанной даты
         */
        @JvmStatic
        fun fromYear(date: Date): DatePeriod = fromYear(date.toCalendar().get(Calendar.YEAR))

        /**
         * Создаёт период для указанного дня
         *
         * @param date дата, для которой будет создан период
         * @return период типа [PeriodType.DAY] для указанной даты
         */
        @JvmStatic
        fun fromDay(date: Date): DatePeriod = DatePeriod(date.withoutTime(), date.withoutTime())

        /**
         * Создаёт период для полугодия указанной даты
         *
         * @param date дата, для полугодия которой будет создан период
         * @return период типа [PeriodType.HALF_YEAR] для полугодия указанной даты
         */
        @JvmStatic
        fun fromHalfYear(date: Date): DatePeriod {
            val year = date.toCalendar()
                .get(Calendar.YEAR)
            return if (date.withoutTime() <= fromMonth(Calendar.JUNE, year).to) {
                fromMonthsRange(year, Calendar.JANUARY, Calendar.JUNE)
            } else {
                fromMonthsRange(year, Calendar.JULY, Calendar.DECEMBER)
            }
        }

        /**
         * Создаёт период для квартала указанной даты
         *
         * @param date дата, для квартала которой будет создан период
         * @return период типа [PeriodType.QUARTER] для квартала указанной даты
         */
        @JvmStatic
        fun fromQuarter(date: Date): DatePeriod {
            val year = date.toCalendar()
                .get(Calendar.YEAR)
            val month = date.toCalendar()
                .get(Calendar.MONTH)
            return when (month / 3 + 1) {
                1    -> return fromMonthsRange(year, Calendar.JANUARY, Calendar.MARCH)
                2    -> return fromMonthsRange(year, Calendar.APRIL, Calendar.JUNE)
                3    -> return fromMonthsRange(year, Calendar.JULY, Calendar.SEPTEMBER)
                else -> fromMonthsRange(year, Calendar.OCTOBER, Calendar.DECEMBER)
            }
        }

        @JvmStatic
        private fun fromMonthsRange(
            year: Int,
            firstMonth: Int,
            lastMonth: Int
        ): DatePeriod {
            val from = Date(0).toCalendar()
                .apply {
                    set(year, firstMonth, 1, 0, 0, 0)
                    set(Calendar.DAY_OF_MONTH, getActualMinimum(Calendar.DAY_OF_MONTH))
                }
            val to = Date(0).toCalendar()
                .apply {
                    set(year, lastMonth, 1, 0, 0, 0)
                    set(Calendar.DAY_OF_MONTH, getActualMaximum(Calendar.DAY_OF_MONTH))
                }
            return DatePeriod(from.time, to.time)
        }

        /**
         * Создаёт экземпляр периода по умолчанию (для текущего месяца)
         *
         * @return экземпляр периода по умолчанию
         */
        @JvmStatic
        fun getDefaultInstance(): DatePeriod = fromMonth(Date().withoutTime())

        /**
         * Создаёт экземпляр периода на основе дат в UTC
         *
         * @param utcFrom начальная дата в UTC
         * @param utcTo конечная дата в UTC
         * @return экземпляр заданного периода
         */
        @JvmStatic
        fun fromUtc(
            utcFrom: Date,
            utcTo: Date
        ) = DatePeriod(
            utcFrom.toUtcCalendar().copyDateOnly(),
            utcTo.toUtcCalendar().copyDateOnly()
        )

        /**
         * Создаёт экземпляр периода на основе заданных дат в текущем часовом поясе
         *
         * @param from дата в произвольном часовом поясе
         * @param to дата в произвольном часовом поясе
         * @return экземпляр заданного периода
         */
        @JvmStatic
        fun createWithTimeZoneConversion(
            from: Calendar,
            to: Calendar
        ) = DatePeriod(from.copyDateOnly(), to.copyDateOnly())

        /**
         * Создаёт экземпляр периода заданного типа, содержащий указанную дату
         *
         * @param date дата, которую должен содержать период
         * @param type желаемый тип периода
         * @return экземпляр периода заданного типа, актуальный на заданную дату
         */
        @JvmStatic
        fun fromDate(
            date: Date,
            type: PeriodType
        ): DatePeriod = when (type) {
            PeriodType.YEAR      -> fromYear(date)
            PeriodType.HALF_YEAR -> fromHalfYear(date)
            PeriodType.QUARTER   -> fromQuarter(date)
            PeriodType.MONTH     -> fromMonth(date)
            PeriodType.DAY       -> fromDay(date)
            PeriodType.UNDEFINED -> DatePeriod()
        }
    }
}

/**
 * Типы периода
 */
enum class PeriodType {
    YEAR,
    HALF_YEAR,
    QUARTER,
    MONTH,
    DAY,
    UNDEFINED
}

private fun DatePeriod.isMonthsRangeWithinOneYear(
    firstMonth: Int,
    lastMonth: Int
): Boolean {
    val year = if (calendarFrom.get(Calendar.YEAR) == calendarTo.get(Calendar.YEAR)) {
        calendarFrom.get(Calendar.YEAR)
    } else {
        return false
    }
    return from.withoutTime() == createDateWithoutTime(
        year,
        firstMonth,
        calendarFrom.getActualMinimum(Calendar.DAY_OF_MONTH)
    ) &&
            to.withoutTime() == createDateWithoutTime(
        year,
        lastMonth,
        calendarTo.getActualMaximum(Calendar.DAY_OF_MONTH)
    )
}

private fun Date.toUtcCalendar(): Calendar = Calendar.getInstance(getUtcTimeZone()).apply {
    time = this@toUtcCalendar
}