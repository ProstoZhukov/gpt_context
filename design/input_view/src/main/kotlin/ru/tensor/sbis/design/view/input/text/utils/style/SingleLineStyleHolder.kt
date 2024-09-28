package ru.tensor.sbis.design.view.input.text.utils.style

import android.content.Context
import android.graphics.Color.MAGENTA
import android.util.AttributeSet
import android.view.View
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.annotation.Dimension
import androidx.annotation.StyleRes
import androidx.core.content.withStyledAttributes
import org.apache.commons.lang3.StringUtils
import ru.tensor.sbis.design.theme.global_variables.FontSize
import ru.tensor.sbis.design.theme.global_variables.IconSize
import ru.tensor.sbis.design.theme.global_variables.TextColor
import ru.tensor.sbis.design.view.input.R

/**
 * Класс для хранения стилевых значений однострочного поля ввода.
 *
 * @author ps.smirnyh
 */
internal class SingleLineStyleHolder(
    val style: StyleHolder = StyleHolder(),
    val property: PropertyHolder = PropertyHolder()
) {

    fun loadStyle(
        context: Context,
        attrs: AttributeSet?,
        @AttrRes defStyleAttr: Int,
        @StyleRes defStyleRes: Int
    ) {
        context.withStyledAttributes(attrs, R.styleable.TextInputView, defStyleAttr, defStyleRes) {
            with(style) {
                linkTextColor = getColor(
                    R.styleable.TextInputView_inputView_linkTextColor,
                    TextColor.LINK.getValue(context)
                )
                linkTextSize = getDimension(
                    R.styleable.TextInputView_inputView_linkTextSize,
                    FontSize.M.getScaleOnDimen(context)
                )
                linkIconSize = getDimension(
                    R.styleable.TextInputView_inputView_linkIconSize,
                    IconSize.X2L.getDimen(context)
                )
                fontIcon = getBoolean(R.styleable.TextInputView_inputView_fontIcon, false)
            }

            with(property) {
                linkText =
                    getString(R.styleable.TextInputView_inputView_linkText) ?: StringUtils.EMPTY
                importantForAutofill = getInt(
                    R.styleable.TextInputView_android_importantForAutofill,
                    View.IMPORTANT_FOR_ACCESSIBILITY_AUTO
                )
                autofillHints = getString(R.styleable.TextInputView_android_autofillHints)
            }
        }
    }

    internal data class StyleHolder(
        // region Colors
        @ColorInt
        var linkTextColor: Int = MAGENTA,
        // endregion

        // region Sizes
        @Dimension
        var linkTextSize: Float = 0f,
        @Dimension
        var linkIconSize: Float = 0f,
        // endregion

        var fontIcon: Boolean = false
    )

    internal data class PropertyHolder(
        var linkText: String = StringUtils.EMPTY,
        var importantForAutofill: Int = 0,
        var autofillHints: String? = null
    )
}
