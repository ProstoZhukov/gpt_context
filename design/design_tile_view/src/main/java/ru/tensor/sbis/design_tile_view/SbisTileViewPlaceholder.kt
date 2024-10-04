package ru.tensor.sbis.design_tile_view

import android.graphics.drawable.Drawable
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import ru.tensor.sbis.design.SbisMobileIcon

/**
 * Заглушка при отсутствии изображения в плитке
 *
 * @author us.bessonov
 */
sealed class SbisTileViewPlaceholder

/**
 * Заглушка, использующая шрифтовую иконку.
 *
 * @param background Цвет фона под иконкой. По умолчанию не задан (прозрачный).
 */
class TextIconPlaceholder(
    val iconString: String,
    @ColorInt
    val background: Int? = null
) : SbisTileViewPlaceholder() {

    constructor(
        icon: SbisMobileIcon.Icon = SbisTileViewImageModel.DEFAULT_PLACEHOLDER,
        @ColorInt
        background: Int? = null
    ) : this(icon.character.toString(), background)
}

/**
 * Заглушка, использующая [Drawable]
 */
class ImagePlaceholder(@DrawableRes val image: Int) : SbisTileViewPlaceholder()