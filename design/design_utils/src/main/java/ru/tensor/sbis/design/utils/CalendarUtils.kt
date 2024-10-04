@file:Suppress("MemberVisibilityCanBePrivate")

package ru.tensor.sbis.design.utils

import android.content.Context
import android.graphics.Point
import android.view.WindowManager
import java.util.*
import ru.tensor.sbis.design.R

/**
 * Utility functions for calendar widget.
 */
object CalendarUtils {

    /**
     * @param context Context
     * @return Width of current screen, in pixels.
     */
    @Suppress("unused")
    fun getScreenWidth(context: Context): Int {
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        @Suppress("SENSELESS_COMPARISON")
        if (wm != null) {
            val display = wm.defaultDisplay
            val size = Point()
            display.getSize(size)
            return size.x
        }
        return 0
    }

    /**
     * Makes first char in string uppercase.
     *
     * @param word String
     * @return Word with first char uppercase.
     */
    fun firstUpperCase(word: String?): String {
        return if (word == null || word.isEmpty()) {
            ""//или return word;
        } else word.substring(0, 1).toUpperCase(Locale.getDefault()) + word.substring(1)
    }

    /**
     * @param context Context
     * @param month   GregorianCalendar that contains month number
     * @param isShort true if use short version
     * @return Localized month name.
     */
    @Suppress("unused")
    fun getMonthString(context: Context, month: GregorianCalendar, isShort: Boolean): String {
        val title: String = if (isShort) {
            val monthStrings = context.resources.getStringArray(R.array.design_months_short_names_calendar)
            val monthIndex = month.get(Calendar.MONTH)
            monthStrings[monthIndex]
        } else {
            firstUpperCase(android.text.format.DateFormat.format("LLLL", month).toString())
        }

        return title
    }

    /**
     * @param context       Context
     * @param calendarMonth GregorianCalendar that contains month and year
     * @param isShort       true if use short version
     * @return Localized month name plus two digits of year.
     */
    @Suppress("unused")
    fun getMonthAndYearString(context: Context, calendarMonth: GregorianCalendar, isShort: Boolean): String {
        var s: String = if (isShort) {
            val monthStrings = context.resources.getStringArray(R.array.design_months_short_names_calendar)
            val monthIndex = calendarMonth.get(Calendar.MONTH)
            monthStrings[monthIndex]
        } else {
            firstUpperCase(android.text.format.DateFormat.format("LLLL", calendarMonth).toString())
        }
        s += "'"
        s += android.text.format.DateFormat.format("yy", calendarMonth).toString()
        return s
    }

    /**
     * @param context    Context
     * @param dayOfWeek  number of day in week (1 - monday, 7 - sunday)
     * @return Short day of week name or empty string if dayOfWeek isn't in 1..7.
     */
    @Suppress("unused")
    fun getDayOfWeekShortName(context: Context, dayOfWeek: Int): String {
        return when (dayOfWeek) {
            1    -> context.getString(R.string.design_monday_short)
            2    -> context.getString(R.string.design_tuesday_short)
            3    -> context.getString(R.string.design_wednesday_short)
            4    -> context.getString(R.string.design_thursday_short)
            5    -> context.getString(R.string.design_friday_short)
            6    -> context.getString(R.string.design_saturday_short)
            7    -> context.getString(R.string.design_sunday_short)
            else -> ""
        }
    }

    /**
     * @param date Date
     * @return Start of given date.
     */
    fun getDateStart(date: Date): Date {
        val calendar = GregorianCalendar.getInstance()
        calendar.time = date
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.time
    }

    /**
     * @param date Calendar
     * @return Start of given date.
     */
    fun getDateStart(date: Calendar): Calendar {
        val calendar = GregorianCalendar.getInstance()
        calendar.time = date.time
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar
    }

    /**
     * @param date Date
     * @return Start of given date.
     */
    fun getDateEnd(date: Date): Date = getDateEnd(GregorianCalendar.getInstance().apply { time = date }).time

    /**
     * @param date Calendar
     * @return End of given date.
     */
    fun getDateEnd(date: Calendar): Calendar {
        val calendar = GregorianCalendar.getInstance()
        calendar.time = date.time
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        calendar.set(Calendar.MILLISECOND, 999)
        return calendar
    }

    /**
     * @param date1 Date
     * @param date2 Date
     * @return true if given dates point to the same day, false otherwise.
     */
    fun isSameDay(date1: Date, date2: Date): Boolean {
        val calendar1 = GregorianCalendar.getInstance()
        calendar1.time = date1
        val calendar2 = GregorianCalendar.getInstance()
        calendar2.time = date2

        return isSameDay(calendar1, calendar2)
    }

    /**
     * @param calendar1 Calendar
     * @param calendar2 Calendar
     * @return true if given calendars point to the same day, false otherwise.
     */
    fun isSameDay(calendar1: Calendar, calendar2: Calendar): Boolean {
        return calendar1.get(Calendar.DATE) == calendar2.get(Calendar.DATE) &&
                calendar1.get(Calendar.MONTH) == calendar2.get(Calendar.MONTH) &&
                calendar1.get(Calendar.YEAR) == calendar2.get(Calendar.YEAR)
    }
}
