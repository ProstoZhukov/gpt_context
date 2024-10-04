package ru.tensor.sbis.design.view.input.accesscode

import android.content.Context
import android.util.AttributeSet
import androidx.annotation.AttrRes
import androidx.annotation.StyleRes
import ru.tensor.sbis.design.view.input.R
import ru.tensor.sbis.design.view.input.accesscode.api.AccessCodeInputViewApi
import ru.tensor.sbis.design.view.input.accesscode.api.AccessCodeInputViewController
import ru.tensor.sbis.design.view.input.text.SingleLineInputView

/**
 * Поле ввода кода доступа с фиксированной маской.
 *
 * @author mb.kruglova
 */
class AccessCodeInputView internal constructor(
    context: Context,
    attrs: AttributeSet?,
    defStyleAttr: Int,
    defStyleRes: Int,
    controller: AccessCodeInputViewController
) : SingleLineInputView(
    context,
    attrs,
    defStyleAttr,
    defStyleRes,
    controller
),
    AccessCodeInputViewApi by controller {

    @JvmOverloads
    constructor(
        context: Context,
        attrs: AttributeSet? = null,
        @AttrRes
        defStyleAttr: Int = R.attr.accessCodeInputViewTheme,
        @StyleRes
        defStyleRes: Int = R.style.AccessCodeInputViewTheme
    ) : this(context, attrs, defStyleAttr, defStyleRes, AccessCodeInputViewController())

    init {
        controller.attach(this, attrs, defStyleAttr, defStyleRes)
    }
}