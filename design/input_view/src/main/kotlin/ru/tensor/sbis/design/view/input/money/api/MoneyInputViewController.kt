package ru.tensor.sbis.design.view.input.money.api

import android.util.AttributeSet
import android.util.TypedValue
import androidx.annotation.ColorInt
import androidx.annotation.Px
import ru.tensor.sbis.design.decorators.FontColorStyle
import ru.tensor.sbis.design.decorators.MoneyDecorator
import ru.tensor.sbis.design.decorators.number.NumberDecoratorFontColorStyle
import ru.tensor.sbis.design.decorators.number.NumberDecoratorFontSize
import ru.tensor.sbis.design.theme.res.SbisColor
import ru.tensor.sbis.design.theme.res.SbisDimen
import ru.tensor.sbis.design.view.input.base.BaseInputView
import ru.tensor.sbis.design.view.input.money.MoneyInputViewFraction
import ru.tensor.sbis.design.view.input.money.MoneyInputViewTextWatcher
import ru.tensor.sbis.design.view.input.money.toMoneyFraction
import ru.tensor.sbis.design.view.input.money.utils.style.MoneyStyleHolder
import ru.tensor.sbis.design.view.input.number.api.NumberInputViewApi
import ru.tensor.sbis.design.view.input.number.api.NumberInputViewController
import ru.tensor.sbis.design.view.input.text.api.single_line.SingleLineInputViewControllerApi
import ru.tensor.sbis.design.view.input.utils.addFirstListenerBeforeSecondListener

/**
 * Класс для управления состоянием и внутренними компонентами поля ввода денег.
 *
 * @author ps.smirnyh
 */
internal class MoneyInputViewController(
    private val numberInputViewController: NumberInputViewController = NumberInputViewController()
) : SingleLineInputViewControllerApi by numberInputViewController,
    NumberInputViewApi by numberInputViewController,
    MoneyInputViewApi {

    /**
     * Помощник с логикой денежного поля.
     */
    private lateinit var moneyTextWatcher: MoneyInputViewTextWatcher
    private val styleHolder = MoneyStyleHolder()
    private lateinit var moneyDecorator: MoneyDecorator
    private var defaultTextSize = 0f

    override var isDecorated: Boolean
        get() = moneyTextWatcher.isDecorated
        set(newValue) {
            val lastValue = moneyTextWatcher.isDecorated
            moneyTextWatcher.isDecorated = newValue
            if (lastValue != newValue) {
                updateTextSize()
                moneyTextWatcher.afterTextChanged(inputView.text)
            }
        }

    override var fraction: MoneyInputViewFraction
        get() = numberInputViewController.fractionDelegate.getValue(this, this::fraction)
            .toMoneyFraction()
        set(value) =
            numberInputViewController.fractionDelegate.setValue(
                this,
                this::fraction,
                value.toNumberFraction()
            )

    override fun attach(
        baseInputView: BaseInputView,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) {
        numberInputViewController.attach(baseInputView, attrs, defStyleAttr, defStyleRes)
        styleHolder.loadStyle(context, attrs, defStyleAttr, defStyleRes)
        with(styleHolder.style) {
            moneyDecorator = MoneyDecorator(context) {
                fontSize = NumberDecoratorFontSize.Custom(SbisDimen.Px(moneySize), SbisDimen.Px(fractionSize))
                fontColorStyle = NumberDecoratorFontColorStyle(
                    FontColorStyle.Custom(
                        SbisColor.Int(moneyColor)
                    ),
                    fractionColor?.let {
                        FontColorStyle.Custom(
                            SbisColor.Int(it)
                        )
                    }
                )
            }
        }
        moneyTextWatcher = MoneyInputViewTextWatcher(moneyDecorator, inputView, valueChangedWatcher)
        inputView.removeTextChangedListener(numberInputViewController.numberInputViewWatcher())
        numberInputViewController.numberInputViewWatcher = { moneyTextWatcher }
        inputView.addFirstListenerBeforeSecondListener(
            numberInputViewController.numberInputViewWatcher(),
            valueChangedWatcher
        )
        applyStyles()
        defaultTextSize = valueSize
        if (isDecorated) updateTextSize()
    }

    override fun setIntegerPartSize(@Px size: Int) {
        if (styleHolder.style.moneySize == size) return
        styleHolder.style.moneySize = size
        moneyDecorator.configure {
            fontSize = NumberDecoratorFontSize.Custom(
                SbisDimen.Px(size),
                SbisDimen.Px(styleHolder.style.fractionSize)
            )
        }
        inputView.setTextKeepState(moneyDecorator.formattedValue)
    }

    override fun setIntegerPartColor(@ColorInt color: Int) {
        if (styleHolder.style.moneyColor == color) return
        styleHolder.style.moneyColor = color
        moneyDecorator.configure {
            fontColorStyle = NumberDecoratorFontColorStyle(
                FontColorStyle.Custom(SbisColor.Int(color)),
                styleHolder.style.fractionColor?.let {
                    FontColorStyle.Custom(SbisColor.Int(it))
                }
            )
        }
        inputView.setTextKeepState(moneyDecorator.formattedValue)
    }

    override fun setFractionPartSize(@Px size: Int) {
        if (styleHolder.style.fractionSize == size) return
        styleHolder.style.fractionSize = size
        moneyDecorator.configure {
            fontSize = NumberDecoratorFontSize.Custom(
                SbisDimen.Px(styleHolder.style.moneySize),
                SbisDimen.Px(size)
            )
        }
        inputView.setTextKeepState(moneyDecorator.formattedValue)
    }

    override fun setFractionPartColor(@ColorInt color: Int) {
        if (styleHolder.style.fractionColor == color) return
        styleHolder.style.fractionColor = color
        moneyDecorator.configure {
            fontColorStyle = NumberDecoratorFontColorStyle(
                FontColorStyle.Custom(SbisColor.Int(styleHolder.style.moneyColor)),
                FontColorStyle.Custom(SbisColor.Int(color))
            )
        }
        inputView.setTextKeepState(moneyDecorator.formattedValue)
    }

    private fun updateTextSize() {
        val textSize = if (isDecorated) {
            styleHolder.style.moneySize.toFloat()
        } else {
            defaultTextSize
        }
        inputView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize)
    }

    private fun applyStyles() = with(styleHolder) {
        isDecorated = property.isDecorated
        fraction = property.fraction
        maxValue = property.maxValue
        minValue = property.minValue
        isShownZeroValue = property.isShownZeroValue
    }
}