package ru.tensor.sbis.common.util

import android.text.SpannableString
import android.text.Spanned
import android.text.style.AbsoluteSizeSpan
import ru.tensor.sbis.common.util.NumberFormatUtils.priceFormat
import ru.tensor.sbis.common.util.NumberFormatUtils.priceFormatFull
import ru.tensor.sbis.common.util.NumberFormatUtils.priceFormatWithZeros
import ru.tensor.sbis.common.util.NumberFormatUtils.priceFormatWithZerosAndWholePart
import ru.tensor.sbis.common.util.date.DateFormatUtils
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Calendar
import kotlin.math.sign

/**
 * Утилита преобразования чисел в строки
 */
object NumberFormatUtils {
    private val decimalFormatSymbols = DecimalFormatSymbols().apply {
        decimalSeparator = '.'
        groupingSeparator = ' '
    }

    val priceFormat = DecimalFormat("###,###.###", decimalFormatSymbols)
    val priceFormatFull = DecimalFormat("###,###.######", decimalFormatSymbols)
    val priceFormatWithZeros = DecimalFormat("###,###.00", decimalFormatSymbols)
    val priceFormatWithZerosAndWholePart = DecimalFormat("###,##0.00", decimalFormatSymbols)
}

/**
 * Преобразование double к строке по стандартам тензор
 */
fun Double.formatWithDot(): String = formatFloor() + formatFractionWithDot()

/**
 * Преобразование double к строке по стандартам тензор
 */
fun BigDecimal?.formatWithDot(): String = this?.toDouble()?.formatWithDot() ?: ""

/**
 * Преобразование double к строке целой части по стандартам тензор
 */
fun Double?.formatFloor(): String = this?.toBigDecimal().formatFloor()

/**
 * Преобразование long к строке целой части по стандартам тензор
 */
fun Long?.formatFloor(): String = this?.let { priceFormat.format(it) } ?: ""

/**
 * Преобразование BigDecimal к строке целой части по стандартам тензор
 */
fun Double?.formatHalfUp(): String = this?.toBigDecimal().format(RoundingMode.HALF_UP)

/**
 * Преобразование BigDecimal к строке целой части по стандартам тензор
 */
fun BigDecimal?.formatFloor(): String = this.format(RoundingMode.DOWN)

/**
 * Преобразование BigDecimal к строке целой части по стандартам тензор
 */
fun BigDecimal?.formatHalfUp(): String = this.format(RoundingMode.HALF_UP)

/**
 * Преобразование BigDecimal к строке целой части по стандартам тензор с округлением [mode]
 */
fun BigDecimal?.format(mode: RoundingMode): String = this?.setScale(0, mode)
    ?.let { priceFormat.format(it) } ?: ""

/**
 * Преобразование Int к строке целой части по стандартам тензор
 */
fun Int?.formatFloor(): String = this?.let { priceFormat.format(it) } ?: ""

/**
 * Преобразование BigDecimal к строке после запятой по стандартам тензор
 */
fun BigDecimal?.formatFractionWithDot(): String = this?.toDouble()?.formatFractionWithDot() ?: ""

/**
 * Преобразование double к строке после запятой по стандартам тензор
 */
fun Double?.formatFractionWithDot(): String =
    this?.let {
        String.format("%.2f", it).takeLast(3).replaceFirst(",", ".")
    } ?: ""

/**
 * Преобразование double к строке после запятой по стандартам тензор + валюта
 */
fun BigDecimal?.formatFractionWithDotAndSign(currencySign: String): String {
    return this?.let {
        val fractionString = if (hasFraction()) {
            String.format("%.2f", it).takeLast(3).replaceFirst(",", ".")
        } else ""
        "$fractionString $currencySign"
    } ?: ""
}

/**
 * Есть ненулевая дробная часть
 */
fun BigDecimal.hasFraction() = stripTrailingZeros().scale() > 0

/**
 * Получить spannable с Ценой и копейками соответствующего шрифта
 */
fun getSpannablePrice(
    price: String,
    currencySign: String,
    priceTextSize: Int,
    currencySignTextSize: Int
): SpannableString {

    val priceSpannable = SpannableString("$price $currencySign")

    if (currencySign.isNotEmpty()) {
        priceSpannable.setSpan(
            AbsoluteSizeSpan(priceTextSize, true),
            0,
            priceSpannable.length - 1,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        priceSpannable.setSpan(
            AbsoluteSizeSpan(currencySignTextSize, true),
            priceSpannable.length - 1,
            priceSpannable.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }

    return priceSpannable
}

/**
 * Преобразование double к строке, где дробная часть отображается, если не равна 0
 */
fun Double?.formatWithDotIfHaveFraction(): String = this?.let { priceFormat.format(it) } ?: ""

/**
 * Преобразование double к строке, где дробная часть отображается до 6 знаков после запятой, если не равна 0
 */
fun Double?.formatWithDotIfHaveFractionFull(): String = this?.let { priceFormatFull.format(it) } ?: ""

/**
 * Преобразование BigDecimal к строке, где дробная часть отображается, если не равна 0 c двумя знаками после запятой
 */
fun BigDecimal?.formatWithDotPriceIfHaveFraction(): String = this?.toDouble()?.let { double ->
    double.formatWithDotIfHaveFraction().takeIf { it.contains(".").not() } ?: priceFormatWithZeros.format(double)
} ?: ""

/**
 * Преобразование BigDecimal к строке, где дробная часть отображается, если не равна 0,
 * c двумя знаками после запятой и целой частью
 */
fun BigDecimal?.formatWithDotAndWholePartPriceIfHaveFraction(): String = this?.toDouble()?.let { double ->
    double.formatWithDotIfHaveFraction().takeIf { it.contains(".").not() } ?: priceFormatWithZerosAndWholePart.format(
        double
    )
} ?: ""

/**
 * Преобразование знака числа
 */
fun Double.getSignIfPlus(): String =
    when (this.toInt().sign) {
        1 -> "+"
        else -> ""
    }

/**
 * Преобразование значения времени услуги в формат hh:mm
 */
fun Int.formatDuration(): String {
    val hour = this / ONE_HOUR_MIN
    val minutes = this % ONE_HOUR_MIN
    val calendar = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, hour)
        set(Calendar.MINUTE, minutes)
    }
    return DateFormatUtils.formatDateOnlyTime(calendar.time)
}

private const val ONE_HOUR_MIN = 60