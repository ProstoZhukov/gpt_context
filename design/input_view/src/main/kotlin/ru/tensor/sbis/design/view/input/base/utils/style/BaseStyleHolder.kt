package ru.tensor.sbis.design.view.input.base.utils.style

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Color.MAGENTA
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.annotation.Dimension
import androidx.annotation.Px
import androidx.annotation.StyleRes
import androidx.annotation.StyleableRes
import androidx.core.content.res.ResourcesCompat
import androidx.core.content.res.ResourcesCompat.ID_NULL
import androidx.core.content.withStyledAttributes
import org.apache.commons.lang3.StringUtils
import ru.tensor.sbis.design.view.input.R
import ru.tensor.sbis.design.view.input.base.BaseInputViewMaxLengthFilter
import ru.tensor.sbis.design.R as RDesign

/**
 * Класс для хранения стилевых значений поля ввода.
 *
 * @author ps.smirnyh
 */
internal class BaseStyleHolder(
    val style: StyleHolder = StyleHolder(),
    val property: PropertyHolder = PropertyHolder()
) {
    fun loadStyle(
        context: Context,
        attrs: AttributeSet?,
        @AttrRes defStyleAttr: Int,
        @StyleRes defStyleRes: Int
    ) {
        context.withStyledAttributes(attrs, R.styleable.BaseInputView, defStyleAttr, defStyleRes) {
            // установка стиля
            with(style) {
                placeholderColor = getColor(
                    R.styleable.BaseInputView_inputView_placeholderTextColor,
                    placeholderColor
                )
                valueColor = getColor(
                    R.styleable.BaseInputView_inputView_valueColor,
                    valueColor
                )
                valueColorHighlight = getColor(
                    R.styleable.BaseInputView_inputView_valueColorHighlight,
                    valueColorHighlight
                )
                validationDefaultColor = getColor(
                    R.styleable.BaseInputView_inputView_validationDefaultColor,
                    validationDefaultColor
                )
                validationDefaultColorReadOnly = getColor(
                    R.styleable.BaseInputView_inputView_validationDefaultColorReadOnly,
                    validationDefaultColorReadOnly
                )
                validationErrorColor = getColor(
                    R.styleable.BaseInputView_inputView_validationErrorColor,
                    validationErrorColor
                )
                validationWarningColor = getColor(
                    R.styleable.BaseInputView_inputView_validationWarningColor,
                    validationWarningColor
                )
                validationSuccessColor = getColor(
                    R.styleable.BaseInputView_inputView_validationSuccessColor,
                    validationSuccessColor
                )
                validationTextDefaultColor = getColor(
                    R.styleable.BaseInputView_inputView_validationTextDefaultColor,
                    validationTextDefaultColor
                )
                validationTextErrorColor = getColor(
                    R.styleable.BaseInputView_inputView_validationTextErrorColor,
                    validationTextErrorColor
                )
                validationTextWarningColor = getColor(
                    R.styleable.BaseInputView_inputView_validationTextWarningColor,
                    validationTextWarningColor
                )
                validationTextSuccessColor = getColor(
                    R.styleable.BaseInputView_inputView_validationTextSuccessColor,
                    validationTextSuccessColor
                )
                titleColor = getColor(
                    R.styleable.BaseInputView_inputView_titleTextColor,
                    titleColor
                )
                titleColorAccent = getColor(
                    R.styleable.BaseInputView_inputView_titleTextColorAccent,
                    titleColorAccent
                )
                iconColor = getColor(
                    R.styleable.BaseInputView_inputView_iconColor,
                    iconColor
                )
                iconColorAccent = getColor(
                    R.styleable.BaseInputView_inputView_iconColorAccent,
                    iconColorAccent
                )
                clearColor = getColor(
                    R.styleable.BaseInputView_inputView_clearColor,
                    clearColor
                )
                progressColor = getColor(
                    R.styleable.BaseInputView_inputView_progressColor,
                    progressColor
                )
                valueSize = getDimensionPixelSizeInFloat(
                    R.styleable.BaseInputView_inputView_valueSize,
                    valueSize
                )
                titleSize = getDimensionPixelSizeInFloat(
                    R.styleable.BaseInputView_inputView_titleTextSize,
                    titleSize
                )
                titleSizeAccent = getDimensionPixelSizeInFloat(
                    R.styleable.BaseInputView_inputView_titleTextSizeAccent,
                    titleSizeAccent
                )
                validationSize = getDimensionPixelSizeInFloat(
                    R.styleable.BaseInputView_inputView_validationTextSize,
                    validationSize
                )
                validationSizeAccent = getDimensionPixelSizeInFloat(
                    R.styleable.BaseInputView_inputView_validationTextSizeAccent,
                    validationSizeAccent
                )
                bottomOffsetUnderLine = getDimensionPixelSizeInFloat(
                    R.styleable.BaseInputView_inputView_bottomOffsetUnderline,
                    bottomOffsetUnderLine
                )
                validationUnderlineSize = getDimensionPixelSizeInFloat(
                    R.styleable.BaseInputView_inputView_validationUnderlineSize,
                    validationUnderlineSize
                )
                validationUnderlineSizeFocus = getDimensionPixelSizeInFloat(
                    R.styleable.BaseInputView_inputView_validationUnderlineSizeFocus,
                    validationUnderlineSizeFocus
                )
                val globalAttrs = intArrayOf(
                    RDesign.attr.offset_m,
                    RDesign.attr.iconSize_2xl,
                    RDesign.attr.fontSize_3xs_scaleOff,
                    RDesign.attr.offset_s,
                    RDesign.attr.offset_xs,
                    RDesign.attr.offset_2xs,
                    RDesign.attr.inlineHeight_6xs
                )
                context.withStyledAttributes(attrs = globalAttrs) {
                    innerSpacing = getDimensionPixelSize(globalAttrs.indexOf(RDesign.attr.offset_m), innerSpacing)
                    iconViewTextSize = getDimensionPixelSizeInFloat(
                        globalAttrs.indexOf(RDesign.attr.iconSize_2xl),
                        iconViewTextSize
                    )
                    clearViewTextSize =
                        getDimensionPixelSizeInFloat(
                            globalAttrs.indexOf(RDesign.attr.fontSize_3xs_scaleOff),
                            clearViewTextSize
                        )
                    titleViewPaddingTop =
                        getDimensionPixelSize(globalAttrs.indexOf(RDesign.attr.offset_s), titleViewPaddingTop)
                    titleViewPaddingBottom =
                        getDimensionPixelSize(globalAttrs.indexOf(RDesign.attr.offset_xs), titleViewPaddingBottom)
                    validationStatusViewPaddingTop = getDimensionPixelSize(
                        globalAttrs.indexOf(RDesign.attr.offset_2xs),
                        validationStatusViewPaddingTop
                    )
                    progressSize = getDimension(globalAttrs.indexOf(RDesign.attr.inlineHeight_6xs), progressSize)
                }
                textStyle = getInt(R.styleable.BaseInputView_android_textStyle, textStyle)
                if (hasValue(R.styleable.BaseInputView_android_fontFamily)) {
                    fontFamily = ResourcesCompat.getFont(
                        context,
                        getResourceId(R.styleable.BaseInputView_android_fontFamily, ID_NULL)
                    )
                }
            }

            with(property) {
                isAccent = getBoolean(
                    R.styleable.BaseInputView_inputView_isAccent,
                    isAccent
                )
                value = getString(R.styleable.BaseInputView_inputView_value) ?: value
                maxLength = getInt(
                    R.styleable.BaseInputView_inputView_maxLength,
                    maxLength
                )
                minEms = getInt(R.styleable.BaseInputView_inputView_minEms, minEms)
                readOnly = getBoolean(
                    R.styleable.BaseInputView_inputView_readOnly,
                    readOnly
                )
                isClearVisible = getBoolean(
                    R.styleable.BaseInputView_inputView_isClearVisible,
                    isClearVisible
                )
                isProgressVisible = getBoolean(
                    R.styleable.BaseInputView_inputView_isProgressVisible,
                    isProgressVisible
                )
                placeholder =
                    getString(R.styleable.BaseInputView_inputView_placeholder) ?: placeholder
                title = getString(R.styleable.BaseInputView_inputView_title) ?: title
                isRequiredField = getBoolean(
                    R.styleable.BaseInputView_inputView_isRequiredField,
                    isRequiredField
                )
                showPlaceholderAsTitle = getBoolean(
                    R.styleable.BaseInputView_inputView_showPlaceholderAsTitle,
                    showPlaceholderAsTitle
                )
                onHideKeyboard = getBoolean(
                    R.styleable.BaseInputView_inputView_onHideKeyboard,
                    onHideKeyboard
                )
                showSoftInputOnFocus = getBoolean(
                    R.styleable.BaseInputView_inputView_showSoftInputOnFocus,
                    showSoftInputOnFocus
                )
                clearFocusOnBackPressed = getBoolean(
                    R.styleable.BaseInputView_inputView_clearFocusOnBackPressed,
                    clearFocusOnBackPressed
                )
                isSelectAllOnBeginEditing = getBoolean(
                    R.styleable.BaseInputView_inputView_isSelectAllOnBeginEditing,
                    isSelectAllOnBeginEditing
                )
                gravity = getInt(R.styleable.BaseInputView_android_gravity, gravity)
                imeOptions = getInt(R.styleable.BaseInputView_android_imeOptions, imeOptions)
                type = getString(R.styleable.BaseInputView_inputView_type)
                digits = getString(R.styleable.BaseInputView_android_digits)
                validationMaxLines = getInt(
                    R.styleable.BaseInputView_inputView_validation_maxLines,
                    validationMaxLines
                )
                nextFocusLeftId = getResourceId(R.styleable.BaseInputView_android_nextFocusLeft, View.NO_ID)
                nextFocusUpId = getResourceId(R.styleable.BaseInputView_android_nextFocusUp, View.NO_ID)
                nextFocusRightId = getResourceId(R.styleable.BaseInputView_android_nextFocusRight, View.NO_ID)
                nextFocusDownId = getResourceId(R.styleable.BaseInputView_android_nextFocusDown, View.NO_ID)
                nextFocusForwardId = getResourceId(R.styleable.BaseInputView_android_nextFocusForward, View.NO_ID)
                nextClusterForwardId = getResourceId(R.styleable.BaseInputView_android_nextClusterForward, View.NO_ID)
            }
        }
    }

    private fun TypedArray.getDimensionPixelSizeInFloat(@StyleableRes styleableRes: Int, defValue: Float): Float =
        getDimensionPixelSize(styleableRes, defValue.toInt()).toFloat()

    internal data class StyleHolder(
        // region Colors
        @ColorInt
        var placeholderColor: Int = MAGENTA,
        @ColorInt
        var valueColor: Int = MAGENTA,
        @ColorInt
        var valueColorHighlight: Int = MAGENTA,
        @ColorInt
        var validationDefaultColor: Int = MAGENTA,
        @ColorInt
        var validationDefaultColorReadOnly: Int = MAGENTA,
        @ColorInt
        var validationErrorColor: Int = MAGENTA,
        @ColorInt
        var validationWarningColor: Int = MAGENTA,
        @ColorInt
        var validationSuccessColor: Int = MAGENTA,
        @ColorInt
        var validationTextDefaultColor: Int = MAGENTA,
        @ColorInt
        var validationTextErrorColor: Int = MAGENTA,
        @ColorInt
        var validationTextWarningColor: Int = MAGENTA,
        @ColorInt
        var validationTextSuccessColor: Int = MAGENTA,
        @ColorInt
        var titleColor: Int = MAGENTA,
        @ColorInt
        var titleColorAccent: Int = MAGENTA,
        @ColorInt
        var iconColor: Int = MAGENTA,
        @ColorInt
        var iconColorAccent: Int = MAGENTA,
        @ColorInt
        var clearColor: Int = MAGENTA,
        @ColorInt
        var progressColor: Int = MAGENTA,
        // endregion

        // region Sizes
        @Px
        var valueSize: Float = 0f,
        @Px
        var titleSize: Float = 0f,
        @Px
        var titleSizeAccent: Float = 0f,
        @Px
        var validationSize: Float = 0f,
        @Px
        var validationSizeAccent: Float = 0f,
        @Px
        var bottomOffsetUnderLine: Float = 0f,
        @Px
        var validationUnderlineSize: Float = 0f,
        @Px
        var validationUnderlineSizeFocus: Float = 0f,
        @Px
        var innerSpacing: Int = 0,
        @Px
        var iconViewTextSize: Float = 0f,
        @Px
        var clearViewTextSize: Float = 0f,
        @Px
        var titleViewPaddingTop: Int = 0,
        @Px
        var titleViewPaddingBottom: Int = 0,
        @Px
        var validationStatusViewPaddingTop: Int = 0,
        @Dimension
        var progressSize: Float = 0f,
        // endregion
        var textStyle: Int = TEXT_STYLE_NORMAL,
        var fontFamily: Typeface? = null
    )

    internal data class PropertyHolder(
        var isAccent: Boolean = DEFAULT_IS_ACCENT,
        var value: String = StringUtils.EMPTY,
        var maxLength: Int = BaseInputViewMaxLengthFilter.NO_MAX_LENGTH,
        var minEms: Int = -1,
        var readOnly: Boolean = DEFAULT_READ_ONLY,
        var isClearVisible: Boolean = DEFAULT_IS_CLEAR_VISIBLE,
        var isProgressVisible: Boolean = DEFAULT_IS_PROGRESS_VISIBLE,
        var placeholder: String = StringUtils.EMPTY,
        var title: String = StringUtils.EMPTY,
        var isRequiredField: Boolean = DEFAULT_IS_REQUIRED_FIELD,
        var showPlaceholderAsTitle: Boolean = DEFAULT_SHOW_PLACEHOLDER_AS_TITLE,
        var onHideKeyboard: Boolean = DEFAULT_ON_HIDE_KEYBOARD,
        var showSoftInputOnFocus: Boolean = DEFAULT_SHOW_SOFT_INPUT_ON_FOCUS,
        var clearFocusOnBackPressed: Boolean = DEFAULT_CLEAR_FOCUS_ON_BACK_PRESSED,
        var isSelectAllOnBeginEditing: Boolean = DEFAULT_IS_SELECT_ALL_ON_BEGIN_EDITING,
        var gravity: Int = -1,
        var imeOptions: Int = EditorInfo.IME_FLAG_NO_EXTRACT_UI,
        var type: String? = null,
        var digits: String? = null,
        var validationMaxLines: Int = DEFAULT_VALIDATION_STATUS_MAX_LINES,
        var nextFocusLeftId: Int = View.NO_ID,
        var nextFocusRightId: Int = View.NO_ID,
        var nextFocusUpId: Int = View.NO_ID,
        var nextFocusDownId: Int = View.NO_ID,
        var nextFocusForwardId: Int = View.NO_ID,
        var nextClusterForwardId: Int = View.NO_ID
    )

    @Suppress("unused")
    companion object {
        // Стандартные значения
        const val DEFAULT_IS_ACCENT = true
        const val DEFAULT_READ_ONLY = false
        const val DEFAULT_SHOW_PLACEHOLDER_AS_TITLE = true
        const val DEFAULT_IS_CLEAR_VISIBLE = false
        const val DEFAULT_IS_PROGRESS_VISIBLE = false
        const val DEFAULT_IS_REQUIRED_FIELD = false
        const val DEFAULT_ON_HIDE_KEYBOARD = false
        const val DEFAULT_SHOW_SOFT_INPUT_ON_FOCUS = true
        const val DEFAULT_CLEAR_FOCUS_ON_BACK_PRESSED = false
        const val DEFAULT_VALIDATION_STATUS_MAX_LINES = 2
        const val DEFAULT_IS_SELECT_ALL_ON_BEGIN_EDITING = false

        const val TEXT_STYLE_NORMAL = Typeface.NORMAL
        const val TEXT_STYLE_BOLD = Typeface.BOLD
        const val TEXT_STYLE_ITALIC = Typeface.ITALIC
    }
}
