package ru.tensor.sbis.hallscheme.v2.presentation.model.shapes

import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.view.ViewGroup
import ru.tensor.sbis.hallscheme.v2.business.model.hallschemeitem.shapes.Shape
import kotlin.math.max
import kotlin.math.min


/**
 * Класс для отображения прямоугольника.
 * @author aa.gulevskiy
 */
internal class RectItemUi(shape: Shape, itemDefaultColor: Int) : ShapeUi(shape, itemDefaultColor) {

    override fun getShape(viewGroup: ViewGroup): Drawable {
        return GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            setStroke(
                min(this@RectItemUi.shape.size, max(this@RectItemUi.shape.width / 2, this@RectItemUi.shape.height / 2)),
                getItemColor(viewGroup.context)
            )
            setSize(this@RectItemUi.shape.width, this@RectItemUi.shape.height)
            val fillColor = getItemFillColor(viewGroup.context)
            colors = intArrayOf(fillColor, fillColor)
            gradientType = GradientDrawable.LINEAR_GRADIENT
        }
    }
}