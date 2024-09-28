package ru.tensor.sbis.design.buttons.base.utils.drawers

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import androidx.annotation.DrawableRes
import androidx.annotation.Px
import androidx.core.content.res.ResourcesCompat
import ru.tensor.sbis.design.buttons.base.models.style.SbisButtonIconStyle.Companion.NO_TINT

/**
 * @author ma.kolpakov
 */
internal class DrawableIconDrawer(
    @DrawableRes private val iconRes: Int,
    private val icon: Drawable?,
    @Px private val iconSize: Int,
    context: Context
) : ButtonComponentDrawer {

    private val drawable by lazy(LazyThreadSafetyMode.NONE) {
        /*
        Ресурс загружается лениво. Если устанавливается то же самое изображение, загрузка не
        понадобится
         */
        (icon ?: ResourcesCompat.getDrawable(context.resources, iconRes, context.theme)!!).apply {
            bounds = Rect(0, 0, iconSize, iconSize)
        }
    }

    override var isVisible: Boolean = true

    override val width: Float = iconSize.toFloat()
    override val height: Float = iconSize.toFloat()

    override fun setTint(color: Int): Boolean {
        return if (color == NO_TINT) {
            false
        } else {
            drawable.setTint(color)
            true
        }
    }

    override fun draw(canvas: Canvas) {
        if (isVisible) {
            drawable.draw(canvas)
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DrawableIconDrawer

        if (iconRes != other.iconRes) return false
        if (icon != other.icon) return false
        if (iconSize != other.iconSize) return false

        return true
    }

    override fun hashCode(): Int {
        var result = iconRes
        result = 31 * result + (icon?.hashCode() ?: 0)
        result = 31 * result + iconSize
        return result
    }
}