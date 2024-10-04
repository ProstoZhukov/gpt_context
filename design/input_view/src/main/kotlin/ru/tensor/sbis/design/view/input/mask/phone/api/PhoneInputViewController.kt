package ru.tensor.sbis.design.view.input.mask.phone.api

import android.util.AttributeSet
import androidx.core.content.withStyledAttributes
import org.apache.commons.lang3.StringUtils
import ru.tensor.sbis.design.utils.delegateNotEqual
import ru.tensor.sbis.design.view.input.R
import ru.tensor.sbis.design.view.input.base.BaseInputView
import ru.tensor.sbis.design.view.input.base.utils.UpdateState
import ru.tensor.sbis.design.view.input.mask.phone.PhoneFormat
import ru.tensor.sbis.design.view.input.mask.phone.PhoneInputDialerKeyListener
import ru.tensor.sbis.design.view.input.mask.phone.PhoneInputViewTextWatcher
import ru.tensor.sbis.design.view.input.text.api.single_line.SingleLineInputViewController
import ru.tensor.sbis.design.view.input.text.api.single_line.SingleLineInputViewControllerApi
import ru.tensor.sbis.design.view.input.utils.addFirstListenerBeforeSecondListener

/**
 * Класс для управления состоянием и внутренними компонентами поля ввода телефона.
 *
 * @author ps.smirnyh
 */
internal class PhoneInputViewController(
    private val singleLineInputViewController: SingleLineInputViewController = SingleLineInputViewController()
) : SingleLineInputViewControllerApi by singleLineInputViewController, PhoneInputViewApi {

    private val phoneTextWatcher: PhoneInputViewTextWatcher by lazy {
        PhoneInputViewTextWatcher(baseInputView)
    }

    override var phoneFormat: PhoneFormat
        get() = phoneTextWatcher.phoneFormat
        set(value) {
            if (phoneFormat == value) return
            phoneTextWatcher.phoneFormat = value
        }

    override var areaCode: UShort by delegateNotEqual(RUSSIA_COUNTRY_CODE) { _ ->
        updateHintCallback.onChange()
    }

    override fun attach(
        baseInputView: BaseInputView,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) {
        singleLineInputViewController.attach(baseInputView, attrs, defStyleAttr, defStyleRes)
        updateHintCallback = UpdateState {
            updateInputViewHint(inputView.isFocused)
        }
        updateFocusCallback = UpdateState {
            updateOnFocusChanged(inputView.isFocused)
        }
        context.withStyledAttributes(attrs, R.styleable.PhoneInputView, defStyleAttr, defStyleRes) {
            phoneFormat = PhoneFormat.values()[
                getInteger(
                    R.styleable.PhoneInputView_inputView_phoneFormat,
                    0
                )
            ]
        }
        placeholder = placeholder.ifBlank {
            baseInputView.resources.getString(R.string.phone_input_view_placeholder_mobile_default)
        }
        actualKeyListener = PhoneInputDialerKeyListener
        inputView.addFirstListenerBeforeSecondListener(phoneTextWatcher, valueChangedWatcher)
    }

    override fun updateInputViewHint(isFocus: Boolean) {
        var (updatePlaceholder, updateTitle) = getPlaceholderAndTitle(isFocus = isFocus, placeholder = placeholder)
        updatePlaceholder = buildString {
            append('+')
            append(areaCode)
            append(' ')
            append(updatePlaceholder)
        }.takeIf { updatePlaceholder.isNotBlank() } ?: updatePlaceholder
        updatePlaceholderAndTitle(updatePlaceholder, updateTitle)
    }

    override fun updateOnFocusChanged(isFocus: Boolean) {
        setInitValueIfInFocusAndEmpty(isFocus)
        clearInitValueIfNotInFocus(isFocus)
        updateInputViewHint(isFocus)
    }

    private fun setInitValueIfInFocusAndEmpty(isFocus: Boolean) {
        if (isFocus && value.isEmpty()) {
            value = "+$areaCode"
            setSelection(value.length)
        }
    }

    private fun clearInitValueIfNotInFocus(isFocus: Boolean) {
        if (!isFocus && "+$areaCode".contains(value)) {
            forceClear()
        }
    }

    private fun forceClear() {
        phoneTextWatcher.doWithOutFormat {
            value = StringUtils.EMPTY
        }
    }
}