package ru.tensor.sbis.design.view.input.mask.phone

import android.text.Editable
import android.text.TextWatcher
import ru.tensor.sbis.design.view.input.base.BaseInputView
import ru.tensor.sbis.design.view.input.mask.phone.formatter.PhoneInputViewFormatter

/**
 * Логика ввода символов по маске для ввода номера телефона.
 * Умеет заменять 8 на +7 и вставлять + перед 7 и +7 перед 9.
 *
 * @author ps.smirnyh
 */
internal class PhoneInputViewTextWatcher(
    baseInputView: BaseInputView
) : TextWatcher {

    private val inputView = baseInputView.inputView
    private var isNotHandle = false
    private var phoneInputViewFormatter = PhoneInputViewFormatter(PhoneFormat.MOBILE, inputView.resources)
    private var isInserting = false
    private var startInsertIndex = 0

    /** @see PhoneInputView.phoneFormat */
    var phoneFormat: PhoneFormat = PhoneFormat.MOBILE
        set(value) {
            field = value
            phoneInputViewFormatter.updatePhoneFormat(value, inputView.resources)
            afterTextChanged(inputView.text)
        }

    init {
        baseInputView.filters = arrayOf(PhoneInputDialerKeyListener, phoneInputViewFormatter.lengthFilter)
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        isInserting = count > 1
        startInsertIndex = start
    }

    override fun afterTextChanged(s: Editable?) {
        s ?: return
        if (isNotHandle) return
        isNotHandle = true

        if (phoneFormat == PhoneFormat.MOBILE && isInserting) {
            s.delete(0, startInsertIndex)
        }

        phoneInputViewFormatter.formatWithoutFilter(s)

        isNotHandle = false
    }

    /** Выполнить [action] без форматирования по правилам номеров телефонов. */
    internal inline fun doWithOutFormat(action: () -> Unit) {
        isNotHandle = true
        action()
        isNotHandle = false
    }
}