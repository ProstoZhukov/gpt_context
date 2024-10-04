package ru.tensor.sbis.design.view.input.money

import android.content.Context
import android.util.AttributeSet
import androidx.annotation.AttrRes
import androidx.annotation.StyleRes
import ru.tensor.sbis.design.view.input.R
import ru.tensor.sbis.design.view.input.money.api.MoneyInputViewApi
import ru.tensor.sbis.design.view.input.money.api.MoneyInputViewController
import ru.tensor.sbis.design.view.input.text.SingleLineInputView

/**
 * Простое однострочное поле ввода денег.
 *
 * @author ps.smirnyh
 */
class MoneyInputView internal constructor(
    context: Context,
    attrs: AttributeSet?,
    defStyleAttr: Int,
    defStyleRes: Int,
    controller: MoneyInputViewController
) : SingleLineInputView(
    context,
    attrs,
    defStyleAttr,
    defStyleRes,
    controller
),
    MoneyInputViewApi by controller {

    @JvmOverloads
    constructor(
        context: Context,
        attrs: AttributeSet? = null,
        @AttrRes
        defStyleAttr: Int = R.attr.moneyInputViewTheme,
        @StyleRes
        defStyleRes: Int = R.style.MoneyInputViewTheme
    ) : this(context, attrs, defStyleAttr, defStyleRes, MoneyInputViewController())

    init {
        controller.attach(this, attrs, defStyleAttr, defStyleRes)
    }
}