package ru.tensor.sbis.design.view.input.base.api

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Rect
import android.graphics.Typeface
import android.os.Build
import android.os.Looper
import android.text.InputFilter
import android.text.InputType
import android.text.method.DigitsKeyListener
import android.text.method.KeyListener
import android.text.method.MovementMethod
import android.text.method.TextKeyListener
import android.util.AttributeSet
import android.util.TypedValue
import android.view.KeyEvent
import android.view.View
import android.view.View.MeasureSpec
import android.view.View.MeasureSpec.AT_MOST
import android.view.View.MeasureSpec.EXACTLY
import android.view.ViewGroup
import androidx.annotation.ColorInt
import androidx.annotation.Px
import androidx.core.graphics.ColorUtils
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import org.apache.commons.lang3.StringUtils
import ru.tensor.sbis.design.SbisMobileIcon
import ru.tensor.sbis.design.TypefaceManager
import ru.tensor.sbis.design.custom_view_tools.TextLayout
import ru.tensor.sbis.design.custom_view_tools.utils.TextLayoutTouchManager
import ru.tensor.sbis.design.custom_view_tools.utils.getTextWidth
import ru.tensor.sbis.design.custom_view_tools.utils.safeRequestLayout
import ru.tensor.sbis.design.custom_view_tools.utils.textHeight
import ru.tensor.sbis.design.theme.zen.ZenThemeModel
import ru.tensor.sbis.design.theme.res.SbisColor
import ru.tensor.sbis.design.utils.delegateNotEqual
import ru.tensor.sbis.design.utils.extentions.setBottomPadding
import ru.tensor.sbis.design.view.input.R
import ru.tensor.sbis.design.view.input.base.BaseInputView
import ru.tensor.sbis.design.view.input.base.BaseInputViewMaxLengthFilter
import ru.tensor.sbis.design.view.input.base.BaseInputViewTextWatcher
import ru.tensor.sbis.design.view.input.base.InputViewClickListener
import ru.tensor.sbis.design.view.input.base.InputViewFocusChangedListener
import ru.tensor.sbis.design.view.input.base.InputViewTouchListener
import ru.tensor.sbis.design.view.input.base.MaskEditText
import ru.tensor.sbis.design.view.input.base.ValidationStatus
import ru.tensor.sbis.design.view.input.base.ValidationStatusAdapter
import ru.tensor.sbis.design.view.input.base.utils.InputViewAccessibilityDelegate
import ru.tensor.sbis.design.view.input.base.utils.UpdateState
import ru.tensor.sbis.design.view.input.base.utils.UpdateValueState
import ru.tensor.sbis.design.view.input.base.utils.factory.CircularProgressFactory
import ru.tensor.sbis.design.view.input.base.utils.factory.TextLayoutFactory
import ru.tensor.sbis.design.view.input.base.utils.factory.ValidationStatusAdapterFactory
import ru.tensor.sbis.design.view.input.base.utils.style.BaseStyleHolder
import ru.tensor.sbis.design.view.input.base.utils.style.BaseStyleHolder.Companion.DEFAULT_IS_ACCENT
import ru.tensor.sbis.design.view.input.base.utils.style.BaseStyleHolder.Companion.DEFAULT_SHOW_PLACEHOLDER_AS_TITLE
import kotlin.math.roundToInt
import kotlin.properties.Delegates

/**
 * Базовый класс для управления состоянием и внутренними компонентами поля ввода.
 *
 * @author ps.smirnyh
 */
internal class BaseInputViewController(
    private val styleHolder: BaseStyleHolder = BaseStyleHolder(),
    private val validationStatusAdapterFactory: ValidationStatusAdapterFactory = ValidationStatusAdapterFactory(),
    private val textLayoutFactory: TextLayoutFactory = TextLayoutFactory(),
    private val circularProgressFactory: CircularProgressFactory = CircularProgressFactory()
) : BaseInputViewApi, BaseInputViewControllerApi {

    /**
     * Текущий фильтр длины.
     */
    private val lengthFilter = BaseInputViewMaxLengthFilter()

    private val validationStatusAdapter: ValidationStatusAdapter by lazy {
        validationStatusAdapterFactory.create(styleHolder)
    }

    private var isAttached = false

    override var baseInputView: BaseInputView by Delegates.notNull()

    override val inputView: MaskEditText
        get() = baseInputView.inputView

    override val context: Context
        get() = baseInputView.context

    override var updateEllipsisCallback = UpdateState {
        updateEllipsis(inputView.isFocused)
    }

    override var updatePropertyCallback = UpdateState {
        updateOnPropertiesChanged()
    }

    override var updateHintCallback = UpdateState {
        updateInputViewHint(inputView.isFocused)
    }

    override var updateFocusCallback = UpdateState {
        updateOnFocusChanged(inputView.isFocused)
    }

    override var updateDigitsCallback = UpdateValueState(::setDigits)

    override var onHideKeyboard = false
        set(value) {
            if (value == field) return
            field = value
            updateHideKeyboard(field)
        }

    override var showSoftInputOnFocus = true
        set(value) {
            if (value == field) return
            field = value
            inputView.showSoftInputOnFocus = field
        }

    override var isExpandedTitle: Boolean
        get() = titleView.maxLines != MAX_LINES_TITLE
        set(value) {
            if (titleView.configure { maxLines = if (value) Int.MAX_VALUE else MAX_LINES_TITLE }) {
                baseInputView.safeRequestLayout()
            }
        }

    override var movementMethod: MovementMethod?
        get() = inputView.movementMethod
        set(value) {
            if (value == movementMethod) return
            inputView.movementMethod = value
        }

    override val innerSpacing: Int
        get() = styleHolder.style.innerSpacing

    override var underlineColorStateList: ColorStateList? = null

    override val iconView: TextLayout by lazy {
        textLayoutFactory.create {
            paint.typeface = TypefaceManager.getSbisMobileIconTypeface(context)
            paint.textSize = styleHolder.style.iconViewTextSize
            isVisible = false
        }.apply { id = R.id.input_view_icon }
    }

    override val clearView: TextLayout by lazy {
        textLayoutFactory.create {
            text = SbisMobileIcon.Icon.smi_navBarClose.character.toString()
            paint.typeface = TypefaceManager.getSbisMobileIconTypeface(context)
            paint.textSize = styleHolder.style.clearViewTextSize
            isVisible = false
            padding = TextLayout.TextLayoutPadding(start = innerSpacing, end = innerSpacing)
        }.apply { id = R.id.input_view_clear }
    }
    override val titleView: TextLayout by lazy {
        textLayoutFactory.create {
            paint.typeface = TypefaceManager.getRobotoRegularFont(context)
            padding = TextLayout.TextLayoutPadding(
                top = styleHolder.style.titleViewPaddingTop,
                bottom = styleHolder.style.titleViewPaddingBottom
            )
            maxLines = MAX_LINES_TITLE
        }.apply {
            id = R.id.input_view_title
            setOnClickListener { _, textLayout ->
                isExpandedTitle = true
                textLayout.setOnClickListener(null)
            }
        }
    }
    override val validationStatusView: TextLayout by lazy {
        textLayoutFactory.create {
            paint.typeface = TypefaceManager.getRobotoRegularFont(context)
            padding = TextLayout.TextLayoutPadding(
                top = styleHolder.style.validationStatusViewPaddingTop
            )
            minLines = 1
            maxLines = 2
        }.apply { id = R.id.input_view_validation_status }
    }

    override val touchManager: TextLayoutTouchManager by lazy {
        TextLayoutTouchManager(baseInputView, titleView, clearView, iconView)
    }

    override val progressView: CircularProgressDrawable by lazy {
        circularProgressFactory.create(context, baseInputView).apply {
            val progressSize = styleHolder.style.progressSize
            strokeWidth = context.resources.getDimension(R.dimen.input_view_progress_stroke_width)
            centerRadius = progressSize / 2 - strokeWidth
            bounds = Rect(0, 0, progressSize.toInt(), progressSize.toInt())
            setColorSchemeColors(styleHolder.style.progressColor)
        }
    }

    override val clickListener: InputViewClickListener by lazy {
        InputViewClickListener(baseInputView)
    }

    override val touchListener: InputViewTouchListener by lazy {
        InputViewTouchListener(baseInputView)
    }

    override val focusChangedListener: InputViewFocusChangedListener by lazy {
        InputViewFocusChangedListener(baseInputView) { updateFocusCallback.onChange() }
    }

    override val valueChangedWatcher: BaseInputViewTextWatcher by lazy {
        BaseInputViewTextWatcher(
            baseInputView = baseInputView,
            updateEllipsize = { updateEllipsisCallback.onChange() },
            updateInputView = { updatePropertyCallback.onChange() }
        )
    }

    override var actualKeyListener: KeyListener = TextKeyListener.getInstance()
        set(value) {
            field = value
            inputView.keyListener = value
        }

    override var value: CharSequence
        get() = inputView.text?.toString() ?: StringUtils.EMPTY
        set(value) {
            if (value.toString() == inputView.text.toString()) return
            inputView.setText(value)
            // При программном изменении значения переносить подсказку в заголовок как при фокусе
            updateHintCallback.onChange()
        }

    override var onValueChanged: ((view: BaseInputView, value: String) -> Unit)?
        get() = valueChangedWatcher.onValueChanged
        set(value) {
            valueChangedWatcher.onValueChanged = value
        }

    override var maxLength: Int
        get() = lengthFilter.maxLength
        set(newValue) {
            lengthFilter.maxLength = newValue
        }

    override var readOnly: Boolean
        get() = !inputView.isEnabled
        set(newValue) {
            val lastValue = readOnly
            if (lastValue != newValue) {
                inputView.isEnabled = !newValue
                baseInputView.isFocusable = !newValue
                baseInputView.refreshDrawableState()
                updatePropertyCallback.onChange()
            }
        }

    override var placeholder: String by delegateNotEqual(StringUtils.EMPTY) { _ ->
        updateHintCallback.onChange()
    }

    override var title: String by delegateNotEqual(StringUtils.EMPTY) { _ ->
        updateHintCallback.onChange()
    }

    override var isRequiredField: Boolean by delegateNotEqual(false) { _ ->
        updateHintCallback.onChange()
    }

    override var showPlaceholderAsTitle: Boolean = DEFAULT_SHOW_PLACEHOLDER_AS_TITLE
        set(newValue) {
            val lastValue = field
            field = newValue
            if (lastValue != newValue) {
                updateHintCallback.onChange()
            }
        }

    override var isClearVisible: Boolean = true
        set(value) {
            field = value
            if (clearView.configure { isVisible = value }) {
                updatePropertyCallback.onChange()
            }
        }

    override var isProgressVisible: Boolean by delegateNotEqual(false) { value ->
        progressView.setVisible(value, true)
        if (value) {
            if (Looper.getMainLooper().thread == Thread.currentThread()) {
                progressView.start()
            } else {
                baseInputView.post { progressView.start() }
            }
        } else {
            if (progressView.isRunning) {
                progressView.stop()
            }
        }
        updatePropertyCallback.onChange()
    }

    override var validationStatus: ValidationStatus = ValidationStatus.Default(StringUtils.EMPTY)
        set(newValue) {
            field = newValue
            validationStatusAdapter.updateValidation(newValue) { newColorStateList, newLayoutConfig ->
                var changed = false
                if (underlineColorStateList != newColorStateList) {
                    underlineColorStateList = newColorStateList
                    changed = true
                }
                changed = changed or validationStatusView.configure(checkDiffs = true, newLayoutConfig)
                if (changed) {
                    baseInputView.safeRequestLayout()
                    baseInputView.refreshDrawableState()
                }
            }
        }

    override var onFieldClickListener: View.OnClickListener?
        get() = clickListener.outer
        set(value) {
            clickListener.outer = value
        }

    override var onFieldTouchListener: View.OnTouchListener?
        get() = touchListener.outer
        set(value) {
            touchListener.outer = value
        }

    override var imeOptions: Int
        get() = inputView.imeOptions
        set(value) {
            inputView.imeOptions = value
        }

    override var inputType: Int
        get() = inputView.inputType
        set(value) {
            val typeface = inputView.typeface
            inputView.inputType = value
            /*
             * После смены inputType typeface меняется на системный, поэтому возвращаем старый.
             * https://online.sbis.ru/opendoc.html?guid=a7710b1a-26a3-4bea-9df0-63beb8cc8a70&client=3
             */
            inputView.typeface = typeface
            inputView.keyListener?.let { keyListener -> actualKeyListener = keyListener }
        }

    override var filters: Array<InputFilter>
        get() = inputView.filters
        set(value) {
            inputView.filters = Array(value.size + 1) { index ->
                if (index == 0) {
                    lengthFilter
                } else {
                    value[index - 1]
                }
            }
        }

    override var onEditorActionListener:
        ((inputView: BaseInputView, actionId: Int, event: KeyEvent?) -> Boolean)? = null
        set(value) {
            field = value
            inputView.setOnEditorActionListener(
                if (value == null) {
                    null
                } else {
                    { _, id, ev -> value(baseInputView, id, ev) }
                }
            )
        }

    override var isAccent: Boolean = DEFAULT_IS_ACCENT
        set(value) {
            if (field == value) return
            field = value
            updateAccent()
            baseInputView.safeRequestLayout()
        }

    override var isSelectAllOnBeginEditing: Boolean
        get() = inputView.isSelectAllOnBeginEditing
        set(value) {
            inputView.isSelectAllOnBeginEditing = value
        }

    override var clearFocusOnBackPressed: Boolean
        get() = inputView.clearFocusOnBackPressed
        set(value) {
            inputView.clearFocusOnBackPressed = value
        }

    override var gravity: Int
        get() = inputView.gravity
        set(value) {
            inputView.gravity = value
        }

    override var valueSize: Float
        get() = inputView.textSize
        set(value) {
            inputView.setTextSize(TypedValue.COMPLEX_UNIT_PX, value)
        }

    override var valueColor: SbisColor
        get() = SbisColor.Int(inputView.currentTextColor)
        set(value) {
            inputView.setTextColor(value.getColor(context))
        }

    override fun setSelection(index: Int) {
        inputView.setSelection(index)
    }

    override fun setBottomOffsetUnderline(offset: Int) {
        inputView.setBottomPadding(offset)
    }

    override fun getValueWidth(text: CharSequence): Int = inputView.paint.getTextWidth(text = text, byLayout = true)

    override fun getValueHeight(): Int = inputView.paint.textHeight

    override fun isInputViewFocused(): Boolean = inputView.isFocused

    @SuppressLint("ClickableViewAccessibility")
    override fun attach(
        baseInputView: BaseInputView,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) {
        this.baseInputView = baseInputView
        isAttached = true
        styleHolder.loadStyle(baseInputView.context, attrs, defStyleAttr, defStyleRes)
        applyStyles()
        baseInputView.initAccessibilityDelegate(
            InputViewAccessibilityDelegate(
                baseInputView,
                inputView,
                titleView,
                clearView,
                iconView,
                validationStatusView
            )
        )
        inputView.filters += lengthFilter
        inputView.setOnClickListener(clickListener)
        inputView.setOnTouchListener(touchListener)
        inputView.onFocusChangeListener = focusChangedListener
        inputView.addTextChangedListener(valueChangedWatcher)
        inputView.updateEllipsis = { updateEllipsisCallback.onChange() }
    }

    override fun updateInputViewHint(isFocus: Boolean) {
        val (updatePlaceholder, updateTitle) = getPlaceholderAndTitle(isFocus)

        updatePlaceholderAndTitle(updatePlaceholder, updateTitle)
    }

    override fun updatePlaceholderAndTitle(updatePlaceholder: String, updateTitle: String) {
        inputView.hint = updatePlaceholder

        if (titleView.configure(checkDiffs = true) {
                text = updateTitle
                isVisible = showPlaceholderAsTitle || title.isNotBlank()
                minHeight = titleView.getDesiredHeight()
            }
        ) {
            baseInputView.safeRequestLayout()
        }
    }

    override fun getPlaceholderAndTitle(isFocus: Boolean, placeholder: String, title: String): Pair<String, String> {
        var updatePlaceholder: String
        var updateTitle: String
        when {
            // поле в фокусе либо со значением
            (isFocus || value.isNotEmpty()) -> {
                updateTitle = title.ifBlank { placeholder }
                // если выключено перетекание и не задана метка, то оставляем подсказку пока не будет введен текст
                updatePlaceholder =
                    placeholder.takeIf { !showPlaceholderAsTitle && title.isBlank() }
                        ?: StringUtils.EMPTY
            }
            // включено перетекание, но поле без фокуса и пустое
            showPlaceholderAsTitle -> {
                updateTitle = StringUtils.EMPTY
                updatePlaceholder =
                    if (title.isNotBlank() && placeholder.isBlank()) title else placeholder
            }
            // выключено перетекание
            else -> {
                updateTitle = title.takeIf { placeholder.isNotBlank() } ?: StringUtils.EMPTY
                updatePlaceholder = placeholder.ifBlank { title }
            }
        }

        if (isRequiredField) {
            updateTitle = "$updateTitle*".takeIf { updateTitle.isNotBlank() } ?: updateTitle
            updatePlaceholder = "$updatePlaceholder*"
                .takeIf { updatePlaceholder.isNotBlank() && updateTitle.isBlank() }
                ?: updatePlaceholder
        }

        return updatePlaceholder to updateTitle
    }

    override fun updateOnFocusChanged(isFocus: Boolean) {
        updateInputViewHint(isFocus)
    }

    override fun updateEllipsis(isFocus: Boolean) = Unit

    override fun updateOnPropertiesChanged() =
        if (baseInputView.layoutParams?.width == ViewGroup.LayoutParams.WRAP_CONTENT) {
            inputView.forceLayout()
            true
        } else {
            false
        }

    override fun setDigits(digits: String?) {
        digits?.let { actualKeyListener = DigitsKeyListener.getInstance(digits) }
    }

    override fun getDefaultWidthChildMeasureSpec(@Px size: Int, parentMode: Int) =
        MeasureSpec.makeMeasureSpec(
            size,
            if (parentMode == AT_MOST) AT_MOST else EXACTLY
        )

    override fun getDefaultHeightChildMeasureSpec(@Px size: Int, parentMode: Int) =
        MeasureSpec.makeMeasureSpec(
            size,
            if (parentMode == EXACTLY) AT_MOST else parentMode
        )

    override fun setTypeface(typeface: Typeface, style: Int) {
        inputView.setTypeface(typeface, style)
    }

    override fun setZenTheme(themeModel: ZenThemeModel) {
        inputView.setTextColor(themeModel.elementsColors.defaultColor.getColor(context))
    }

    override fun onViewStateChanged(drawableState: IntArray) {
        if (!isAttached) {
            return
        }
        with(baseInputView.underlinePaint) {
            val oldStrokeWidth = strokeWidth
            val oldColor = color
            strokeWidth = if (drawableState.contains(android.R.attr.state_focused)) {
                styleHolder.style.validationUnderlineSizeFocus
            } else {
                styleHolder.style.validationUnderlineSize
            }
            underlineColorStateList?.let {
                color = it.getColorForState(drawableState, it.defaultColor)
            }
            if (oldStrokeWidth != strokeWidth || oldColor != color) {
                baseInputView.invalidate()
            }
        }
    }

    private fun applyStyles() = with(styleHolder) {
        // установка стиля
        inputView.setHintTextColor(style.placeholderColor)
        inputView.setTextColor(style.valueColor)
        inputView.highlightColor = ColorUtils.setAlphaComponent(
            style.valueColorHighlight,
            (255 * 0.3).roundToInt()
        )
        inputView.setTextSize(TypedValue.COMPLEX_UNIT_PX, style.valueSize)
        inputView.setTypeface(style.fontFamily ?: TypefaceManager.getRobotoRegularFont(context), style.textStyle)
        inputView.setBottomPadding(style.bottomOffsetUnderLine.toInt())
        validationStatus = ValidationStatus.Default(StringUtils.EMPTY)
        isAccent = property.isAccent
        // установка атрибутов
        value = property.value
        lengthFilter.maxLength = property.maxLength
        property.minEms.takeIf { it >= 0 }?.let { inputView.minEms = it }
        readOnly = property.readOnly
        isClearVisible = property.isClearVisible
        isProgressVisible = property.isProgressVisible
        placeholder = property.placeholder
        title = property.title
        isRequiredField = property.isRequiredField
        showPlaceholderAsTitle = property.showPlaceholderAsTitle
        onHideKeyboard = property.onHideKeyboard
        showSoftInputOnFocus = property.showSoftInputOnFocus
        clearFocusOnBackPressed = property.clearFocusOnBackPressed
        isSelectAllOnBeginEditing = property.isSelectAllOnBeginEditing
        property.gravity.takeIf { it >= 0 }?.let { gravity = it }
        imeOptions = property.imeOptions
        property.type?.toInputType()?.let { inputType = it }
        updateDigitsCallback.onChange(property.digits)
        validationStatusView.configure {
            maxLines = property.validationMaxLines
        }
        with(baseInputView) {
            nextFocusLeftId = property.nextFocusLeftId
            nextFocusRightId = property.nextFocusRightId
            nextFocusUpId = property.nextFocusUpId
            nextFocusDownId = property.nextFocusDownId
            nextFocusForwardId = property.nextFocusForwardId
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                nextClusterForwardId = property.nextClusterForwardId
            }
        }
        clearView.apply {
            textPaint.color = style.clearColor
            setOnClickListener { _, _ ->
                if (!readOnly) value = StringUtils.EMPTY
            }
        }
        updateAccent()
    }

    private fun updateAccent() = with(styleHolder.style) {
        @ColorInt
        val titleTextColor = if (isAccent) titleColorAccent else titleColor

        @ColorInt
        val iconViewTextColor = if (isAccent) iconColorAccent else iconColor

        @Px
        val titleTextSize = if (isAccent) titleSizeAccent else titleSize

        @Px
        val validationTextSize = if (isAccent) validationSizeAccent else validationSize

        titleView.configure {
            paint.color = titleTextColor
            paint.textSize = titleTextSize
        }
        iconView.configure {
            paint.color = iconViewTextColor
        }
        validationStatusView.configure {
            paint.textSize = validationTextSize
        }
    }

    /**
     * Отключает получение фокусу у [inputView], что позволяет обрабатывать клики, но не поднимать клавиатуру.
     * @param hideKeyboard true если не нужно поднимать клавиатуру, иначе false.
     */
    private fun updateHideKeyboard(hideKeyboard: Boolean) {
        inputView.isFocusable = !hideKeyboard
        inputView.isLongClickable = !hideKeyboard
        inputView.isFocusableInTouchMode = !hideKeyboard
        inputView.isCursorVisible = !hideKeyboard
    }

    /**
     * Конвертирует строку в [InputType].
     * Поддержано пока только number, добавлять по необходимости.
     */
    private fun String.toInputType() = when (this) {
        "number" -> InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_NORMAL
        "number_decimal" -> InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
        else -> null
    }
}

private const val MAX_LINES_TITLE = 3