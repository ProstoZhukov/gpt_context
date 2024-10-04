package ru.tensor.sbis.design.view.input.mask.phone

import android.content.Context
import android.util.AttributeSet
import androidx.annotation.AttrRes
import androidx.annotation.StyleRes
import ru.tensor.sbis.design.view.input.R
import ru.tensor.sbis.design.view.input.mask.phone.api.PhoneInputViewApi
import ru.tensor.sbis.design.view.input.mask.phone.api.PhoneInputViewController
import ru.tensor.sbis.design.view.input.text.SingleLineInputView

/**
 * Простое однострочное поле ввода с маской для ввода номера телефона.
 *
 * @author ps.smirnyh
 */
class PhoneInputView internal constructor(
    context: Context,
    attrs: AttributeSet?,
    defStyleAttr: Int,
    defStyleRes: Int,
    controller: PhoneInputViewController
) : SingleLineInputView(
    context,
    attrs,
    defStyleAttr,
    defStyleRes,
    controller
),
    PhoneInputViewApi by controller {

    @JvmOverloads
    constructor(
        context: Context,
        attrs: AttributeSet? = null,
        @AttrRes
        defStyleAttr: Int = R.attr.phoneInputViewTheme,
        @StyleRes
        defStyleRes: Int = R.style.PhoneInputViewTheme
    ) : this(context, attrs, defStyleAttr, defStyleRes, PhoneInputViewController())

    init {
        controller.attach(this, attrs, defStyleAttr, defStyleRes)
    }
}