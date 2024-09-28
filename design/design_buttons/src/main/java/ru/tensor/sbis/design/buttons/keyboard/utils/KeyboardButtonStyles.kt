package ru.tensor.sbis.design.buttons.keyboard.utils

import android.content.Context
import android.content.res.ColorStateList
import ru.tensor.sbis.design.R as RDesign
import ru.tensor.sbis.design.buttons.base.models.style.SbisButtonCustomStyle
import ru.tensor.sbis.design.buttons.base.models.style.SbisButtonIconStyle
import ru.tensor.sbis.design.buttons.base.models.style.SbisButtonTitleStyle
import ru.tensor.sbis.design.buttons.base.utils.style.PRESSED_COLOR_STATES
import ru.tensor.sbis.design.theme.global_variables.IconColor
import ru.tensor.sbis.design.theme.global_variables.TextColor
import ru.tensor.sbis.design.utils.extentions.getColorFromAttr

/**
 * Файл со стилями для кнопки виртуальной клавиатуры.
 *
 * @author ra.geraskin
 */

/**
 * Стиль для стандартной кнопки ввода.
 */
internal fun getStyleInputType(context: Context) = SbisButtonCustomStyle(
    backgroundColors = ColorStateList(
        PRESSED_COLOR_STATES,
        intArrayOf(
            context.getColorFromAttr(RDesign.attr.defaultActiveBackgroundColorKeyboardView),
            context.getColorFromAttr(RDesign.attr.defaultBackgroundColorKeyboardView)
        )
    ),
    iconStyle = SbisButtonIconStyle(ColorStateList.valueOf(TextColor.DEFAULT.getValue(context))),
    titleStyle = SbisButtonTitleStyle.create(ColorStateList.valueOf(TextColor.DEFAULT.getValue(context)))
)

/**
 * Стиль для кнопки действия.
 */
internal fun getStyleActionType(context: Context) = SbisButtonCustomStyle(
    backgroundColors = ColorStateList(
        PRESSED_COLOR_STATES,
        intArrayOf(
            context.getColorFromAttr(RDesign.attr.actionActiveBackgroundColorKeyboardView),
            context.getColorFromAttr(RDesign.attr.actionBackgroundColorKeyboardView)
        )
    ),
    iconStyle = SbisButtonIconStyle(
        ColorStateList
            .valueOf(context.getColorFromAttr(RDesign.attr.actionTextColorKeyboardView))
    ),
    titleStyle = SbisButtonTitleStyle.create(
        ColorStateList
            .valueOf(context.getColorFromAttr(RDesign.attr.actionTextColorKeyboardView))
    )
)

/**
 * Стиль для кнопки дополнительного действия.
 */
internal fun getStyleMainActionType(context: Context) = SbisButtonCustomStyle(
    backgroundColors = ColorStateList(
        PRESSED_COLOR_STATES,
        intArrayOf(
            context.getColorFromAttr(RDesign.attr.primaryActiveColor),
            context.getColorFromAttr(RDesign.attr.primaryColor)
        )
    ),
    iconStyle = SbisButtonIconStyle(ColorStateList.valueOf(IconColor.CONTRAST.getValue(context))),
    titleStyle = SbisButtonTitleStyle.create(ColorStateList.valueOf(IconColor.CONTRAST.getValue(context)))
)
