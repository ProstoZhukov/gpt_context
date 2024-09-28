/**
 * Инструменты для вычисления предполагаемой ширины текста при отображении
 *
 * @author us.bessonov
 */
package ru.tensor.sbis.design.utils

import android.graphics.Paint
import androidx.annotation.Px
import kotlin.math.min
import kotlin.math.roundToInt

const val ELLIPSIS = "\u2026"
private const val MIN_VISIBLE_CHARACTERS = 2

/**
 * Возвращает предполагаемую ширину текста при отображении
 */
@Px
fun getExpectedTextWidth(text: CharSequence?, paint: Paint): Int {
    return text?.let { paint.measureText(it.toString()).roundToInt() } ?: 0
}

/**
 * Возвращает минимальную ширину текста при отображении, с учётом его возможного сокращения
 *
 * @param minVisibleCharacters мин. число видимых значимых символов в пределах длины строки
 */
@Px
fun getMinTextWidth(
    text: CharSequence?,
    paint: Paint,
    minVisibleCharacters: Int = MIN_VISIBLE_CHARACTERS
): Int {
    return text?.let {
        val string = it.toString()
        val ellipsized = string.take(minVisibleCharacters).plus(ELLIPSIS)
        min(paint.measureText(string), paint.measureText(ellipsized)).roundToInt()
    } ?: 0
}