package ru.tensor.sbis.design.text_span.text

import android.annotation.SuppressLint
import android.content.Context
import android.os.Parcelable
import android.text.*
import android.text.method.DigitsKeyListener
import android.util.AttributeSet
import android.util.SparseArray
import android.view.*
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.appcompat.widget.AppCompatEditText
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import ru.tensor.sbis.design.text_span.R
import ru.tensor.sbis.design.R as RDesign
import ru.tensor.sbis.design.text_span.adapter.TextWatcherAdapter
import ru.tensor.sbis.design.text_span.databinding.TextSpanInputTextBoxBinding
import ru.tensor.sbis.design.utils.ThemeContextBuilder
import ru.tensor.sbis.design.utils.delegateProperty
import androidx.core.view.doOnPreDraw
import androidx.core.view.updateLayoutParams
import ru.tensor.sbis.design.text_span.text.InputTextBox.OnKeyPreImeListener
import ru.tensor.sbis.design.text_span.text.util.SeparateChildrenSavedState
import ru.tensor.sbis.design.text_span.text.util.TextChangedListener
import kotlin.math.max

private const val PHONE_ACCEPTED_SYMBOLS = "1234567890+"

/**
 * Поле ввода с возможностью добавления иконки слева ([android.R.attr.icon]), задания стандартной рамки
 * ([R.attr.InputTextBox_hasFrame]), задания состояния ошибки, предполагающего соответствующее изменение цвета фона,
 * с поддержкой кнопки сброса введённого текста ([R.attr.InputTextBox_hasClearButton]), задания слушателя событий
 * нажатия кнопок устройства ([OnKeyPreImeListener]).
 *
 * Padding сверху зависит от того, превышает ли фактическое число линий минимальное. Значение gravity зависит от
 * минимального числа строк: если оно больше 1, то используется [Gravity.TOP], иначе - [Gravity.CENTER_VERTICAL]. Если
 * задана иконка, то задаётся фон контейнера по умолчанию. Для поля ввода с минимальным числом строк не превышающим 1
 * задаётся мин. высота по умолчанию, в зависимости от наличия рамки, если иная не задана
 *
 * Имеется возможность задания layout'a, содержащего [EditText], если [AppCompatEditText] не подходит (см.
 * [R.attr.InputTextBox_editTextLayoutRes]). В этом случае, не рекомендуется указывать собственные значения атрибутов,
 * перечисленных в [R.style.InputTextBoxEditText], а также [android.R.attr.background], [android.R.attr.padding] и
 * [android.R.attr.gravity]
 *
 * - [Стандарт](http://axure.tensor.ru/MobileStandart8/#p=поля_ввода&g=1)
 * - [Документация](https://git.sbis.ru/mobileworkspace/android-design/blob/development/README_inputtextbox.md)
 *
 * @author us.bessonov
 */
@Suppress("unused")
class InputTextBox @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = R.attr.inputTextBoxStyle,
    defStyleRes: Int = R.style.InputTextBox
) : ConstraintLayout(
    ThemeContextBuilder(context, defStyleAttr = defStyleAttr, defaultStyle = defStyleRes, attrs = attrs).build(),
    attrs,
    defStyleAttr
) {

    private var edittextVerticalPaddingDefault = 0
    private var edittextTopPaddingVerticalOverflow = 0

    private var editTextTopPaddingExtra = 0
    private var edittextBottomPaddingExtra = 0

    private var hasClearButton = false
    private var isStateError = false
    private var isBackgroundWithFrame = false
    private var isDynamicMultiline = false

    private var delegateDispatchOnKeyPreImeToListener = false
    private var onKeyPreImeListener: OnKeyPreImeListener? = null

    private val viewBinding = TextSpanInputTextBoxBinding.inflate(LayoutInflater.from(getContext()), this)

    val editText: EditText

    interface OnKeyPreImeListener {
        /**
         * Callback события нажатия кнопки перед его обработкой IME
         *
         * @param keyCode код нажатой кнопки
         * @param event описание события
         * @return true если событие было обработнно и не подлежит дальнейшей передаче (имеет смысл только если вызов
         * [dispatchKeyEventPreIme] делегируется [OnKeyPreImeListener])
         */
        fun onKeyPreImeEvent(keyCode: Int, event: KeyEvent): Boolean
    }

    init {
        with(getContext().theme.obtainStyledAttributes(attrs, R.styleable.InputTextBox, 0, 0)) {
            val editTextLayoutRes = getResourceId(
                R.styleable.InputTextBox_InputTextBox_editTextLayoutRes,
                R.layout.text_span_input_text_box_default_edit_text
            )
            isDynamicMultiline = getBoolean(R.styleable.InputTextBox_InputTextBox_dynamicMultiline, false)

            editText = inflateEditText(editTextLayoutRes).apply {
                getText(R.styleable.InputTextBox_android_text)
                    ?.let { text = SpannableStringBuilder(it) }
                getColorStateList(R.styleable.InputTextBox_android_textColor)?.let { setTextColor(it) }
                hint = getText(R.styleable.InputTextBox_android_hint)
                imeOptions = getInt(R.styleable.InputTextBox_android_imeOptions, imeOptions)
                inputType = getInt(R.styleable.InputTextBox_android_inputType, inputType)
                maxLines = getInt(R.styleable.InputTextBox_android_maxLines, maxLines)
                setNewMinLines(getInt(R.styleable.InputTextBox_android_minLines, 1))
                gravity = getInt(R.styleable.InputTextBox_android_gravity, Gravity.TOP)

                getDimensionPixelSize(R.styleable.InputTextBox_InputTextBox_editTextMinHeight, -1)
                    .takeIf { it > 0 }
                    ?.let { minHeight = it }
                getDimensionPixelSize(R.styleable.InputTextBox_InputTextBox_editTextMaxHeight, -1)
                    .takeIf { it > 0 }
                    ?.let { maxHeight = it }
                getResourceId(R.styleable.InputTextBox_android_fontFamily, 0)
                    .takeIf { it != 0 }
                    ?.let { typeface = ResourcesCompat.getFont(context, it) }
                isSaveEnabled = this@InputTextBox.isSaveEnabled
                isFocusable = true
                isFocusableInTouchMode = true

                getInt(R.styleable.InputTextBox_android_maxLength, -1)
                    .takeIf { it > 0 }
                    ?.let(::setMaximumLength)
            }

            applyDynamicMultilineIfNeeded()
            setHasClearButton(getBoolean(R.styleable.InputTextBox_InputTextBox_hasClearButton, false))

            isBackgroundWithFrame = getBoolean(R.styleable.InputTextBox_InputTextBox_hasFrame, false)
            updateTextBackground()

            val iconRes = getResourceId(R.styleable.InputTextBox_android_icon, 0)
            val hasIcon = iconRes != 0
            setIcon(iconRes)
            applyDefaultBackgroundIfNeeded(hasIcon)

            applyDefaultInputMinHeightIfNeeded()
            initEditTextExtraPadding(hasIcon, paddingTop, paddingBottom)
            recycle()
        }

        initTextChangedListener()
    }

    var text by delegateProperty<CharSequence?>(editText::getText, editText::setText)
    var hint by delegateProperty<CharSequence?>(editText::getHint, editText::setHint)
    var imeOptions by delegateProperty(editText::getImeOptions, editText::setImeOptions)
    var inputType by delegateProperty(editText::getInputType, editText::setInputType)
    var editTextMinHeight by delegateProperty(editText::getMinHeight, editText::setMinHeight)
    var editTextMaxHeight by delegateProperty(editText::getMaxHeight, editText::setMaxHeight)
    var minLines by delegateProperty(editText::getMinLines, editText::setNewMinLines)
    var maxLines by delegateProperty(editText::getMaxLines, editText::setNewMaxLines)
    var gravity by delegateProperty(editText::getGravity, editText::setGravity)
    var maxLength by delegateProperty(editText::getMaximumLength, editText::setMaximumLength)

    /**
     * Задаёт id ресурса иконки слева от поля ввода
     */
    fun setIcon(
        @StringRes
        iconResId: Int
    ) {
        val hasIconRes = iconResId != 0
        if (hasIconRes) {
            viewBinding.textSpanIcon.setText(iconResId)
        }
        viewBinding.textSpanIcon.setVisible(hasIconRes)
        configurePaddings(hasIconRes)
    }

    /**
     * Задаёт, находится ли поле ввода в состоянии ошибки
     *
     * @param isStateError флаг состояния ошибки, определяющий цвет фона
     */
    fun setErrorState(isStateError: Boolean) {
        this.isStateError = isStateError
        updateTextBackground()
    }

    /**
     * Задаёт слушатель событий нажатия кнопок перед их обработкой IME
     *
     * @param onKeyPreImeListener слушатель событий нажатия кнопок
     * @param delegateDispatchOnKeyPreImeToListener должен ли вызов [dispatchKeyEventPreIme] делегироваться
     * [onKeyPreImeListener]. Если значение false, то метод [OnKeyPreImeListener.onKeyPreImeEvent] будет вызван,
     * но возвращаемое значение будет проигнорировано
     */
    @JvmOverloads
    fun setOnKeyPreImeListener(
        onKeyPreImeListener: OnKeyPreImeListener?,
        delegateDispatchOnKeyPreImeToListener: Boolean = false
    ) {
        this.onKeyPreImeListener = onKeyPreImeListener
        this.delegateDispatchOnKeyPreImeToListener = delegateDispatchOnKeyPreImeToListener
    }

    /**
     * Задаёт id ресурса текста подсказки
     */
    fun setHint(
        @StringRes
        hintResId: Int
    ) = editText.setHint(hintResId)

    /**
     * Задаёт слушатель событий изменения текста
     *
     * @see EditText.addTextChangedListener
     */
    fun addTextChangedListener(textWatcher: TextWatcher) = editText.addTextChangedListener(textWatcher)

    /**
     * Задаёт упрощённый слушатель событий изменения текста
     *
     * @param afterTextChanged лямбда, вызываемая с новым введённым текстом при его изменении
     * @return [TextWatcher], созданный на основе [afterTextChanged] и установленный посредством
     * [addTextChangedListener]
     */
    fun addTextChangedListener(afterTextChanged: (text: String) -> Unit): TextWatcher {
        return TextChangedListener(afterTextChanged)
            .also { addTextChangedListener(it) }
    }

    /**
     * Удаляет слушатель событий изменения текста
     *
     * @see EditText.removeTextChangedListener
     */
    fun removeTextChangedListener(textWatcher: TextWatcher) = editText.removeTextChangedListener(textWatcher)

    /**
     * Задаёт обработчик кликов по полю ввода.
     * Клик срабатывает вне зависимости от наличия фокуса
     *
     * @param listener обработчик кликов
     */
    @SuppressLint("ClickableViewAccessibility")
    fun setEditTextOnClickListener(listener: ((View) -> Unit)?) {
        editText.setOnTouchListener { view, motionEvent ->
            if (motionEvent.action == MotionEvent.ACTION_UP) {
                listener?.invoke(view)
                true
            } else {
                false
            }
        }
    }

    /**
     * Метод аналогичный [EditText.setSelectAllOnFocus] для указания,
     * нужно ли выделять весь текст при получении фокуса.
     */
    fun setSelectAllOnFocus(selectAllOnFocus: Boolean) {
        editText.setSelectAllOnFocus(selectAllOnFocus)
    }

    /**
     * Задаёт тип ввода, переопределяя набор допустимых для ввода символов, если тип ввода [InputType.TYPE_CLASS_PHONE],
     * позволяя в этом случае вводить только цифры и символ '+'
     *
     * @param inputType произвольный тип ввода
     */
    fun setInputTypeWithRestrictedPhoneSymbols(inputType: Int) {
        if (inputType and EditorInfo.TYPE_MASK_CLASS == InputType.TYPE_CLASS_PHONE) {
            editText.keyListener = DigitsKeyListener.getInstance(PHONE_ACCEPTED_SYMBOLS)
            editText.setRawInputType(inputType)
        } else {
            editText.inputType = inputType
        }
    }

    override fun setEnabled(enabled: Boolean) {
        super.setEnabled(enabled)
        editText.isEnabled = enabled
    }

    override fun setSaveEnabled(enabled: Boolean) {
        super.setSaveEnabled(enabled)
        editText.isSaveEnabled = enabled
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (this@InputTextBox.layoutParams?.width == LayoutParams.WRAP_CONTENT) {
            editText.updateLayoutParams { width = LayoutParams.WRAP_CONTENT }
        }

        updateTextBackgroundVerticalMargin()
    }

    override fun onSaveInstanceState(): Parcelable {
        val superState = super.onSaveInstanceState()
        val savedState = SeparateChildrenSavedState(superState)
        val childrenStates = SparseArray<Parcelable>()
        (0 until childCount)
            .forEach { getChildAt(it).saveHierarchyState(childrenStates) }
        savedState.setChildrenStates(childrenStates)
        return savedState
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        val savedState = state as? SeparateChildrenSavedState
        savedState?.let {
            super.onRestoreInstanceState(savedState.superState)
            val childrenStates = savedState.getChildrenStates()
            (0 until childCount)
                .forEach { getChildAt(it).restoreHierarchyState(childrenStates) }
        } ?: super.onRestoreInstanceState(null)

    }

    override fun dispatchSaveInstanceState(container: SparseArray<Parcelable>?) {
        dispatchFreezeSelfOnly(container)
    }

    override fun setPadding(left: Int, top: Int, right: Int, bottom: Int) {
        super.setPadding(left, 0, right, 0)
        editTextTopPaddingExtra = top
        edittextBottomPaddingExtra = bottom
        updateTextPaddingVerticalAccordingToLineCount()
    }

    override fun dispatchKeyEventPreIme(event: KeyEvent?): Boolean {
        event ?: return super.dispatchKeyEventPreIme(event)

        onKeyPreImeListener?.onKeyPreImeEvent(event.keyCode, event)
            ?.takeIf { delegateDispatchOnKeyPreImeToListener }
            ?.let { return it }

        if (event.keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_UP) {
            // User has pressed Back key. So hide the keyboard and clear search view focus
            (context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager)?.let {
                it.hideSoftInputFromWindow(this.windowToken, 0)
                editText.clearFocus()
            }
        }

        return super.dispatchKeyEventPreIme(event)
    }

    override fun getBaseline() = editText.baseline

    private fun initEditTextExtraPadding(
        hasIcon: Boolean,
        extraPaddingTop: Int = editTextTopPaddingExtra,
        extraPaddingBottom: Int = edittextBottomPaddingExtra
    ) {
        editTextTopPaddingExtra = extraPaddingTop.takeUnless { it == 0 && hasIcon }
            ?: resources.getDimensionPixelSize(RDesign.dimen.input_text_box_default_padding_vertical)
        edittextBottomPaddingExtra = extraPaddingBottom.takeUnless { it == 0 && hasIcon }
            ?: resources.getDimensionPixelSize(RDesign.dimen.input_text_box_default_padding_vertical)
    }

    private fun configurePaddings(hasIcon: Boolean) {
        val paddingHorizontal = if (hasIcon) {
            resources.getDimensionPixelSize(RDesign.dimen.input_text_box_with_icon_padding_horizontal)
        } else {
            0
        }

        initEditTextExtraPadding(hasIcon)

        val edittextPaddingHorizontal = resources.getDimensionPixelSize(
            if (isBackgroundWithFrame) {
                RDesign.dimen.input_text_box_with_frame_edittext_padding_horizontal
            } else {
                RDesign.dimen.input_text_box_without_frame_edittext_padding_horizontal
            }
        )
        edittextVerticalPaddingDefault = resources.getDimensionPixelSize(
            if (isBackgroundWithFrame) {
                RDesign.dimen.input_text_box_with_frame_edittext_padding_vertical
            } else {
                RDesign.dimen.input_text_box_without_frame_edittext_padding_vertical
            }
        )
        edittextTopPaddingVerticalOverflow = resources.getDimensionPixelSize(
            if (isBackgroundWithFrame) {
                RDesign.dimen.input_text_box_with_frame_edittext_padding_vertical_overflow
            } else {
                RDesign.dimen.input_text_box_without_frame_edittext_padding_vertical_overflow
            }
        )

        editText.run {
            setPadding(edittextPaddingHorizontal, paddingTop, paddingRight, paddingBottom)
            (layoutParams as? LayoutParams)?.goneEndMargin = edittextPaddingHorizontal
        }
        viewBinding.textSpanClearButton.run {
            setPadding(paddingLeft, paddingTop, edittextPaddingHorizontal, paddingBottom)
        }

        setPadding(paddingHorizontal, editTextTopPaddingExtra, paddingHorizontal, edittextBottomPaddingExtra)
    }

    private fun inflateEditText(
        @LayoutRes
        editTextLayout: Int
    ): EditText {
        // баг при использовании ViewBinding, актуален для AGP версии 4.1.0
        // https://stackoverflow.com/questions/58198298/android-3-6-viewstubproxy-unresolved-reference
        val editTextStub: ViewStub? = findViewById(R.id.text_span_edit_text_stub)
        return editTextStub?.run {
            layoutInflater = LayoutInflater.from(context)
            layoutResource = editTextLayout
            inflate() as? EditText
        } ?: throw IllegalArgumentException(
            "Корневым элементом Layout, заданного в editTextLayoutRes должен быть EditText"
        )
    }

    private fun setHasClearButton(hasClearButton: Boolean) {
        this.hasClearButton = hasClearButton
        viewBinding.textSpanClearButton.setOnClickListener {
            editText.text?.clear()
        }
        updateClearButtonVisibility()
    }

    private fun initTextChangedListener() {
        editText.addTextChangedListener(
            TextWatcherAdapter(editText) { _, _ ->
                updateMaxLinesCountIfNeeded()
                updateTextPaddingVerticalAccordingToLineCount()
                updateClearButtonVisibility()
            }
        )
    }

    private fun updateMaxLinesCountIfNeeded() {
        if (isDynamicMultiline) editText.maxLines = max(editText.lineCount, 1)
    }

    private fun applyDynamicMultilineIfNeeded() {
        if (isDynamicMultiline) {
            editText.doOnPreDraw {
                editText.maxLines = max(editText.lineCount, 1)
                editText.ellipsize = TextUtils.TruncateAt.END
                editText.inputType = editText.inputType or InputType.TYPE_TEXT_FLAG_MULTI_LINE
            }
        }
    }

    private fun updateTextPaddingVerticalAccordingToLineCount() {
        with(editText) {
            val paddingTop = editTextTopPaddingExtra +
                (
                    edittextVerticalPaddingDefault.takeUnless { lineCount > max(minLines, 1) }
                        ?: edittextTopPaddingVerticalOverflow
                    )
            val paddingBottom = edittextVerticalPaddingDefault + edittextBottomPaddingExtra

            if (this.paddingTop == paddingTop && this.paddingBottom == paddingBottom) return
            setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom)
            updateTextBackgroundVerticalMargin()
        }
    }

    private fun updateTextBackgroundVerticalMargin() {
        viewBinding.textSpanTextBackground.layoutParams =
            (viewBinding.textSpanTextBackground.layoutParams as? MarginLayoutParams)?.apply {
                topMargin = editTextTopPaddingExtra
                bottomMargin = edittextBottomPaddingExtra
            }
    }

    private fun updateClearButtonVisibility() {
        viewBinding.textSpanClearButton.setVisible(hasClearButton && !editText.text.isNullOrEmpty())
    }

    private fun updateTextBackground() {
        val backgroundRes = when {
            isBackgroundWithFrame && isStateError -> RDesign.drawable.input_text_box_background_with_frame_error
            isBackgroundWithFrame && !isStateError -> RDesign.drawable.input_text_box_background_with_frame
            isStateError -> RDesign.color.input_text_box_background_color_error
            else -> RDesign.color.input_text_box_background_color_normal
        }
        viewBinding.textSpanTextBackground.setBackgroundResource(backgroundRes)
    }

    private fun applyDefaultBackgroundIfNeeded(hasIcon: Boolean) {
        if (hasIcon && background == null) {
            setBackgroundResource(RDesign.color.input_text_box_background_color_normal)
        }
    }

    private fun applyDefaultInputMinHeightIfNeeded() {
        if (editText.minLines > 1 || editText.minHeight > 0) return
        val minHeightRes = if (isBackgroundWithFrame) {
            RDesign.dimen.input_text_box_with_frame_min_height
        } else {
            RDesign.dimen.input_text_box_without_frame_min_height
        }
        editText.minHeight = resources.getDimensionPixelSize(minHeightRes)
    }

    private fun View.setVisible(isVisible: Boolean) {
        visibility = if (isVisible) View.VISIBLE else View.GONE
    }
}

private fun EditText.setMaximumLength(maxLength: Int) {
    val filtersArr = filters.filter { it !is InputFilter.LengthFilter }.toTypedArray()
    filters = arrayOf(*filtersArr, InputFilter.LengthFilter(maxLength))
}

private fun EditText.getMaximumLength(): Int = filters
    .filterIsInstance<InputFilter.LengthFilter>()
    .firstOrNull()?.max ?: Int.MAX_VALUE

private fun EditText.setNewMinLines(minLines: Int) {
    this.minLines = minLines
    setSingleLineIfNeeded()
}

private fun EditText.setNewMaxLines(maxLines: Int) {
    this.maxLines = maxLines
    setSingleLineIfNeeded()
}

private fun EditText.setSingleLineIfNeeded() {
    if (minLines == 1 && maxLines == 1 && (inputType and EditorInfo.TYPE_MASK_CLASS == EditorInfo.TYPE_CLASS_TEXT)) {
        setSingleLine()
    }
}