package ru.tensor.sbis.design.view.input.selection

import android.content.Context
import android.util.AttributeSet
import androidx.annotation.AttrRes
import androidx.annotation.StyleRes
import ru.tensor.sbis.design.view.input.R
import ru.tensor.sbis.design.view.input.selection.api.ValueSelectionInputViewApi
import ru.tensor.sbis.design.view.input.selection.api.ValueSelectionInputViewController
import ru.tensor.sbis.design.view.input.text.SingleLineInputView

/**
 * Однострочное поле ввода для выбора значения.
 *
 * @author ps.smirnyh
 */
class ValueSelectionInputView internal constructor(
    context: Context,
    attrs: AttributeSet?,
    defStyleAttr: Int,
    defStyleRes: Int,
    controller: ValueSelectionInputViewController
) : SingleLineInputView(
    context,
    attrs,
    defStyleAttr,
    defStyleRes,
    controller
),
    ValueSelectionInputViewApi by controller {

    @JvmOverloads
    constructor(
        context: Context,
        attrs: AttributeSet? = null,
        @AttrRes
        defStyleAttr: Int = R.attr.valueSelectionInputViewTheme,
        @StyleRes
        defStyleRes: Int = R.style.ValueSelectionInputViewTheme
    ) : this(context, attrs, defStyleAttr, defStyleRes, ValueSelectionInputViewController())

    init {
        controller.attach(this, attrs, defStyleAttr, defStyleRes)
        inputView.apply {
            isLongClickable = false
            isFocusable = false
        }
    }

    override fun setOnClickListener(clickListener: OnClickListener?) {
        super.setOnClickListener(clickListener)
        onListIconClickListener = { clickListener?.onClick(it) }
    }
}