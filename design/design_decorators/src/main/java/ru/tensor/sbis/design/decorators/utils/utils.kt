/**
 * Файл с вспомогательными функциями.
 *
 * @author ps.smirnyh
 */
package ru.tensor.sbis.design.decorators.utils

import android.text.Spannable
import android.text.Spanned
import android.text.style.CharacterStyle
import ru.tensor.sbis.design.decorators.number.DEFAULT_DELIMITER

/** @SefDocumented */
internal fun CharSequence.takeIfExistDecimal(delimiter: Char = DEFAULT_DELIMITER): Int? =
    indexOf(delimiter).takeIf { it > -1 }

/**
 * Функция для установки span с аргументами по умолчанию.
 */
@Suppress("EXTENSION_SHADOWED_BY_MEMBER")
internal fun Spannable.setSpan(
    span: CharacterStyle,
    start: Int = 0,
    end: Int = length,
    spanApplyMode: Int = Spanned.SPAN_INCLUSIVE_EXCLUSIVE
) = setSpan(span, start, end, spanApplyMode)