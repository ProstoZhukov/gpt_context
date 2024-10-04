package ru.tensor.sbis.design.view.input.text

import android.content.Context
import android.util.AttributeSet
import androidx.annotation.AttrRes
import androidx.annotation.StyleRes
import ru.tensor.sbis.design.view.input.R

/**
 * Однострочное поле ввода текста.
 *
 * @author ps.smirnyh
 */
class TextInputView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes
    defStyleAttr: Int = R.attr.textInputViewTheme,
    @StyleRes
    defStyleRes: Int = R.style.TextInputViewTheme
) : SingleLineInputView(
    context,
    attrs,
    defStyleAttr,
    defStyleRes
) {
    init {
        controller.attach(this, attrs, defStyleAttr, defStyleRes)
    }
}