package ru.tensor.sbis.design.buttons.round.api

import android.content.Context
import android.content.res.ColorStateList
import android.content.res.TypedArray
import android.graphics.Color
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import androidx.core.content.res.ResourcesCompat.ID_NULL
import androidx.core.content.withStyledAttributes
import ru.tensor.sbis.design.buttons.R
import ru.tensor.sbis.design.buttons.SbisRoundButton
import ru.tensor.sbis.design.buttons.base.api.AbstractSbisButtonController
import ru.tensor.sbis.design.buttons.base.models.counter.SbisButtonCounter
import ru.tensor.sbis.design.buttons.base.models.icon.SbisButtonIcon
import ru.tensor.sbis.design.buttons.base.models.icon.SbisButtonTextIcon
import ru.tensor.sbis.design.buttons.base.models.state.SbisButtonState
import ru.tensor.sbis.design.buttons.base.models.style.SbisButtonCustomStyle
import ru.tensor.sbis.design.buttons.base.models.style.SbisButtonResourceStyle
import ru.tensor.sbis.design.buttons.base.models.style.SbisButtonStyle
import ru.tensor.sbis.design.buttons.base.utils.drawers.ButtonComponentDrawer
import ru.tensor.sbis.design.buttons.base.utils.drawers.CounterDrawer
import ru.tensor.sbis.design.buttons.base.utils.drawers.updateVisibilityByState
import ru.tensor.sbis.design.buttons.base.utils.drawers.updateVisivilityProgressByState
import ru.tensor.sbis.design.buttons.base.utils.style.COLOR_STATES
import ru.tensor.sbis.design.buttons.base.utils.style.SbisButtonStyleHolder
import ru.tensor.sbis.design.buttons.base.utils.style.loadAlpha
import ru.tensor.sbis.design.buttons.base.utils.style.loadColorStateList
import ru.tensor.sbis.design.buttons.base.utils.style.loadCornerRadius
import ru.tensor.sbis.design.buttons.base.utils.style.loadEnum
import ru.tensor.sbis.design.buttons.base.utils.style.loadFontIcon
import ru.tensor.sbis.design.buttons.base.utils.style.loadRoundButtonIconStyle
import ru.tensor.sbis.design.buttons.base.utils.style.loadState
import ru.tensor.sbis.design.buttons.base.utils.style.loadStyle
import ru.tensor.sbis.design.buttons.round.model.SbisRoundButtonSize
import ru.tensor.sbis.design.buttons.round.model.SbisRoundButtonSize.L
import ru.tensor.sbis.design.buttons.round.model.SbisRoundButtonType
import ru.tensor.sbis.design.buttons.round.model.SbisRoundButtonType.Filled
import ru.tensor.sbis.design.buttons.round.model.SbisRoundButtonType.Gradient
import ru.tensor.sbis.design.buttons.round.model.SbisRoundButtonType.Transparent
import ru.tensor.sbis.design.buttons.round.utils.CircleBackgroundDrawer
import ru.tensor.sbis.design.buttons.round.utils.CircleOutlineProvider
import ru.tensor.sbis.design.custom_view_tools.utils.safeRequestLayout
import ru.tensor.sbis.design.theme.Direction.BOTTOM_TO_TOP
import ru.tensor.sbis.design.theme.Direction.LEFT_TO_RIGHT
import ru.tensor.sbis.design.theme.Direction.RIGHT_TO_LEFT
import ru.tensor.sbis.design.theme.Direction.TOP_TO_BOTTOM
import ru.tensor.sbis.design.theme.HorizontalPosition
import ru.tensor.sbis.design.theme.global_variables.BackgroundColor
import ru.tensor.sbis.design.theme.models.AbstractHeight
import ru.tensor.sbis.design.utils.ThemeContextBuilder

/**
 * Контроллер для управления состоянием и внешним видом кнопки [SbisRoundButton].
 *
 * @author ma.kolpakov
 */
class SbisRoundButtonController internal constructor() :
    AbstractSbisButtonController<SbisRoundButtonSize>(L, SbisButtonCustomStyle()),
    SbisRoundButtonApi {

    private var backgroundDrawer: ButtonComponentDrawer? = null

    internal val activeBackgroundDrawer: ButtonComponentDrawer get() = backgroundDrawer!!

    internal val activeIconDrawer: ButtonComponentDrawer get() = iconDrawer!!

    override val inlineHeight get() = size

    internal var accessibilityText = ""

    override fun setInlineHeight(height: AbstractHeight) {
        size = SbisRoundButtonSize.values()
            .lastOrNull { it.globalVar.getDimen(button.context) <= height.getDimen(button.context) } ?: size
    }

    override var icon: SbisButtonIcon = SbisButtonTextIcon("")
        set(value) {
            field = value
            val iconSize = icon.size ?: size.iconSize
            val sizeChanged = updateIconDrawer(button, field, iconSize)

            val iconStyle = icon.style
            val styleUpdated = when {
                iconStyle != null -> {
                    iconStyle.loadStyle(button.context) { default, _, transparent ->
                        styleHolder.iconColors = default
                        styleHolder.iconTransparentColors = transparent
                    }
                    true
                }

                else -> false
            }

            if (sizeChanged or styleUpdated) {
                updateIconStyle(button, styleHolder)
                button.safeRequestLayout()
            }

            updateAccessibilityText(field)
        }

    override var type: SbisRoundButtonType = Filled
        set(value) {
            if (field == value) return
            field = value
            updateIconStyle(button, styleHolder)
            updateBackgroundDrawer()
        }

    override var cornerRadiusValue = Float.NaN
        set(value) {
            if (field == value) return
            field = value
            updateBackgroundDrawer()
            updateOutline(size)
        }

    override var counterPosition: HorizontalPosition = HorizontalPosition.RIGHT
        set(value) {
            field = value
            button.invalidate()
        }

    /**
     * Для корректного отображения счётчика, для родителя  необходимо применить флаг View.clipChildren = false.
     */
    override var counter: SbisButtonCounter? = null
        set(value) {
            field = value
            counterDrawer.counter = value
            button.invalidate()
        }

    internal val counterDrawer: CounterDrawer by lazy {
        CounterDrawer(button).apply {
            counter = this@SbisRoundButtonController.counter
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

        val styledAttributes = R.styleable.SbisRoundButton
        button.context.withStyledAttributes(attrs, styledAttributes, defStyleAttr, defStyleRes) {
            /*
            Стили установленные непосредственно на view в xml сейчас не поддерживаются. Требуется
            проработка поведение в случае, если установлен и стиль и атрибуты. Не нужно затирать
            атрибуты, котрые уже загружены из стандартного стиля
             */
            style = loadStyle(R.styleable.SbisRoundButton_SbisRoundButton_style, super.style)
            size = loadEnum(
                R.styleable.SbisRoundButton_SbisRoundButton_size,
                super.size,
                *SbisRoundButtonSize.values()
            )
            state = loadState(R.styleable.SbisRoundButton_SbisRoundButton_state, super.state)
            loadFontIcon(
                R.styleable.SbisRoundButton_SbisRoundButton_icon,
                R.styleable.SbisRoundButton_SbisRoundButton_iconSize
            )?.let { icon = it }

            cornerRadiusValue = loadCornerRadius(R.styleable.SbisRoundButton_SbisRoundButton_cornerRadius)
        }
    }

    override fun onViewStateUpdated(): Boolean =
        updateIconStyle(button, styleHolder) or
            updateBackgroundStyle(button, type, styleHolder)

    override fun onStyleUpdated(style: SbisButtonStyle, attrs: AttributeSet?) {
        applyStyle(button.context, attrs, style)
        onStateUpdated(state)
        onViewStateUpdated()
    }

    override fun onSizeUpdated(size: SbisRoundButtonSize) {
        updateProgress(size, styleHolder.progressContrastColor)
        updateOutline(size)

        button.apply {
            minimumWidth = globalStyleHolder.getSize(button, size)
            minimumHeight = minimumWidth
        }

        val buttonWidth = button.minimumWidth.toFloat()
        if (backgroundDrawer?.width != buttonWidth) {
            updateCircleBackgroundDrawer(buttonWidth)
            updateBackgroundStyle(button, type, styleHolder)
        }

        val iconSize = icon.size ?: size.iconSize
        if (updateIconDrawer(button, icon, iconSize)) {
            updateIcon()
        }

        button.apply {
            if (isLaidOut) {
                requestLayout()
            }
        }
    }

    override fun onStateUpdated(state: SbisButtonState) {
        iconDrawer?.apply {
            changeState(state)
            updateVisibilityByState(state)
        }
        button.elevation = if (state == SbisButtonState.DISABLED) 0F else elevation
        backgroundDrawer?.apply {
            changeState(state)
        }
        updateBackgroundDrawer()
        progressDrawer.updateVisivilityProgressByState(state)
        if (state == SbisButtonState.IN_PROGRESS) updateProgress(size, styleHolder.progressContrastColor)
    }

    fun updateIconStyle() {
        updateIconStyle(button, styleHolder)
    }

    override fun updateIconStyle(
        view: View,
        styleHolder: SbisButtonCustomStyle
    ) = with(styleHolder) {
        iconDrawer?.let {
            val color = when (type) {
                Transparent ->
                    iconTransparentColors.getColorForState(
                        button.drawableState,
                        iconTransparentColors.defaultColor
                    )

                else ->
                    iconColors.getColorForState(button.drawableState, iconColors.defaultColor)
            }

            it.setTint(color)
        } ?: false
    }

    private fun updateBackgroundDrawer() {
        backgroundDrawer?.let {
            updateCircleBackgroundDrawer(it.width)
            updateBackgroundStyle(button, type, styleHolder)
            button.invalidate()
        }
    }

    private fun updateCircleBackgroundDrawer(width: Float) {
        backgroundDrawer = when (type) {
            Filled -> CircleBackgroundDrawer(
                button.context,
                width,
                cornerRadiusValue,
                styleHolder.borderWidth.toFloat(),
                state
            )

            Transparent -> {
                button.elevation = 0F
                elevation = 0F
                CircleBackgroundDrawer(button.context, width, cornerRadiusValue, 0F, state)
            }

            is Gradient -> {
                val pressedState = intArrayOf(android.R.attr.state_pressed)
                val gradientColor = styleHolder.gradientBackgroundColors.getColorForState(
                    pressedState,
                    styleHolder.gradientBackgroundColors.defaultColor
                )
                CircleBackgroundDrawer(
                    button.context,
                    width,
                    cornerRadiusValue,
                    styleHolder.borderWidth.toFloat(),
                    state,
                    (type as Gradient).direction to gradientColor
                )
            }
        }
    }

    fun updateBackgroundStyle() {
        updateBackgroundStyle(button, type, styleHolder)
    }

    private fun updateBackgroundStyle(
        button: View,
        bgType: SbisRoundButtonType,
        styleHolder: SbisButtonCustomStyle
    ) = with(styleHolder) {
        if (state == SbisButtonState.IN_PROGRESS) {
            return backgroundDrawer?.setTint(
                backgroundColors.getColorForState(
                    button.drawableState.plus(android.R.attr.state_enabled),
                    backgroundColors.defaultColor
                )
            ) ?: false
        }
        backgroundDrawer?.let {
            val color = when (bgType) {
                Transparent ->
                    transparentBackgroundColors.getColorForState(
                        button.drawableState,
                        transparentBackgroundColors.defaultColor
                    )

                is Gradient ->
                    gradientBackgroundColors.getColorForState(
                        button.drawableState,
                        gradientBackgroundColors.defaultColor
                    )

                else ->
                    backgroundColors.getColorForState(button.drawableState, backgroundColors.defaultColor)
            }

            it.setTint(color)
        } ?: false
    }

    private fun updateOutline(size: SbisRoundButtonSize) {
        val outlineSize = globalStyleHolder.getSize(button, size)
        button.outlineProvider = CircleOutlineProvider(outlineSize, cornerRadiusValue)
    }

    private fun updateAccessibilityText(icon: SbisButtonIcon?) {
        accessibilityText = (icon as? SbisButtonTextIcon)?.icon.toString()
    }

    /**
     * Применить [стиль][style] кнопке при динамическом изменении.
     */
    private fun applyStyle(context: Context, attrs: AttributeSet?, style: SbisButtonStyle) {
        styleHolder = when (style) {
            is SbisButtonResourceStyle -> {
                val styleContext = ThemeContextBuilder(
                    context,
                    style.roundButtonStyle,
                    style.defaultRoundButtonStyle
                ).build()
                styleContext.theme.applyStyle(R.style.SbisRoundButtonBaseTheme, false)

                val loadedStyle = SbisButtonCustomStyle()
                styleContext.withStyledAttributes(
                    attrs,
                    R.styleable.SbisRoundButton,
                    ID_NULL,
                    ID_NULL
                ) {
                    loadedStyle.loadStyle(this)
                    type = loadBackgroundType()
                }
                loadedStyle
            }

            is SbisButtonCustomStyle -> style.apply {
                iconStyle?.loadStyle(context) { default, contrast, transparent ->
                    iconColors = default
                    iconContrastColors = contrast
                    iconTransparentColors = transparent
                }
            }
        }
    }

    /**
     * Загрузить стиль из атрибутов.
     */
    private fun SbisButtonCustomStyle.loadStyle(array: TypedArray) = with(array) {
        backgroundColors = loadColorStateList(
            R.styleable.SbisRoundButton_SbisRoundButton_backgroundColor,
            R.styleable.SbisRoundButton_SbisRoundButton_backgroundColorPressed,
            R.styleable.SbisRoundButton_SbisRoundButton_backgroundColorDisabled,
            BackgroundColor.DEFAULT.getValue(button.context)
        )

        transparentBackgroundColors = ColorStateList(
            COLOR_STATES,
            intArrayOf(Color.TRANSPARENT, Color.TRANSPARENT, Color.TRANSPARENT)
        )

        gradientBackgroundColors = loadColorStateList(
            R.styleable.SbisRoundButton_SbisRoundButton_endGradientBackgroundColor,
            R.styleable.SbisRoundButton_SbisRoundButton_startGradientBackgroundColor,
            R.styleable.SbisRoundButton_SbisRoundButton_backgroundColorDisabled
        )

        borderWidth = getDimensionPixelSize(R.styleable.SbisRoundButton_SbisRoundButton_borderWidthDisabled, 0)

        val alpha = loadAlpha(R.styleable.SbisRoundButton_SbisRoundButton_iconDisabledColorAlpha)

        loadRoundButtonIconStyle(
            { default, _, transparent ->
                iconColors = default
                iconTransparentColors = transparent
            },
            (255 * alpha).toInt()
        )

        progressColor = getColor(R.styleable.SbisRoundButton_SbisRoundButton_progressColor, 0)
        progressContrastColor =
            getColor(R.styleable.SbisRoundButton_SbisRoundButton_progressContrastColor, progressColor)

        elevation = getDimension(R.styleable.SbisRoundButton_SbisRoundButton_elevation, 0F)
        if (elevation == 0F && button.elevation != 0F) {
            elevation = button.elevation
        } else {
            button.elevation = elevation
        }
    }

    private fun TypedArray.loadBackgroundType(): SbisRoundButtonType {
        return when (
            val typeCode =
                if (getType(R.styleable.SbisRoundButton_SbisRoundButton_type) == TypedValue.TYPE_ATTRIBUTE) {
                    0
                } else {
                    getInteger(R.styleable.SbisRoundButton_SbisRoundButton_type, 0)
                }
        ) {
            // не реагирует на тип фона common, border_only - возвращает кнопку с заливкой
            0, 1, 2, 3 -> Filled
            4 -> Transparent
            5 -> Gradient(LEFT_TO_RIGHT)
            6 -> Gradient(RIGHT_TO_LEFT)
            7 -> Gradient(TOP_TO_BOTTOM)
            8 -> Gradient(BOTTOM_TO_TOP)
            else -> error("Unexpected type $typeCode")
        }
    }
}