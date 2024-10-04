package ru.tensor.sbis.design.view.input.utils

import android.text.Selection
import android.text.Spannable
import android.text.TextWatcher
import android.widget.EditText

/**
 * Добавить [firstListener] перед [secondListener].
 *
 * @author ps.smirnyh
 */
internal fun EditText.addFirstListenerBeforeSecondListener(firstListener: TextWatcher, secondListener: TextWatcher) {
    removeTextChangedListener(secondListener)
    addTextChangedListener(firstListener)
    addTextChangedListener(secondListener)
}

/**
 * Безопасно установить [Selection] с ограничениями длинны исходного текста.
 *
 * @author ps.smirnyh
 */
internal fun Spannable.safeSetSelection(index: Int) {
    Selection.setSelection(this, index.coerceIn(0, length))
}