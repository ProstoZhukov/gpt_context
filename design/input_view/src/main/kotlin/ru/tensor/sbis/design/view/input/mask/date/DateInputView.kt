package ru.tensor.sbis.design.view.input.mask.date

import android.content.Context
import android.util.AttributeSet
import androidx.annotation.AttrRes
import androidx.annotation.StyleRes
import ru.tensor.sbis.design.view.input.R
import ru.tensor.sbis.design.view.input.text.SingleLineInputView

/**
 * Простое однострочное поле ввода с маской для ввода даты-времени.
 *
 * @author ps.smirnyh
 */
class DateInputView internal constructor(
    context: Context,
    attrs: AttributeSet?,
    defStyleAttr: Int,
    defStyleRes: Int,
    controller: DateInputViewController
) : SingleLineInputView(
    context,
    attrs,
    defStyleAttr,
    defStyleRes,
    controller
),
    DateInputViewApi by controller {

    @JvmOverloads
    constructor(
        context: Context,
        attrs: AttributeSet? = null,
        @AttrRes
        defStyleAttr: Int = R.attr.dateInputViewTheme,
        @StyleRes
        defStyleRes: Int = R.style.DateInputViewTheme
    ) : this(context, attrs, defStyleAttr, defStyleRes, DateInputViewController())

    init {
        controller.attach(this, attrs, defStyleAttr, defStyleRes)
    }
}