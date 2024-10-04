package ru.tensor.sbis.design.buttons.round.utils

import android.content.Context
import android.content.res.ColorStateList
import ru.tensor.sbis.design.buttons.SbisRoundButton
import ru.tensor.sbis.design.buttons.base.models.style.SbisButtonCustomStyle
import ru.tensor.sbis.design.buttons.base.models.style.SbisButtonIconStyle
import ru.tensor.sbis.design.buttons.base.utils.style.PRESSED_COLOR_STATES
import ru.tensor.sbis.design.theme.global_variables.IconColor
import ru.tensor.sbis.design.theme.global_variables.StyleColor

/**
 * Создание круглой кнопки, стилизованная для использования с меню быстрых действий в пустом представлении.
 */
fun provideQuickActionMenuRoundButton(context: Context) = SbisRoundButton(context).apply {
    style = SbisButtonCustomStyle(
        backgroundColors = ColorStateList(
            PRESSED_COLOR_STATES,
            intArrayOf(
                StyleColor.UNACCENTED.getColor(context),
                StyleColor.UNACCENTED.getColor(context)
            )
        ),
        iconStyle = SbisButtonIconStyle(ColorStateList.valueOf(IconColor.CONTRAST.getValue(context)))
    )
    isClickable = false
    elevation = 0f
}
