package ru.tensor.sbis.pin_code.view

import android.annotation.SuppressLint
import android.content.Context
import android.text.Editable
import android.text.InputType.TYPE_CLASS_NUMBER
import android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.text.buildSpannedString
import androidx.core.text.inSpans
import androidx.lifecycle.coroutineScope
import androidx.lifecycle.findViewTreeLifecycleOwner
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.tensor.sbis.design.TypefaceManager
import ru.tensor.sbis.design.theme.global_variables.StyleColor
import ru.tensor.sbis.pin_code.R

/**
 * Вью поля ввода кода, ограниченного по количеству символов и с точками.
 *
 * @author mb.kruglova
 */
class BubbleLimitedInputView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = com.google.android.material.R.attr.editTextStyle
) : AppCompatEditText(context, attrs, defStyleAttr), TextWatcher {

    internal var dispatcher: CoroutineDispatcher = Dispatchers.Main
    internal var scope: CoroutineScope? = null

    private var oldDigits: String? = null

    private val digits get() = text?.filter { it.isDigit() }.toString()

    private fun createBubbleStr(length: Int) = BUBBLE.repeat(length)

    /**
     * Скрывать ли вводимые пользователем цифры.
     */
    private var isMaskedCode: Boolean = false

    /**
     * Использовать цифровую клавиатура.
     */
    private var isNumericKeyboard: Boolean = false

    /**
     * Максимальная длина ввода пин-кода.
     */
    private var maxLength: Int = DEFAULT_PIN_CODE_LENGTH

    /**
     * Слушатель вызываемый по достижению максимальной длины ввода кода.
     */
    var maxLengthReachedListener: (() -> Unit)? = null

    /**
     * Задает тип ввода пин-кода с ограничением по максимальной длине и окончательно визуализирует поле ввода.
     *
     * [isMaskedCode] скрывать ли вводимые пользователем цифры.
     * [isNumericKeyboard] использовать цифровую клавиатуру.
     * [maxLength] максимальная длина ввода пин-кода.
     */
    fun setInputTypeAndMaxLength(
        isMaskedCode: Boolean,
        isNumericKeyboard: Boolean,
        maxLength: Int
    ) {
        this.isMaskedCode = isMaskedCode
        this.maxLength = maxLength
        this.isNumericKeyboard = isNumericKeyboard

        inputType = when {
            !isNumericKeyboard -> TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            else -> TYPE_CLASS_NUMBER
        }

        layoutParams.height = resources.getDimensionPixelSize(R.dimen.pin_code_input_height)
        isCursorVisible = false
        setBackgroundResource(0)
        setTextSize(TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.pin_code_input_size))
        letterSpacing = LETTER_SPACING
        gravity = Gravity.CENTER
        setText(applySpans(createBubbleStr(maxLength)))

        typeface = TypefaceManager.getRobotoRegularFont(context)
    }

    init {
        isFocusableInTouchMode = false
        addTextChangedListener(this)
        setTextIsSelectable(false)
        setSingleLine()
    }

    /**
     * Делает доступным ввод с клавиатуры и отображает ее.
     * Необходимо чтобы клавиатура не показывалась до момента завершения анимации отображения контента иначе контент будет за клавиатурой.
     */
    fun activateInput() {
        setInputTypeAndMaxLength(isMaskedCode, isNumericKeyboard, maxLength)
        isFocusableInTouchMode = true
        requestFocus()
    }

    @SuppressLint("MissingSuperCall")
    override fun onSelectionChanged(selStart: Int, selEnd: Int) = setSelection(digits.length)

    override fun onTextChanged(text: CharSequence?, start: Int, lengthBefore: Int, lengthAfter: Int) = Unit

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        oldDigits = s?.filter { it.isDigit() }.toString()
    }

    override fun afterTextChanged(s: Editable) {
        removeTextChangedListener(this)
        setText(applySpans(s.toString()))
        addTextChangedListener(this)

        if (digits.length == maxLength && oldDigits != digits && !oldDigits.isNullOrBlank()) {
            // небольшая задержка, чтобы можно было увидеть последнее введенное значение
            delayOn(CONFIRMATION_DELAY_MS) {
                maxLengthReachedListener?.invoke()
            }
        }
    }

    private fun applySpans(text: String): CharSequence {
        val digits = text.filter { it.isDigit() }.take(maxLength)
        val bubbles = createBubbleStr((maxLength - digits.length))

        return buildSpannedString {
            (digits + bubbles).forEach { inSpans(createSpan()) { append(it) } }
        }
    }

    private fun createSpan() = CodeSpan(
        StyleColor.UNACCENTED.getTextColor(context),
        resources.getDimensionPixelSize(R.dimen.pin_code_letter_width),
        resources.getDimension(R.dimen.pin_code_bubble_radius),
        isPrivate = isMaskedCode
    )

    /** @SelfDocumented */
    private fun delayOn(
        duration: Long,
        block: () -> Unit
    ) {
        (scope ?: findViewTreeLifecycleOwner()?.lifecycle?.coroutineScope)?.launch(dispatcher) {
            delay(duration)
            block()
        }
    }
}

internal const val BUBBLE = "•"
private const val DEFAULT_PIN_CODE_LENGTH = 4
private const val LETTER_SPACING = 0.3f
private const val CONFIRMATION_DELAY_MS = 100L
