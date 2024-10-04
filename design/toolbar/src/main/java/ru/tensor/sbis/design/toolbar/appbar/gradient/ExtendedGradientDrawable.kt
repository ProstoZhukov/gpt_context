package ru.tensor.sbis.design.toolbar.appbar.gradient

import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.PixelFormat
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import androidx.annotation.ColorInt
import ru.tensor.sbis.design.utils.createGradientDrawable

/**
 * [Drawable], содержащий градиент с заданной высотой [gradientHeight], пространство под которым заполняется указанным
 * цветом
 *
 * @author us.bessonov
 */
internal class ExtendedGradientDrawable(@ColorInt color: Int, private val gradientHeight: Int) : Drawable() {

    private val gradientDrawable = createGradientDrawable(color)
    private val fillDrawable = ColorDrawable(color)

    override fun setBounds(left: Int, top: Int, right: Int, bottom: Int) {
        val gradientBottom = (top + gradientHeight).coerceAtMost(bottom)
        gradientDrawable.setBounds(left, top, right, gradientBottom)
        fillDrawable.setBounds(left, gradientBottom, right, bottom)
        super.setBounds(left, top, right, bottom)
    }

    override fun draw(canvas: Canvas) {
        gradientDrawable.draw(canvas)
        fillDrawable.draw(canvas)
    }

    override fun setAlpha(alpha: Int) {
        gradientDrawable.alpha = alpha
        fillDrawable.alpha = alpha
    }

    override fun getOpacity() = PixelFormat.TRANSLUCENT

    override fun setColorFilter(colorFilter: ColorFilter?) {
        gradientDrawable.colorFilter = colorFilter
        fillDrawable.colorFilter = colorFilter
    }
}