package ru.tensor.sbis.design.view.input.mask

import android.content.Context
import android.util.AttributeSet
import androidx.annotation.AttrRes
import androidx.annotation.StyleRes
import ru.tensor.sbis.design.view.input.R
import ru.tensor.sbis.design.view.input.text.SingleLineInputView

/**
 * Простое однострочное поле ввода с маской.
 *
 * @author ps.smirnyh
 */
class MaskInputView internal constructor(
    context: Context,
    attrs: AttributeSet?,
    defStyleAttr: Int,
    defStyleRes: Int,
    controller: MaskInputViewController
) : SingleLineInputView(
    context,
    attrs,
    defStyleAttr,
    defStyleRes,
    controller
) {

    @JvmOverloads
    constructor(
        context: Context,
        attrs: AttributeSet? = null,
        @AttrRes
        defStyleAttr: Int = R.attr.maskInputViewTheme,
        @StyleRes
        defStyleRes: Int = R.style.MaskInputViewTheme
    ) : this(context, attrs, defStyleAttr, defStyleRes, MaskInputViewController())

    init {
        controller.attach(this, attrs, defStyleAttr, defStyleRes)
    }
}