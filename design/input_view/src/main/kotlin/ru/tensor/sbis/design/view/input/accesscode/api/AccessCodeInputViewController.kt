package ru.tensor.sbis.design.view.input.accesscode.api

import android.text.SpannableStringBuilder
import android.text.method.DigitsKeyListener
import android.text.style.ForegroundColorSpan
import android.util.AttributeSet
import androidx.core.graphics.ColorUtils
import ru.tensor.sbis.design.theme.global_variables.TextColor
import ru.tensor.sbis.design.view.input.R
import ru.tensor.sbis.design.view.input.accesscode.AccessCodeInputViewDecorationHelper
import ru.tensor.sbis.design.view.input.accesscode.AccessCodeInputViewWatcher
import ru.tensor.sbis.design.view.input.base.BaseInputView
import ru.tensor.sbis.design.view.input.base.utils.UpdateState
import ru.tensor.sbis.design.view.input.text.api.single_line.SingleLineInputViewController
import ru.tensor.sbis.design.view.input.text.api.single_line.SingleLineInputViewControllerApi

/**
 * Класс для управления состоянием и внутренними компонентами поля ввода кода доступа с фиксированной маской.
 *
 * @author mb.kruglova
 */
internal class AccessCodeInputViewController(
    private val singleLineInputViewController: SingleLineInputViewController = SingleLineInputViewController()
) : SingleLineInputViewControllerApi by singleLineInputViewController, AccessCodeInputViewApi {

    override var maxLengthReachedListener: ((String) -> Unit)? = null
        set(value) {
            field = value
            textWatcher.maxLengthReachedListener = value
        }

    /**
     * Помощник с логикой поля ввода кода доступа с фиксированной маской.
     */
    private lateinit var textWatcher: AccessCodeInputViewWatcher

    /**
     * Хелпер декорирования поля ввода.
     */
    private lateinit var decorationHelper: AccessCodeInputViewDecorationHelper

    override fun attach(
        baseInputView: BaseInputView,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) {
        singleLineInputViewController.attach(baseInputView, attrs, defStyleAttr, defStyleRes)
        actualKeyListener = DigitsKeyListener.getInstance("0123456789")

        initDecorationHelper()
        initInputView()
    }

    override fun updateOnFocusChanged(isFocus: Boolean) {
        singleLineInputViewController.updateOnFocusChanged(isFocus)
        if (isFocus) {
            inputView.setSelection(textWatcher.cursorPosition)
        }
    }

    private fun initDecorationHelper() {
        val maskColor = TextColor.READ_ONLY.getValue(context)
        val inputColor = TextColor.DEFAULT.getValue(context)

        decorationHelper = AccessCodeInputViewDecorationHelper(
            ForegroundColorSpan(maskColor),
            ForegroundColorSpan(ColorUtils.setAlphaComponent(inputColor, 0xB3)),
            ForegroundColorSpan(inputColor)
        )
    }

    private fun initInputView() {
        val permanentPart =
            context.resources.getString(R.string.access_code_input_view_permanent_part)
        val blankPart = context.resources.getString(R.string.access_code_input_view_blank_part)
        textWatcher = AccessCodeInputViewWatcher(inputView, decorationHelper, permanentPart)

        baseInputView.title = context.resources.getString(R.string.access_code_input_view_title)
        val initialValue = SpannableStringBuilder(permanentPart + blankPart)
        decorationHelper.setInitialColorSpan(
            initialValue,
            AccessCodeInputViewWatcher.START_POSITION
        )
        inputView.text = initialValue

        inputView.moveCursorToEndPosition = false
        inputView.requestFocus()
        updateFocusCallback = UpdateState {
            updateOnFocusChanged(inputView.isFocused)
        }

        inputView.addTextChangedListener(textWatcher)
    }
}