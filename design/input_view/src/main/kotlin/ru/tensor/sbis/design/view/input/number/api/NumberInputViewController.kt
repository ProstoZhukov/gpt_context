package ru.tensor.sbis.design.view.input.number.api

import android.util.AttributeSet
import android.view.View
import ru.tensor.sbis.design.utils.delegateProperty
import ru.tensor.sbis.design.view.input.base.BaseInputView
import ru.tensor.sbis.design.view.input.number.NumberInputViewKeyListener
import ru.tensor.sbis.design.view.input.number.NumberInputViewWatcher
import ru.tensor.sbis.design.view.input.number.utils.style.NumberStyleHolder
import ru.tensor.sbis.design.view.input.text.api.single_line.SingleLineInputViewController
import ru.tensor.sbis.design.view.input.text.api.single_line.SingleLineInputViewControllerApi

/**
 * Класс для управления состоянием и внутренними компонентами числового поля ввода.
 *
 * @author ps.smirnyh
 */
internal class NumberInputViewController(
    private val singleLineInputViewController: SingleLineInputViewController = SingleLineInputViewController()
) : SingleLineInputViewControllerApi by singleLineInputViewController,
    NumberInputViewApi,
    NumberInputViewFractionApi,
    NumberInputViewGroupDecorationApi {

    private val styleHolder = NumberStyleHolder()
    private val watcher: NumberInputViewWatcher by lazy {
        NumberInputViewWatcher()
    }
    private val numberInputViewKeyListener by lazy {
        NumberInputViewKeyListener(inputView)
    }

    /**
     * Делегат для логики установки ограничения количества символов после точки [fraction].
     */
    internal val fractionDelegate = delegateProperty(
        {
            numberInputViewWatcher().numberFraction
        },
        { value ->
            if (fraction == value) return@delegateProperty
            numberInputViewKeyListener.allowDot = value != 0.toUByte()
            numberInputViewWatcher().numberFraction = value
            numberInputViewWatcher().onTextChanged(inputView.text, 0, 0, inputView.length())
            numberInputViewWatcher().afterTextChanged(inputView.text)
            numberInputViewKeyListener.filter(
                inputView.text,
                0,
                inputView.length(),
                inputView.text,
                0,
                inputView.length()
            )?.let(inputView::setText)

        }
    )

    /**
     * Callback для получения [NumberInputViewWatcher].
     * Может быть переопределен в наследниках.
     */
    internal var numberInputViewWatcher: () -> NumberInputViewWatcher = { watcher }

    override var minValue: Double
        get() = numberInputViewWatcher().min
        set(value) {
            numberInputViewWatcher().min = value
            // для выполнения методов обновления текста в TextWatcher
            inputView.text = inputView.text
        }

    override var maxValue: Double
        get() = numberInputViewWatcher().max
        set(value) {
            numberInputViewWatcher().max = value
            // для выполнения методов обновления текста в TextWatcher
            inputView.text = inputView.text
        }

    override var isShownZeroValue: Boolean
        get() = numberInputViewWatcher().isShownZeroValue
        set(value) {
            if (isShownZeroValue == value) return
            numberInputViewWatcher().isShownZeroValue = value
            numberInputViewWatcher().afterTextChanged(inputView.text)
            updateHintCallback.onChange()
        }

    override var fraction: UByte by fractionDelegate

    override var usesGroupingSeparator: Boolean
        get() = numberInputViewWatcher().usesGroupingSeparator
        set(value) {
            if (numberInputViewWatcher().usesGroupingSeparator == value) return
            numberInputViewWatcher().usesGroupingSeparator = value
            inputView.text = inputView.text
            updateHintCallback.onChange()
        }

    override fun attach(
        baseInputView: BaseInputView,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) {
        singleLineInputViewController.attach(baseInputView, attrs, defStyleAttr, defStyleRes)
        styleHolder.loadStyle(baseInputView.context, attrs, defStyleAttr, defStyleRes)
        applyStyles()
        inputView.addTextChangedListener(numberInputViewWatcher())
        actualKeyListener = numberInputViewKeyListener
    }

    /**
     * Метод вызывается при [View.onDetachedFromWindow] у [baseInputView].
     */
    internal fun detach() {
        numberInputViewKeyListener.inputView = null
    }

    private fun applyStyles() = with(styleHolder) {
        fraction = property.fraction
        maxValue = property.maxValue
        minValue = property.minValue
        isShownZeroValue = property.isShownZeroValue
    }
}