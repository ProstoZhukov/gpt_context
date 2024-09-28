package ru.tensor.sbis.common.util.dateperiod

import ru.tensor.sbis.common.R
import ru.tensor.sbis.common.util.ResourceProvider
import ru.tensor.sbis.common.util.date.DateFormatTemplate
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 * Формат даты с указанием времени, но без часового пояса
 */
const val TEMPLATE_DATE_WITH_TIME_NO_TIMEZONE = "yyyy-MM-dd kk:mm:ss"

private val shortMonthNames = listOf("Янв", "Февр", "Март", "Апр", "Май", "Июнь", "Июль", "Авг", "Сент", "Окт", "Нояб", "Дек")
private val monthNamesWithDot = listOf("янв.", "февр.", "марта", "апр.", "мая", "июня", "июля", "авг.", "сент.", "окт.", "нояб.", "дек.")
private val monthNamesWithoutDot = listOf("янв", "февр", "марта", "апр", "мая", "июня", "июля", "авг", "сент", "окт", "нояб", "дек")

/**
 * Класс для хранения пары маска-текст. Используется для подмены части формата даты/времени кастомной строкой.
 * @property mask Маска.
 * @property name Текст.
 *
 * @author vv.malyhin
 */
class FormatPart(val mask: Char, val name: String)

/**
 * Форматирует дату в виде префикса + года.
 * Пример: Август'17
 *
 * @param date дата, год которой используется
 * @param prefix строка, предшествующая году
 * @return строка в виде префикса + года
 */
fun getFormattedYearWithPrefix(date: Date, prefix: String): String {
    val formattedDate = SimpleDateFormat("''yy", Locale.getDefault()).format(date)
    return "$prefix$formattedDate"
}

/**
 * Создаёт строку, представляющую период года (квартал, или полугодие), с обозначением номера
 * римской цифрой.
 * Пример: III квартал
 *
 * @param periodName название периода
 */
internal fun getPeriodNameWithNumber(periodName: String, number: Int): String {
    return when (number) {
        1    -> "I $periodName"
        2    -> "II $periodName"
        3    -> "III $periodName"
        4    -> "IV $periodName"
        else -> periodName
    }
}

private fun getPeriodPrefixForHalfYear(
    datePeriod: DatePeriod,
    shortFormat: Boolean,
    resourceProvider: ResourceProvider?
): String {
    val halfyearText = if (resourceProvider == null) {
        if (shortFormat) "пг" else "полугодие"
    } else if (shortFormat) resourceProvider.getString(R.string.common_period_half_year_short)
    else resourceProvider.getString(R.string.common_period_half_year)
    return getPeriodNameWithNumber(
        halfyearText,
        if (datePeriod.isFirstHalfYear()) 1 else 2
    )
}

private fun getPeriodPrefixForQuarter(
    datePeriod: DatePeriod,
    shortFormat: Boolean,
    resourceProvider: ResourceProvider?
): String {
    val quarterText = if (resourceProvider == null) {
        if (shortFormat) "кв" else "квартал"
    } else if (shortFormat) resourceProvider.getString(R.string.common_period_quarters_short)
    else resourceProvider.getString(R.string.common_period_quarters)
    return getPeriodNameWithNumber(
        quarterText,
        when {
            datePeriod.isFirstQuarter()  -> 1
            datePeriod.isSecondQuarter() -> 2
            datePeriod.isThirdQuarter()  -> 3
            else                         -> 4
        }
    )
}

/**
 * Форматирует год даты
 *
 * @param date исходная дата
 * @return строка с годом [date]
 */
fun formatYear(date: Date): String {
    return SimpleDateFormat("yyyy", Locale.getDefault()).format(date)
}

/**
 * Форматирует месяц даты
 *
 * @param date исходная дата
 * @param resourceProvider провайдер ресурсов, если null ответ не будет локализован
 * @return строка с полным названием месяца [date]
 */
fun formatMonth(date: Date, resourceProvider: ResourceProvider? = null): String {
    val longMonths = resourceProvider?.getStringArray(R.array.common_months)?.toList() ?: shortMonthNames
    return longMonths[date.toCalendar().get(Calendar.MONTH)]
}

/**
 * Форматирует месяц даты в сокращенном варианте
 *
 * @param date исходная дата
 * @param resourceProvider провайдер ресурсов, если null ответ не будет локализован
 * @return строка с кратким названием месяца [date]
 */
fun formatMonthShort(date: Date, resourceProvider: ResourceProvider? = null): String {
    val shortMonths = resourceProvider?.getStringArray(R.array.common_months_short)?.toList() ?: shortMonthNames
    return shortMonths[date.toCalendar().get(Calendar.MONTH)]
}

/**
 * Форматирует месяц даты в сокращенном варианте c точкой
 *
 * @param date исходная дата
 * @param resourceProvider провайдер ресурсов, если null ответ не будет локализован
 * @return строка с кратким названием месяца [date]
 */
fun formatMonthShortWithDot(date: Date, resourceProvider: ResourceProvider? = null): String {
    val shortMonths = resourceProvider?.getStringArray(R.array.common_months_short_with_dot)?.toList() ?: monthNamesWithDot
    return shortMonths[date.toCalendar().get(Calendar.MONTH)]
}

/**
 * Форматирует месяц даты в сокращенном варианте (без точки и в родительном падеже).
 *
 * @param date Исходная дата.
 * @param resourceProvider Провайдер ресурсов. Если null, то ответ не будет локализован.
 * @return Строка с кратким названием месяца [date].
 */
fun formatMonthShortGenitive(date: Date, resourceProvider: ResourceProvider? = null): String {
    val shortGenitiveMonths =
        resourceProvider?.getStringArray(R.array.common_months_short_without_dot)?.toList() ?: monthNamesWithoutDot
    return shortGenitiveMonths[date.toCalendar().get(Calendar.MONTH)]
}

/**
 * Подменить часть формата. Ищет в формате участок по переданной маске и заменяет его переданным текстом.
 * @param format    Формат для редактирования.
 * @param part      Пара маска-текст.
 * @return          Строка с изменённым форматом, либо строка с исходным форматом при ошибке.
 */
fun getChangedFormat(format: DateFormatTemplate, part: FormatPart): String {
    if (!format.template.contains(part.mask) || part.mask.isWhitespace() || part.name.isBlank()) return format.template
    val separator = ' '
    val splitted = format.template
        .split(separator)
        .toMutableList()
    splitted.forEachIndexed { i, value ->
        if (value.matches(Regex("[${part.mask}]+"))) splitted[i] = part.name
    }
    return splitted.joinToString(separator = separator.toString())
}

/**
 * Форматирует квартал года
 *
 * @param period период, представляющий квартал года
 * @param shortFormat сокращать ли название периода
 * @param resourceProvider провайдер ресурсов, если null ответ не будет локализован
 * @return строка с текстовым представлением указанного квартала
 */
fun formatQuarter(
    period: DatePeriod,
    shortFormat: Boolean = false,
    resourceProvider: ResourceProvider? = null
): String {
    return getFormattedYearWithPrefix(period.from, getPeriodPrefixForQuarter(period, shortFormat, resourceProvider))
}

/**
 * Форматирует полугодие
 *
 * @param period период, представляющий полугодие
 * @param shortFormat сокращать ли название периода
 * @param resourceProvider провайдер ресурсов, если null ответ не будет локализован
 * @return строка с текстовым представлением указанного полугодия
 */
fun formatHalfYear(
    period: DatePeriod,
    shortFormat: Boolean = false,
    resourceProvider: ResourceProvider? = null
): String {
    return getFormattedYearWithPrefix(period.from, getPeriodPrefixForHalfYear(period, shortFormat, resourceProvider))
}

/**
 * Форматирует дату в виде ДД.ММ.ГГ.
 * Пример: 11.22.63
 *
 * @param date исходная дата
 * @return строка с датой
 */
fun formatDateShort(date: Date): String {
    return SimpleDateFormat("dd.MM.yy", Locale.getDefault()).format(date)
}

/**
 * Форматирует день и месяц даты в виде ДД.ММ.
 * Пример: 03.04
 *
 * @param date исходная дата
 * @return строка с днём и месяцем даты
 */
fun formatDateShortWithoutYear(date: Date): String {
    return SimpleDateFormat("dd.MM", Locale.getDefault()).format(date)
}

/**
 * Форматирует дату в полном варианте.
 * Пример: 03 февр. 2019 или 03 Февраля'2019
 *
 * @param date исходная дата
 * @param resourceProvider провайдер ресурсов, если null ответ не будет локализован
 * @param shortMonth полный или сокращенный месяц
 * @return строка с полной датой
 */
internal fun longFormat(
    date: Date,
    resourceProvider: ResourceProvider? = null,
    shortMonth: Boolean = true
) = when(shortMonth) {
    true -> formatMonthShortWithDot(date, resourceProvider).let {
        SimpleDateFormat("dd '$it' yyyy", Locale.getDefault()).format(date)
    }
    false -> SimpleDateFormat(
        DateFormatTemplate.DATE_WITH_FULL_MONTH_SHORT_DAY_WITH_YEAR.template,
        Locale.getDefault()
    ).format(date)
}