package ru.tensor.sbis.design.retail_views.money_input_field

import android.content.Context
import android.graphics.Outline
import android.text.InputFilter
import android.text.TextUtils
import android.text.method.DigitsKeyListener
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.view.ViewOutlineProvider
import android.widget.EditText
import android.widget.FrameLayout
import androidx.annotation.AttrRes
import androidx.annotation.StringRes
import androidx.annotation.StyleRes
import ru.tensor.sbis.design.retail_views.R
import ru.tensor.sbis.design.retail_views.common.text_watcher.DecimalTextWatcher
import ru.tensor.sbis.design.retail_views.utils.amountFormat
import ru.tensor.sbis.design.retail_views.utils.applyStyle
import ru.tensor.sbis.design.retail_views.utils.kopeckCursorOffset
import ru.tensor.sbis.design.retail_views.utils.requestFocusAndShowKeyboard

/**
 * Кастомное вью предназначенное для отображения поля ввода денежной суммы.
 */
class MoneyInputEditableField @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = R.attr.retail_views_money_input_editable_field_theme,
    @StyleRes defStyleRes: Int = R.style.RetailViewsMoneyInputEditableFieldStyle_Retail
) : FrameLayout(context.applyStyle(defStyleAttr, defStyleRes), attrs, defStyleAttr, defStyleRes) {

    val editableView: EditText by lazy { findViewById(R.id.editable_view) }
    private val bottomDivider: View by lazy { findViewById(R.id.bottom_divider) }
    private var integerPartMaxLength: Int? = null

    init {
        inflate(getContext(), R.layout.money_input_editable_field, this)

        val typedArray = getContext().theme
            .obtainStyledAttributes(
                attrs,
                R.styleable.RetailViewsMoneyInputFieldAttrs,
                defStyleAttr,
                defStyleRes
            )

        try {
            val integerPartMaxLength = typedArray.getInteger(
                R.styleable.RetailViewsMoneyInputFieldAttrs_retail_views_money_input_editable_field_integerPartMaxLength, // ktlint-disable max-line-length
                -1
            )
            if (integerPartMaxLength != -1) {
                this.integerPartMaxLength = integerPartMaxLength
            }

            val textSize =
                typedArray.getDimension(R.styleable.RetailViewsMoneyInputFieldAttrs_android_textSize, -1f)
            if (textSize != -1f) {
                editableView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize)
            }

            editableView.setText(
                typedArray.getString(
                    R.styleable.RetailViewsMoneyInputFieldAttrs_retail_views_money_input_editable_field_editableViewText
                )
            )
            editableView.hint = typedArray.getString(
                R.styleable.RetailViewsMoneyInputFieldAttrs_retail_views_money_input_editable_field_editableViewHint
            )
            editableView.setSelectAllOnFocus(
                typedArray.getBoolean(
                    R.styleable.RetailViewsMoneyInputFieldAttrs_android_selectAllOnFocus,
                    false
                )
            )

            val androidMaxLength = typedArray.getInt(
                R.styleable.RetailViewsMoneyInputFieldAttrs_android_maxLength,
                Integer.MIN_VALUE
            )
            if (androidMaxLength != Integer.MIN_VALUE) {
                editableView.filters =
                    arrayOf<InputFilter>(InputFilter.LengthFilter(androidMaxLength)) + editableView.filters
            }

            val androidEllipsize = typedArray.getInt(
                R.styleable.RetailViewsMoneyInputFieldAttrs_android_ellipsize,
                Integer.MIN_VALUE
            )
            if (androidEllipsize != Integer.MIN_VALUE) {
                editableView.ellipsize = androidEllipsize.toTruncateAt()
            }

            val privateImeOptions =
                typedArray.getString(R.styleable.RetailViewsMoneyInputFieldAttrs_android_privateImeOptions)
            if (privateImeOptions != null) {
                editableView.privateImeOptions = privateImeOptions
            }

            val isHorizontalFadingEdgeEnabled = typedArray.getBoolean(
                R.styleable.RetailViewsMoneyInputFieldAttrs_retail_views_money_input_editable_field_isHorizontalFadingEdgeEnabled, // ktlint-disable max-line-length
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

            val isEnabled = typedArray.getBoolean(
                R.styleable.RetailViewsMoneyInputFieldAttrs_android_enabled,
                true
            )
            editableView.isEnabled = isEnabled
        } finally {
            typedArray.recycle()
        }

        editableView.keyListener = DigitsKeyListener.getInstance(INPUT_FIELD_NUMBERS_WITH_POINT)
        editableView.addTextChangedListener(
            DecimalTextWatcher(editableView, amountFormat, kopeckCursorOffset, integerPartMaxLength)
        )
    }

    /**@SelfDocumented*/
    fun getText() = editableView.text.toString()

    /**@SelfDocumented*/
    fun setText(text: String?) = editableView.setText(text)

    /**@SelfDocumented*/
    fun setText(@StringRes strRes: Int) = editableView.setText(strRes)

    /**@SelfDocumented*/
    fun setHint(hint: String) {
        editableView.hint = hint
    }

    override fun isEnabled(): Boolean {
        return editableView.isEnabled
    }

    override fun setEnabled(enabled: Boolean) {
        super.setEnabled(enabled)
        setInputFieldState(if (enabled) InputFieldState.EDITABLE else InputFieldState.READABLE)
    }

    override fun getBaseline(): Int {
        return when (editableView.visibility) {
            View.VISIBLE -> editableView.baseline
            else -> return super.getBaseline()
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        outlineProvider = CustomOutline(w, h)
    }

    /** Данная функция позволяет менять состояние поля. */
    private fun setInputFieldState(state: InputFieldState) {
        editableView.isEnabled = state.editable
        editableView.isFocusable = state.focusable
        editableView.isFocusableInTouchMode = state.focusable
        bottomDivider.visibility = state.bottomDividerViewVisibility
    }

    private class CustomOutline(private var width: Int, private var height: Int) : ViewOutlineProvider() {

        override fun getOutline(view: View, outline: Outline) {
            outline.setRect(0, 0, width, height)
        }
    }

    private enum class InputFieldState(
        val editable: Boolean,
        val focusable: Boolean,
        val bottomDividerViewVisibility: Int
    ) {

        /**
         * Данное состояние обозначает возможность вносить изменения в поле ввода.
         */
        EDITABLE(
            editable = true,
            focusable = true,
            bottomDividerViewVisibility = View.VISIBLE
        ),

        /**
         * Данное состояние предназначено для отображения того, что находится в поле ввода без возможности редактирования.
         */
        READABLE(
            editable = false,
            focusable = false,
            bottomDividerViewVisibility = View.VISIBLE
        )
    }
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

/**@SelfDocumented*/
fun MoneyInputEditableField.requestFocusAndShowKeyboardIfNeeded(isSoftKeyboardRequired: Boolean) {
    requestFocusAndShowKeyboard(this, editableView, isSoftKeyboardRequired)
}

private const val INPUT_FIELD_NUMBERS = "0123456789"
private const val INPUT_FIELD_NUMBERS_WITH_POINT = "$INPUT_FIELD_NUMBERS."