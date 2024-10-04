/**
 * Расширения для Spannable.
 *
 * @author ps.smirnyh
 */
package ru.tensor.sbis.design.utils.extentions

import android.text.Spannable
import androidx.core.text.getSpans

/** Удалить спаны типа [T]. */
inline fun <reified T : Any> Spannable.clearSpans(
    start: Int = 0,
    end: Int = length
) {
    getSpans<T>(start, end).forEach { removeSpan(it) }
}