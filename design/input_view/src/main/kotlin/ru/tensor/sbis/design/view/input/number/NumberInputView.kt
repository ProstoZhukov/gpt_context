package ru.tensor.sbis.design.view.input.number

import android.content.Context
import android.util.AttributeSet
import androidx.annotation.AttrRes
import androidx.annotation.StyleRes
import ru.tensor.sbis.design.view.input.R
import ru.tensor.sbis.design.view.input.number.api.NumberInputViewApi
import ru.tensor.sbis.design.view.input.number.api.NumberInputViewController
import ru.tensor.sbis.design.view.input.number.api.NumberInputViewFractionApi
import ru.tensor.sbis.design.view.input.number.api.NumberInputViewGroupDecorationApi
import ru.tensor.sbis.design.view.input.text.SingleLineInputView

/**
 * Простое однострочное поле ввода чисел. Позволяет вводить только цифры.
 *
 * @author ps.smirnyh
 */
class NumberInputView internal constructor(
    context: Context,
    attrs: AttributeSet?,
    defStyleAttr: Int,
    defStyleRes: Int,
    override val controller: NumberInputViewController
) : SingleLineInputView(
    context,
    attrs,
    defStyleAttr,
    defStyleRes,
    controller
),
    NumberInputViewApi by controller,
    NumberInputViewFractionApi by controller,
    NumberInputViewGroupDecorationApi by controller {

    @JvmOverloads
    constructor(
        context: Context,
        attrs: AttributeSet? = null,
        @AttrRes
        defStyleAttr: Int = R.attr.numberInputViewTheme,
        @StyleRes
        defStyleRes: Int = R.style.NumberInputViewTheme
    ) : this(context, attrs, defStyleAttr, defStyleRes, NumberInputViewController())

    init {
        controller.attach(this, attrs, defStyleAttr, defStyleRes)
    }

    override fun onDetachedFromWindow() {
        controller.detach()
        super.onDetachedFromWindow()
    }
}