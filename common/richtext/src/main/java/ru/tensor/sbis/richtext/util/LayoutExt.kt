@file:JvmName("LayoutExt")

package ru.tensor.sbis.richtext.util

import android.text.Layout

/**
 * Осуществляет дополнительную валидацию [Layout.getOffsetForHorizontal], т.к метод возвращает всегда начало
 * или конец строки, даже если передана позиция вне текста.
 * Возвращает -1 в случае если позиция [x] не найдена в строке [line].
 */
fun Layout.getOffsetForHorizontalValidated(line: Int, x: Float): Int {
    return getOffsetForHorizontal(line, x).takeIf {
        val lineStart = getLineStart(line)
        val lineEnd = getLineEnd(line)
        val lineCharEnd = lineEnd - 1
        when (it) {
            lineStart -> x >= getPrimaryHorizontal(lineStart)
            lineEnd -> x <= getPrimaryHorizontal(lineEnd)
            lineCharEnd -> x <= getPrimaryHorizontal(lineCharEnd)
            else -> true
        }
    } ?: -1
}
