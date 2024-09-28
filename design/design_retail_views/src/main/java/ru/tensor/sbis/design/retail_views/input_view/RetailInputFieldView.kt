package ru.tensor.sbis.design.retail_views.input_view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Outline
import android.text.InputFilter
import android.text.InputType
import android.text.TextUtils
import android.text.TextWatcher
import android.text.method.DigitsKeyListener
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewOutlineProvider
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.TextView
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.annotation.StringRes
import androidx.annotation.StyleRes
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isGone
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.android.schedulers.AndroidSchedulers
import ru.tensor.sbis.design.retail_views.R
import ru.tensor.sbis.design.retail_views.common.text_watcher.DecimalTextWatcher
import ru.tensor.sbis.design.retail_views.utils.amountFormat
import ru.tensor.sbis.design.retail_views.utils.applyStyle
import ru.tensor.sbis.design.retail_views.utils.kopeckCursorOffset
import ru.tensor.sbis.design.retail_views.utils.quantityDecimalCursorOffset
import ru.tensor.sbis.design.retail_views.utils.quantityFormat
import ru.tensor.sbis.design.retail_views.utils.requestFocusAndShowKeyboard
import ru.tensor.sbis.design.sbis_text_view.SbisTextView
import ru.tensor.sbis.design.text_span.text.masked.formatter.StaticFormatterImpl
import ru.tensor.sbis.design.text_span.text.masked.watcher.StaticFormatterHolder
import ru.tensor.sbis.design.theme.global_variables.FontSize
import ru.tensor.sbis.design.theme.global_variables.Offset
import ru.tensor.sbis.design.utils.getFontFromTheme
import ru.tensor.sbis.design.utils.getThemeColor

/**
 * Кастомное вью предназначенное для отображения поля ввода
 * Имеется 5 типов отображения {@link #InputFieldType}
 * Так же посредством метода {@link #setState(state: InputFieldState)} можно менять состояние поля (с редактированием, без редактирования, ошибка)
 */
class RetailInputFieldView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = R.attr.retail_views_input_field_theme,
    @StyleRes defStyleRes: Int = R.style.RetailViewsInputFieldViewStyle_Light
) : FrameLayout(context.applyStyle(defStyleAttr, defStyleRes), attrs, defStyleAttr, defStyleRes) {

    /** @SelfDocumented */
    var currentType: InputFieldType
        private set

    private var currentState: InputFieldState = InputFieldState.EDITABLE

    val editableView: EditText by lazy { findViewById(R.id.retail_views_edit_field) }
    val leftIcon: SbisTextView by lazy { findViewById(R.id.retail_views_left_icon) }
    val rightIcon2: SbisTextView by lazy { findViewById(R.id.retail_views_right_icon2) }
    val rightIcon1: SbisTextView by lazy { findViewById(R.id.retail_views_right_icon1) }
    val mainContainer: FrameLayout get() = this
    private val bottomDivider: View by lazy { findViewById(R.id.retail_views_bottom_divider) }
    private var currentTextWatcher: TextWatcher? = null
    private var integerPartMaxLength: Int? = null

    init {
        inflate(getContext(), R.layout.input_field_view, this)

        val typedArray = getContext().theme
            .obtainStyledAttributes(attrs, R.styleable.RetailViewsInputFieldSettings, defStyleAttr, defStyleRes)

        try {
            val integerPartMaxLength = typedArray.getInteger(
                R.styleable.RetailViewsInputFieldSettings_retail_integerPartMaxLength,
                -1
            )
            if (integerPartMaxLength != -1) {
                this.integerPartMaxLength = integerPartMaxLength
            }

            currentType = typedArray.getInt(
                R.styleable.RetailViewsInputFieldSettings_retailInputFieldType,
                -1
            ).toInputFieldType()
            setTypeConfig(currentType)
            setGravity(
                typedArray.getInt(R.styleable.RetailViewsInputFieldSettings_retail_inputFieldGravity, 0)
                    .toInputFieldGravity()
            )
            setSingleLineEnabled(
                typedArray.getBoolean(
                    R.styleable.RetailViewsInputFieldSettings_retail_inputFieldSingleLine,
                    true
                )
            )

            //region Icons/Text
            leftIcon.text = typedArray.getString(R.styleable.RetailViewsInputFieldSettings_retail_leftIconText)
            rightIcon2.text = typedArray.getString(R.styleable.RetailViewsInputFieldSettings_retail_rightIcon2Text)
            rightIcon1.text = typedArray.getString(R.styleable.RetailViewsInputFieldSettings_retail_rightIcon1Text)

            val iconsSize = typedArray.getDimension(R.styleable.RetailViewsInputFieldSettings_retail_iconsSize, -1f)
            if (iconsSize != -1f) {
                leftIcon.setTextSize(TypedValue.COMPLEX_UNIT_PX, iconsSize)
                rightIcon2.setTextSize(TypedValue.COMPLEX_UNIT_PX, iconsSize)
                rightIcon1.setTextSize(TypedValue.COMPLEX_UNIT_PX, iconsSize)
            }

            val textSize = typedArray.getDimension(R.styleable.RetailViewsInputFieldSettings_android_textSize, -1f)
            if (textSize != -1f) {
                editableView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize)
            }

            editableView.setText(
                typedArray.getString(R.styleable.RetailViewsInputFieldSettings_retail_editableViewText)
            )
            editableView.hint = typedArray.getString(R.styleable.RetailViewsInputFieldSettings_retail_editableViewHint)
            editableView.setSelectAllOnFocus(
                typedArray.getBoolean(
                    R.styleable.RetailViewsInputFieldSettings_android_selectAllOnFocus,
                    false
                )
            )

            val minLines = typedArray.getInteger(
                R.styleable.RetailViewsInputFieldSettings_retail_editableMinLines,
                -1
            )
            if (minLines != -1) {
                editableView.minLines = minLines
            }

            val maxLines = typedArray.getInteger(
                R.styleable.RetailViewsInputFieldSettings_retail_editableMaxLines,
                -1
            )
            if (maxLines != -1) {
                editableView.maxLines = maxLines
            }

            // Этот параметр может быть переписан если параметр inputFieldType указан как DATE или MONEY
            val androidInputType =
                typedArray.getInt(R.styleable.RetailViewsInputFieldSettings_android_inputType, Integer.MIN_VALUE)
            if (androidInputType != Integer.MIN_VALUE) {
                editableView.inputType = androidInputType
            }

            val androidMaxLength =
                typedArray.getInt(R.styleable.RetailViewsInputFieldSettings_android_maxLength, Integer.MIN_VALUE)
            if (androidMaxLength != Integer.MIN_VALUE) {
                editableView.filters =
                    arrayOf<InputFilter>(InputFilter.LengthFilter(androidMaxLength)) + editableView.filters
            }

            val androidEllipsize =
                typedArray.getInt(R.styleable.RetailViewsInputFieldSettings_android_ellipsize, Integer.MIN_VALUE)
            if (androidEllipsize != Integer.MIN_VALUE) {
                editableView.ellipsize = androidEllipsize.toTruncateAt()
            }

            val privateImeOptions =
                typedArray.getString(R.styleable.RetailViewsInputFieldSettings_android_privateImeOptions)
            if (privateImeOptions != null) {
                editableView.privateImeOptions = privateImeOptions
            }

            val isHorizontalFadingEdgeEnabled = typedArray.getBoolean(
                R.styleable.RetailViewsInputFieldSettings_retail_isHorizontalFadingEdgeEnabled,
                false
            )
            if (isHorizontalFadingEdgeEnabled) {
                editableView.run {
                    this.isHorizontalFadingEdgeEnabled = true
                    isSingleLine = true
                    ellipsize = null
                    setFadingEdgeLength(resources.getDimensionPixelSize(R.dimen.retail_views_fading_edge_length))
                }
            }
            //endregion
        } finally {
            typedArray.recycle()
        }
    }

    /**
     * Установка типа для поля ввода
     */
    fun setRetailInputFieldType(type: InputFieldType) {
        currentType = type
        setTypeConfig(type)
        setRetailInputFieldState(currentState)
    }

    /**
     * Установка плейсхолдера на поле редактирования
     */
    fun setRetailEditableViewHintRes(@StringRes hintRes: Int) {
        if (hintRes != 0) {
            editableView.setHint(hintRes)
        }
    }

    private fun setTypeConfig(type: InputFieldType) {
        if (type == InputFieldType.CUSTOM) return
        editableView.isEnabled = type.editable
        bottomDivider.visibility = type.bottomDividerViewVisibility
        leftIcon.visibility = type.leftIconViewVisibility
        rightIcon2.visibility = type.rightIcon2ViewVisibility
        rightIcon1.visibility = type.rightIcon1ViewVisibility

        editableView.setTextSize(TypedValue.COMPLEX_UNIT_PX, type.editableViewTextSize.getScaleOffDimen(context))
        rightIcon2.setTextColor(context.getColorFrom(type.rightIcon2ViewTextColorResId))

        val verticalPadding = type.textViewVerticalPadding.getDimenPx(context)
        val leftPadding = type.textViewHorizontalPadding.getDimenPx(context)
        val rightPadding =
            if (rightIcon1.isVisible || rightIcon2.isVisible) {
                0
            } else {
                leftPadding
            }

        editableView.setPadding(leftPadding, verticalPadding, rightPadding, verticalPadding)

        mainContainer.setBackgroundResource(type.mainContainerBackgroundDrawableResId)

        when (type) {
            InputFieldType.AUTOCOMPLETE, InputFieldType.SEARCH, InputFieldType.CLEARABLE_MENU -> {
                setTextObserver(editableView)
                rightIcon2.setOnClickListener { editableView.setText("") }
            }

            InputFieldType.DATE -> setupDateField()
            InputFieldType.MONEY -> setupMoneyField()
            InputFieldType.QUANTITY -> setupQuantityField()
            InputFieldType.NUMBER_DECIMAL -> setupNumberDecimalField()
            InputFieldType.NUMBER -> setupNumberField()
            InputFieldType.PASSWORD -> setupPasswordField()
            else -> Unit
        }
    }

    private fun setupMoneyField() {
        editableView.removeTextChangedListener(currentTextWatcher)

        editableView.keyListener = DigitsKeyListener.getInstance(INPUT_FIELD_NUMBERS_WITH_POINT)

        currentTextWatcher = DecimalTextWatcher(editableView, amountFormat, kopeckCursorOffset, integerPartMaxLength)

        editableView.addTextChangedListener(currentTextWatcher)
    }

    private fun setupQuantityField() {
        editableView.keyListener = DigitsKeyListener.getInstance(INPUT_FIELD_NUMBERS_WITH_POINT)

        editableView.removeTextChangedListener(currentTextWatcher)

        currentTextWatcher = DecimalTextWatcher(
            editableView,
            quantityFormat,
            quantityDecimalCursorOffset,
            integerPartMaxLength
        )

        editableView.addTextChangedListener(currentTextWatcher)
    }

    private fun setupNumberDecimalField() {
        editableView.removeTextChangedListener(currentTextWatcher)

        editableView.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
    }

    private fun setupNumberField() {
        editableView.removeTextChangedListener(currentTextWatcher)

        editableView.inputType = InputType.TYPE_CLASS_NUMBER
    }

    private fun setupDateField() {
        with(editableView) {
            removeTextChangedListener(currentTextWatcher)
            inputType = InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS

            StaticFormatterImpl("##.##.##", text)
                .run(::StaticFormatterHolder)
                .let(::addTextChangedListener)
        }
    }

    private fun setupPasswordField() {
        editableView.inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD
        // Ставим стандартный шрифт, т.к. он слетает при выставлении inputType
        editableView.typeface =
            ResourcesCompat.getFont(context, context.getFontFromTheme(R.attr.retail_views_default_retail_font))
    }

    private fun setGravity(type: InputFieldGravity) {
        editableView.gravity = type.gravity or Gravity.CENTER_VERTICAL
    }

    private fun setSingleLineEnabled(isSingleLine: Boolean) {
        editableView.isSingleLine = isSingleLine
        editableView.isNestedScrollingEnabled = !isSingleLine
        editableView.isVerticalScrollBarEnabled = !isSingleLine
    }

    @SuppressLint("CheckResult")
    private fun setTextObserver(editableView: TextView) {
        RxTextView.afterTextChangeEvents(editableView)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { rightIcon2.isGone = editableView.text.isBlank() }
    }

    override fun getBaseline(): Int {
        return when {
            editableView.visibility == View.VISIBLE -> editableView.baseline
            else -> return super.getBaseline()
        }
    }

    fun getText() = editableView.text.toString()

    fun setText(text: String?) = editableView.setText(text)

    fun setText(@StringRes strRes: Int) = editableView.setText(strRes)

    var editable: Boolean
        get() = editableView.isEnabled
        set(value) {
            setRetailInputFieldState(if (value) InputFieldState.EDITABLE else InputFieldState.READABLE)
        }

    /**
     * Данная функция позволяет менять состояние поля (правильная работа гарантируется при использовании InputFieldType кроме CUSTOM)
     */
    fun setRetailInputFieldState(state: InputFieldState) {
        val isSearchType = currentType == InputFieldType.SEARCH
        if (isSearchType || currentType == InputFieldType.MENU && state == InputFieldState.ERROR) {
            return
        }

        currentState = state

        if (state == InputFieldState.READABLE_WITH_SINGLE_LINE_AND_ELLIPSIZE_AT_END) {
            editableView.isSingleLine = true
            editableView.keyListener = null
        }

        if (state == InputFieldState.READABLE_WITH_SINGLE_LINE_AND_ELLIPSIZE_AT_END_FOR_SPINNER) {
            editableView.isSingleLine = true
        }

        editableView.isEnabled = state.editable
        editableView.isFocusable = state.focusable
        editableView.isFocusableInTouchMode = state.focusable
        bottomDivider.visibility = state.bottomDividerViewVisibility
        bottomDivider.setBackgroundColor(context.getColorFrom(state.bottomLineColorResId))

        if (currentType == InputFieldType.EDIT_WITH_LEFT_ICON) {
            leftIcon.isInvisible = state == InputFieldState.READABLE
        } else {
            leftIcon.isGone = true
        }

        if (currentType == InputFieldType.MENU) {
            rightIcon1.isInvisible = state == InputFieldState.READABLE
            editableView.isEnabled = false
        } else {
            rightIcon1.isGone = true
        }

        if (currentType == InputFieldType.EDIT_WITH_RIGHT_ICON) {
            rightIcon1.isGone = state == InputFieldState.READABLE
        }

        if (currentType == InputFieldType.AUTOCOMPLETE) {
            if (state == InputFieldState.READABLE) {
                rightIcon2.isInvisible = true
            } else {
                rightIcon2.isInvisible = editableView.text.isBlank()
            }
        } else {
            rightIcon2.isGone = true
        }

        val res = state.mainContainerBackgroundDrawableResId
        if (res != null) {
            mainContainer.setBackgroundResource(res)
        } else {
            mainContainer.background = res
        }

        if (currentType == InputFieldType.READ_HIDE_ALL) {
            bottomDivider.isVisible = false
            editableView.keyListener = null
            editableView.inputType = InputType.TYPE_NULL
            rightIcon1.isVisible = false
            rightIcon2.isVisible = false
            mainContainer.background = null
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        outlineProvider = CustomOutline(w, h)
    }

    private class CustomOutline(private var width: Int, private var height: Int) : ViewOutlineProvider() {

        override fun getOutline(view: View, outline: Outline) {
            outline.setRect(0, 0, width, height)
        }
    }

    @ColorInt
    fun Context.getColorFrom(@AttrRes colorAttr: Int): Int {
        val resId = getThemeColor(colorAttr)
        return ContextCompat.getColor(this, resId)
    }
}

enum class InputFieldType(
    val editable: Boolean = true,
    val leftIconViewVisibility: Int = View.GONE,
    val rightIcon2ViewVisibility: Int = View.GONE,
    val rightIcon1ViewVisibility: Int = View.GONE,
    val bottomDividerViewVisibility: Int = View.VISIBLE,
    val mainContainerBackgroundDrawableResId: Int = R.drawable.retail_views_input_field_editable_background,
    val editableViewTextSize: FontSize = FontSize.M,
    val rightIcon2ViewTextColorResId: Int = R.attr.retail_views_main_background_disabled_text_color,
    val textViewVerticalPadding: Offset = Offset.X2S,
    val textViewHorizontalPadding: Offset = Offset.S
) {

    /**
     * Самый простой тип поля ввода
     */
    EDIT(
        editable = true,
        leftIconViewVisibility = View.GONE,
        rightIcon2ViewVisibility = View.GONE,
        rightIcon1ViewVisibility = View.GONE,
        bottomDividerViewVisibility = View.VISIBLE,
        mainContainerBackgroundDrawableResId = R.drawable.retail_views_input_field_editable_background
    ),

    /**
     * Самый простой тип поля ввода, без рамки
     */
    EDIT_NO_BACKGROUND(mainContainerBackgroundDrawableResId = 0),

    /**
     * Тип поля ввода для использования с автодополнением,
     * предусмотрена иконка справа для возможности очистки всей строки
     * (по дефолту нажатии на rightIcon2 очищает строку)
     */
    AUTOCOMPLETE(
        editable = true,
        leftIconViewVisibility = View.GONE,
        rightIcon2ViewVisibility = View.INVISIBLE,
        rightIcon1ViewVisibility = View.GONE,
        bottomDividerViewVisibility = View.VISIBLE,
        mainContainerBackgroundDrawableResId = R.drawable.retail_views_input_field_editable_background
    ),

    /**
     * Тип поля в виде строки поиска, имеет возможность отображения с двумя иконками справа,
     * (по дефолту нажатии на rightIcon2 очищает строку)
     */
    SEARCH(
        editable = true,
        leftIconViewVisibility = View.GONE,
        rightIcon2ViewVisibility = View.GONE,
        rightIcon1ViewVisibility = View.VISIBLE,
        bottomDividerViewVisibility = View.GONE,
        mainContainerBackgroundDrawableResId = R.drawable.retail_views_input_field_searchable_background,
        editableViewTextSize = FontSize.X2L,
        rightIcon2ViewTextColorResId = R.attr.retail_views_secondary_text_color,
        textViewVerticalPadding = Offset.S
    ),

    /**
     * Простой тип поля ввода с иконкой слева
     */
    EDIT_WITH_LEFT_ICON(
        editable = true,
        leftIconViewVisibility = View.VISIBLE,
        rightIcon2ViewVisibility = View.GONE,
        rightIcon1ViewVisibility = View.GONE,
        bottomDividerViewVisibility = View.VISIBLE,
        mainContainerBackgroundDrawableResId = R.drawable.retail_views_input_field_editable_background,
        editableViewTextSize = FontSize.X2L
    ),

    /**
     * Данное поле отличается от предыдущих тем, что ввести текст с клавиатуры нельзя,
     * но предполгается для использования в виде поля отображения значений выпадающих меню
     */
    MENU(
        editable = false,
        leftIconViewVisibility = View.GONE,
        rightIcon2ViewVisibility = View.GONE,
        rightIcon1ViewVisibility = View.VISIBLE,
        bottomDividerViewVisibility = View.VISIBLE,
        mainContainerBackgroundDrawableResId = R.drawable.retail_views_input_field_editable_background
    ),

    /**
     * Отображает нередактируемый текст с полоской снизу
     */
    READ(
        editable = false,
        leftIconViewVisibility = View.GONE,
        rightIcon2ViewVisibility = View.GONE,
        rightIcon1ViewVisibility = View.GONE,
        bottomDividerViewVisibility = View.VISIBLE,
        mainContainerBackgroundDrawableResId = 0
    ),

    /**
     * Отображает только не редактируемый текст, убирая всё остальное
     */
    READ_HIDE_ALL(
        editable = false,
        leftIconViewVisibility = View.GONE,
        rightIcon2ViewVisibility = View.GONE,
        rightIcon1ViewVisibility = View.GONE,
        bottomDividerViewVisibility = View.GONE,
        mainContainerBackgroundDrawableResId = 0
    ),

    /**
     * Поле для отображения и редактирования даты
     */
    DATE(
        editable = true,
        leftIconViewVisibility = View.GONE,
        rightIcon2ViewVisibility = View.GONE,
        rightIcon1ViewVisibility = View.GONE,
        bottomDividerViewVisibility = View.GONE,
        mainContainerBackgroundDrawableResId = R.drawable.retail_views_input_field_editable_background
    ),

    MONEY(
        editable = true,
        leftIconViewVisibility = View.VISIBLE,
        rightIcon2ViewVisibility = View.GONE,
        rightIcon1ViewVisibility = View.GONE,
        bottomDividerViewVisibility = View.VISIBLE,
        mainContainerBackgroundDrawableResId = R.drawable.retail_views_input_field_editable_background
    ),

    /**
     * Данное поле отличается от предыдущих тем, что ввести текст с клавиатуры нельзя,
     * но предполгается для использования в виде поля отображения значений выпадающих меню.
     * В этом виде меню также присутствует кнопка очистки поля.
     */
    CLEARABLE_MENU(
        editable = false,
        leftIconViewVisibility = View.GONE,
        rightIcon2ViewVisibility = View.VISIBLE,
        rightIcon1ViewVisibility = View.VISIBLE,
        bottomDividerViewVisibility = View.VISIBLE,
        mainContainerBackgroundDrawableResId = R.drawable.retail_views_input_field_editable_background
    ),

    QUANTITY(
        editable = true,
        leftIconViewVisibility = View.GONE,
        rightIcon2ViewVisibility = View.GONE,
        rightIcon1ViewVisibility = View.GONE,
        bottomDividerViewVisibility = View.VISIBLE,
        mainContainerBackgroundDrawableResId = R.drawable.retail_views_input_field_editable_background
    ),

    NUMBER_DECIMAL(
        editable = true,
        leftIconViewVisibility = View.GONE,
        rightIcon2ViewVisibility = View.GONE,
        rightIcon1ViewVisibility = View.GONE,
        bottomDividerViewVisibility = View.VISIBLE,
        mainContainerBackgroundDrawableResId = R.drawable.retail_views_input_field_editable_background
    ),

    NUMBER(
        editable = true,
        leftIconViewVisibility = View.GONE,
        rightIcon2ViewVisibility = View.GONE,
        rightIcon1ViewVisibility = View.GONE,
        bottomDividerViewVisibility = View.VISIBLE,
        mainContainerBackgroundDrawableResId = R.drawable.retail_views_input_field_editable_background
    ),

    /**
     * Простой тип поля ввода с иконкой справа
     */
    EDIT_WITH_RIGHT_ICON(
        editable = true,
        leftIconViewVisibility = View.GONE,
        rightIcon2ViewVisibility = View.GONE,
        rightIcon1ViewVisibility = View.VISIBLE,
        bottomDividerViewVisibility = View.VISIBLE,
        mainContainerBackgroundDrawableResId = R.drawable.retail_views_input_field_editable_background
    ),

    /**
     * Простое поле ввода с увеличенным размером шрифта
     */
    EDIT_LARGE(editableViewTextSize = FontSize.X2L),

    PASSWORD(
        editable = true,
        leftIconViewVisibility = View.GONE,
        rightIcon2ViewVisibility = View.GONE,
        rightIcon1ViewVisibility = View.GONE,
        bottomDividerViewVisibility = View.VISIBLE,
        mainContainerBackgroundDrawableResId = R.drawable.retail_views_input_field_editable_background
    ),

    /**
     * Используется по дефолту или для возможности настроить поле иначе, чем предложено в доступных вариантах
     */
    CUSTOM
}

enum class InputFieldState(
    val editable: Boolean,
    val focusable: Boolean,
    val bottomDividerViewVisibility: Int,
    val mainContainerBackgroundDrawableResId: Int?,
    @AttrRes val bottomLineColorResId: Int
) {

    /**
     * Данное состояние обозначает возможность вносить изменения в поле ввода
     */
    EDITABLE(
        editable = true,
        focusable = true,
        bottomDividerViewVisibility = View.VISIBLE,
        mainContainerBackgroundDrawableResId = R.drawable.retail_views_input_field_editable_background,
        bottomLineColorResId = R.attr.retail_views_input_field_background_bottom_stroke_color
    ),

    /**
     * Данное состояние предназначено для отображения того, что находится в поле ввода без возможности редактирования
     */
    READABLE(
        editable = false,
        focusable = false,
        bottomDividerViewVisibility = View.VISIBLE,
        mainContainerBackgroundDrawableResId = null,
        bottomLineColorResId = R.attr.retail_views_input_field_background_main_stroke_color
    ),

    /**
     * Данное состояние предназначено для отображения того, что находится в поле ввода без возможности редактирования
     * Удаляется keyListener, устанавливается singleLine и ellipsize
     * // TODO: Не является состоянием стейта(ошибка/редактирование/...), является типом(разновидность InputFieldState.READ).
     * // TODO: Оказался здесь по ошибке и должен быть перенесён в InputFieldType\
     * // TODO: https://online.sbis.ru/opendoc.html?guid=1b00eaa0-040b-4ebd-bb2d-fd9e254e6fb3
     */
    READABLE_WITH_SINGLE_LINE_AND_ELLIPSIZE_AT_END(
        editable = false,
        focusable = false,
        bottomDividerViewVisibility = View.VISIBLE,
        mainContainerBackgroundDrawableResId = null,
        bottomLineColorResId = R.attr.retail_views_input_field_background_main_stroke_color
    ),

    /** Состояние предназначенное для RetailSpinnerWithArrowView, чтобы впоследствии переопределить клик по нему
     *  https://online.sbis.ru/opendoc.html?guid=eb2a4428-b75f-4e43-887d-858c08cad746&client=3
     */
    READABLE_WITH_SINGLE_LINE_AND_ELLIPSIZE_AT_END_FOR_SPINNER(
        editable = true,
        focusable = true,
        bottomDividerViewVisibility = View.VISIBLE,
        mainContainerBackgroundDrawableResId = null,
        bottomLineColorResId = R.attr.retail_views_input_field_background_main_stroke_color
    ),

    /**
     * Данное состояние предназначено для отображения ошибки в поле ввода
     */
    ERROR(
        editable = true,
        focusable = true,
        bottomDividerViewVisibility = View.INVISIBLE,
        mainContainerBackgroundDrawableResId = R.drawable.retail_views_input_field_error_background,
        bottomLineColorResId = R.attr.retail_views_input_field_background_bottom_stroke_color
    )
}

private enum class InputFieldGravity(val gravity: Int) {
    START(Gravity.START),
    END(Gravity.END),
    TOP(Gravity.TOP),
    BOTTOM(Gravity.BOTTOM)
}

private fun Int.toInputFieldType(): InputFieldType =
    when (this) {
        -1 -> InputFieldType.CUSTOM
        0 -> InputFieldType.EDIT
        1 -> InputFieldType.AUTOCOMPLETE
        2 -> InputFieldType.SEARCH
        3 -> InputFieldType.EDIT_WITH_LEFT_ICON
        4 -> InputFieldType.MENU
        5 -> InputFieldType.READ
        6 -> InputFieldType.DATE
        7 -> InputFieldType.MONEY
        8 -> InputFieldType.CLEARABLE_MENU
        9 -> InputFieldType.QUANTITY
        10 -> InputFieldType.EDIT_LARGE
        11 -> InputFieldType.NUMBER_DECIMAL
        12 -> InputFieldType.NUMBER
        13 -> InputFieldType.EDIT_WITH_RIGHT_ICON
        14 -> InputFieldType.PASSWORD
        15 -> InputFieldType.EDIT_NO_BACKGROUND
        else -> throw RuntimeException(
            "You must set app:retailInputFieldType\"someType\" to RetailInputFieldView in your xml"
        )
    }

private fun Int.toInputFieldGravity(): InputFieldGravity =
    when (this) {
        0 -> InputFieldGravity.START
        1 -> InputFieldGravity.END
        2 -> InputFieldGravity.TOP
        3 -> InputFieldGravity.BOTTOM
        else -> throw RuntimeException(
            "You must set app:retail_inputFieldGravity=\"someGravity\" to RetailInputFieldView in your xml"
        )
    }

private fun Int.toTruncateAt(): TextUtils.TruncateAt? =
    when (this) {
        0 -> null
        1 -> TextUtils.TruncateAt.START
        2 -> TextUtils.TruncateAt.MIDDLE
        3 -> TextUtils.TruncateAt.END
        4 -> TextUtils.TruncateAt.MARQUEE
        else -> throw RuntimeException("Unsupported value for android:ellipsize")
    }

fun RetailInputFieldView.requestFocusAndShowKeyboardIfNeeded(isSoftKeyboardRequired: Boolean) {
    requestFocusAndShowKeyboard(this, editableView, isSoftKeyboardRequired)
}

private const val INPUT_FIELD_NUMBERS = "0123456789"
private const val INPUT_FIELD_NUMBERS_WITH_POINT = "$INPUT_FIELD_NUMBERS."