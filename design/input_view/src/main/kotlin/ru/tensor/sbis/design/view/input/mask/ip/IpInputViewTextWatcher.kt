package ru.tensor.sbis.design.view.input.mask.ip

import android.text.Editable
import android.text.TextWatcher
import ru.tensor.sbis.design.view.input.base.BaseInputView

/**
 * Логика ввода символов для ip-адреса.
 * Умеет автоматически отделять октеты точками, ограничивать значение в октете и длину адреса.
 *
 * @author ia.nikitin
 */
internal class IpInputViewTextWatcher(val baseInputView: BaseInputView) : TextWatcher {

    private var deleting = false

    override fun afterTextChanged(s: Editable) {
        if (!deleting) {
            val working = s.toString()
            val split = working.split(".").dropLastWhile { it.isEmpty() }
            val string = split[split.lastIndex]
            if (string.length == 3 || string.length == 2 && string.toInt() > 25 || string == "0") {
                s.append('.')
            }
        }
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        deleting = count == 0
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
}
