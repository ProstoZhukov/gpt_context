package ru.tensor.sbis.hallscheme.v2.presentation.model.shapes

import android.content.Context
import android.graphics.BitmapShader
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import ru.tensor.sbis.hallscheme.v2.HallSchemeV2
import ru.tensor.sbis.hallscheme.v2.business.model.hallschemeitem.shapes.Shape
import ru.tensor.sbis.hallscheme.v2.presentation.model.HallSchemeItemUi

/**
 * Абстрактный класс для отображения фигур (прямоугольник, овал, линия).
 * @author aa.gulevskiy
 */
internal abstract class ShapeUi(val shape: Shape, private val itemDefaultColor: Int) : HallSchemeItemUi(shape) {

    override fun draw(
        viewGroup: ViewGroup,
        onItemClickListener: HallSchemeV2.OnHallSchemeItemClickListener?
    ) {
        super.draw(viewGroup, null)
    }

    override fun draw3D(
        viewGroup: ViewGroup,
        pressedShader: BitmapShader,
        unpressedShader: BitmapShader,
        onItemClickListener: HallSchemeV2.OnHallSchemeItemClickListener?
    ) {
        super.draw3D(viewGroup, pressedShader, unpressedShader, null)
    }

    override fun getView(viewGroup: ViewGroup): View {
        return getShapeView(viewGroup)
    }

    override fun get3dView(viewGroup: ViewGroup): View {
        return getView(viewGroup)
    }

    private fun getShapeView(viewGroup: ViewGroup): View {
        val imageView = ImageView(viewGroup.context)

        imageView.layoutParams = ViewGroup.LayoutParams(shape.rect.width, shape.rect.height)

        imageView.x = shape.rect.left.toFloat()
        imageView.y = shape.rect.top.toFloat()

        imageView.setImageDrawable(getShape(viewGroup))

        return imageView
    }

    /**
     * Возвращает фигуру, которую необходимо нарисовать.
     */
    abstract fun getShape(viewGroup: ViewGroup): Drawable

    /**
     * Возвращает цвет фигуры.
     */
    @ColorInt
    protected fun getItemColor(context: Context): Int {
        return getHallSchemeColor(context, this.shape.color) ?: ContextCompat.getColor(context, itemDefaultColor)
    }

    /**
     * Возвращает цвет заливки.
     */
    @ColorInt
    protected fun getItemFillColor(context: Context): Int {
        return getHallSchemeColor(context, this.shape.fillColor) ?: Color.TRANSPARENT
    }
}