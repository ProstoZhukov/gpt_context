package ru.tensor.sbis.business.common.ui.utils

import ru.tensor.sbis.common.util.ResourceProvider
import ru.tensor.sbis.design.moneyview.R
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.LinkedList
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.roundToLong

/**  Максимальное кол-во цифр в выручке, по умолчанию */
private const val DEFAULT_MAX_REVENUE_DIGITS = 5
private const val BIG_DECIMAL_FRACTIONAL_SIZE = 3
private const val MIN_FACTOR = 1

/** Основание для возведения в степень  */
private const val MIN_FACTOR_BASE = 1000.0

/** Максимальная интересуемая степень [MIN_FACTOR_BASE] */
private const val EXPONENT = 6

/**
 * Утилиты для округления значений.
 */
object RoundUtils {

    /**
     * возвращает фактор округления - степень числа 1000
     *
     * @param value число, к которому подбирается фактор
     * @param maxDigits максимальное кол-во символов, которое ожидается в числе округления
     * @return фактор - 1000 в степени 0...5
     */
    fun getFactor(value: Double, maxDigits: Int = DEFAULT_MAX_REVENUE_DIGITS): Double {
        val upperBound = 10.0.pow(maxDigits)
        val absValue = abs(value)
        return (5 downTo 0)
            .map { 1000.0.pow(it) }
            .takeWhile { absValue / it < upperBound }
            .lastOrNull()
            ?: 1.0
    }

    /**
     * Возвращает максимально возможный фактор округления - степень числа 1000
     *
     * @param value число, к которому подбирается фактор
     * @return фактор - [MIN_FACTOR_BASE] в подобранной степени
     */
    fun getMaxFactor(value: Double): Double {
        var factor = 1.0
        val absValue = abs(value)
        for (i in 0..EXPONENT) {
            val newFactor = MIN_FACTOR_BASE.pow(i)
            if ((absValue / newFactor) >= MIN_FACTOR) {
                factor = newFactor
            } else {
                break
            }
        }
        return factor
    }

    /**
     * Округление с учетом фактора, окруляет до "красивых" чисел
     *
     * @param value  округляемое число
     * @param factor фактор для округления
     * @return округленное значение
     */
    private fun round(
        value: Double,
        factor: Double,
    ): Double {
        val sign = if (value < 0) -1.0 else 1.0
        val absValue = abs(value)
        val additionalFactor =
            getFactor(
                absValue / factor,
                maxDigits = 3
            )
        val totalFactor = factor * additionalFactor

        val div = absValue / totalFactor

        val roundValues: MutableList<Double> = LinkedList()
        roundValues.add(0.0)
        var k = 1
        do {
            roundValues.addAll(listOf(0.1, 0.2, 0.25, 0.5).map { it * k })
            k *= 10
        } while (k <= 1000)
        roundValues.add(1000.0)

        val rounded = roundValues
            .filter { it - (it.toLong()) == 0.0 || additionalFactor > 1 }
            .firstOrNull { div <= it }
            ?: absValue

        return sign * rounded * additionalFactor
    }

    /**
     * Деление с округлением
     *
     * @param value  делимое
     * @param grade  делитель
     * @param factor степень 1000 для округления
     * @return округленное значение
     */
    fun roundDivided(
        value: Double,
        grade: Int,
        factor: Double,
    ): Double {
        val div = value / grade.toDouble()
        return grade.toDouble() * round(
            div,
            factor
        )
    }

    /**
     * Применяет округление значения к ближайшему целому числу с заданным фактором
     *
     * @param value округляемое число
     * @param factor степень 1000 для округления
     * @return значение, округленное с заданным фактором до ближайшего целого
     */
    fun roundedDouble(value: Double, factor: Double): Double {
        // round(-0.5) = 0, в то время как round(0.5) = 1, т.к. округление происходит
        // к бОльшему целому числу. Поэтому округляем по модулю, а затем восстанавливаем знак.
        val result = abs(value / factor).roundToLong().toDouble()
        return if (value >= 0) {
            result
        } else {
            -result
        }
    }

    /**
     * Аналогично [roundedDouble], но не округляет [value] до 0, если число было > 0 или < 0.
     */
    fun roundedDoubleNormalized(value: Double, factor: Double): Double {
        val result = roundedDouble(value, factor)
        return when {
            value > 0 -> max(result, 1.0)
            value < 0 -> min(result, -1.0)
            else -> 0.0
        }
    }

    /**
     * Аналогично [roundedDouble], но работает с [BigDecimal].
     */
    fun roundedBigDecimal(value: BigDecimal, factor: Long): Double {
        return if (factor == 1L || factor == 0L) {
            roundedWithoutFactor(value)
        } else {
            value.setScale(BIG_DECIMAL_FRACTIONAL_SIZE)
                .divide(factor.toBigDecimal(), RoundingMode.HALF_UP)
                .toDouble()
        }
    }

    /**
     * Применяет округление значения к ближайшему целому числу без фактора (для маленьких чисел)
     *
     * @param value округляемое число
     * @return значение, округленное до ближайшего целого, если округленное значение не равно нулю. иначе не округляем
     */
    private fun roundedWithoutFactor(value: BigDecimal): Double {
        val roundedValue = value.setScale(0, RoundingMode.HALF_UP).toDouble()
        return if (roundedValue == 0.0) {
            value.toDouble()
        } else {
            roundedValue
        }
    }

    /**
     * Возвращает локализованное строковое представление фактора округления.
     *
     * @param factor фактор округления
     * @return локализованная строка с названием фактора округления
     */
    fun ResourceProvider.getFactorUnits(factor: Double) = when (factor) {
        1_000.0, 10_000.0, 100_000.0 -> getString(R.string.design_moneyview_suffix_k)
        1_000_000.0 -> getString(R.string.design_moneyview_suffix_million)
        1_000_000_000.0 -> getString(R.string.design_moneyview_suffix_billion)
        1_000_000_000_000.0 -> getString(R.string.design_moneyview_suffix_trillion)
        1_000_000_000_000_000.0 -> getString(R.string.design_moneyview_suffix_quadrillion)
        else -> ""
    }
}
