package ru.tensor.sbis.design.view.input.password

import android.content.Context
import android.util.AttributeSet
import androidx.annotation.AttrRes
import androidx.annotation.StyleRes
import ru.tensor.sbis.design.view.input.R
import ru.tensor.sbis.design.view.input.password.api.PasswordInputViewApi
import ru.tensor.sbis.design.view.input.password.api.PasswordInputViewController
import ru.tensor.sbis.design.view.input.password.api.PasswordInputViewControllerApi
import ru.tensor.sbis.design.view.input.text.SingleLineInputView

/**
 * Однострочное поле ввода пароля.
 *
 * @author ps.smirnyh
 */
class PasswordInputView internal constructor(
    context: Context,
    attrs: AttributeSet?,
    defStyleAttr: Int,
    defStyleRes: Int,
    controller: PasswordInputViewControllerApi
) : SingleLineInputView(
    context,
    attrs,
    defStyleAttr,
    defStyleRes,
    controller
),
    PasswordInputViewApi by controller {

    @JvmOverloads
    constructor(
        context: Context,
        attrs: AttributeSet? = null,
        @AttrRes
        defStyleAttr: Int = R.attr.passwordInputViewTheme,
        @StyleRes
        defStyleRes: Int = R.style.PasswordInputViewTheme
    ) : this(context, attrs, defStyleAttr, defStyleRes, PasswordInputViewController())

    init {
        controller.attach(this, attrs, defStyleAttr, defStyleRes)
    }
}