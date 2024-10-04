package ru.tensor.sbis.design.checkbox

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.text.TextUtils
import android.util.AttributeSet
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.annotation.StyleRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.withStyledAttributes
import ru.tensor.sbis.design.TypefaceManager
import ru.tensor.sbis.design.checkbox.drawers.CheckboxContentDrawer
import ru.tensor.sbis.design.checkbox.drawers.IconDrawableContentDrawer
import ru.tensor.sbis.design.checkbox.drawers.IconTextContentDrawer
import ru.tensor.sbis.design.checkbox.models.SbisCheckboxBackgroundType
import ru.tensor.sbis.design.checkbox.models.SbisCheckboxBackgroundType.*
import ru.tensor.sbis.design.checkbox.models.SbisCheckboxContent
import ru.tensor.sbis.design.checkbox.models.SbisCheckboxContent.*
import ru.tensor.sbis.design.checkbox.models.SbisCheckboxMode
import ru.tensor.sbis.design.checkbox.models.SbisCheckboxSize
import ru.tensor.sbis.design.checkbox.models.SbisCheckboxValidationState
import ru.tensor.sbis.design.checkbox.models.SbisCheckboxValue
import ru.tensor.sbis.design.checkbox.style.SbisCheckboxStyleHolder
import ru.tensor.sbis.design.custom_view_tools.TextLayout
import ru.tensor.sbis.design.custom_view_tools.utils.safeRequestLayout
import ru.tensor.sbis.design.theme.HorizontalPosition
import ru.tensor.sbis.design.theme.global_variables.FontSize
import ru.tensor.sbis.design.utils.loadEnum

/**
 * Контроллер для управления состоянием и внешним видом чекбокса [SbisCheckboxView].
 *
 * @author mb.kruglova
 */
class SbisCheckboxController : SbisCheckboxAPI {

    internal lateinit var checkbox: SbisCheckboxView
        private set

    internal var checkboxImage: Drawable? = null
        private set

    private lateinit var styleHolder: SbisCheckboxStyleHolder

    internal lateinit var textLayout: TextLayout
    internal lateinit var textLayoutValidation: TextLayout
    internal var iconDrawer: CheckboxContentDrawer? = null
    internal var accessibilityText = ""

    @ColorInt
    private var textColorFromAttr: Int = 0
    private var isMaxLinesFromAttr: Boolean = false

    @ColorInt
    private var iconColorFromAttr: Int = 0

    private val textColorStateList by lazy {
        AppCompatResources.getColorStateList(
            checkbox.context,
            R.color.checkbox_selector_color_text
        )
    }

    private val iconColorStateList by lazy {
        AppCompatResources.getColorStateList(
            checkbox.context,
            R.color.checkbox_selector_color_icon
        )
    }

    override var mode = SbisCheckboxMode.STANDARD
        set(value) {
            if (field == value) return
            field = value
            setCheckboxImage()
            checkbox.invalidate()
        }

    override var validationState: SbisCheckboxValidationState = SbisCheckboxValidationState.Default()
        set(value) {
            if (field == value) return
            field = value
            setValidationText(value)
            checkbox.refreshPresetDrawable()
            setCheckboxImage()
            checkbox.safeRequestLayout()
        }

    override var content: SbisCheckboxContent = NoContent
        set(value) {
            if (field == value) return
            field = value
            iconDrawer = null
            when (value) {
                NoContent -> {}
                is TextContent -> {
                    val isMax = if (value.isMaxLines != null) value.isMaxLines else isMaxLinesFromAttr
                    setTextContent(
                        value.text,
                        if (isMax == true) Int.MAX_VALUE else 1
                    )
                    setTextColor(value)
                }

                is IconContent -> {
                    setIconContent(value)
                    setIconColor(value)
                }
            }

            updateAccessibilityText()
            checkbox.safeRequestLayout()
        }

    override var position = HorizontalPosition.RIGHT
        set(value) {
            if (field == value) return
            field = value
            checkbox.safeRequestLayout()
        }

    override var size = SbisCheckboxSize.SMALL
        set(value) {
            if (field == value) return
            field = value
            checkbox.safeRequestLayout()
        }

    override var value: SbisCheckboxValue = SbisCheckboxValue.UNCHECKED
        set(value) {
            if (field == value) return
            field = value
            when (value) {
                SbisCheckboxValue.CHECKED -> checkbox.isCheckBoxChecked = true
                SbisCheckboxValue.UNCHECKED -> checkbox.isCheckBoxChecked = false
                SbisCheckboxValue.UNDEFINED -> {}
            }
            setCheckboxImage()
            updateAccessibilityText()
            checkbox.invalidate()
        }

    override var presetValue: SbisCheckboxValue? = null
        set(value) {
            if (field == value) return
            field = value
            checkbox.refreshPresetDrawable()
            checkbox.invalidate()
        }

    override var useVerticalOffset: Boolean = false
        set(value) {
            if (field == value) return
            field = value
            checkbox.requestLayout()
        }

    override var backgroundType = FILLED
        set(value) {
            field = value
            setCheckboxImage()
            checkbox.invalidate()
        }

    internal fun attach(
        checkbox: SbisCheckboxView,
        attrs: AttributeSet?,
        @AttrRes defStyleAttr: Int,
        @StyleRes defStyleRes: Int,
        styleHolder: SbisCheckboxStyleHolder
    ) {
        this.checkbox = checkbox
        this.styleHolder = styleHolder

        textLayout = TextLayout {
            paint.typeface = TypefaceManager.getRobotoRegularFont(checkbox.context)
            includeFontPad = false
            paint.textSize = FontSize.DEFAULT.getScaleOnDimenPx(checkbox.context).toFloat()
            ellipsize = TextUtils.TruncateAt.END
            maxLines = 1
        }

        textLayoutValidation = TextLayout {
            paint.typeface = TypefaceManager.getRobotoRegularFont(checkbox.context)
            includeFontPad = false
            paint.textSize = styleHolder.textValidationSize
            ellipsize = TextUtils.TruncateAt.END
            maxLines = Int.MAX_VALUE
        }

        val styledAttributes = R.styleable.SbisCheckbox
        checkbox.context.withStyledAttributes(attrs, styledAttributes, defStyleAttr, defStyleRes) {
            size = loadEnum(
                R.styleable.SbisCheckbox_SbisCheckbox_size,
                size,
                *SbisCheckboxSize.values()
            )

            mode = loadEnum(
                R.styleable.SbisCheckbox_SbisCheckbox_mode,
                mode,
                *SbisCheckboxMode.values()
            )

            getString(R.styleable.SbisCheckbox_SbisCheckbox_text)?.let {
                content = TextContent(it)
            }

            textColorFromAttr = getColor(R.styleable.SbisCheckbox_SbisCheckbox_textColor, 0)
            if (textColorFromAttr != 0 && content is TextContent) {
                content = (content as TextContent).copy(color = textColorFromAttr)
            }

            getString(R.styleable.SbisCheckbox_SbisCheckbox_icon)?.let {
                content = IconContent(it)
            }

            isMaxLinesFromAttr = getBoolean(R.styleable.SbisCheckbox_SbisCheckbox_isMaxLines, false)
            if (content is TextContent) {
                content = (content as TextContent).copy(isMaxLines = isMaxLinesFromAttr)
                textLayout.configure {
                    maxLines = if (isMaxLinesFromAttr) Int.MAX_VALUE else 1
                }
                checkbox.safeRequestLayout()
            }

            iconColorFromAttr = getColor(R.styleable.SbisCheckbox_SbisCheckbox_iconColor, 0)
            if (iconColorFromAttr != 0 && content is IconContent) {
                content = (content as IconContent).copy(color = iconColorFromAttr)
            }

            position = loadEnum(
                R.styleable.SbisCheckbox_SbisCheckbox_contentPosition,
                position,
                *HorizontalPosition.values()
            )

            useVerticalOffset = getBoolean(R.styleable.SbisCheckbox_SbisCheckbox_useVerticalOffset, false)

            backgroundType = loadEnum(
                R.styleable.SbisCheckbox_SbisCheckbox_backgroundType,
                backgroundType,
                *SbisCheckboxBackgroundType.values()
            )

            checkbox.isEnabled = getBoolean(R.styleable.SbisCheckbox_android_enabled, true)

            checkbox.isClickable = getBoolean(R.styleable.SbisCheckbox_android_clickable, true)
        }
    }

    internal fun setCheckboxImage() {
        when (backgroundType) {
            FILLED -> applyStyle(R.style.SbisCheckboxDefaultsTheme)
            OUTLINED -> applyStyle(R.style.SbisCheckboxOutlinedTheme)
        }

        if (this.value == SbisCheckboxValue.UNDEFINED) setUndefinedCheckboxImage()
        else setDefaultCheckboxImage()
    }

    private fun setValidationText(state: SbisCheckboxValidationState) {
        val validationText = state.text ?: return
        val color = when (state) {
            is SbisCheckboxValidationState.Default -> Color.MAGENTA
            is SbisCheckboxValidationState.Error -> styleHolder.textValidationColor
        }
        textLayoutValidation.configure {
            text = validationText
            paint.color = color
            maxLines = Int.MAX_VALUE
        }
    }

    private fun setDefaultCheckboxImage() {
        checkboxImage = when {
            value == SbisCheckboxValue.UNCHECKED &&
                validationState is SbisCheckboxValidationState.Error &&
                this.checkbox.isEnabled ->
                AppCompatResources.getDrawable(
                    this.checkbox.context,
                    R.drawable.checkbox_default_state_off_readonly_false_validation_error
                )

            mode == SbisCheckboxMode.STANDARD -> AppCompatResources.getDrawable(
                this.checkbox.context,
                R.drawable.checkbox_selector_default
            )

            mode == SbisCheckboxMode.ACCENT -> AppCompatResources.getDrawable(
                this.checkbox.context,
                R.drawable.checkbox_selector_accent
            )

            else -> null
        }

        checkbox.setButtonDrawable(checkboxImage)
        setContentColor()
    }

    private fun setUndefinedCheckboxImage() {
        val checkboxDrawableRes = when {
            !checkbox.isEnabled -> R.drawable.checkbox_default_state_null_readonly_true
            mode == SbisCheckboxMode.ACCENT -> R.drawable.checkbox_accent_state_null_readonly_false
            else -> R.drawable.checkbox_default_state_null_readonly_false
        }

        checkboxImage = AppCompatResources.getDrawable(this.checkbox.context, checkboxDrawableRes)
        checkbox.setButtonDrawable(checkboxImage)
    }

    internal fun setContentColor() {
        when (val currentContent = content) {
            is TextContent -> setTextColor(currentContent)
            is IconContent -> setIconColor(currentContent)
            NoContent -> {}
        }
    }

    internal fun getPresetCheckboxImage(): Drawable? {
        val currentPresetState = presetValue
        return currentPresetState?.let {
            when {

                value == SbisCheckboxValue.UNCHECKED && validationState is SbisCheckboxValidationState.Error ->
                    AppCompatResources.getDrawable(
                        checkbox.context,
                        R.drawable.checkbox_default_state_off_readonly_false_validation_error
                    )

                value == SbisCheckboxValue.CHECKED && it == SbisCheckboxValue.CHECKED ->
                    AppCompatResources.getDrawable(
                        checkbox.context,
                        R.drawable.checkbox_preset_state_on_readonly_false
                    )

                value == SbisCheckboxValue.UNCHECKED && it == SbisCheckboxValue.UNCHECKED ->
                    AppCompatResources.getDrawable(
                        checkbox.context,
                        R.drawable.checkbox_preset_state_off_readonly_false
                    )

                value == SbisCheckboxValue.UNDEFINED && it == SbisCheckboxValue.UNDEFINED ->
                    AppCompatResources.getDrawable(
                        checkbox.context,
                        R.drawable.checkbox_preset_state_null_readonly_false
                    )

                else -> null
            }
        }
    }

    private fun setTextColor(content: TextContent) {
        val defaultColorStateList = textColorStateList.getColorForState(
            checkbox.drawableState,
            textColorStateList.defaultColor
        )

        val color = if (checkbox.disabledAndUnchecked()) defaultColorStateList
        else content.color ?: if (textColorFromAttr != 0) textColorFromAttr else textColorStateList.defaultColor

        textLayout.configure {
            paint.color = color
        }

        checkbox.invalidate()
    }

    private fun setIconColor(content: IconContent) {
        val defaultColorStateList = iconColorStateList.getColorForState(
            checkbox.drawableState,
            iconColorStateList.defaultColor
        )

        val color = if (checkbox.disabledAndUnchecked()) defaultColorStateList
        else content.color ?: if (iconColorFromAttr != 0) iconColorFromAttr else iconColorStateList.defaultColor

        iconDrawer?.setTint(color)
    }

    private fun setTextContent(text: String?, maxLines: Int) {
        if (text == null) {
            return
        }

        textLayout.configure {
            this.text = text
            this.maxLines = maxLines
        }

        checkbox.safeRequestLayout()
    }

    private fun setIconContent(
        iconContent: IconContent
    ) {
        val iconValue = iconContent.drawable ?: iconContent.iconText

        if (iconValue == null) {
            iconDrawer = null
            return
        }

        val iconSize = styleHolder.iconSize

        val newIconDrawer = if (iconContent.drawable == null)
            iconContent.iconText?.let { IconTextContentDrawer(it, iconSize, checkbox.context) }
        else IconDrawableContentDrawer(iconContent.drawable, iconSize)

        if (newIconDrawer != iconDrawer) iconDrawer = newIconDrawer
    }

    private fun updateAccessibilityText() {
        accessibilityText = value.name.lowercase() + when (val currentContent = content) {
            is TextContent -> "|${currentContent.text}|"
            is IconContent -> "|${currentContent.iconText}|"
            NoContent -> ""
        }
    }

    private fun applyStyle(@StyleRes style: Int) {
        checkbox.context.theme.applyStyle(style, true)
    }
}