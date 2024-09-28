package ru.tensor.sbis.design.view.input.money.utils.style

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Color.MAGENTA
import android.util.AttributeSet
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.annotation.Px
import androidx.annotation.StyleRes
import androidx.core.content.withStyledAttributes
import ru.tensor.sbis.design.theme.global_variables.StyleColor
import ru.tensor.sbis.design.view.input.R
import ru.tensor.sbis.design.view.input.money.MoneyInputViewFraction

/**
 * Класс для хранения стилевых значений денежного поля ввода.
 *
 * @author ps.smirnyh
 */
internal class MoneyStyleHolder(
    val style: StyleHolder = StyleHolder(),
    val property: PropertyHolder = PropertyHolder()
) {

    fun loadStyle(
        context: Context,
        attrs: AttributeSet?,
        @AttrRes defStyleAttr: Int,
        @StyleRes defStyleRes: Int
    ) {
        context.withStyledAttributes(attrs, R.styleable.MoneyInputView, defStyleAttr, defStyleRes) {
            style.moneyColor = getColor(
                R.styleable.MoneyInputView_inputView_moneyColor,
                StyleColor.SECONDARY.getTextColor(context)
            )
            style.moneySize = getDimensionPixelSize(
                R.styleable.MoneyInputView_inputView_moneySize,
                style.moneySize
            )
            if (hasValue(R.styleable.MoneyInputView_inputView_fractionColor)) {
                style.fractionColor = getColor(
                    R.styleable.MoneyInputView_inputView_fractionColor,
                    MAGENTA
                )
            }
            style.fractionSize = getDimensionPixelSize(
                R.styleable.MoneyInputView_inputView_fractionSize,
                style.fractionSize
            )

            property.isDecorated = getBoolean(
                R.styleable.MoneyInputView_inputView_isDecorated,
                DEFAULT_IS_DECORATED
            )

            property.fraction = loadMoneyFraction()

            if (hasValue(R.styleable.MoneyInputView_inputView_maxValue)) {
                property.maxValue =
                    getString(R.styleable.MoneyInputView_inputView_maxValue)?.toDoubleOrNull()
                        ?: property.maxValue
            }
            if (hasValue(R.styleable.MoneyInputView_inputView_minValue)) {
                property.minValue =
                    getString(R.styleable.MoneyInputView_inputView_minValue)?.toDoubleOrNull()
                        ?: property.minValue
            }
            property.isShownZeroValue =
                getBoolean(
                    R.styleable.MoneyInputView_inputView_isShownZeroValue,
                    property.isShownZeroValue
                )
        }
    }

    private fun TypedArray.loadMoneyFraction(): MoneyInputViewFraction = when (
        val fraction = getInteger(R.styleable.MoneyInputView_inputView_moneyFraction, 0)
    ) {
        0 -> MoneyInputViewFraction.ON
        1 -> MoneyInputViewFraction.OFF
        2 -> MoneyInputViewFraction.ONLY_TENS
        else -> error("Unexpected money fraction $fraction")
    }

    internal data class StyleHolder(
        // region Colors
        @ColorInt
        var moneyColor: Int = MAGENTA,
        @ColorInt
        var fractionColor: Int? = null,
        // endregion

        // region Sizes
        @Px
        var moneySize: Int = 0,
        @Px
        var fractionSize: Int = 0
        // endregion
    )

    internal data class PropertyHolder(
        var isDecorated: Boolean = DEFAULT_IS_DECORATED,
        var fraction: MoneyInputViewFraction = MoneyInputViewFraction.ON,
        var maxValue: Double = Double.MAX_VALUE,
        var minValue: Double = 0.0,
        var isShownZeroValue: Boolean = false
    )

    companion object {
        const val DEFAULT_IS_DECORATED = false
    }
}