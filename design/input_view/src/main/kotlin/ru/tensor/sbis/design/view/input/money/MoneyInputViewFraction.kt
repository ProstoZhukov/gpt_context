package ru.tensor.sbis.design.view.input.money

/**
 * Вид дробной части (копеек).
 * @property digits количество знаков.
 *
 * @author ps.smirnyh
 */
enum class MoneyInputViewFraction(val digits: Int) {
    /**
     * Обычная дробная часть - 2 знака (копейки).
     */
    ON(2),

    /**
     * Нет дробной части (копеек).
     */
    OFF(0),

    /**
     * Только десятая часть - 1 знак.
     */
    ONLY_TENS(1);

    fun toNumberFraction(): UByte = this.digits.toUByte()
}

internal fun UByte.toMoneyFraction() = when (this) {
    0u.toUByte() -> MoneyInputViewFraction.OFF
    1u.toUByte() -> MoneyInputViewFraction.ONLY_TENS
    else -> MoneyInputViewFraction.ON
}