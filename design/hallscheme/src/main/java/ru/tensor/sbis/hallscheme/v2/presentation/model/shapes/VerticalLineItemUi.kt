package ru.tensor.sbis.hallscheme.v2.presentation.model.shapes

import android.graphics.drawable.Drawable
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RectShape
import android.view.ViewGroup
import ru.tensor.sbis.hallscheme.v2.business.model.hallschemeitem.shapes.Shape


/**
 * Класс для отображения горизонтальной линии.
 * @author aa.gulevskiy
 */
internal class VerticalLineItemUi(shape: Shape, itemDefaultColor: Int) : ShapeUi(shape, itemDefaultColor) {
    override fun getShape(viewGroup: ViewGroup): Drawable {
        return ShapeDrawable(RectShape()).apply {
            intrinsicHeight = this@VerticalLineItemUi.shape.height
            intrinsicWidth = this@VerticalLineItemUi.shape.size
            paint.color = getItemColor(viewGroup.context)
        }
    }
}