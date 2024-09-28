package ru.tensor.sbis.design.view.input.mask.api

import android.text.method.DigitsKeyListener
import android.util.AttributeSet
import androidx.core.content.res.use
import ru.tensor.sbis.design.view.input.R
import ru.tensor.sbis.design.view.input.base.BaseInputView
import ru.tensor.sbis.design.view.input.base.utils.UpdateValueState
import ru.tensor.sbis.design.view.input.mask.BaseMaskInputViewTextWatcher
import ru.tensor.sbis.design.view.input.mask.WIDE_SPACE_PLACEHOLDER
import ru.tensor.sbis.design.view.input.text.api.single_line.SingleLineInputViewController
import ru.tensor.sbis.design.view.input.text.api.single_line.SingleLineInputViewControllerApi
import timber.log.Timber

/**
 * Базовый класс для управления состоянием и внутренними компонентами поля ввода с маской.
 *
 * @author ps.smirnyh
 */
internal abstract class BaseMaskInputViewController(
    private val singleLineInputViewController: SingleLineInputViewController = SingleLineInputViewController()
) : SingleLineInputViewControllerApi by singleLineInputViewController,
    BaseMaskInputViewControllerApi {

    /**
     * Применяет маску при изменении текста в поле.
     */
    private lateinit var maskTextWatcher: BaseMaskInputViewTextWatcher

    override var mask: String
        get() = maskTextWatcher.mask
        set(value) {
            if (checkMask(value)) {
                maskTextWatcher.changeMask(value, inputView.editableText)
                inputView.apply {
                    this.mask = mask
                    requestLayout()
                }
            }
        }

    /**
     * Сеттер неактуален, т.к. этот параметр определяется маской.
     * Будет выброшен warning с [UnsupportedOperationException].
     */
    override var maxLength: Int
        get() = mask.length
        set(_) {
            Timber.w(UnsupportedOperationException("Use mask to set max length"))
        }

    override fun attach(
        baseInputView: BaseInputView,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) {
        updateDigitsCallback = UpdateValueState(::setDigits)
        singleLineInputViewController.attach(baseInputView, attrs, defStyleAttr, defStyleRes)
        maskTextWatcher = context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.MaskInputView,
            defStyleAttr,
            defStyleRes
        ).use {
            createMaskTextWatcher(it, attrs, defStyleAttr, defStyleRes)
        }
        inputView.addTextChangedListener(maskTextWatcher)

        // делаем для того, чтобы базовый watcher стал последним при обработке текста
        inputView.removeTextChangedListener(valueChangedWatcher)
        inputView.addTextChangedListener(valueChangedWatcher)

        inputView.mask = mask
    }

    override fun setDigits(digits: String?) {
        digits?.let {
            actualKeyListener = DigitsKeyListener.getInstance(digits + WIDE_SPACE_PLACEHOLDER)
        }
    }

    abstract fun checkMask(mask: String): Boolean
}