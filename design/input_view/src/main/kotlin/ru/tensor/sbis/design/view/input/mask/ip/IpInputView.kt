package ru.tensor.sbis.design.view.input.mask.ip

import android.content.Context
import android.util.AttributeSet
import androidx.annotation.AttrRes
import androidx.annotation.StyleRes
import ru.tensor.sbis.design.view.input.R
import ru.tensor.sbis.design.view.input.mask.ip.api.IpInputViewController
import ru.tensor.sbis.design.view.input.text.SingleLineInputView

/**
 * Простое однострочное поле ввода с маской для ввода ip-адреса.
 *
 * @author ia.nikitin
 */
class IpInputView internal constructor(
    context: Context,
    attrs: AttributeSet?,
    defStyleAttr: Int,
    defStyleRes: Int,
    controller: IpInputViewController
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
    ) : this(context, attrs, defStyleAttr, defStyleRes, IpInputViewController())

    init {
        controller.attach(this, attrs, defStyleAttr, defStyleRes)
    }
}