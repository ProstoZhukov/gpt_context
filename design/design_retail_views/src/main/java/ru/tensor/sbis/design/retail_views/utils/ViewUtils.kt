package ru.tensor.sbis.design.retail_views.utils

import android.view.View
import android.widget.EditText
import ru.tensor.sbis.design.utils.KeyboardUtils

/** Открывает и закрывает клавиатуру с задеркой для предотвращения её дёргания. */
internal fun requestFocusAndShowKeyboard(parent: View, editText: EditText, isSoftKeyboardRequired: Boolean) {
    // Задержка, чтобы предотвратить "дёрганье" софт клавиатуры
    val delay = 200L
    editText.postDelayed(
        {
            parent.requestFocus()
            editText.selectAll()
            if (isSoftKeyboardRequired) {
                KeyboardUtils.showKeyboard(editText)
            } else {
                KeyboardUtils.hideKeyboard(editText)
            }
        },
        delay
    )
}
