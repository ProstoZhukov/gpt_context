package ru.tensor.sbis.formatter.currency

/**
 * Режим разделения целой и дробной части суммы.
 *
 * @author ps.smirnyh
 */
enum class CurrencyTranslationMode {

    /** Разделенный пробелом (100 рублей 25 копеек). */
    SPLIT,

    /** Разделенный сепаратором (точка/запятая) (100.25 рублей). */
    SEPARATED;

    internal companion object {
        const val fractionSeparator = '.'
        const val triadDelimiter = ' '
    }
}
