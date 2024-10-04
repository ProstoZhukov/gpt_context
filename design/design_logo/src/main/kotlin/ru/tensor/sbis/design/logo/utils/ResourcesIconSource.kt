package ru.tensor.sbis.design.logo.utils

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import androidx.annotation.AttrRes
import ru.tensor.sbis.design.R
import ru.tensor.sbis.design.logo.api.IconSource
import ru.tensor.sbis.design.utils.extentions.getDrawableFrom
import ru.tensor.sbis.design.utils.extentions.getDrawableFromAttr
import ru.tensor.sbis.design.utils.getDataFromAttrOrNull

/**
 * Поставщик иконок из ресурсов.
 *
 * @author ra.geraskin
 */
internal class ResourcesIconSource(context: Context) : IconSource {

    /** Стандартная иконка птицы. */
    override val defaultIcon: Drawable =
        context.getDrawableFromAttr(R.attr.logoViewDefaultIcon) ?: ColorDrawable(Color.MAGENTA)

    /** Брендовая маленькая иконка. Используется вместо птицы.  */
    override val brandLogo: Drawable? = context.getDrawableOrNull(R.attr.logoViewBrandLogo)

    /** Брендовое изображение логотипа. Используется вместо всего компонента логотипа.  */
    override val brandImage: Drawable? = context.getDrawableOrNull(R.attr.logoViewBrandImage)

    private fun Context.getDrawableOrNull(@AttrRes attr: Int): Drawable? =
        getDataFromAttrOrNull(attr, false)?.let { res ->
            if (res == 0) {
                null
            } else {
                getDrawableFrom(res)
            }
        }

}