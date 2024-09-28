package ru.tensor.sbis.design.buttons.group.utils

import android.content.res.ColorStateList
import androidx.annotation.ColorInt

/**
 * @author ma.kolpakov
 */
internal class ButtonGroupStyleHolder {

    internal lateinit var backgroundColors: ColorStateList
    internal lateinit var borderColors: ColorStateList
    internal var borderWidth = 0F

    @ColorInt
    internal var secondaryButtonProgressColor: Int = 0
}
