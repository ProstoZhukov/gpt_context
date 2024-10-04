package ru.tensor.sbis.design.retail_views.utils

import android.widget.EditText
import timber.log.Timber
import java.math.BigDecimal
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols

/**
 * Утилиты для работы с цифровой клавиатурой
 */

/**@SelfDocumented */
const val whitespace = ' '

/**@SelfDocumented */
const val divider = '.'

/**@SelfDocumented */
const val moneyZeroValue = "0.00"

/**@SelfDocumented */
const val quantityZeroValue = "0.000"

/**@SelfDocumented */
const val numberZeroValue = "0"

/**@SelfDocumented */
const val kopeckCursorOffset = 2

/**@SelfDocumented */
const val quantityDecimalCursorOffset = 3

/**@SelfDocumented */
const val numberDecimalCursorOffset = 0

private val decimalSymbols = DecimalFormatSymbols().apply {
    groupingSeparator = ' '
    decimalSeparator = '.'
}

/**@SelfDocumented */
val intAmountFormat = DecimalFormat("##,###,###.##", decimalSymbols)

/**@SelfDocumented */
val amountFormat = DecimalFormat("##,###,##0.00", decimalSymbols)

/**@SelfDocumented */
val quantityFormat = DecimalFormat("##,###,##0.000", decimalSymbols)

/** Формат целого числа, состоящий из 8 разрядов. В случае 0 возвращает один 0. */
val numberFormat = DecimalFormat("#######0")

/**@SelfDocumented */
fun format(text: String, format: DecimalFormat): String {
    return try {
        text.replace(
            decimalSymbols.groupingSeparator.toString(),
            ""
        ).run { format.format(toDouble()) }
    } catch (e: NumberFormatException) {
        Timber.i(e)
        text
    }
}

/**@SelfDocumented */
fun formatAmount(text: String): String {
    return format(text, amountFormat)
}

/**@SelfDocumented */
fun getAmount(inputField: EditText): Double {
    return try {
        inputField.text.toString()
            .replace(" ", "")
            .toDouble()
    } catch (ex: NumberFormatException) {
        0.0
    }
}

/**@SelfDocumented */
fun getBigDecimalAmount(inputField: EditText, scale: Int): BigDecimal {
    return getBigDecimalAmount(inputField.text.toString(), scale)
}

/**@SelfDocumented */
fun getBigDecimalAmount(text: String, scale: Int): BigDecimal {
    return try {
        text.replace(" ", "").toBigDecimal()
    } catch (ex: NumberFormatException) {
        BigDecimal(0)
    }.apply {
        setScale(scale)
    }
}