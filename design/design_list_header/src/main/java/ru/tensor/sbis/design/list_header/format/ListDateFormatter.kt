package ru.tensor.sbis.design.list_header.format

import android.content.Context
import org.apache.commons.lang3.StringUtils
import ru.tensor.sbis.common.util.date.DateFormatTemplate
import ru.tensor.sbis.common.util.date.DateFormatUtils
import ru.tensor.sbis.common.util.dateperiod.toCalendar
import ru.tensor.sbis.design.utils.CalendarUtils
import java.util.Calendar
import java.util.Date
import ru.tensor.sbis.design.R as RDesign

/**
 *
 * Форматирует представление FormattedDateTime для конкретной ячейки.
 *
 * @author ra.petrov
 */
sealed class ListDateFormatter {

    /**
     *  Форматирует представление [FormattedDateTime] для конкретной ячейки.
     *
     *  @param date - дата из ячейки
     *  @param previousDate дата из предыдущей ячейки или null, если её нет
     *  @param используется ли параметр reverseLayout в layoutManager
     */
    abstract fun format(date: Date, previousDate: Date? = null, reverseLayout: Boolean = false): FormattedDateTime

    /**
     *  Форматирует представление [FormattedDateTime] для заголовка.
     *
     *  @param date - дата из ячейки
     */
    abstract fun format(date: Date): FormattedDateTime

    /**
     *  Форматирует дату для конкретной ячейки.
     *
     *  @param date - дата из ячейки
     */
    abstract fun formatDate(date: Date): String

    /**
     *  Форматирует время для конкретной ячейки.
     *
     *  @param time - время из ячейки
     */
    abstract fun formatTime(time: Date): String

    /**
     *  Форматирует дату и время в соответствии с правилами:
     *
     *      | Дата             | Заголовок   |  Первая в дне ячейка  | Другие ячейки |
     *      | ---------------- |-------------|-----------------------|
     *      | Текущий день     |             | 23.04 13:43           | 13:43
     *      | Текущий год      |  23.04      | 23.04 13:43           | 13:43
     *      | Прошлый год      |  23.04.19   | 23.04.19 13:43        | 13:43
     */
    class DateTime : ListDateFormatter() {

        private val now = Date()

        override fun format(date: Date, previousDate: Date?, reverseLayout: Boolean): FormattedDateTime {
            val dateFormatted = if (previousDate != null && CalendarUtils.isSameDay(date, previousDate))
                ""
            else
                formatDate(date)

            return FormattedDateTime(dateFormatted, formatTime(date))
        }

        override fun format(date: Date): FormattedDateTime = format(date, null)

        override fun formatDate(date: Date): String = when (date.year) {
            now.year -> DateFormatUtils.format(date, DateFormatTemplate.DATE_WITHOUT_YEAR)
            else -> DateFormatUtils.format(date, DateFormatTemplate.DATE_SPLIT_BY_POINTS_WITH_SHORT_YEAR)
        }

        override fun formatTime(time: Date): String =
            DateFormatUtils.format(time, DateFormatTemplate.ONLY_TIME)
    }

    /**
     * Форматирует дату и время в соответствии с правилами:
     *
     *     | Дата             | Заголовок   |  Первая в дне ячейка  | Другие ячейки |
     *     | ---------------- |-------------|-----------------------|
     *     | Текущий день     |  Сегодня    | 13:43                 | 13:43
     *     | Текущий год      |  23.04      | 23.04                 | 23.04
     *     | Прошлый год      |  23.04.19   | 23.04.19              | 23.04.19
     */
    class DateTimeWithToday(context: Context) : ListDateFormatter() {

        /**
         * Дата "сегодня"
         */
        private val now = Date()

        /**
         * Строка "Сегодня"
         */
        private val stringToday: String = context.getString(RDesign.string.design_date_today)

        override fun format(date: Date, previousDate: Date?, reverseLayout: Boolean): FormattedDateTime {
            val dateFormatted = if (date.isToday())
                ""
            else
                formatDate(date)

            return FormattedDateTime(dateFormatted, if (date.isToday()) formatTime(date) else "")
        }

        override fun format(date: Date): FormattedDateTime {
            return FormattedDateTime(formatDate(date), formatTime(date))
        }

        override fun formatDate(date: Date): String = when {
            date.isToday() -> stringToday
            date.year == now.year -> DateFormatUtils.format(date, DateFormatTemplate.DATE_WITHOUT_YEAR)
            else -> DateFormatUtils.format(date, DateFormatTemplate.DATE_SPLIT_BY_POINTS_WITH_SHORT_YEAR)
        }

        override fun formatTime(time: Date): String =
            DateFormatUtils.format(time, DateFormatTemplate.ONLY_TIME)
    }

    /**
     * Форматирует дату и время в соответствии с правилами:
     *
     *     | Дата             | Заголовок   |  Первая в дне ячейка  | Другие ячейки |
     *     | ---------------- |-------------|-----------------------|
     *     | Текущий день     | Сегодня     |  Сегодня              |
     *     | Текущий год      | 23.04       |  23.04                |
     *     | Прошлый год      | 23.04.19    |  23.04.19             |
     */
    class DatesOnlyWithToday(context: Context) : ListDateFormatter() {

        /**
         * Дата "сегодня"
         */
        private val now = Date()

        /**
         * Строка "Сегодня"
         */
        private val stringToday: String = context.getString(RDesign.string.design_date_today)

        override fun format(date: Date, previousDate: Date?, reverseLayout: Boolean): FormattedDateTime {
            val dateFormatted = if (previousDate == null || !CalendarUtils.isSameDay(date, previousDate))
                formatDate(date)
            else
                ""

            return FormattedDateTime(dateFormatted, formatTime(date))
        }

        override fun format(date: Date): FormattedDateTime {
            return FormattedDateTime(formatDate(date), formatTime(date))
        }

        override fun formatDate(date: Date): String = when {
            date.isToday() -> stringToday
            date.year == now.year -> DateFormatUtils.format(date, DateFormatTemplate.DATE_WITHOUT_YEAR)
            else -> DateFormatUtils.format(date, DateFormatTemplate.DATE_SPLIT_BY_POINTS_WITH_SHORT_YEAR)
        }

        override fun formatTime(time: Date): String = ""
    }

    /**
     * Форматирует дату и время в соответствии с правилами:
     *
     *     | Дата             | Заголовок   |  Первая в дне ячейка  | Другие ячейки |
     *     | ---------------- |-------------|-----------------------|
     *     | Текущий день     | Сегодня     |  13:43                | 13:43
     *     | Текущий год      | 23 апр      |  23 апр 13:43         | 13:43
     *     | Прошлый год      | 23 апр 2019 |  23 апр 2019 13:43    | 13:43
     *
     * @author ns.staricyn
     */
    class DateTimeWithTodayStandard(context: Context) : ListDateFormatter() {

        /**
         * Дата "сегодня"
         */
        private val now = Date()

        /**
         * Строка "Сегодня"
         */
        private val stringToday: String = context.getString(RDesign.string.design_date_today)
        private val months = context.resources.getStringArray(RDesign.array.design_months_short_standard_without_dot)

        override fun format(
            date: Date,
            previousDate: Date?,
            reverseLayout: Boolean
        ): FormattedDateTime {
            val dateFormatted = when {
                previousDate == null -> formatDate(date)
                CalendarUtils.isSameDay(date, previousDate) -> ""
                else -> formatDate(date)
            }

            return FormattedDateTime(dateFormatted, formatTime(date))
        }

        override fun format(date: Date): FormattedDateTime {
            return FormattedDateTime(formatDate(date), formatTime(date))
        }

        override fun formatDate(date: Date): String = when {
            date.isToday() -> stringToday
            date.toCalendar().getYear() == now.toCalendar().getYear() -> getDayWithMonth(date)
            else -> "${getDayWithMonth(date)} ${DateFormatUtils.format(date, DateFormatTemplate.ONLY_YEAR)}"
        }

        override fun formatTime(time: Date): String =
            DateFormatUtils.format(time, DateFormatTemplate.ONLY_TIME)

        private fun Calendar.getYear() = get(Calendar.YEAR)

        private fun getDayWithMonth(date: Date) =
            "${DateFormatUtils.format(date, DateFormatTemplate.ONLY_DAY)} ${getMonth(date)}"

        private fun getMonth(date: Date): String {
            return months[date.toCalendar().get(Calendar.MONTH)]
        }
    }

    /**
     * Форматирует дату и время в соответствии с правилами:
     *
     *     | Дата             | Заголовок   |  Первая в дне ячейка  | Другие ячейки |
     *     | ---------------- |-------------|-----------------------|
     *     | Текущий день     |             |  13:43                | 13:43
     *     | Текущий год      | 23.04       |  23.04                | 23.04
     *     | Прошлый год      | 23.04.2019  |  23.04.2019           | 23.04.2019
     */
    class DateWithMonth : ListDateFormatter() {

        /**
         * Дата "сегодня"
         */
        private val now = Date()

        override fun format(date: Date, previousDate: Date?, reverseLayout: Boolean): FormattedDateTime =
            FormattedDateTime(formatDate(date), formatTime(date))

        override fun format(date: Date): FormattedDateTime = format(date, null)

        override fun formatDate(date: Date): String = when {
            date.isToday() -> ""
            date.year == now.year -> DateFormatUtils.format(date, DateFormatTemplate.DATE_WITHOUT_YEAR)
            else -> DateFormatUtils.format(date, DateFormatTemplate.DATE_SPLIT_BY_POINTS)
        }

        override fun formatTime(time: Date): String =
            if (time.isToday()) DateFormatUtils.format(time, DateFormatTemplate.ONLY_TIME) else ""
    }

    /**
     *
     * Форматирует дату и время в соответствии с правилами:
     *
     *     | Дата             | Заголовок   |  Первая в дне ячейка  | Другие ячейки |
     *     | ---------------- |-------------|-----------------------|
     *     | Текущий день     | Сегодня     |  13:43                | 13:43
     *     | Текущий год      | 23 апр      |  23.04 13:43          | 13:43
     *     | Прошлый год      | 23.04.19    |  23.04.2019  13:43    | 13:43
     */
    class DateTimeWithTodayCellsWithTime(context: Context) : ListDateFormatter() {

        /**
         * Дата "сегодня"
         */
        private val now = Date()

        /**
         * Строка "Сегодня"
         */
        private val stringToday: String = context.getString(RDesign.string.design_date_today)

        override fun format(
            date: Date,
            previousDate: Date?,
            reverseLayout: Boolean
        ): FormattedDateTime {
            val dateFormatted = when {
                previousDate == null && reverseLayout -> formatDate(date)
                previousDate == null && !reverseLayout -> ""
                CalendarUtils.isSameDay(date, previousDate!!) -> ""
                else -> formatDate(date)
            }

            return FormattedDateTime(dateFormatted, formatTime(date))
        }

        override fun format(date: Date): FormattedDateTime {
            return FormattedDateTime(formatDate(date), formatTime(date))
        }

        override fun formatDate(date: Date): String = when {
            date.isToday() -> stringToday
            date.year == now.year -> DateFormatUtils.format(date, DateFormatTemplate.DATE_WITHOUT_YEAR)
            else -> DateFormatUtils.format(date, DateFormatTemplate.DATE_SPLIT_BY_POINTS_WITH_SHORT_YEAR)
        }

        override fun formatTime(time: Date): String =
            DateFormatUtils.format(time, DateFormatTemplate.ONLY_TIME)
    }

    /**
     *
     * Форматирует дату и время в соответствии с правилами:
     *
     *     | Дата             | Заголовок   |  Первая в дне ячейка  | Другие ячейки |
     *     | ---------------- |-------------|-----------------------|
     *     | Текущий день     | 13:43       |  13:43                | 13:43
     *     | Текущий год      | 23 апр      |  23 апр               | 23 апр
     *     | Прошлый год      | 23 апр 2019 |  23 апр 2019          | 23 апр 2019
     */
    class TimeForTodayAndDateElse(context: Context) : ListDateFormatter() {

        /**
         * Дата "сегодня"
         */
        private val now = Date()

        private val months =
            context.resources.getStringArray(RDesign.array.design_months_short_names_lower_case_without_dot)

        override fun format(
            date: Date,
            previousDate: Date?,
            reverseLayout: Boolean
        ): FormattedDateTime = format(date)

        override fun format(date: Date): FormattedDateTime {
            return FormattedDateTime(formatDate(date), if (date.isToday()) formatTime(date) else "")
        }

        override fun formatDate(date: Date): String = when {
            date.isToday() -> ""
            date.toCalendar().getYear() == now.toCalendar().getYear() -> getDayWithMonth(date)
            else -> "${getDayWithMonth(date)} ${DateFormatUtils.format(date, DateFormatTemplate.ONLY_YEAR)}"
        }

        override fun formatTime(time: Date): String =
            DateFormatUtils.format(time, DateFormatTemplate.ONLY_TIME)

        private fun Calendar.getYear() = get(Calendar.YEAR)

        private fun getDayWithMonth(date: Date) =
            "${DateFormatUtils.format(date, DateFormatTemplate.ONLY_DAY)} ${getMonth(date)}"

        private fun getMonth(date: Date): String {
            return months[date.toCalendar().get(Calendar.MONTH)]
        }

    }

    /**
     *
     * Форматирует дату и время в соответствии с правилами:
     *
     *     | Дата             | Заголовок         |  Первая в дне ячейка   | Другие ячейки |
     *     | ---------------- |-------------------|------------------------|---------------|
     *     | Текущий день     | Сегодня 13:43     |  Сегодня 13:43         | 13:43         |
     *     | Текущий год      | 23 апр  13:43     |  23 апр 13:43          | 13:43         |
     *     | Прошлый год      | 23 апр 2019 13:43 |  23 апр 2019 13:43     | 13:43         |
     */
    class DateTimeWithTodayShort(context: Context) : ListDateFormatter() {

        /**
         * Дата "сегодня"
         */
        private val now = Date()

        /**
         * Строка "Сегодня"
         */
        private val stringToday: String = context.getString(RDesign.string.design_date_today)
        private val months = context.resources.getStringArray(
            RDesign.array.design_months_short_names_lower_case_without_dot_with_declension
        )

        override fun format(
            date: Date,
            previousDate: Date?,
            reverseLayout: Boolean
        ): FormattedDateTime {
            val dateFormatted = when {
                previousDate == null -> formatDate(date)
                CalendarUtils.isSameDay(date, previousDate) -> StringUtils.EMPTY
                else -> formatDate(date)
            }

            return FormattedDateTime(dateFormatted, formatTime(date))
        }

        override fun format(date: Date): FormattedDateTime {
            return FormattedDateTime(formatDate(date), formatTime(date))
        }

        override fun formatDate(date: Date): String = when {
            date.isToday() -> stringToday
            date.toCalendar().getYear() == now.toCalendar().getYear() -> getDayWithMonth(date)
            else -> "${getDayWithMonth(date)} ${DateFormatUtils.format(date, DateFormatTemplate.ONLY_YEAR)}"
        }

        override fun formatTime(time: Date): String =
            DateFormatUtils.format(time, DateFormatTemplate.ONLY_TIME)

        private fun Calendar.getYear() = get(Calendar.YEAR)

        private fun getDayWithMonth(date: Date) =
            "${DateFormatUtils.format(date, DateFormatTemplate.ONLY_DAY)} ${getMonth(date)}"

        private fun getMonth(date: Date): String {
            return months[date.toCalendar().get(Calendar.MONTH)]
        }
    }

    /**
     * Форматирует дату и время в соответствии с правилами:
     *
     *     | Дата             | Заголовок     |  Первая в дне ячейка  | Другие ячейки |
     *     | ---------------- |---------------|-----------------------|
     *     | Текущий день     | Сегодня 13:43 |  Сегодня 13:43        | Сегодня 13:43
     *     | Текущий год      | 23 апр        |  23 апр               | 23 апр
     *     | Прошлый год      | 23 апр 2019   |  23 апр 2019          | 23 апр 2019
     */
    class TimeForTodayAndShortDateElse(context: Context) : ListDateFormatter() {

        /**
         * Дата "сегодня"
         */
        private val now = Date()

        /**
         * Строка "Сегодня"
         */
        private val stringToday: String = context.getString(RDesign.string.design_date_today)
        private val months = context.resources.getStringArray(
            RDesign.array.design_months_short_names_lower_case_without_dot_with_declension
        )

        override fun format(
            date: Date,
            previousDate: Date?,
            reverseLayout: Boolean
        ): FormattedDateTime = format(date)

        override fun format(date: Date): FormattedDateTime {
            return FormattedDateTime(formatDate(date), if (date.isToday()) formatTime(date) else "")
        }

        override fun formatDate(date: Date): String = when {
            date.isToday() -> stringToday
            date.toCalendar().getYear() == now.toCalendar().getYear() -> getDayWithMonth(date)
            else -> "${getDayWithMonth(date)} ${DateFormatUtils.format(date, DateFormatTemplate.ONLY_YEAR)}"
        }

        override fun formatTime(time: Date): String =
            DateFormatUtils.format(time, DateFormatTemplate.ONLY_TIME)

        private fun Calendar.getYear() = get(Calendar.YEAR)

        private fun getDayWithMonth(date: Date) =
            "${DateFormatUtils.format(date, DateFormatTemplate.ONLY_DAY)} ${getMonth(date)}"

        private fun getMonth(date: Date): String {
            return months[date.toCalendar().get(Calendar.MONTH)]
        }
    }

    /**
     * Форматирует дату и время в соответствии с правилами:
     *
     *  | Дата             | Заголовок    |  Первая в дне ячейка  | Другие ячейки |
     *  | ---------------- |--------------|-----------------------|
     *  | Текущий день     | 13:43        |  13:43                | 13:43
     *  | Текущий год      | 23 марта     |  23 апр 13:43         | 13:43
     *  | Прошлый год      | 23 февр 2019 |  23 апр 2019 13:43    | 13:43
     */
    class DateTimeWithoutTodayStandard(context: Context) : ListDateFormatter() {

        private val now = Date()
        private val months = context.resources.getStringArray(RDesign.array.design_months_short_standard_without_dot)

        override fun format(
            date: Date,
            previousDate: Date?,
            reverseLayout: Boolean
        ): FormattedDateTime {
            val dateFormatted = when {
                previousDate == null -> formatDate(date)
                CalendarUtils.isSameDay(date, previousDate) -> ""
                else -> formatDate(date)
            }

            return FormattedDateTime(dateFormatted, formatTime(date))
        }

        override fun format(date: Date): FormattedDateTime {
            return FormattedDateTime(formatDate(date), formatTime(date))
        }

        override fun formatDate(date: Date): String = when {
            date.isToday() -> formatTime(date)
            date.toCalendar().getYear() == now.toCalendar().getYear() -> getDayWithMonth(date)
            else -> "${getDayWithMonth(date)} ${DateFormatUtils.format(date, DateFormatTemplate.ONLY_YEAR)}"
        }

        override fun formatTime(time: Date): String =
            DateFormatUtils.format(time, DateFormatTemplate.ONLY_TIME)

        private fun Calendar.getYear() = get(Calendar.YEAR)

        private fun getDayWithMonth(date: Date) =
            "${DateFormatUtils.format(date, DateFormatTemplate.ONLY_DAY)} ${getMonth(date)}"

        private fun getMonth(date: Date): String {
            return months[date.toCalendar().get(Calendar.MONTH)]
        }
    }

    /**
     * Форматирует дату и время в соответствии с правилами:
     *
     *  | Дата             | Заголовок    |
     *  | ---------------- |--------------|
     *  | Текущий день     | Сегодня      |
     *  | Текущий год      | 23 марта    |
     *  | Прошлый год      | 23.03.2019   |
     */
    class DateOnlyWithToday(context: Context) : ListDateFormatter() {

        private val stringToday = context.getString(RDesign.string.design_date_today)
        private val months = context.resources.getStringArray(RDesign.array.design_months_short_standard_without_dot)

        override fun format(
            date: Date,
            previousDate: Date?,
            reverseLayout: Boolean
        ) = FormattedDateTime(
            date = if (previousDate != null && CalendarUtils.isSameDay(date, previousDate)) "" else formatDate(date),
            time = formatTime(date)
        )

        override fun format(date: Date) = FormattedDateTime(
            date = formatDate(date),
            time = formatTime(date)
        )

        override fun formatDate(date: Date): String {
            val calendar = Calendar.getInstance()
            val currentDay = calendar.get(Calendar.DAY_OF_YEAR)
            val currentYear = calendar.get(Calendar.YEAR)
            calendar.time = date
            val isCurrentYear = calendar.get(Calendar.YEAR) == currentYear
            return when {
                calendar.get(Calendar.DAY_OF_YEAR) == currentDay && isCurrentYear ->
                    stringToday
                isCurrentYear ->
                    "${calendar.get(Calendar.DAY_OF_MONTH)} ${months[calendar.get(Calendar.MONTH)]}"
                else ->
                    DateFormatUtils.format(date, DateFormatTemplate.DATE_SPLIT_BY_POINTS_WITH_SHORT_YEAR)
            }
        }

        override fun formatTime(time: Date): String = ""
    }

    /**
     *
     * "Смешнное форматирование". Используется в службе поддержки и выполнено в соответствии с требованиями этой задачи:
     * https://online.sbis.ru/doc/19e2d7bf-9e8b-4b0e-8de7-c596ac480375?client=3
     *
     * Форматирует дату и время в соответствии с правилами:
     *
     *  | Дата             | Заголовок    |  Первая в дне ячейка  | Другие ячейки |
     *  | ---------------- |--------------|-----------------------|
     *  | Текущий день     |              |  13:43                | 13:43
     *  | Текущий год      | 23 мар       |  23 мар               | 23 мар
     *  | Прошлый год      | 23.03.2019   |  23.03.2019           | 23.03.2019
     */
    class MixedListDateFormatter(context: Context) : ListDateFormatter() {

        private val now = Date()
        private val months = context.resources.getStringArray(RDesign.array.design_months_short_standard_without_dot)

        override fun format(
            date: Date,
            previousDate: Date?,
            reverseLayout: Boolean
        ): FormattedDateTime {
            val dateFormatted = when {
                previousDate == null -> formatDate(date)
                CalendarUtils.isSameDay(date, previousDate) -> ""
                else -> formatDate(date)
            }

            return FormattedDateTime(dateFormatted, formatTime(date))
        }

        override fun format(date: Date): FormattedDateTime {
            return FormattedDateTime(formatDate(date), formatTime(date))
        }

        override fun formatDate(date: Date): String = when {
            date.isToday() -> formatTime(date)
            date.toCalendar().getYear() == now.toCalendar().getYear() -> getDayWithMonth(date)
            else -> DateFormatUtils.format(date, DateFormatTemplate.DATE_SPLIT_BY_POINTS_WITH_SHORT_YEAR)
        }

        override fun formatTime(time: Date): String =
            DateFormatUtils.format(time, DateFormatTemplate.ONLY_TIME)

        private fun Calendar.getYear() = get(Calendar.YEAR)

        private fun getDayWithMonth(date: Date) =
            "${DateFormatUtils.format(date, DateFormatTemplate.ONLY_DAY)} ${getMonth(date)}"

        private fun getMonth(date: Date): String {
            return months[date.toCalendar().get(Calendar.MONTH)]
        }
    }
}

/**
 * Проверяет, является ли дата сегодняшней
 *
 * @author ra.petrov
 */
fun Date.isToday(): Boolean {
    return CalendarUtils.isSameDay(this, Date())
}
