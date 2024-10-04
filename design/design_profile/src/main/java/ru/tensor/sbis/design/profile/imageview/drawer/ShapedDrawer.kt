package ru.tensor.sbis.design.profile.imageview.drawer

import android.graphics.Canvas
import android.graphics.drawable.Drawable
import androidx.annotation.ColorInt

/**
 * Выполняет отрисовку изображения с заданной формой.
 *
 * @author us.bessonov
 */
interface ShapedDrawer {

    /** @SelfDocumented */
    fun setShape(shape: Drawable)

    /** @SelfDocumented */
    fun onDraw(drawable: Drawable?, canvas: Canvas)

    /** @SelfDocumented */
    fun onSizeChanged(w: Int, h: Int, oldW: Int, oldH: Int)

    /** @SelfDocumented */
    fun setBackgroundColor(@ColorInt color: Int)

    /** @SelfDocumented */
    fun invalidate()
}