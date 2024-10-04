package ru.tensor.sbis.design.view.input.number.utils.style

import android.content.Context
import android.util.AttributeSet
import androidx.annotation.AttrRes
import androidx.annotation.StyleRes
import androidx.core.content.withStyledAttributes
import ru.tensor.sbis.design.view.input.R

/**
 * Класс для хранения стилевых значений числового поля ввода.
 *
 * @author ps.smirnyh
 */
internal class NumberStyleHolder(
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
            R.styleable.NumberInputView,
            defStyleAttr,
            defStyleRes
        ) {
            if (hasValue(R.styleable.NumberInputView_inputView_fraction)) {
                property.fraction =
                    getInteger(
                        R.styleable.NumberInputView_inputView_fraction,
                        property.fraction.toInt()
                    ).toUByte()
            }
            if (hasValue(R.styleable.NumberInputView_inputView_maxValue)) {
                property.maxValue =
                    getString(R.styleable.NumberInputView_inputView_maxValue)?.toDoubleOrNull()
                        ?: property.maxValue
            }
            if (hasValue(R.styleable.NumberInputView_inputView_minValue)) {
                property.minValue =
                    getString(R.styleable.NumberInputView_inputView_minValue)?.toDoubleOrNull()
                        ?: property.minValue
            }
            property.isShownZeroValue =
                getBoolean(
                    R.styleable.NumberInputView_inputView_isShownZeroValue,
                    property.isShownZeroValue
                )
        }
    }

    internal data class PropertyHolder(
        var fraction: UByte = UByte.MAX_VALUE,
        var maxValue: Double = Double.MAX_VALUE,
        var minValue: Double = -Double.MAX_VALUE,
        var isShownZeroValue: Boolean = false
    )
}