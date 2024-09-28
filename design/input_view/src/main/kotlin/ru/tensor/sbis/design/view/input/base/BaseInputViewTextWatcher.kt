package ru.tensor.sbis.design.view.input.base

import android.text.Editable
import android.text.TextWatcher

/**
 * Обёртка над колбеком изменения текста в поле ввода для передачи изменений текста в подписку [onValueChanged].
 * @property baseInputView поле ввода для передачи в [onValueChanged].
 * @property updateEllipsize функция обновления троеточия в конце строки текста.
 * @property updateInputView обновление состояния элементов поля ввода при вводе текста (скрытие/показ крестика).
 *
 * @author ps.smirnyh
 */
internal class BaseInputViewTextWatcher(
    private val baseInputView: BaseInputView,
    private val updateEllipsize: () -> Unit,
    private val updateInputView: () -> Unit
) : TextWatcher {

    private var oldText = ""

    var onValueChanged: ((view: BaseInputView, value: String) -> Unit)? = null

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        oldText = s?.toString() ?: ""
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit

    override fun afterTextChanged(s: Editable?) {
        if (s == null || s.toString() == oldText) return
        updateInputView()
        if (!baseInputView.inputView.hasFocus()) updateEllipsize()
        onValueChanged?.invoke(baseInputView, s.toString())
    }
}