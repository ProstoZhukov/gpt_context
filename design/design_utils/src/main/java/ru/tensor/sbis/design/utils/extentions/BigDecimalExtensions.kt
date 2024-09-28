package ru.tensor.sbis.design.utils.extentions

import java.math.BigDecimal

/**
 * Расширения для BigDecimal.
 */

/**@SelfDocumented*/
fun BigDecimal.isEmpty(): Boolean = this == BigDecimal.ZERO

/**@SelfDocumented*/
fun BigDecimal.isNotEmpty(): Boolean = this != BigDecimal.ZERO

/**@SelfDocumented*/
fun BigDecimal?.isNotNil(): Boolean = this != null && this.toDouble() > 1E-3

/**@SelfDocumented*/
fun BigDecimal?.isNil(): Boolean = isNotNil().not()

/**@SelfDocumented*/
fun BigDecimal?.map(): BigDecimal =
    this?.toDouble()?.let { BigDecimal.valueOf(it) } ?: BigDecimal.ZERO

/**@SelfDocumented*/
fun Double?.isNotNil(): Boolean = this != null && this > 1E-3

/**@SelfDocumented*/
fun Float?.isNotNil() = this != null && this > 1E-3