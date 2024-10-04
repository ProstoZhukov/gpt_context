package ru.tensor.sbis.design.utils.shadow_clipper.utils

import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.PixelFormat
import android.graphics.drawable.Drawable

/**
 * Объект, реализующий пустой [Drawable] для использования в качестве background для view тени.
 *
 * @author ra.geraskin
 */
internal object EmptyDrawable : Drawable() {
    override fun draw(canvas: Canvas) = Unit
    override fun setAlpha(alpha: Int) = Unit
    override fun setColorFilter(filter: ColorFilter?) = Unit

    @Deprecated("Deprecated in Java", ReplaceWith("PixelFormat.TRANSLUCENT", "android.graphics.PixelFormat"))
    override fun getOpacity() = PixelFormat.TRANSLUCENT
}
