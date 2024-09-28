package ru.tensor.sbis.business.common.ui.utils

import android.content.Context
import android.os.Build
import ru.tensor.sbis.business.common.ui.utils.RoundUtils.getFactorUnits
import ru.tensor.sbis.common.util.ResourceProvider
import ru.tensor.sbis.common.util.dateperiod.DatePeriod
import ru.tensor.sbis.common.util.dateperiod.getUtcTimeZone
import ru.tensor.sbis.common.util.dateperiod.toCalendar
import ru.tensor.sbis.common.util.formatMoney
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.regex.Pattern
import kotlin.math.abs
import ru.tensor.sbis.common.R as RCommon

private const val UTC_TEMPLATE = "yyyy-MM-dd"
private const val UNIT_MAX_LENGTH = 5
private const val FRACTIONAL_LIMIT = 0.5
private const val BIG_DECIMAL_FRACTIONAL_SIZE = 3
private const val TIME_PATTERN = "(0[0-9]|1[0-9]|2[0-3]):[0-5][0-9]"

private val toolbarDateMonthNames =
    listOf("янв.", "февр.", "марта", "апр.", "мая", "июня", "июля", "авг.", "сент.", "окт.", "нояб.", "дек.")

// region Date format
/**
 * Форматирует название месяца даты в формате "dd.MM"
 *
 * @param date исходная дата в формате "dd.MM"
 * @param resourceProvider провайдер ресурсов, если null ответ не будет локализован
 * @return строка с названием месяца
 */
fun formatDayOfMonthForCheck(date: String, resourceProvider: ResourceProvider? = null): String {
    val matcher = Pattern.compile("\\d{2}.\\d{2}").matcher(date)
    val months = resourceProvider?.getStringArray(RCommon.array.common_months_short_with_dot)?.toList() ?: toolbarDateMonthNames
    return if (matcher.matches()) {
        val month = date.substringAfter(".").let {
            val index = it.toInt() - 1
            months.getOrElse(index) { "" }
        }
        val day = date.substringBefore(".")
        return "$day $month"
    } else date
}

/**
 * Форматирует день и название месяца даты.
 * Пример: 16 авг.
 *
 * @param date исходная дата
 * @param context контекст
 * @return строка с полной датой
 */
fun formatDayOfYear(date: Date, context: Context): String {
    val months = context.resources.getStringArray(RCommon.array.common_months_short_with_dot).toList()
    val month = months[date.toCalendar().get(Calendar.MONTH)]
    return SimpleDateFormat("dd '$month'", Locale.getDefault()).format(date)
}

/**
 * Форматирует день недели даты в сокращённом виде
 *
 * @param date исходная дата
 * @return строка с кратким названием дня недели
 */
fun formatDayOfWeek(date: Date): String {
    val dateFormat = SimpleDateFormat("EEE", Locale.getDefault())
    return dateFormat.format(date)
}

/**
 * Форматирует краткое представление времени и даты работы последней смены
 *
 * @param opened дата и время открытия
 * @param closed дата и время закрытия
 * @param context контекст
 * @return строка с представлением времени работы смены
 */
fun formatShiftWorkTimeAndDay(opened: Date?, closed: Date?, context: Context): String {
    if (opened == null) {
        return ""
    }
    val todayOpenPattern = context.getString(RCommon.string.common_period_from)
    val todayClosedPattern = "%s-%s"
    val yesterdayOpenPattern = context.getString(RCommon.string.common_period_yesterday_from)
    val yesterdayClosedPattern = context.getString(RCommon.string.common_period_yesterday)
    return if (closed == null) {
        DatePeriod(opened, Date()).run {
            when {
                isSpecificDay()   -> String.format(todayOpenPattern, formatTime(opened))
                isFromYesterday() -> String.format(yesterdayOpenPattern, formatTime(opened))
                else              -> formatDayOfYear(opened, context)
            }
        }
    } else {
        DatePeriod(closed, Date()).run {
            when {
                isSpecificDay()   -> String.format(
                    todayClosedPattern,
                    formatTime(opened),
                    formatTime(closed)
                )
                isFromYesterday() -> yesterdayClosedPattern
                else              -> formatDayOfYear(closed, context)
            }
        }
    }
}

/**
 * Форматирует день смены
 *
 * @param date дата смены
 * @param context контекст
 * @return строка дня смены ("сегодня", "вчера", либо день и месяц)
 */
fun formatShiftDay(date: Date?, context: Context): String {
    return DatePeriod(date ?: return "", Date()).run {
        when {
            isSpecificDay()   -> context.getString(RCommon.string.common_period_today)
            isFromYesterday() -> context.getString(RCommon.string.common_period_yesterday)
            else              -> formatDayOfYear(date, context)
        }
    }
}

/**
 * Форматирует время работы смены
 *
 * @param opened дата и время открытия
 * @param closed дата и время закрытия (если смена закрыта)
 * @return строка с интервалом времени работы смены, или только временем открытия, если она ещё открыта
 */
fun formatShiftWorkTime(opened: Date?, closed: Date?): String {
    if (opened == null) {
        return ""
    }

    return if (closed == null) {
        formatTime(opened)
    } else {
        String.format("%s-%s", formatTime(opened), formatTime(closed))
    }
}

/**
 * Форматирует [Date] в строку, интерпретируя как дату в UTC
 *
 * Пример: 2018-06-18
 *
 * @param date дата и время
 * @return строка utc даты
 */
fun formatUTC(date: Date): String {
    return SimpleDateFormat(UTC_TEMPLATE, Locale.getDefault()).apply {
        timeZone = getUtcTimeZone()
    }.format(date)
}

/**
 * Разбирает строку с датой и создаёт [Date], соответствующий ей в UTC
 *
 * @param utc строка даты без времени
 * @return дата
 */
fun parseUTCFormat(utc: String): Date? {
    SimpleDateFormat(UTC_TEMPLATE, Locale.getDefault()).run {
        timeZone = getUtcTimeZone()
        return try {
            parse(utc)
        } catch (e1: ParseException) {
            try {
                parse(if (Build.VERSION.SDK_INT >= 24) utc + "00" else utc)
            } catch (e2: ParseException) {
                null
            }
        }
    }
}
//endregion

/**
 * Форматирует сумму, округляя её до степеней 1000, чтобы число цифр не превышало пяти
 *
 * @param money сумма денег
 * @return строка с суммой, округлённой до степеней 1000 с не более чем пятью цифрами
 */
fun formatThousandMoney(money: Double, resourceProvider: ResourceProvider): String {
    val factor = RoundUtils.getFactor(money, 5)
    val units = resourceProvider.getFactorUnits(factor)
    return if (units.isEmpty()) {
        formatMoney(money, false)
    } else {
        val roundedMoney = RoundUtils.roundedDouble(money, factor)
        "${formatMoney(roundedMoney, false)} $units."
    }
}

/**
 * Форматирует число по триадам
 * В случае если [roundedMoney] по модулю меньшее чем [FRACTIONAL_LIMIT],
 * то пишем дробную часть длиной [BIG_DECIMAL_FRACTIONAL_SIZE]
 */
fun formatCashflowMoney(roundedMoney: Double): String {

    val fractionalCondition = abs(roundedMoney) <= FRACTIONAL_LIMIT
    var result = formatMoney(roundedMoney, fractionalCondition, BIG_DECIMAL_FRACTIONAL_SIZE)

    if (fractionalCondition) {
        // Удаление нулей в конце дробной части, например 0.200 -> 0.2
        while (result.endsWith('0')) {
            result = result.removeSuffix("0")
        }
        // Удаление '.', которая осталась после удаления всех нулей в дробной части 0.000 -> 0. -> 0
        if (result.endsWith('.'))
            result = result.removeSuffix(".")
    }
    return result
}

/**
 * Форматирует дробное количество или число единиц
 *
 * @param quantity дробное количество или число единиц
 * @return строка с заданным числом с не более чем двумя знаками после запятой и пробелом между группами цифр
 */
fun formatQuantity(quantity: Double): String = DecimalFormat().apply {
    val pointSymbols = DecimalFormatSymbols()
    pointSymbols.groupingSeparator = ' '
    pointSymbols.decimalSeparator = '.'

    isGroupingUsed = true
    groupingSize = 3
    decimalFormatSymbols = pointSymbols
    maximumFractionDigits = 2
}.format(quantity)


/**
 * Форматирует число с не более чем двумя знаками после запятой
 *
 * @param amount число
 * @return строка с числом с не более чем двумя знаками после запятой
 */
fun addDecimalSeparator(amount: Int): String {
    return DecimalFormat().apply {
        decimalFormatSymbols = DecimalFormatSymbols().apply {
            decimalSeparator = '.'
        }
        minimumFractionDigits = 0
        maximumFractionDigits = 2
    }.format(amount)
}

/**
 * Вычисляет и форматирует значение среднего чека
 *
 * @param revenue выручка
 * @param checksNumber количество чеков
 * @return строка со значением среднего чека
 */
fun formatAverageCheck(revenue: Double, checksNumber: Int): String {
    return if (checksNumber > 0) formatMoney(revenue / checksNumber, false) else "-"
}

/**
 * Форматирует сумму, возвращая "-" для нулевой суммы
 *
 * @param money сумма
 * @return строковое представление ненулевой суммы, либо "-"
 */
fun formatMoneyWithDashInsteadOfZero(money: Double): String {
    return if (money != 0.0) formatMoney(money) else "-"
}

/**
 * Форматирует целочисленное количество, возвращая "-" для нулевого значения
 *
 * @param count количество
 * @return строковое представление ненулевого количества, либо "-"
 */
fun formatQuantityWithDashInsteadOfZero(count: Int): String {
    return if (count > 0.0) formatQuantity(count.toDouble()) else "-"
}

/**
 * Обрезает и добавляет многоточие к строке [unit], если длина больше [UNIT_MAX_LENGTH]
 * @param unit единица измерения
 * @return форматированная строка
 */
fun formatUnitWithDots(unit: String): String {
    return if (unit.length > UNIT_MAX_LENGTH) "${unit.slice(0..2)}..." else unit
}

/**
 * Форматирует время на текущий час.
 * Пример: 05:55
 *
 * @param date дата и время
 * @return строка временем
 */
fun formatTime(date: Date): String {
    return SimpleDateFormat("HH:mm", Locale.getDefault()).format(date)
}

/**
 * Ищет в строке ЧЧ:ММ
 * Пример: "2020-04-02 12:59:00" => "12:59"
 *
 * @return результат поиска, иначе пустую строку
 */
fun String.findTime(): String =
    Regex(TIME_PATTERN).find(this).firstOrEmpty()

private fun MatchResult?.firstOrEmpty(): String = this?.groups?.firstOrNull()?.value.orEmpty()