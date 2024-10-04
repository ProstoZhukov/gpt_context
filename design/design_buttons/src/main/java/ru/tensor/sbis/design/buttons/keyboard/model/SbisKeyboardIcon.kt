package ru.tensor.sbis.design.buttons.keyboard.model

import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import androidx.annotation.DrawableRes
import ru.tensor.sbis.design.SbisMobileIcon
import ru.tensor.sbis.design.buttons.base.models.icon.SbisButtonDrawableIcon
import ru.tensor.sbis.design.buttons.base.models.icon.SbisButtonIcon
import ru.tensor.sbis.design.buttons.base.models.icon.SbisButtonTextIcon
import ru.tensor.sbis.design.buttons.base.models.style.SbisButtonIconStyle
import ru.tensor.sbis.design.buttons.base.models.style.SbisButtonTitleStyle
import ru.tensor.sbis.design.buttons.base.models.title.SbisButtonTitle

/**
 * Модель иконки для SbisKeyboardButton.
 * Введена из-за того что SbisMobileIcon и обычная текстовая иконка в макете имеют разные размеры.
 *
 * @author ra.geraskin
 */
class SbisKeyboardIcon private constructor(
    val icon: SbisButtonIcon? = null,
    val title: SbisButtonTitle? = null
) {

    /**
     * Конструктор для использования текстовой иконки (буквы / цифры).
     */
    constructor(
        textIcon: CharSequence,
        buttonSize: SbisKeyboardIconSize,
        colorList: ColorStateList
    ) : this(
        title = SbisButtonTitle(
            text = textIcon,
            size = buttonSize.textIconSize,
            style = SbisButtonTitleStyle.create(colorList)
        )
    )

    /**
     * Конструктор для использования шрифтовой иконки ([SbisMobileIcon.Icon]).
     */
    constructor(
        mobileIcon: SbisMobileIcon.Icon,
        buttonSize: SbisKeyboardIconSize,
        colorList: ColorStateList
    ) : this(
        icon = SbisButtonTextIcon(mobileIcon, buttonSize.mobileIconSize, SbisButtonIconStyle(colorList))
    )

    /**
     * Конструктор для использования картинки в качестве иконки ([Drawable]).
     */
    @Suppress("unused")
    constructor(
        drawableIcon: Drawable,
        buttonSize: SbisKeyboardIconSize,
        colorList: ColorStateList
    ) : this(
        icon = SbisButtonDrawableIcon(drawableIcon, buttonSize.mobileIconSize, SbisButtonIconStyle(colorList))
    )

    /**
     * Конструктор для использования картинки из ресурсов в качестве иконки ([DrawableRes]).
     */
    @Suppress("unused")
    constructor(
        @DrawableRes drawableResIcon: Int,
        buttonSize: SbisKeyboardIconSize,
        colorList: ColorStateList
    ) : this(
        icon = SbisButtonDrawableIcon(drawableResIcon, buttonSize.mobileIconSize, SbisButtonIconStyle(colorList))
    )

}