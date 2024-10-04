package ru.tensor.sbis.design.text_span.text.util

import android.text.Editable
import android.text.TextWatcher

/**
 * Упрощённая реализация [TextWatcher] для обработки изменения введённого текста
 *
 * @param onTextChanged лямбда, вызываемая после изменения текста [TextWatcher.afterTextChanged]
 *
 * @author us.bessonov
 */
class TextChangedListener(private val onTextChanged: (newText: String) -> Unit) : TextWatcher {

    override fun afterTextChanged(s: Editable?) {
        onTextChanged(s?.toString().orEmpty())
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        // ignore
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        // ignore
    }
}