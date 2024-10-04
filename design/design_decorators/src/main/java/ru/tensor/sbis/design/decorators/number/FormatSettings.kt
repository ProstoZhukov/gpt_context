package ru.tensor.sbis.design.decorators.number

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols

/**
 * Класс для настройки форматтера чисел.
 *
 * @author ps.smirnyh
 */
data class FormatSettings(
    val decimalFormat: DecimalFormat = DecimalFormat(),
    val pointSymbols: DecimalFormatSymbols = DecimalFormatSymbols()
) {
    companion object {
        fun getDefault(
            withFractionalPart: Boolean,
            fractionalSize: Int = DEFAULT_FRACTIONAL_SIZE,
            delimiter: Char = DEFAULT_DELIMITER,
            roundingMode: RoundMode = RoundMode.TRUNC,
            useGrouping: Boolean = false,
            showEmptyDecimals: Boolean = false
        ) =
            FormatSettings().apply {
                pointSymbols.groupingSeparator = DEFAULT_GROUPING_SEPARATOR
                pointSymbols.decimalSeparator = delimiter

                decimalFormat.roundingMode = roundingMode.mode
                decimalFormat.isGroupingUsed = useGrouping
                decimalFormat.groupingSize = DEFAULT_SPLIT_SIZE
                decimalFormat.decimalFormatSymbols = pointSymbols

                val fractionDigits = if (withFractionalPart) fractionalSize else 0
                decimalFormat.maximumFractionDigits = fractionDigits
                decimalFormat.minimumFractionDigits = if (showEmptyDecimals) fractionDigits else 0
            }
    }
}

internal const val DEFAULT_DELIMITER = '.'
private const val DEFAULT_FRACTIONAL_SIZE = 20
private const val DEFAULT_SPLIT_SIZE = 3
private const val DEFAULT_GROUPING_SEPARATOR = ' '