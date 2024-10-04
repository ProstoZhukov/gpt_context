package ru.tensor.sbis.design.buttons.keyboard.api

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import androidx.annotation.StyleableRes
import androidx.core.content.withStyledAttributes
import org.apache.commons.lang3.StringUtils
import ru.tensor.sbis.design.SbisMobileIcon
import ru.tensor.sbis.design.buttons.R
import ru.tensor.sbis.design.buttons.SbisKeyboardButton
import ru.tensor.sbis.design.buttons.base.models.icon.SbisButtonIconSize
import ru.tensor.sbis.design.buttons.base.models.state.SbisButtonState
import ru.tensor.sbis.design.buttons.base.models.style.SbisButtonCustomStyle
import ru.tensor.sbis.design.buttons.base.models.style.SbisButtonStyle
import ru.tensor.sbis.design.buttons.base.models.title.SbisButtonTitle
import ru.tensor.sbis.design.buttons.base.models.title.SbisButtonTitleSize
import ru.tensor.sbis.design.buttons.base.utils.drawers.ButtonComponentDrawer
import ru.tensor.sbis.design.buttons.base.utils.drawers.updateVisibilityByState
import ru.tensor.sbis.design.buttons.base.utils.style.SbisButtonStyleHolder
import ru.tensor.sbis.design.buttons.base.utils.style.loadEnum
import ru.tensor.sbis.design.buttons.button.models.SbisButtonModel
import ru.tensor.sbis.design.buttons.icon_text.SbisIconAndTextButtonModel
import ru.tensor.sbis.design.buttons.icon_text.api.SbisIconAndTextButtonController
import ru.tensor.sbis.design.buttons.keyboard.model.SbisKeyboardButtonItemType
import ru.tensor.sbis.design.buttons.keyboard.model.SbisKeyboardButtonSize
import ru.tensor.sbis.design.buttons.keyboard.model.SbisKeyboardIcon
import ru.tensor.sbis.design.buttons.keyboard.model.SbisKeyboardIconSize
import ru.tensor.sbis.design.buttons.keyboard.utils.getStyleActionType
import ru.tensor.sbis.design.buttons.keyboard.utils.getStyleInputType
import ru.tensor.sbis.design.buttons.keyboard.utils.getStyleMainActionType
import ru.tensor.sbis.design.buttons.round.utils.CircleBackgroundDrawer
import ru.tensor.sbis.design.buttons.round.utils.CircleOutlineProvider
import ru.tensor.sbis.design.theme.HorizontalAlignment
import ru.tensor.sbis.design.theme.global_variables.BorderRadius
import ru.tensor.sbis.design.theme.global_variables.Elevation
import ru.tensor.sbis.design.theme.models.AbstractHeight
import ru.tensor.sbis.design.utils.shadow_clipper.clipOutlineShadow

/**
 * Контроллер для управления состоянием и внешним видом кнопки [SbisKeyboardButton].
 *
 * @author ra.geraskin
 */
class SbisKeyboardButtonController internal constructor() :
    SbisIconAndTextButtonController<SbisKeyboardButtonSize>(SbisKeyboardButtonSize.L, SbisButtonCustomStyle()),
    SbisKeyboardButtonApi {

    private var backgroundDrawer: ButtonComponentDrawer? = null

    internal val activeBackgroundDrawer: ButtonComponentDrawer get() = backgroundDrawer!!

    override val inlineHeight get() = size

    private val defaultElevation
        get() = Elevation.M.getDimen(button.context)

    override fun setInlineHeight(height: AbstractHeight) {
        size = SbisKeyboardButtonSize.values()
            .lastOrNull { it.globalVar.getDimen(button.context) <= height.getDimen(button.context) } ?: size
    }

    override fun getTitleSize(model: SbisIconAndTextButtonModel, size: SbisKeyboardButtonSize): SbisButtonTitleSize {
        val title = model.title
        return when {
            title == null -> size.iconSize.textIconSize
            title.size != null -> title.size
            else -> size.iconSize.textIconSize
        }
    }

    override fun getIconSize(model: SbisIconAndTextButtonModel, size: SbisKeyboardButtonSize): SbisButtonIconSize {
        val icon = model.icon
        val iconSize = icon?.size
        return when {
            icon == null -> size.iconSize.mobileIconSize
            iconSize != null -> iconSize
            else -> size.iconSize.mobileIconSize
        }
    }

    override fun attach(
        button: View,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int,
        globalStyleHolder: SbisButtonStyleHolder
    ) {
        super.attach(button, attrs, defStyleAttr, defStyleRes, globalStyleHolder)
        cornerRadiusValue = BorderRadius.X3S.getDimen(button.context)
        val styledAttributes = R.styleable.SbisKeyboardButton
        button.context.withStyledAttributes(attrs, styledAttributes, defStyleAttr, defStyleRes) {
            needSetupShadow = getBoolean(R.styleable.SbisKeyboardButton_SbisKeyboardButton_need_setup_shadow, true)
            size = loadEnum(
                R.styleable.SbisKeyboardButton_SbisKeyboardButton_size,
                super.size,
                *SbisKeyboardButtonSize.values()
            )
            itemType = loadEnum(
                R.styleable.SbisKeyboardButton_SbisKeyboardButton_item_type,
                SbisKeyboardButtonItemType.INPUT,
                *SbisKeyboardButtonItemType.values()
            )
            keyboardIcon = loadKeyboardIcon(
                R.styleable.SbisKeyboardButton_SbisKeyboardButton_icon,
                R.styleable.SbisKeyboardButton_SbisKeyboardButton_icon_size,
                R.styleable.SbisKeyboardButton_SbisKeyboardButton_icon_color
            )
        }
        button.clipOutlineShadow()
    }

    override var keyboardIcon: SbisKeyboardIcon = SbisKeyboardIcon(
        StringUtils.EMPTY,
        size.iconSize,
        styleHolder.iconColors
    )
        set(value) {
            field = value
            when {
                field.title != null -> {
                    model = model.copy(
                        title = field.title,
                        icon = null
                    )
                }

                field.icon != null -> {
                    model = model.copy(
                        icon = field.icon,
                        title = null
                    )

                }
            }
        }

    override var cornerRadiusValue: Float = Float.NaN
        set(value) {
            if (field == value) return
            field = value
            updateBackgroundDrawer()
            updateOutline(size)
        }

    override var itemType: SbisKeyboardButtonItemType = SbisKeyboardButtonItemType.INPUT
        set(value) {
            field = value
            updateButtonTypeStyle()
        }

    override var needSetupShadow: Boolean = true
        set(value) {
            field = value
            updateShadow()
        }

    override fun updateIconStyle(view: View, styleHolder: SbisButtonCustomStyle) = with(styleHolder) {
        iconDrawer?.let {
            val color = iconColors.getColorForState(button.drawableState, iconColors.defaultColor)
            it.setTint(color)
        } ?: false
    }

    override fun updateTitleStyle(
        view: View,
        styleHolder: SbisButtonCustomStyle
    ) = with(styleHolder) {
        titleDrawer?.let {
            val color = titleColors.getColorForState(view.drawableState, titleColors.defaultColor)
            it.setTint(color)
        } ?: false
    }

    override var model: SbisButtonModel = SbisButtonModel()
        set(value) {
            val styleComponentChanged = field.compareIconTitleStyleTo(value)
            val stateChanged = field.compareStateTo(value)
            val styleButtonChanged = field.compareStyleTo(value)

            field = value

            val isInvalidated = updateComponentDrawer(field) ||
                styleComponentChanged ||
                stateChanged ||
                styleButtonChanged

            if (styleComponentChanged) updateComponentStyle(field)
            if (stateChanged) state = field.state
            if (styleButtonChanged) field.style?.let { style = it }

            if (isInvalidated) button.invalidate()

            updateAccessibilityText(field.title)
        }

    override var align = HorizontalAlignment.CENTER
        set(value) {
            if (field != value) {
                field = value
                button.invalidate()
            }
        }

    override fun onSizeUpdated(size: SbisKeyboardButtonSize) {
        val icon = model.icon
        if (icon != null && updateIconDrawer(button, icon, getIconSize(model, size))) {
            updateIconStyle(button, styleHolder)
            iconDrawer?.updateVisibilityByState(state)
        }
        val title = model.title
        if (title != null && updateTitleDrawer(button, title, getTitleSize(model, size))) {
            updateTitleStyle(button, styleHolder)
            titleDrawer?.updateVisibilityByState(state)
        }

        updateOutline(size)

        button.apply {
            minimumWidth = globalStyleHolder.getSize(button, size)
            minimumHeight = minimumWidth
        }

        val buttonWidth = button.minimumWidth.toFloat()
        if (backgroundDrawer?.width != buttonWidth) {
            updateCircleBackgroundDrawer(buttonWidth)
            updateBackgroundStyle(button, styleHolder)
        }

        button.apply {
            if (isLaidOut) {
                requestLayout()
            }
        }

    }

    override fun onViewStateUpdated(): Boolean =
        updateIconStyle(button, styleHolder) or updateTitleStyle(button, styleHolder) or
            updateBackgroundStyle(button, styleHolder)

    override fun onStyleUpdated(style: SbisButtonStyle, attrs: AttributeSet?) {
        applyStyle(button.context, style)
        onViewStateUpdated()
    }

    override fun onStateUpdated(state: SbisButtonState) {
        iconDrawer?.apply {
            changeState(state)
            updateVisibilityByState(state)
        }
        titleDrawer?.apply {
            changeState(state)
            updateVisibilityByState(state)
        }
        button.elevation = if (state == SbisButtonState.DISABLED) 0F else defaultElevation
        backgroundDrawer?.apply {
            changeState(state)
        }
        updateBackgroundDrawer()
    }

    internal fun updateShadow() {
        button.elevation = if (needSetupShadow) {
            defaultElevation
        } else {
            0f
        }
    }

    private fun updateOutline(size: SbisKeyboardButtonSize) {
        val outlineSize = globalStyleHolder.getSize(button, size)
        button.outlineProvider = CircleOutlineProvider(outlineSize, cornerRadiusValue)
    }

    private fun updateBackgroundDrawer() {
        backgroundDrawer?.let {
            updateCircleBackgroundDrawer(it.width)
            updateBackgroundStyle(button, styleHolder)
            button.invalidate()
        }
    }

    private fun updateCircleBackgroundDrawer(width: Float) {
        backgroundDrawer =
            CircleBackgroundDrawer(button.context, width, cornerRadiusValue, styleHolder.borderWidth.toFloat(), state)
    }

    private fun updateBackgroundStyle(
        button: View,
        styleHolder: SbisButtonCustomStyle
    ) = with(styleHolder) {
        backgroundDrawer?.let {
            val color = transparentBackgroundColors.getColorForState(
                button.drawableState,
                transparentBackgroundColors.defaultColor
            )
            it.setTint(color)
        } ?: false
    }

    /**
     * Применить [стиль][style] к кнопке при динамическом изменении.
     */
    private fun applyStyle(context: Context, style: SbisButtonStyle) {
        if (style is SbisButtonCustomStyle) {
            styleHolder = style.apply {
                iconStyle?.loadStyle(context) { default, contrast, transparent ->
                    iconColors = default
                    iconContrastColors = contrast
                    iconTransparentColors = transparent
                }
            }
        }
    }

    private fun updateButtonTypeStyle() {
        style = when (itemType) {
            SbisKeyboardButtonItemType.INPUT -> getStyleInputType(button.context)
            SbisKeyboardButtonItemType.ACTION -> getStyleActionType(button.context)
            SbisKeyboardButtonItemType.ACTION_MAIN -> getStyleMainActionType(button.context)
        }
    }

    private fun TypedArray.loadKeyboardIcon(
        @StyleableRes iconTextAttr: Int,
        @StyleableRes iconSizeAttr: Int,
        @StyleableRes iconColorAttr: Int
    ): SbisKeyboardIcon {
        val iconSize = loadEnum(iconSizeAttr, size.iconSize, *SbisKeyboardIconSize.values())
        val iconColorList = getColorStateList(iconColorAttr) ?: (style as SbisButtonCustomStyle).iconColors
        val iconText = if (getType(iconTextAttr) == TypedValue.TYPE_STRING) {
            getString(iconTextAttr)
        } else {
            return SbisKeyboardIcon(StringUtils.EMPTY, iconSize, iconColorList)
        }
        if (iconText.isNullOrEmpty()) return SbisKeyboardIcon(StringUtils.EMPTY, iconSize, iconColorList)
        val mobileIcon = SbisMobileIcon.Icon.values().firstOrNull { it.character == iconText[0] }
        return if (mobileIcon != null) {
            SbisKeyboardIcon(mobileIcon, iconSize, iconColorList)
        } else {
            SbisKeyboardIcon(iconText, iconSize, iconColorList)
        }
    }

    private fun updateAccessibilityText(title: SbisButtonTitle?) {
        accessibilityText = title?.text.toString()
    }

}