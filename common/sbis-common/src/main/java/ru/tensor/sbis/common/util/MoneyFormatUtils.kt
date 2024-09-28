package ru.tensor.sbis.common.util

import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import kotlin.math.abs
import kotlin.math.roundToLong

/**
 * Форматирует сумму денег
 *
 * @param money сумма денег
 * @param withFractionalPart должна ли присутствовать в строке дробная часть
 * @return строка с суммой денег c ровно 0 или [DEFAULT_FRACTIONAL_SIZE] знаками после запятой, в зависимости от
 * [withFractionalPart]
 */
@JvmOverloads
fun formatMoney(
    money: Number,
    withFractionalPart: Boolean = true,
    fractionalSize: Int = DEFAULT_FRACTIONAL_SIZE
): String = when (money) {
    is BigDecimal -> formatBigMoney(money, withFractionalPart, fractionalSize)
    is Double -> formatMoney(money, withFractionalPart, fractionalSize)
    else -> formatMoney(money.toDouble(), withFractionalPart, fractionalSize)
}

/**
 * Форматирует сумму денег
 *
 * @param money сумма денег
 * @param withFractionalPart должна ли присутствовать в строке дробная часть
 * @param delimiter разделитель дробной части
 * @param roundingMode способ округления
 * @return строка с суммой денег c ровно 0 или [DEFAULT_FRACTIONAL_SIZE] знаками после запятой, в зависимости от
 * [withFractionalPart]
 */
@JvmOverloads
fun formatMoney(
    money: Double,
    withFractionalPart: Boolean = true,
    fractionalSize: Int = DEFAULT_FRACTIONAL_SIZE,
    delimiter: Char = DEFAULT_DELIMITER,
    roundingMode: RoundingMode = RoundingMode.HALF_UP,
): String = FormatSettings.getDefault(withFractionalPart, fractionalSize, delimiter, roundingMode)
    .decimalFormat.format(money)

/**
 * Аналогично [formatMoney], за исключением возможности работы с типом [BigDecimal]
 *
 * @see formatMoney
 */
fun formatBigMoney(
    money: BigDecimal,
    withFractionalPart: Boolean = true,
    fractionalSize: Int = DEFAULT_FRACTIONAL_SIZE
): String =
    FormatSettings.getDefault(withFractionalPart, fractionalSize).decimalFormat.format(money)

/**
 * Форматирует сумму денег
 *
 * @param money сумма денег
 * @param fractionalPredicate условие определяющее должна ли присутствовать в строке дробная часть
 * @return [formatMoney]
 */
fun formatMoney(money: Double, fractionalPredicate: (Double?) -> Boolean): String {
    return formatMoney(money, fractionalPredicate.invoke(money))
}

/**
 * Форматирует сумму денег
 * Отбрасывает копейки
 */
fun Double.removeKopecks(): Double {
    // round(-0.5) = 0, а round(0.5) = 1, т.к. округление происходит
    // к бОльшему целому числу. Поэтому округляем по модулю, а затем восстанавливаем знак.
    val result = abs(this).roundToLong().toDouble()
    val sign = if (this >= 0) 1 else -1
    return result * sign
}

data class FormatSettings(
    val decimalFormat: DecimalFormat = DecimalFormat(),
    val pointSymbols: DecimalFormatSymbols = DecimalFormatSymbols()
) {
    companion object {
        fun getDefault(
            withFractionalPart: Boolean,
            fractionalSize: Int = DEFAULT_FRACTIONAL_SIZE,
            delimiter: Char = DEFAULT_DELIMITER,
            roundingMode: RoundingMode = RoundingMode.HALF_UP
        ) =
            FormatSettings().apply {
                pointSymbols.groupingSeparator = ' '
                pointSymbols.decimalSeparator = delimiter

                decimalFormat.roundingMode = roundingMode
                decimalFormat.isGroupingUsed = true
                decimalFormat.groupingSize = DEFAULT_SPLIT_SIZE
                decimalFormat.decimalFormatSymbols = pointSymbols

                val fractionDigits = if (withFractionalPart) fractionalSize else 0
                decimalFormat.maximumFractionDigits = fractionDigits
                decimalFormat.minimumFractionDigits = fractionDigits
            }
    }
}

private const val DEFAULT_FRACTIONAL_SIZE = 2
private const val DEFAULT_SPLIT_SIZE = 3
private const val DEFAULT_DELIMITER = '.'