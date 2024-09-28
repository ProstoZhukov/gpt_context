package ru.tensor.sbis.design.checkbox.drawers

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.Drawable
import androidx.annotation.Px

/**
 * Реализация [CheckboxContentDrawer], когда контент чекбокса представляет собой рисуемую иконку
 *
 * @author mb.kruglova
 */
internal class IconDrawableContentDrawer(
    private val icon: Drawable,
    @Px private val iconSize: Int
) : CheckboxContentDrawer {

    private val drawable by lazy(LazyThreadSafetyMode.NONE) {
        icon.apply {
            bounds = Rect(0, 0, iconSize, iconSize)
        }
    }

    override val width: Float = iconSize.toFloat()

    override val height: Float = iconSize.toFloat()

    override fun setTint(color: Int) {
        if (color == Color.TRANSPARENT) return

        drawable.setTint(color)
    }

    override fun draw(canvas: Canvas) {
        drawable.draw(canvas)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as IconDrawableContentDrawer

        if (iconSize != other.iconSize) return false
        if (icon.constantState != other.icon.constantState) return false

        return true
    }

    override fun hashCode(): Int {
        var result = icon.constantState.hashCode()
        result = 31 * result + iconSize
        return result
    }
}