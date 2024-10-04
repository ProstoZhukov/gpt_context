package ru.tensor.sbis.design.buttons.arrow.model

import android.content.Context
import android.content.res.ColorStateList
import androidx.annotation.AttrRes
import ru.tensor.sbis.design.R
import ru.tensor.sbis.design.buttons.base.models.style.SbisButtonCustomStyle
import ru.tensor.sbis.design.buttons.base.models.style.SbisButtonIconStyle
import ru.tensor.sbis.design.buttons.base.utils.style.COLOR_STATES
import ru.tensor.sbis.design.buttons.arrow.model.SbisArrowButtonStyle.PALE
import ru.tensor.sbis.design.buttons.arrow.model.SbisArrowButtonStyle.DEFAULT
import ru.tensor.sbis.design.buttons.arrow.model.SbisArrowButtonBackgroundType.FILLED
import ru.tensor.sbis.design.buttons.arrow.model.SbisArrowButtonBackgroundType.FILLED_ON_TAP
import ru.tensor.sbis.design.utils.extentions.getColorFromAttr
import ru.tensor.sbis.design.utils.getDimenPx

/**
 * Возможные комбинации стилей и типов для кнопок пролистывания.
 *
 * @author ra.geraskin
 */
internal enum class ArrowButtonStyleSet(
    @AttrRes val pressedBackgroundColor: Int,
    @AttrRes val disabledBackgroundColor: Int,
    @AttrRes val defaultBackgroundColor: Int,
    @AttrRes val disabledBorderWidth: Int,
    @AttrRes val pressedIconColor: Int,
    @AttrRes val disabledIconColor: Int,
    @AttrRes val defaultIconColor: Int
) {

    /**
     * Стиль "С заливкой pale".
     */
    FILLED_PALE(
        pressedBackgroundColor = R.attr.paleActiveColor,
        disabledBackgroundColor = com.google.android.material.R.attr.backgroundColor,
        defaultBackgroundColor = R.attr.paleColor,
        disabledBorderWidth = R.attr.borderThickness_s,
        pressedIconColor = R.attr.labelIconColor,
        disabledIconColor = R.attr.readonlyIconColor,
        defaultIconColor = R.attr.labelIconColor
    ),

    /**
     * Стиль "С заливкой по клику pale".
     */
    FILLED_ON_TAP_PALE(
        pressedBackgroundColor = R.attr.paleActiveColor,
        disabledBackgroundColor = com.google.android.material.R.attr.backgroundColor,
        defaultBackgroundColor = android.R.color.transparent,
        disabledBorderWidth = R.attr.borderThickness_s,
        pressedIconColor = R.attr.iconColor,
        disabledIconColor = R.attr.readonlyIconColor,
        defaultIconColor = R.attr.labelIconColor
    ),

    /**
     * Стиль "С заливкой default".
     */
    FILLED_DEFAULT(
        pressedBackgroundColor = R.attr.defaultActiveBackgroundColorButton,
        disabledBackgroundColor = com.google.android.material.R.attr.backgroundColor,
        defaultBackgroundColor = R.attr.defaultContrastBackgroundColorButton,
        disabledBorderWidth = R.attr.borderThickness_s,
        pressedIconColor = R.attr.iconColor,
        disabledIconColor = R.attr.readonlyIconColor,
        defaultIconColor = R.attr.iconColor
    );

    /** @SelfDocumented */
    fun getButtonStyle(context: Context) = SbisButtonCustomStyle(
        backgroundColors = ColorStateList(
            COLOR_STATES,
            intArrayOf(
                context.getColorFromAttr(pressedBackgroundColor),
                context.getColorFromAttr(disabledBackgroundColor),
                context.getColorFromAttr(defaultBackgroundColor)
            )
        ),
        borderWidth = context.getDimenPx(disabledBorderWidth),
        iconStyle = SbisButtonIconStyle(
            ColorStateList(
                COLOR_STATES,
                intArrayOf(
                    context.getColorFromAttr(pressedIconColor),
                    context.getColorFromAttr(disabledIconColor),
                    context.getColorFromAttr(defaultIconColor)
                )
            )
        )
    )

    companion object {

        /**
         * Выбор стилизации по типу фона кнопки и её стилю.
         */
        fun getStyleSet(
            arrowButtonBackgroundType: SbisArrowButtonBackgroundType,
            arrowButtonStyle: SbisArrowButtonStyle
        ) = when (arrowButtonBackgroundType to arrowButtonStyle) {
            FILLED to PALE -> FILLED_PALE
            FILLED to DEFAULT -> FILLED_DEFAULT
            FILLED_ON_TAP to PALE -> FILLED_ON_TAP_PALE
            FILLED_ON_TAP to DEFAULT -> FILLED_DEFAULT
            else -> FILLED_PALE
        }

    }
}