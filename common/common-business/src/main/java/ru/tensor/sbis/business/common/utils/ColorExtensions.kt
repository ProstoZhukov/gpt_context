package ru.tensor.sbis.business.common.utils

import android.graphics.Color
import androidx.annotation.FloatRange

/**
 * Набор утилит для работы с цветами
 */

private const val HEX_RADIX = 16
private const val HEX_COLOR_LENGTH = 6
private const val HEX_ZERO_CHAR = '0'

private const val MIN_ALPHA = 0.0f
private const val MAX_ALPHA = 1.0f
private const val HEX_MAX_ALPHA = 0xFF
private const val HEX_ALPHA_LENGTH = 2

private const val MIN_COLOR = 0x000000
private const val MAX_COLOR = 0xFFFFFF

/**
 * Добавления компонета альфы к цвету.
 * Бывает, что цвет приходит извне и его нужно применить ко View. В этом случае цвет будет считаться прозрачным из-за
 * того, что у него отсутствует альфа.
 * Метод не выбросит исключения, так как исправляет минимальные и максимальные значения.
 *
 * @param alpha значение альфы, которую нужно применит к цвету
 *
 * @return цвет с применённой альфой
 */
fun Int.toColor(@FloatRange(from = 0.0, to = 1.0) alpha: Float = 1.0f): Int {
    val hexColor = coerceIn(MIN_COLOR..MAX_COLOR).toHexString(HEX_COLOR_LENGTH)
    val hexAlpha = (alpha.coerceIn(MIN_ALPHA..MAX_ALPHA) * HEX_MAX_ALPHA).toInt().toHexString(HEX_ALPHA_LENGTH)
    return Color.parseColor("#${hexAlpha}${hexColor}")
}

private fun Int.toHexString(maxLength: Int): String = this.toString(HEX_RADIX).padEnd(maxLength, HEX_ZERO_CHAR)
