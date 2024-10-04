package ru.tensor.sbis.design.view.input.selection.utils.style

import android.content.Context
import android.util.AttributeSet
import androidx.annotation.AttrRes
import androidx.annotation.StyleRes
import androidx.core.content.withStyledAttributes
import ru.tensor.sbis.design.SbisMobileIcon
import ru.tensor.sbis.design.view.input.R

/**
 * Класс для хранения стилевых значений поля ввода выбора.
 *
 * @author ps.smirnyh
 */
internal class ValueSelectionStyleHolder(
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
            R.styleable.ValueSelectionInputView,
            defStyleAttr,
            defStyleRes
        ) {
            property.iconText = getString(R.styleable.ValueSelectionInputView_inputView_iconText)
                ?: property.iconText
            property.isIconVisible = getBoolean(
                R.styleable.ValueSelectionInputView_inputView_isIconVisible,
                property.isIconVisible
            )
        }
    }

    internal data class PropertyHolder(
        var iconText: CharSequence = SbisMobileIcon.Icon.smi_menu.character.toString(),
        var isIconVisible: Boolean = true
    )
}