package ru.tensor.sbis.design.profile.imageview.drawer

import android.graphics.Canvas
import android.graphics.drawable.Drawable
import ru.tensor.sbis.design.profile.personcollagelist.contentviews.CounterDrawer
import ru.tensor.sbis.fresco_view.shapeddrawer.ShapedImageView
import ru.tensor.sbis.fresco_view.shapeddrawer.SwapBufferShapedImageDrawer

/**
 * Выполняет отрисовку изображения с заданной формой, используя [SwapBufferShapedImageDrawer].
 *
 * @author us.bessonov
 */
internal class DefaultShapedDrawer(imageView: ShapedImageView, counterDrawer: CounterDrawer) : ShapedDrawer {

    private val drawer = SwapBufferShapedImageDrawer(imageView).apply {
        setOnDrawForeground {
            counterDrawer.draw(it)
        }
    }

    override fun setShape(shape: Drawable) = drawer.setShape(shape)

    override fun onDraw(drawable: Drawable?, canvas: Canvas) = drawer.onDraw(canvas)

    override fun onSizeChanged(w: Int, h: Int, oldW: Int, oldH: Int) = drawer.onSizeChanged(w, h, oldW, oldH)

    override fun setBackgroundColor(color: Int) = drawer.setBackgroundColor(color)

    override fun invalidate() = drawer.invalidate()
}