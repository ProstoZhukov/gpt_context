package ru.tensor.sbis.design.buttons.base.models.icon

import android.graphics.drawable.Drawable
import androidx.annotation.DrawableRes
import androidx.core.content.res.ResourcesCompat.ID_NULL
import ru.tensor.sbis.design.CbucIcon
import ru.tensor.sbis.design.SbisMobileIcon
import ru.tensor.sbis.design.buttons.base.models.style.SbisButtonIconStyle

/**
 * Модель иконки в кнопке.
 *
 * @author ma.kolpakov
 */
sealed class SbisButtonIcon {

    /**
     * Размер иконки. Если не задан, применяется размер от стиля кнопки.
     */
    abstract val size: SbisButtonIconSize?

    /**
     * Стиль иконки в кнопке. Если не задан, применяется стиль кнопки.
     */
    abstract val style: SbisButtonIconStyle?

    /**
     * Флаг скалируемости иконки в зависимости от системного размера шрифта.
     */
    abstract val scaleOn: Boolean?
}

/**
 * Модель текстовой иконки.
 *
 * @param icon Иконка из шрифта
 */
data class SbisButtonTextIcon(
    val icon: CharSequence?,
    override val size: SbisButtonIconSize? = null,
    override val style: SbisButtonIconStyle? = null,
    override val scaleOn: Boolean? = null
) : SbisButtonIcon() {

    constructor(
        char: Char,
        size: SbisButtonIconSize? = null,
        style: SbisButtonIconStyle? = null
    ) : this(char.toString(), size, style)

    constructor(
        fontIcon: SbisMobileIcon.Icon,
        size: SbisButtonIconSize? = null,
        style: SbisButtonIconStyle? = null
    ) : this(fontIcon.character, size, style)

    constructor(
        fontIcon: CbucIcon.Icon,
        size: SbisButtonIconSize? = null,
        style: SbisButtonIconStyle? = null
    ) : this(fontIcon.character, size, style)
}

/**
 * Модель иконки из ресурсов.
 *
 * @param iconRes Ресурс иконки
 * @param icon Изображение иконки
 */
data class SbisButtonDrawableIcon internal constructor(
    @DrawableRes val iconRes: Int,
    val icon: Drawable?,
    override val size: SbisButtonIconSize?,
    override val style: SbisButtonIconStyle?,
    override val scaleOn: Boolean? = null
) : SbisButtonIcon() {

    constructor(
        @DrawableRes iconRes: Int,
        size: SbisButtonIconSize? = null,
        style: SbisButtonIconStyle? = null
    ) : this(iconRes, null, size, style)

    @Suppress("unused")
    constructor(
        icon: Drawable,
        size: SbisButtonIconSize? = null,
        style: SbisButtonIconStyle? = null
    ) : this(ID_NULL, icon, size, style)
}
