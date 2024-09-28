package ru.tensor.sbis.design.retail_models.utils

import java.math.BigDecimal
import java.math.RoundingMode

/** @SelfDocumented */
val ZERO_MONEY_VALUE = "0.0".toPriceBigDecimal()

/**@SelfDocumented */
const val PRICE_SCALE = 2

/**@SelfDocumented */
fun BigDecimal?.isZero() = this != null && this.compareTo(BigDecimal.ZERO) == 0

/**@SelfDocumented */
fun BigDecimal.isNotZero() = !isZero()

/**@SelfDocumented */
fun BigDecimal.isMoreZero() = this.toFloat() > 0

/**@SelfDocumented */
fun BigDecimal.isLessZero() = this.toFloat() < 0

/**@SelfDocumented */
fun BigDecimal.roundHalfUp(newScale: Int = 2): BigDecimal = this.setScale(newScale, RoundingMode.HALF_UP)

/**@SelfDocumented */
fun BigDecimal.isInteger() =
    // stripTrailingZeros() не работает для 0.00
    // this == BigDecimal.Zero не подходит т. к. учитывает scale и 0.00 != 0
    this.compareTo(BigDecimal.ZERO) == 0 || scale() <= 0 || stripTrailingZeros().scale() <= 0

/**@SelfDocumented */
fun Double.toPriceBigDecimal() = BigDecimal.valueOf(this).setScale(PRICE_SCALE, RoundingMode.HALF_UP)

private fun String.toPriceBigDecimal(roundingMode: RoundingMode = RoundingMode.HALF_UP) =
    toBigDecimal(PRICE_SCALE, roundingMode)

private fun String.toBigDecimal(scale: Int, roundingMode: RoundingMode): BigDecimal {
    val bigDecimal = formatStringToBigDecimalConversionFormat().toBigDecimal()
    return bigDecimal.setScale(scale, roundingMode)
}

private fun String.formatStringToBigDecimalConversionFormat() =
    if (isEmpty()) "0" else filterNot { it.isWhitespace() }