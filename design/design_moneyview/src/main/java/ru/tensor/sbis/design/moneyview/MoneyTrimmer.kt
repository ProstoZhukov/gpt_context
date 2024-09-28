package ru.tensor.sbis.design.moneyview

/**
 * Разбивает [Long] на [Int] с указанием едениц измерения [MoneyUnit]
 *
 * Например:
 * 123 456 = 123 456 NONE
 *
 * 1 234 567 = 1 234 THOUSAND
 * 12 345 678 = 12 345 THOUSAND
 * 123 456 789 = 123 456 THOUSAND
 *
 * 1 234 567 890 = 1 234 MILLION
 * 12 345 678 901 = 12 345 MILLION
 * 123 456 789 012 = 123 456 MILLION
 *
 * 1 234 567 890 123 = 1 234 BILLION
 */
fun trim(money: Long): Pair<Int, MoneyUnit> {
    return when {
        money < 1_000_000 -> Pair(money.toInt(), MoneyUnit.NONE)
        money < 1_000_000_000 -> Pair(money.cutOfDigit(1000), MoneyUnit.THOUSAND)
        money < 1_000_000_000_000 -> Pair(money.cutOfDigit(1_000_000), MoneyUnit.MILLION)
        money < 1_000_000_000_000_000 -> Pair(money.cutOfDigit(1_000_000_000), MoneyUnit.BILLION)
        else -> Pair(money.toInt(), MoneyUnit.NONE)
    }
}

private fun Long.cutOfDigit(denominator: Int): Int {
    return (this / denominator).toInt()
}