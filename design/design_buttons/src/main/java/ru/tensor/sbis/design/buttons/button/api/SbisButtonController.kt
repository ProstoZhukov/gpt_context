package ru.tensor.sbis.design.buttons.button.api

import android.content.res.ColorStateList
import android.content.res.TypedArray
import android.graphics.Color
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import androidx.core.content.res.ResourcesCompat.ID_NULL
import androidx.core.content.withStyledAttributes
import androidx.core.graphics.ColorUtils
import org.apache.commons.lang3.StringUtils
import ru.tensor.sbis.design.buttons.R
import ru.tensor.sbis.design.buttons.SbisButton
import ru.tensor.sbis.design.buttons.base.models.counter.SbisButtonCounter
import ru.tensor.sbis.design.buttons.base.models.icon.SbisButtonIcon
import ru.tensor.sbis.design.buttons.base.models.icon.SbisButtonIconSize
import ru.tensor.sbis.design.buttons.base.models.icon.SbisButtonTextIcon
import ru.tensor.sbis.design.buttons.base.models.state.SbisButtonState
import ru.tensor.sbis.design.buttons.base.models.style.SbisButtonCustomStyle
import ru.tensor.sbis.design.buttons.base.models.style.SbisButtonResourceStyle
import ru.tensor.sbis.design.buttons.base.models.style.SbisButtonStyle
import ru.tensor.sbis.design.buttons.base.models.title.SbisButtonTitle
import ru.tensor.sbis.design.buttons.base.models.title.SbisButtonTitleSize
import ru.tensor.sbis.design.buttons.base.utils.drawers.CounterDrawer
import ru.tensor.sbis.design.buttons.base.utils.drawers.updateVisibilityByState
import ru.tensor.sbis.design.buttons.base.utils.style.COLOR_STATES
import ru.tensor.sbis.design.buttons.base.utils.style.SbisButtonStyleHolder
import ru.tensor.sbis.design.buttons.base.utils.style.getColorFromAttr
import ru.tensor.sbis.design.buttons.base.utils.style.loadAlpha
import ru.tensor.sbis.design.buttons.base.utils.style.loadButtonIconStyle
import ru.tensor.sbis.design.buttons.base.utils.style.loadColorStateList
import ru.tensor.sbis.design.buttons.base.utils.style.loadCornerRadius
import ru.tensor.sbis.design.buttons.base.utils.style.loadEnum
import ru.tensor.sbis.design.buttons.base.utils.style.loadFontIcon
import ru.tensor.sbis.design.buttons.base.utils.style.loadState
import ru.tensor.sbis.design.buttons.base.utils.style.loadStyle
import ru.tensor.sbis.design.buttons.base.utils.style.loadTitleStyle
import ru.tensor.sbis.design.buttons.button.models.SbisButtonBackground
import ru.tensor.sbis.design.buttons.button.models.SbisButtonBackground.BorderOnly
import ru.tensor.sbis.design.buttons.button.models.SbisButtonBackground.Contrast
import ru.tensor.sbis.design.buttons.button.models.SbisButtonBackground.Default
import ru.tensor.sbis.design.buttons.button.models.SbisButtonBackground.Gradient
import ru.tensor.sbis.design.buttons.button.models.SbisButtonBackground.Transparent
import ru.tensor.sbis.design.buttons.button.models.SbisButtonModel
import ru.tensor.sbis.design.buttons.button.models.SbisButtonPlacement
import ru.tensor.sbis.design.buttons.button.models.SbisButtonSize
import ru.tensor.sbis.design.buttons.button.models.SbisButtonSize.M
import ru.tensor.sbis.design.buttons.button.models.UNDEFINED_LISTENER
import ru.tensor.sbis.design.buttons.button.utils.BackgroundHolder
import ru.tensor.sbis.design.buttons.icon_text.SbisIconAndTextButtonModel
import ru.tensor.sbis.design.buttons.icon_text.api.SbisIconAndTextButtonController
import ru.tensor.sbis.design.theme.Direction.BOTTOM_TO_TOP
import ru.tensor.sbis.design.theme.Direction.LEFT_TO_RIGHT
import ru.tensor.sbis.design.theme.Direction.RIGHT_TO_LEFT
import ru.tensor.sbis.design.theme.Direction.TOP_TO_BOTTOM
import ru.tensor.sbis.design.theme.HorizontalAlignment
import ru.tensor.sbis.design.theme.HorizontalPosition
import ru.tensor.sbis.design.theme.global_variables.BackgroundColor
import ru.tensor.sbis.design.theme.models.AbstractHeight
import ru.tensor.sbis.design.utils.ThemeContextBuilder

/**
 * Контроллер для управления состоянием и внешним видом кнопки [SbisButton].
 *
 * @author ma.kolpakov
 */
class SbisButtonController internal constructor() :
    SbisIconAndTextButtonController<SbisButtonSize>(M, SbisButtonCustomStyle()),
    SbisButtonApi {

    private lateinit var backgroundHolder: BackgroundHolder
    internal var counterDrawer: CounterDrawer? = null
        private set

    /**
     * Способ размещения счётчика: в строку или над иконкой.
     */
    internal val isCounterInlined: Boolean
        get() = size < M || getIconSize(model, size) < SbisButtonIconSize.X5L

    override val inlineHeight get() = size

    override fun setInlineHeight(height: AbstractHeight) {
        size = SbisButtonSize.values()
            .lastOrNull { it.globalVar.getDimen(button.context) <= height.getDimen(button.context) } ?: size
    }

    override fun measureText(text: String) = titleDrawer?.measureText(text) ?: 0F

    override var model: SbisButtonModel = SbisButtonModel()
        set(value) {
            val styleComponentChanged = field.compareIconTitleStyleTo(value)
            val counterChanged = field.compareCounterTo(value)
            val stateChanged = field.compareStateTo(value)
            val styleButtonChanged = field.compareStyleTo(value)
            val backgroundTypeChanged = field.compareBackgroundTypeTo(value)

            field = value

            if (model.clickListener != UNDEFINED_LISTENER) {
                val modelListener = model.clickListener
                if (modelListener == null) {
                    button.setOnClickListener(null)
                } else {
                    button.setOnClickListener { modelListener(button as SbisButton) }
                }
            }

            val componentDrawerUpdated = updateComponentDrawer(field)

            // на Android 5 если иерархия вьюшек всё ещё проходит этап компоновки
            // отрисовка может обгонять вычисления размеров кнопки
            if (componentDrawerUpdated && field.style != null) isMeasured = false
            // обновляет styleHolder, из которого берутся цвета для текста и иконки
            // должен быть вызван раньше, чем обновления текста и иконки
            if (styleButtonChanged) field.style?.let { style = it }

            var isInvalidated = componentDrawerUpdated ||
                styleComponentChanged ||
                counterChanged ||
                stateChanged ||
                styleButtonChanged

            if (styleComponentChanged) updateComponentStyle(value)
            // размер счётчика не влияет на размер кнопки. Рисуется поверх иконки
            if (counterChanged) updateCounter(field.counter)
            if (stateChanged) state = field.state
            if (backgroundTypeChanged) {
                /*
                 Обновление типа фона должно применяться только для самостоятельных кнопок.
                 Кнопки в группах игнорируют этот параметр
                 */
                val backgroundType = field.backgroundType
                if (placement == SbisButtonPlacement.STANDALONE && backgroundType != null) {
                    /*
                    Следует обновлять после возможного изменения размеров
                    т.к.при обновлении типа фона будет вызван invalidate()
                     */
                    this.backgroundType = backgroundType
                    isInvalidated = false
                }
            }

            if (isInvalidated) button.invalidate()

            updateAccessibilityText(field.title, field.icon, field.counter)
        }

    override var backgroundType: SbisButtonBackground = Default
        set(value) {
            if (field == value) return
            field = value
            backgroundHolder.updateStyle(styleHolder, field)
            updateTitleStyle(button, styleHolder)
            updateIconStyle(button, styleHolder)
            if (model.backgroundType != field) {
                model = model.copy(backgroundType = field)
            }
        }

    override var cornerRadiusValue = Float.NaN
        set(value) {
            if (field == value) return
            field = value
            backgroundHolder.updateCornerRadius(globalStyleHolder.getCornerRadius(button, size, field))
        }

    override var hasHorizontalPadding: Boolean = true
        set(value) {
            if (field == value) return
            field = value
            button.requestLayout()
        }

    override var align = HorizontalAlignment.CENTER
        set(value) {
            if (field == value) return
            field = value
            button.invalidate()
        }

    override fun attach(
        button: View,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int,
        globalStyleHolder: SbisButtonStyleHolder
    ) {
        super.attach(button, attrs, defStyleAttr, defStyleRes, globalStyleHolder)

        backgroundHolder = BackgroundHolder(button)

        if (!button.isInEditMode) counterDrawer = CounterDrawer(button)

        val styledAttributes = R.styleable.SbisButton
        button.context.withStyledAttributes(attrs, styledAttributes, defStyleAttr, defStyleRes) {
            /*
            Стили установленные непосредственно на view в xml сейчас не поддерживаются. Требуется
            проработка поведение в случае, если установлен и стиль и атрибуты. Не нужно затирать
            атрибуты, котрые уже загружены из стандартного стиля
             */
            style = loadStyle(R.styleable.SbisButton_SbisButton_style, super.style)
            size = loadEnum(
                R.styleable.SbisButton_SbisButton_size,
                super.size,
                *SbisButtonSize.values()
            )
            val icon = loadFontIcon(
                R.styleable.SbisButton_SbisButton_icon,
                R.styleable.SbisButton_SbisButton_iconSize
            )
            val text = getString(R.styleable.SbisButton_SbisButton_title)
            val position = loadEnum(
                R.styleable.SbisButton_SbisButton_titlePosition,
                HorizontalPosition.RIGHT,
                *HorizontalPosition.values()
            )
            val titleSize = loadEnum(
                R.styleable.SbisButton_SbisButton_titleSize,
                size.titleSize,
                *SbisButtonTitleSize.values()
            )
            val title = SbisButtonTitle(text, position, titleSize)

            cornerRadiusValue = loadCornerRadius(R.styleable.SbisButton_SbisButton_cornerRadius)

            hasHorizontalPadding = getBoolean(R.styleable.SbisButton_SbisButton_hasHorizontalPadding, true)

            // базовые атрибуты применяются в методах on*Changed
            // TODO: 10/10/2021 во всех методах on*Changed обновлять модель
            model = model.copy(
                icon = icon,
                title = title,
                state = loadState(R.styleable.SbisButton_SbisButton_state, super.state)
            )
        }

        button.background = backgroundHolder.background
    }

    override fun onViewStateUpdated(): Boolean =
        updateTitleStyle(button, styleHolder) or
            updateIconStyle(button, styleHolder) or
            updateAlpha(button, backgroundType)

    override fun onStyleUpdated(style: SbisButtonStyle, attrs: AttributeSet?) {
        styleHolder = when (style) {
            is SbisButtonResourceStyle -> {
                val styleContext = ThemeContextBuilder(
                    button.context,
                    style.buttonStyle,
                    style.defaultButtonStyle
                ).build()
                val loadedStyle = SbisButtonCustomStyle()
                styleContext.withStyledAttributes(attrs, R.styleable.SbisButton, ID_NULL, ID_NULL) {
                    loadedStyle.loadStyle(this)
                    backgroundType = loadBackgroundType()
                }
                loadedStyle
            }

            is SbisButtonCustomStyle -> style.apply {
                titleStyle?.loadStyle(button.context) { default, contrast, transparent ->
                    titleColors = default
                    titleContrastColors = contrast
                    titleTransparentColors = transparent
                }

                iconStyle?.loadStyle(button.context) { default, contrast, transparent ->
                    iconColors = default
                    iconContrastColors = contrast
                    iconTransparentColors = transparent
                }
            }
        }
        // стиль обновляем, только если размер кнопки вычислен
        if (isMeasured) updateStyle()
    }

    internal fun updateStyle() {
        backgroundHolder.updateStyle(styleHolder, backgroundType)
        onViewStateUpdated()
    }

    override fun onSizeUpdated(size: SbisButtonSize) {
        val icon = model.icon
        if (icon != null && updateIconDrawer(button, icon, getIconSize(model, size))) {
            updateIcon()
        }
        val title = model.title
        if (title != null && updateTitleDrawer(button, title, getTitleSize(model, size))) {
            updateTitle()
        }

        backgroundHolder.size = size
        backgroundHolder.updateCornerRadius(globalStyleHolder.getCornerRadius(button, size, cornerRadiusValue))
        button.apply {
            minimumHeight = globalStyleHolder.getSize(button, size)

            if (isLaidOut) {
                requestLayout()
            }
        }
    }

    /**
     * Реализация стандартного правила "Кнопка с иконкой без текста должна быть X5L размера".
     */
    override fun getIconSize(model: SbisIconAndTextButtonModel, size: SbisButtonSize): SbisButtonIconSize {
        val icon = model.icon
        val iconSize = icon?.size
        return when {
            icon == null -> size.iconSize
            iconSize != null -> iconSize
            model.title?.text == null && size > SbisButtonSize.S -> SbisButtonIconSize.X5L
            else -> size.iconSize
        }
    }

    override fun getTitleSize(model: SbisIconAndTextButtonModel, size: SbisButtonSize): SbisButtonTitleSize {
        val title = model.title
        return when {
            title == null -> size.titleSize
            title.size != null -> title.size
            else -> size.titleSize
        }
    }

    override fun onStateUpdated(state: SbisButtonState) {
        val previousState = model.state
        super.onStateUpdated(state)
        button.elevation = if (state == SbisButtonState.DISABLED) 0F else elevation
        backgroundHolder.state = state
        if (previousState == SbisButtonState.DISABLED || state == SbisButtonState.DISABLED) {
            backgroundHolder.updateStyle(styleHolder, backgroundType)
        }
        counterDrawer?.updateVisibilityByState(state)
    }

    private fun updateAccessibilityText(
        title: SbisButtonTitle?,
        icon: SbisButtonIcon?,
        counter: SbisButtonCounter?
    ) {
        fun Any?.getPropertyString(name: String) = "\"$name\": \"${this?.toString() ?: StringUtils.EMPTY}\""

        accessibilityText =
            "{${title?.text.getPropertyString("text")}, " +
            "${(icon as? SbisButtonTextIcon)?.icon.getPropertyString("icon")}, " +
            "${counter?.counter.getPropertyString("counter")}}"
    }

    private fun updateCounter(counter: SbisButtonCounter?) {
        if (counterDrawer?.counter != counter) {
            counterDrawer?.counter = counter
        }
    }

    private fun updateAlpha(button: View, background: SbisButtonBackground): Boolean {
        val alpha = button.alpha
        button.alpha =
            when {
                button.isPressed -> background.buttonPressedAlpha
                // Переход из нажатого состояния в обычное
                alpha != background.buttonPressedAlpha -> alpha
                else -> background.buttonAlpha
            }
        return alpha != button.alpha
    }

    override fun updateIconStyle(
        view: View,
        styleHolder: SbisButtonCustomStyle
    ) = with(styleHolder) {
        iconDrawer?.let {
            val color = when (backgroundType) {
                Contrast, is Gradient ->
                    iconContrastColors.getColorForState(
                        view.drawableState,
                        iconContrastColors.defaultColor
                    )

                BorderOnly, Transparent ->
                    iconTransparentColors.getColorForState(
                        view.drawableState,
                        iconTransparentColors.defaultColor
                    )

                else ->
                    iconColors.getColorForState(view.drawableState, iconColors.defaultColor)
            }

            it.setTint(color)
        } ?: false
    }

    override fun updateTitleStyle(
        view: View,
        styleHolder: SbisButtonCustomStyle
    ) = with(styleHolder) {
        titleDrawer?.let {
            val color = when (backgroundType) {
                Contrast, is Gradient ->
                    titleContrastColors.getColorForState(
                        view.drawableState,
                        titleContrastColors.defaultColor
                    )

                BorderOnly, Transparent ->
                    titleTransparentColors.getColorForState(
                        view.drawableState,
                        titleTransparentColors.defaultColor
                    )

                else ->
                    titleColors.getColorForState(view.drawableState, titleColors.defaultColor)
            }

            it.setTint(color)
        } ?: false
    }

    /**
     * Загрузить стиль из атрибутов.
     */
    private fun SbisButtonCustomStyle.loadStyle(array: TypedArray) = with(array) {
        backgroundColors = loadColorStateList(
            R.styleable.SbisButton_SbisButton_backgroundColor,
            R.styleable.SbisButton_SbisButton_backgroundColorPressed,
            R.styleable.SbisButton_SbisButton_backgroundColorDisabled
        )
        contrastBackgroundColors = loadColorStateList(
            R.styleable.SbisButton_SbisButton_contrastBackgroundColor,
            R.styleable.SbisButton_SbisButton_contrastBackgroundColorPressed,
            R.styleable.SbisButton_SbisButton_contrastBackgroundColorDisabled,
            BackgroundColor.DEFAULT.getValue(button.context)
        )
        // полупрозрачный цвет при нажатии на кнопку
        val translucentColor = ColorUtils.setAlphaComponent(
            contrastBackgroundColors.defaultColor,
            78 /* Прозрачность 30% */
        )
        transparentBackgroundColors = ColorStateList(
            COLOR_STATES,
            intArrayOf(translucentColor, Color.TRANSPARENT, Color.TRANSPARENT)
        )
        gradientBackgroundColors = loadColorStateList(
            R.styleable.SbisButton_SbisButton_endGradientBackgroundColor,
            R.styleable.SbisButton_SbisButton_startGradientBackgroundColor,
            R.styleable.SbisButton_SbisButton_contrastBackgroundColorDisabled
        )

        borderColors = loadColorStateList(
            R.styleable.SbisButton_SbisButton_borderColor,
            R.styleable.SbisButton_SbisButton_borderColorPressed,
            R.styleable.SbisButton_SbisButton_borderColorDisabled
        )
        borderWidth = getDimensionPixelSize(R.styleable.SbisButton_SbisButton_borderWidth, 0)

        val alpha = loadAlpha(R.styleable.SbisButton_SbisButton_titleDisabledColorAlpha)

        loadTitleStyle(
            { default, contrast, transparent ->
                titleColors = default
                titleContrastColors = contrast
                titleTransparentColors = transparent
            },
            (255 * alpha).toInt()
        )

        loadButtonIconStyle(
            { default, contrast, transparent ->
                iconColors = default
                iconContrastColors = contrast
                iconTransparentColors = transparent
            },
            (255 * alpha).toInt()
        )

        progressColor = getColorFromAttr(R.styleable.SbisButton_SbisButton_progressColor, 0)
        progressContrastColor = getColorFromAttr(R.styleable.SbisButton_SbisButton_progressContrastColor, progressColor)

        elevation = getDimension(R.styleable.SbisButton_SbisButton_elevation, 0F)
        scaleOn = getBoolean(R.styleable.SbisButton_SbisButton_scale_on, false)
        if (elevation == 0F && button.elevation != 0F) {
            elevation = button.elevation
        } else {
            button.elevation = elevation
        }
    }

    private fun TypedArray.loadBackgroundType(): SbisButtonBackground {
        return when (
            val backgroundCode =
                if (getType(R.styleable.SbisButton_SbisButton_backgroundType) == TypedValue.TYPE_ATTRIBUTE) {
                    0
                } else {
                    getInteger(R.styleable.SbisButton_SbisButton_backgroundType, 0)
                }
        ) {
            // значение по умолчанию
            0, 1 -> backgroundType
            2 -> Contrast
            3 -> BorderOnly
            4 -> Transparent
            5 -> Gradient(LEFT_TO_RIGHT)
            6 -> Gradient(RIGHT_TO_LEFT)
            7 -> Gradient(TOP_TO_BOTTOM)
            8 -> Gradient(BOTTOM_TO_TOP)
            else -> error("Unexpected background type $backgroundCode")
        }
    }

    internal fun updateCornerRadius(radius: Float) = backgroundHolder.updateCornerRadius(radius)
}