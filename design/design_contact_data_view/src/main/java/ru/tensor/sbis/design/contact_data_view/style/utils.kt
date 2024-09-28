package ru.tensor.sbis.design.contact_data_view.style

import android.content.res.ColorStateList
import android.content.res.TypedArray
import androidx.annotation.StyleableRes

/**
 * Поддерживаемые состояния кнопки для раскраски
 */
internal val COLOR_STATES = arrayOf(
    intArrayOf(android.R.attr.state_pressed),
    intArrayOf(-android.R.attr.state_enabled),
    intArrayOf(),
)

internal fun TypedArray.loadColorStateList(
    @StyleableRes pressed: Int,
    @StyleableRes disabled: Int,
    @StyleableRes default: Int
) = ColorStateList(
    COLOR_STATES,
    intArrayOf(
        getColor(pressed, 0),
        getColor(disabled, 0),
        getColor(default, 0)
    )
)