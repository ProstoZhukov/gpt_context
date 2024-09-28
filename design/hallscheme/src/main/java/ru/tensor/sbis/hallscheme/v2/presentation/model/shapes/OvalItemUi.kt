package ru.tensor.sbis.hallscheme.v2.presentation.model.shapes

import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.view.ViewGroup
import ru.tensor.sbis.hallscheme.v2.business.model.hallschemeitem.shapes.Shape


/**
 * Класс для отображения овала.
 * @author aa.gulevskiy
 */
internal class OvalItemUi(shape: Shape, itemDefaultColor: Int) : ShapeUi(shape, itemDefaultColor) {

    override fun getShape(viewGroup: ViewGroup): Drawable {
        return GradientDrawable().apply {
            shape = GradientDrawable.OVAL
            setStroke(this@OvalItemUi.shape.size, getItemColor(viewGroup.context))
            setSize(this@OvalItemUi.shape.width, this@OvalItemUi.shape.height)
            val fillColor = getItemFillColor(viewGroup.context)
            colors = intArrayOf(fillColor, fillColor)
            gradientType = GradientDrawable.LINEAR_GRADIENT
        }
    }
}