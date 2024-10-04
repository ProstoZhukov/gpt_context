package ru.tensor.sbis.design.decorators.number

import ru.tensor.sbis.design.decorators.number.AbbreviationNumbers.BILLION
import ru.tensor.sbis.design.decorators.number.AbbreviationNumbers.MILLION
import ru.tensor.sbis.design.decorators.number.AbbreviationNumbers.THOUSAND
import ru.tensor.sbis.design.decorators.number.AbbreviationNumbers.TRILLION

/**
 * Форматирование числа для отобаржения значения с аббривиатурой сокращения.
 *
 * @author ps.smirnyh
 */
fun abbreviationFormat(
    value: Double,
    precision: Int,
    roundMode: RoundMode,
    useEmptyDecimals: Boolean,
    abbreviationType: AbbreviationType
): String {
    val formatter = FormatSettings.getDefault(
        withFractionalPart = precision > 0,
        fractionalSize = precision,
        roundingMode = roundMode,
        showEmptyDecimals = useEmptyDecimals
    )
    return when {
        value >= TRILLION.number || value <= -TRILLION.number -> {
            formatter.decimalFormat.format((value / TRILLION.number)) +
                if (abbreviationType == AbbreviationType.SHORT) {
                    TRILLION.abbreviationShort
                } else {
                    TRILLION.abbreviationLong
                }
        }
        value >= BILLION.number || value <= -BILLION.number -> {
            formatter.decimalFormat.format(value / BILLION.number) +
                if (abbreviationType == AbbreviationType.SHORT) {
                    BILLION.abbreviationShort
                } else {
                    BILLION.abbreviationLong
                }
        }
        value >= MILLION.number || value <= -MILLION.number -> {
            formatter.decimalFormat.format(value / MILLION.number) +
                if (abbreviationType == AbbreviationType.SHORT) {
                    MILLION.abbreviationShort
                } else {
                    MILLION.abbreviationLong
                }
        }
        value >= THOUSAND.number || value <= -THOUSAND.number -> {
            formatter.decimalFormat.format(value / THOUSAND.number) +
                if (abbreviationType == AbbreviationType.SHORT) {
                    THOUSAND.abbreviationShort
                } else {
                    THOUSAND.abbreviationLong
                }
        }
        else -> value.toString()
    }
}

/** Поддерживаемые сокращения чисел с аббревиатурой. */
enum class AbbreviationNumbers(
    val number: Long,
    val abbreviationShort: String,
    val abbreviationLong: String
) {
    TRILLION(1_000_000_000_000, "т", " трлн"),
    BILLION(1_000_000_000, "г", " млрд"),
    MILLION(1_000_000, "м", " млн"),
    THOUSAND(1000, "к", " тыс")
}