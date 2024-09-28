/**
 * @author ma.kolpakov
 */
package ru.tensor.sbis.design.buttons.base.utils.style

import android.content.Context
import android.content.res.ColorStateList
import android.content.res.TypedArray
import android.graphics.Color
import android.util.TypedValue
import androidx.annotation.ColorRes
import androidx.annotation.StyleableRes
import androidx.core.content.res.ResourcesCompat
import androidx.core.content.res.ResourcesCompat.ID_NULL
import androidx.core.graphics.ColorUtils
import ru.tensor.sbis.design.buttons.R
import ru.tensor.sbis.design.buttons.base.models.icon.SbisButtonIconSize
import ru.tensor.sbis.design.buttons.base.models.icon.SbisButtonTextIcon
import ru.tensor.sbis.design.buttons.base.models.state.SbisButtonState
import ru.tensor.sbis.design.buttons.base.models.style.*

/**
 * Поддерживаемые состояния кнопки для раскраски.
 */
internal val COLOR_STATES = arrayOf(
    intArrayOf(android.R.attr.state_pressed),
    intArrayOf(-android.R.attr.state_enabled),
    intArrayOf()
)

/**
 * Состояния кнопки для раскраски - нажата и все остальные.
 */
internal val PRESSED_COLOR_STATES = arrayOf(
    intArrayOf(android.R.attr.state_pressed),
    intArrayOf()
)

/**
 * Состояния "выбран" и "не выбран" кнопки с залипанием.
 */
internal val CHECKED_STATE_SET = arrayOf(
    intArrayOf(-android.R.attr.state_checked),
    intArrayOf(android.R.attr.state_checked)
)

/**
 * Получение alpha из атрибутов.
 */
internal fun TypedArray.loadAlpha(@StyleableRes styleAttr: Int) = try {
    getFloat(styleAttr, 1F)
} catch (ex: RuntimeException) {
    1.0F
}

internal fun TypedArray.loadStyle(@StyleableRes styleAttr: Int, default: SbisButtonStyle) =
    when (val styleCode = getInteger(styleAttr, ID_NULL)) {
        // стиль по умолчанию
        ID_NULL -> default
        1 -> PrimaryButtonStyle
        2 -> SecondaryButtonStyle
        3 -> SuccessButtonStyle
        4 -> UnaccentedButtonStyle
        5 -> BonusButtonStyle
        6 -> DangerButtonStyle
        7 -> WarningButtonStyle
        8 -> InfoButtonStyle
        9 -> DefaultButtonStyle
        10 -> NavigationButtonStyle
        11 -> PaleButtonStyle
        12 -> BrandButtonStyle
        13 -> LinkButtonStyle
        14 -> LabelButtonStyle
        else -> error("Unexpected button style $styleCode")
    }

internal fun <SIZE : Enum<*>?> TypedArray.loadEnum(
    @StyleableRes valueAttr: Int,
    default: SIZE,
    vararg values: SIZE
): SIZE {
    val valueCode = getInteger(valueAttr, ID_NULL)
    val ordinal = valueCode - 1
    return when {
        valueCode == ID_NULL -> default
        ordinal in values.indices -> values[ordinal]
        else -> error("Unexpected value code $valueCode")
    }
}

internal fun TypedArray.loadState(@StyleableRes stateAttr: Int, default: SbisButtonState) =
    when (val stateCode = getInteger(stateAttr, ID_NULL)) {
        ID_NULL -> default
        1 -> SbisButtonState.ENABLED
        2 -> SbisButtonState.DISABLED
        3 -> SbisButtonState.IN_PROGRESS
        else -> error("Unexpected button state $stateCode")
    }

/**
 * Загружает шрифтовую иконку.
 */
internal fun TypedArray.loadFontIcon(@StyleableRes iconAttr: Int, @StyleableRes iconSizeAttr: Int) =
    when (val iconType = getType(iconAttr)) {
        TypedValue.TYPE_NULL, TypedValue.DATA_NULL_EMPTY -> null
        TypedValue.TYPE_STRING -> {
            val iconSize = loadEnum(iconSizeAttr, null, *SbisButtonIconSize.values())
            getString(iconAttr)?.let { SbisButtonTextIcon(it, iconSize) }
        }
        else -> error("Unsupported icon type $iconType")
    }

/**
 * Загружает скругление края кнопки.
 */
internal fun TypedArray.loadCornerRadius(@StyleableRes cornerRadiusAttr: Int) =
    if (getType(cornerRadiusAttr) == TypedValue.TYPE_DIMENSION) {
        getDimension(cornerRadiusAttr, Float.NaN)
    } else {
        Float.NaN
    }

internal fun TypedArray.loadRoundButtonIconStyle(consumer: StyleConsumer, alpha: Int = 255) {
    val default = getColorFromAttr(R.styleable.SbisRoundButton_SbisRoundButton_iconColor, Color.MAGENTA)
    val disabled = getDisablesColor(R.styleable.SbisRoundButton_SbisRoundButton_iconDisabledColor, alpha)
    val transparent = getColorFromAttr(R.styleable.SbisRoundButton_SbisRoundButton_iconTransparentColor, Color.MAGENTA)
    val defaultColors = ColorStateList(COLOR_STATES, intArrayOf(default, disabled, default))
    val transparentPressed = ColorUtils.setAlphaComponent(transparent, (255 * 0.8).toInt())
    consumer.onStyleLoaded(
        defaultColors,
        defaultColors,
        ColorStateList(COLOR_STATES, intArrayOf(transparentPressed, disabled, transparent))
    )
}

internal fun TypedArray.loadLinkButtonIconStyle(consumer: StyleConsumer, alpha: Int = 255) {
    val default = getColorFromAttr(R.styleable.SbisLinkButton_SbisLinkButton_iconColor, Color.MAGENTA)
    val disabled = getDisablesColor(R.styleable.SbisLinkButton_SbisLinkButton_iconDisabledColor, alpha)
    val pressed = ColorUtils.setAlphaComponent(default, (255 * 0.8).toInt())
    val defaultColors = ColorStateList(COLOR_STATES, intArrayOf(pressed, disabled, default))
    consumer.onStyleLoaded(defaultColors, defaultColors, defaultColors)
}

internal fun TypedArray.loadButtonIconStyle(consumer: StyleConsumer, alpha: Int = 255) {
    val default = getColorFromAttr(R.styleable.SbisButton_SbisButton_iconColor, Color.MAGENTA)
    val contrast = getColorFromAttr(R.styleable.SbisButton_SbisButton_iconContrastColor, Color.MAGENTA)
    val transparent = getColorFromAttr(R.styleable.SbisButton_SbisButton_iconTransparentColor, Color.MAGENTA)
    val disabled = getDisablesColor(R.styleable.SbisButton_SbisButton_iconDisabledColor, alpha)
    consumer.onStyleLoaded(
        ColorStateList(COLOR_STATES, intArrayOf(default, disabled, default)),
        ColorStateList(COLOR_STATES, intArrayOf(contrast, disabled, contrast)),
        ColorStateList(COLOR_STATES, intArrayOf(transparent, disabled, transparent))
    )
}

internal fun TypedArray.loadTitleStyle(consumer: StyleConsumer, alpha: Int = 255) {
    val default = getColorFromAttr(R.styleable.SbisButton_SbisButton_titleColor, Color.MAGENTA)
    val contrast = getColorFromAttr(R.styleable.SbisButton_SbisButton_titleContrastColor, Color.MAGENTA)
    val transparent = getColorFromAttr(R.styleable.SbisButton_SbisButton_titleTransparentColor, Color.MAGENTA)
    val disabled = getDisablesColor(R.styleable.SbisButton_SbisButton_titleDisabledColor, alpha)
    consumer.onStyleLoaded(
        ColorStateList(COLOR_STATES, intArrayOf(default, disabled, default)),
        ColorStateList(COLOR_STATES, intArrayOf(contrast, disabled, contrast)),
        ColorStateList(COLOR_STATES, intArrayOf(transparent, disabled, transparent))
    )
}

internal fun TypedArray.loadLinkButtonTitleStyle(consumer: StyleConsumer, alpha: Int = 255) {
    val default = getColorFromAttr(R.styleable.SbisLinkButton_SbisLinkButton_titleColor, Color.MAGENTA)
    val disabled = getDisablesColor(R.styleable.SbisLinkButton_SbisLinkButton_titleDisabledColor, alpha)
    val pressed = ColorUtils.setAlphaComponent(default, (255 * 0.8).toInt())
    val defaultColors = ColorStateList(COLOR_STATES, intArrayOf(pressed, disabled, default))
    consumer.onStyleLoaded(defaultColors, defaultColors, defaultColors)
}

private fun TypedArray.getDisablesColor(@StyleableRes styleAttr: Int, alpha: Int): Int =
    if (alpha < 255) {
        ColorUtils.setAlphaComponent(getColorFromAttr(styleAttr, Color.MAGENTA), alpha)
    } else {
        getColorFromAttr(styleAttr, Color.MAGENTA)
    }

internal fun Context.loadColorStateList(
    @ColorRes default: Int,
    @ColorRes pressed: Int,
    @ColorRes disabled: Int
) = ColorStateList(
    COLOR_STATES,
    intArrayOf(
        ResourcesCompat.getColor(resources, pressed, theme),
        ResourcesCompat.getColor(resources, disabled, theme),
        ResourcesCompat.getColor(resources, default, theme)
    )
)

internal fun TypedArray.loadColorStateList(
    @StyleableRes default: Int,
    @StyleableRes pressed: Int,
    @StyleableRes disabled: Int,
    disabledDefault: Int = 0
) = ColorStateList(
    COLOR_STATES,
    intArrayOf(
        getColorFromAttr(pressed),
        getColorFromAttr(disabled, disabledDefault),
        getColorFromAttr(default)
    )
)

/** @SelfDocumented */
internal fun TypedArray.getColorFromAttr(@StyleableRes color: Int, defaultColor: Int = 0) =
    if (getType(color) == TypedValue.TYPE_ATTRIBUTE) defaultColor else getColor(color, defaultColor)