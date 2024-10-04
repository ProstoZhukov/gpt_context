@file:JvmName("FormatUtils")

package ru.tensor.sbis.design.utils

import androidx.annotation.IntRange
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*
import kotlin.math.floor

/**
 * Форматтер чисел с пробелом, например: 328, 1 028, 1 500 000
 */
@JvmField
val DECIMAL_FORMATTER = DecimalFormat(
    "",
    DecimalFormatSymbols(Locale.US).apply {
        groupingSeparator = ' '
    }
).apply {
    groupingSize = 3
}

/**
 * Форматирование для счётчика с округлением до тысяч.
 */
fun formatCount(count: Int): String {
    // https://online.sbis.ru/open_dialog.html?guid=2ecc9516-ef86-4acc-8823-7c38706cb36f&message=ff67f8a6-e23e-49f7-ba29-6c9a7c520419
    @Suppress("DEPRECATION")
    return when {
        count < 1 -> ""
        count < 1000 -> count.toString()
        count < 1100 -> "1K"
        // учитываем сотни
        count < 10000 -> String
            .format(Locale.US, "%.1fK", floor((count / 1000F) * 10F) / 10F)
            .replace(".0", "")
        count < 11000 -> "10K"
        // считаем только тысячи
        count < 100000 -> (count / 1000).toString() + 'K'
        else -> "99K"
    }
}

/**
 * Форматирование для счётчика с округлением до тысяч
 *
 * @throws IllegalArgumentException если получено отрицательное значение счётчика
 *
 * @sample formatCount
 *
 * @see formatHundredCounter
 */
fun formatThousandsCounter(@IntRange(from = 0) count: Int): String {
    require(count >= 0)
    return formatCount(count)
}

/**
 * Форматирование для счётчика с пределом 99. Далее отображается как 99+
 *
 * @throws IllegalArgumentException если получено отрицательное значение счётчика
 *
 * @see formatThousandsCounter
 */
fun formatHundredCounter(@IntRange(from = 0) count: Int): String {
    require(count >= 0)
    return if (count == 0) "" else if (count < 100) count.toString() else "99+"
}

/**
 * Форматирование числа с добавлением лидирующего символа "+", если число > 0.
 */
fun formatDiffCount(count: Int): String {
    return when {
        count <= 0 -> ""
        else -> "+" + formatCount(count)
    }
}

/**
 * Форматирование числа с заменой его на 0, если число <= 0.
 */
fun formatCountNotEmpty(count: Int): String {
    return when {
        count <= 0 -> "0"
        else -> formatCount(count)
    }
}

/**
 * Форматирование чисел с прорбелом, например: 328, 1 028, 1 500 000
 */
fun formatCountSimple(count: Int): String {
    return when {
        count <= 0 -> ""
        count < 1000 -> count.toString()
        else -> DECIMAL_FORMATTER.format(count)
    }
}