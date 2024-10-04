package ru.tensor.sbis.design.view.input.text.utils.style

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.annotation.StyleRes
import androidx.core.content.withStyledAttributes
import ru.tensor.sbis.design.theme.global_variables.IconColor
import ru.tensor.sbis.design.view.input.R

/**
 * Класс для хранения стилевых значений многострочного поля ввода.
 *
 * @author ps.smirnyh
 */
internal class MultilineStyleHolder(
    val property: PropertyHolder = PropertyHolder()
) {

    fun loadStyle(
        context: Context,
        attrs: AttributeSet?,
        @AttrRes defStyleAttr: Int,
        @StyleRes defStyleRes: Int
    ) {
        context.withStyledAttributes(
            attrs,
            R.styleable.MultilineInputView,
            defStyleAttr,
            defStyleRes
        ) {
            with(property) {
                maxLines = getInt(
                    R.styleable.MultilineInputView_inputView_maxLines,
                    DEFAULT_MAX_LINES
                )
                minLines = getInt(
                    R.styleable.MultilineInputView_inputView_minLines,
                    DEFAULT_MIN_LINES
                )
                iconText = getString(R.styleable.MultilineInputView_inputView_rightIconText)
                iconColor = getColor(
                    R.styleable.MultilineInputView_inputView_rightIconColor,
                    IconColor.LABEL.getValue(context)
                )
            }
        }
    }

    internal data class PropertyHolder(
        var minLines: Int = DEFAULT_MIN_LINES,
        var maxLines: Int = DEFAULT_MAX_LINES,
        var iconText: String? = null,
        @ColorInt
        var iconColor: Int = Color.MAGENTA
    )

    companion object {
        // Стандартные значения
        const val DEFAULT_MIN_LINES = 1
        const val DEFAULT_MAX_LINES = Int.MAX_VALUE
    }
}