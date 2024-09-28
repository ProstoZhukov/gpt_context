package ru.tensor.sbis.business.common.utils

import ru.tensor.sbis.common.util.date.DateFormatTemplate
import ru.tensor.sbis.common.util.date.DateFormatUtils
import ru.tensor.sbis.common.util.date.DateParseTemplate
import ru.tensor.sbis.common.util.date.DateParseUtils
import ru.tensor.sbis.common.util.dateperiod.DatePeriod
import ru.tensor.sbis.common.util.dateperiod.TEMPLATE_DATE_WITH_TIME_NO_TIMEZONE
import ru.tensor.sbis.common.util.dateperiod.toCalendar
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


/** Если Date равен null, возвращает пустую строку, если нет, форматирует по шаблону*/
@Suppress("NOTHING_TO_INLINE")
inline fun Date?.formatIfNotNull(template: DateFormatTemplate = DateFormatTemplate.DATE_SPLIT_BY_POINTS_WITH_SHORT_YEAR): String =
    if (this != null) {
        DateFormatUtils.format(this, template)
    } else {
        ""
    }

/**
 * Регулирует часовые пояса, когда изменение пояса привело к изменению даты
 */
fun DatePeriod.adjustForDefaultTimeZone(): DatePeriod {
    if (TimeZone.getDefault() != timeZone) {
        return if (isActualPeriod()) {
            toActualPeriodOfSameTypeForCurrentTimeZone()
        } else {
            toSamePeriodInDefaultTimeZone()
        }
    }
    return this
}

/**
 * Десериализация дат
 */
fun String.formatDate(template: DateParseTemplate = DateParseTemplate.ONLY_DATE): Date? =
    toDate(TEMPLATE_DATE_WITH_TIME_NO_TIMEZONE) ?: toDate(template.pattern)

/**
 * Формирует строку из года, дня, месяца с добавлением ведущих нулей, исключая время и таймзону
 *
 * @return строка в формате `yyyy-MM-dd` для разбора контроллером
 */
fun Date.formatFreeZoneDate(): String =
    with(toCalendar()) {
        val month = (get(Calendar.MONTH) + 1).toString()
        val leadingMonth = LEADING_ZERO.takeIf { month.length == 1 }?.plus(month) ?: month

        val day = get(Calendar.DAY_OF_MONTH).toString()
        val leadingDay = LEADING_ZERO.takeIf { day.length == 1 }?.plus(day) ?: day

        val year = get(Calendar.YEAR)

        String.format(YEAR_MONTH_DAY_FORMAT, year, leadingMonth, leadingDay)
    }

/**
 * Аналогично основному методу [DateParseUtils.parseDate]
 * Создает новый парсер вместо [SimpleDateFormat.applyPattern]
 */
private fun String.toDate(pattern: String): Date? {
    val parser = SimpleDateFormat(pattern, Locale.getDefault())
    return try {
        return parser.parse(this)
    } catch (e1: ParseException) {
        try {
            return parser.parse(DateParseUtils.adaptTimezone(this))
        } catch (ignored: ParseException) {
            null
        }
    }
}

private const val LEADING_ZERO = "0"
private const val YEAR_MONTH_DAY_FORMAT = "%s-%s-%s"